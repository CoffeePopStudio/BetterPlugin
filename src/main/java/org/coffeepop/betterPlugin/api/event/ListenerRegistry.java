package org.coffeepop.betterPlugin.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Registers and unregisters event listeners without requiring annotated
 * {@link Listener} classes.
 */
public final class ListenerRegistry {

    private final JavaPlugin plugin;
    private final Listener listener = new Listener() {
    };
    private final Set<Class<? extends Event>> registered = ConcurrentHashMap.newKeySet();

    /**
     * Creates a registry owned by the given plugin.
     *
     * @param plugin the plugin that owns the listeners
     */
    public ListenerRegistry(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Registers a handler for an event at normal priority.
     *
     * @param eventClass the event type
     * @param handler    the handler receiving the event
     * @param <T>        the event type
     */
    public <T extends Event> void register(Class<T> eventClass, Consumer<T> handler) {
        register(Objects.requireNonNull(eventClass, "eventClass"), EventPriority.NORMAL, Objects.requireNonNull(handler, "handler"), false);
    }

    /**
     * Registers a handler for an event with explicit settings.
     *
     * @param eventClass      the event type
     * @param priority        the event priority
     * @param handler         the handler receiving the event
     * @param ignoreCancelled whether cancelled events should be ignored
     * @param <T>             the event type
     */
    public <T extends Event> void register(Class<T> eventClass, EventPriority priority, Consumer<T> handler, boolean ignoreCancelled) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(handler, "handler");
        plugin.getServer().getPluginManager().registerEvent(
                eventClass,
                listener,
                priority,
                (l, event) -> handler.accept(eventClass.cast(event)),
                plugin,
                ignoreCancelled
        );
        registered.add(eventClass);
    }

    /**
     * Unregisters every listener registered through this instance.
     */
    public void unregisterAll() {
        HandlerList.unregisterAll(listener);
        registered.clear();
    }
}
