package org.coffeepop.betterPlugin.api.item;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemBuilderTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void buildsItemWithNameAndLore() {
        ItemStack item = ItemBuilder.of(Material.DIAMOND)
                .amount(2)
                .name("Sword")
                .lore("First line", "Second line")
                .build();

        assertEquals(Material.DIAMOND, item.getType());
        assertEquals(2, item.getAmount());
        assertNotNull(item.getItemMeta());
        assertEquals(Component.text("Sword"), item.getItemMeta().displayName());
        assertEquals(List.of(Component.text("First line"), Component.text("Second line")), item.getItemMeta().lore());
    }

    @Test
    void buildsPlainItemWithoutMetaOverrides() {
        ItemStack item = ItemBuilder.of(Material.STONE).build();

        assertEquals(Material.STONE, item.getType());
        assertEquals(1, item.getAmount());
    }
}
