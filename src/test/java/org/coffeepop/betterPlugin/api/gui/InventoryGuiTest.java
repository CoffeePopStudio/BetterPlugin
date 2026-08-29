package org.coffeepop.betterPlugin.api.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InventoryGuiTest {

    private ServerMock server;
    private PluginMock plugin;
    private PlayerMock player;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        server = MockBukkit.getMock();
        plugin = MockBukkit.createMockPlugin();
        player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void buildAndOpenPutsItemsIntoTopInventory() {
        InventoryGui gui = InventoryGui.builder(plugin, 9, "Menu")
                .item(0, new ItemStack(Material.DIAMOND))
                .build();

        gui.open(player);

        ItemStack topItem = player.getOpenInventory().getTopInventory().getItem(0);
        assertNotNull(topItem, "item should be present in the opened GUI");
        assertEquals(Material.DIAMOND, topItem.getType());
    }

    @Test
    void closeDoesNotThrowWhenNeverOpened() {
        InventoryGui gui = InventoryGui.builder(plugin, 9, "Menu").build();

        assertDoesNotThrow(gui::close);
    }

    @Test
    void closeAndReopenRegistersListenerAgain() {
        InventoryGui gui = InventoryGui.builder(plugin, 9, "Menu")
                .item(0, new ItemStack(Material.DIAMOND))
                .build();

        gui.open(player);
        gui.close();
        assertDoesNotThrow(() -> gui.open(player));

        ItemStack topItem = player.getOpenInventory().getTopInventory().getItem(0);
        assertEquals(Material.DIAMOND, topItem.getType());
    }
}
