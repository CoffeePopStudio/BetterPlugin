package org.coffeepop.betterPlugin.api.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.coffeepop.betterPlugin.api.event.ListenerRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Minimal inventory GUI helper.
 * <p>
 * Create a GUI with {@link #builder(JavaPlugin, int, String)}, set items and
 * optional click handlers, then call {@link #open(Player)}. The internal click
 * listener is registered lazily on first open and unregistered automatically
 * when the last viewer closes the GUI.
 */
public final class InventoryGui {

    /**
     * Handles a click on a GUI slot.
     */
    @FunctionalInterface
    public interface ClickHandler {
        /**
         * Called when the slot is clicked.
         *
         * @param event the click event, already cancelled
         */
        void onClick(InventoryClickEvent event);
    }

    /**
     * Builder for {@link InventoryGui}.
     */
    public static final class Builder {
        private final JavaPlugin plugin;
        private final int size;
        private final Component title;
        private final Map<Integer, ItemStack> items = new HashMap<>();
        private final Map<Integer, ClickHandler> handlers = new HashMap<>();

        private Builder(JavaPlugin plugin, int size, Component title) {
            this.plugin = Objects.requireNonNull(plugin, "plugin");
            this.size = size;
            this.title = Objects.requireNonNull(title, "title");
        }

        /**
         * Puts an item in a slot.
         *
         * @param slot the slot index
         * @param item the item
         * @return this builder
         */
        public Builder item(int slot, ItemStack item) {
            items.put(slot, Objects.requireNonNull(item, "item"));
            return this;
        }

        /**
         * Puts an item in a slot and attaches a click handler.
         *
         * @param slot    the slot index
         * @param item    the item
         * @param handler the click handler
         * @return this builder
         */
        public Builder item(int slot, ItemStack item, ClickHandler handler) {
            items.put(slot, Objects.requireNonNull(item, "item"));
            handlers.put(slot, Objects.requireNonNull(handler, "handler"));
            return this;
        }

        /**
         * Builds the GUI.
         *
         * @return the new GUI
         */
        public InventoryGui build() {
            if (size <= 0 || size % 9 != 0) {
                throw new IllegalArgumentException("inventory size must be a positive multiple of 9");
            }
            Inventory inventory = plugin.getServer().createInventory(null, size, title);
            for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                int slot = entry.getKey();
                if (slot < 0 || slot >= size) {
                    throw new IllegalArgumentException("slot " + slot + " is outside inventory size " + size);
                }
                inventory.setItem(slot, entry.getValue());
            }
            return new InventoryGui(plugin, inventory, Map.copyOf(handlers));
        }
    }

    private final JavaPlugin plugin;
    private final Inventory inventory;
    private final Map<Integer, ClickHandler> handlers;
    private final ListenerRegistry listeners;
    private final Set<Player> viewers = Collections.newSetFromMap(new IdentityHashMap<>());
    private boolean registered;

    private InventoryGui(JavaPlugin plugin, Inventory inventory, Map<Integer, ClickHandler> handlers) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.inventory = Objects.requireNonNull(inventory, "inventory");
        this.handlers = Objects.requireNonNull(handlers, "handlers");
        this.listeners = new ListenerRegistry(plugin);
    }

    /**
     * Starts building a GUI.
     *
     * @param plugin the owning plugin
     * @param size   inventory size (multiple of 9)
     * @param title  GUI title
     * @return a builder
     */
    public static Builder builder(JavaPlugin plugin, int size, String title) {
        return new Builder(plugin, size, Component.text(title));
    }

    /**
     * Opens the GUI for a player.
     *
     * @param player the player
     */
    public void open(Player player) {
        Objects.requireNonNull(player, "player");
        ensureListener();
        viewers.add(player);
        player.openInventory(inventory);
    }

    /**
     * Unregisters the GUI's internal listeners and clears viewers.
     */
    public void close() {
        synchronized (this) {
            if (!registered) {
                return;
            }
            registered = false;
            listeners.unregisterAll();
            viewers.clear();
        }
    }

    private void ensureListener() {
        synchronized (this) {
            if (registered) {
                return;
            }
            registered = true;
            listeners.register(InventoryClickEvent.class, event -> {
                if (event.getView().getTopInventory() != inventory) {
                    return;
                }
                event.setCancelled(true);
                ClickHandler handler = handlers.get(event.getSlot());
                if (handler != null) {
                    handler.onClick(event);
                }
            });
            listeners.register(InventoryCloseEvent.class, event -> {
                if (event.getInventory() != inventory) {
                    return;
                }
                viewers.remove(event.getPlayer());
                if (viewers.isEmpty()) {
                    close();
                }
            });
        }
    }
}
