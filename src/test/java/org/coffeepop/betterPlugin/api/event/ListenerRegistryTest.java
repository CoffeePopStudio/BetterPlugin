package org.coffeepop.betterPlugin.api.event;

import net.kyori.adventure.text.Component;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ListenerRegistryTest {

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
    void registerAndReceiveEvent() {
        ListenerRegistry registry = new ListenerRegistry(plugin);
        AtomicReference<PlayerJoinEvent> seen = new AtomicReference<>();

        registry.register(PlayerJoinEvent.class, seen::set);

        PlayerJoinEvent event = new PlayerJoinEvent(player, Component.text("joined"));
        server.getPluginManager().callEvent(event);

        assertNotNull(seen.get(), "registered handler should receive the event");
        assertSame(event, seen.get());
    }

    @Test
    void unregisterAllStopsReceivingEvents() {
        ListenerRegistry registry = new ListenerRegistry(plugin);
        AtomicReference<PlayerJoinEvent> seen = new AtomicReference<>();
        registry.register(PlayerJoinEvent.class, seen::set);

        registry.unregisterAll();

        server.getPluginManager().callEvent(new PlayerJoinEvent(player, Component.text("joined")));

        assertNull(seen.get(), "listener should be removed after unregisterAll");
    }

    @Test
    void canRegisterAgainAfterUnregisterAll() {
        ListenerRegistry registry = new ListenerRegistry(plugin);
        AtomicReference<PlayerJoinEvent> seen = new AtomicReference<>();

        registry.register(PlayerJoinEvent.class, seen::set);
        registry.unregisterAll();

        registry.register(PlayerJoinEvent.class, seen::set);
        PlayerJoinEvent event = new PlayerJoinEvent(player, Component.text("joined"));
        server.getPluginManager().callEvent(event);

        assertSame(event, seen.get(), "registry should be reusable after unregisterAll");
    }
}
