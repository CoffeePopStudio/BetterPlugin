package org.coffeepop.betterPlugin.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fluent builder for creating {@link ItemStack}s with display names and lore.
 */
public final class ItemBuilder {

    private final Material material;
    private int amount = 1;
    private Component displayName;
    private List<Component> lore = new ArrayList<>();

    private ItemBuilder(Material material) {
        this.material = Objects.requireNonNull(material, "material");
    }

    /**
     * Starts building an item of the given material.
     *
     * @param material the item material
     * @return a new builder
     */
    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material);
    }

    /**
     * Sets the stack size.
     *
     * @param amount the amount
     * @return this builder
     */
    public ItemBuilder amount(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        this.amount = amount;
        return this;
    }

    /**
     * Sets the display name.
     *
     * @param name the display name
     * @return this builder
     */
    public ItemBuilder name(String name) {
        return name(Component.text(Objects.requireNonNull(name, "name")));
    }

    /**
     * Sets the display name as a component.
     *
     * @param name the display name component
     * @return this builder
     */
    public ItemBuilder name(Component name) {
        this.displayName = Objects.requireNonNull(name, "name");
        return this;
    }

    /**
     * Adds lore lines as plain text.
     *
     * @param lines the lore lines
     * @return this builder
     */
    public ItemBuilder lore(String... lines) {
        for (String line : lines) {
            lore.add(Component.text(Objects.requireNonNull(line, "line")));
        }
        return this;
    }

    /**
     * Adds lore lines as plain text.
     *
     * @param lines the lore lines
     * @return this builder
     */
    public ItemBuilder lore(List<String> lines) {
        for (String line : lines) {
            lore.add(Component.text(Objects.requireNonNull(line, "line")));
        }
        return this;
    }

    /**
     * Builds the item.
     *
     * @return the item stack
     */
    public ItemStack build() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (displayName != null) {
            meta.displayName(displayName);
        }
        if (!lore.isEmpty()) {
            meta.lore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
}
