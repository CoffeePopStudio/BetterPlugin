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

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void setItemUpdatesOpenedInventory() {
        InventoryGui gui = InventoryGui.builder(plugin, 9, "Menu").build();

        gui.open(player);
        gui.setItem(0, new ItemStack(Material.EMERALD));

        assertEquals(Material.EMERALD, player.getOpenInventory().getTopInventory().getItem(0).getType());
    }

    @Test
    void onCloseHandlerRunsWhenPlayerCloses() {
        AtomicBoolean closed = new AtomicBoolean();
        InventoryGui gui = InventoryGui.builder(plugin, 9, "Menu")
                .onClose(p -> closed.set(true))
                .build();

        gui.open(player);
        player.closeInventory();

        assertTrue(closed.get(), "close handler should run when the player closes the GUI");
    }
}
