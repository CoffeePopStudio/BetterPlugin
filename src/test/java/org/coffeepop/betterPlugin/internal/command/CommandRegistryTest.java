package org.coffeepop.betterPlugin.internal.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.command.brigadier.PaperCommandsMock;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommandRegistryTest {

    private PluginMock plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    private PaperCommandsMock newCommands() {
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        return commands;
    }

    @Test
    void registerAllRegistersCommandAndClearsSuppliers() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        PaperCommandsMock commands = newCommands();

        registry.addCommand(c -> LiteralArgumentBuilder
                .<CommandSourceStack>literal("hello")
                .executes(ctx -> 1));
        registry.registerAll(commands);

        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("hello"), "command should be registered in the dispatcher");
        assertEquals(0, registry.pendingCount(), "registrations should be cleared after registerAll");
    }

    @Test
    void registerAllRegistersMultipleCommands() {
        CommandRegistry registry = new CommandRegistry();
        PaperCommandsMock commands = newCommands();

        registry.addCommand(c -> LiteralArgumentBuilder.<CommandSourceStack>literal("one").executes(ctx -> 1));
        registry.addCommand(c -> LiteralArgumentBuilder.<CommandSourceStack>literal("two").executes(ctx -> 2));

        registry.registerAll(commands);

        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("one"));
        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("two"));
    }

    @Test
    void supplierReceivesTheCommandsRegistrarUsedForRegistration() {
        CommandRegistry registry = new CommandRegistry();
        AtomicReference<Commands> seenCommands = new AtomicReference<>();

        registry.addCommand(commands -> {
            seenCommands.set(commands);
            return LiteralArgumentBuilder.<CommandSourceStack>literal("cmd").executes(ctx -> 1);
        });

        PaperCommandsMock commands = newCommands();
        registry.registerAll(commands);

        assertSame(commands, seenCommands.get(), "supplier should receive the same registrar that commands are registered to");
    }

    @Test
    void addCommandAfterRegisterAllIsIgnored() {
        CommandRegistry registry = new CommandRegistry();
        PaperCommandsMock commands = newCommands();

        registry.addCommand(c -> LiteralArgumentBuilder.<CommandSourceStack>literal("first").executes(ctx -> 1));
        registry.registerAll(commands);
        assertEquals(0, registry.pendingCount());

        registry.addCommand(c -> LiteralArgumentBuilder.<CommandSourceStack>literal("second").executes(ctx -> 1));
        assertEquals(0, registry.pendingCount(), "commands added after COMMANDS has fired should be ignored");

        registry.registerAll(commands);
        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("first"));
        assertNull(commands.getDispatcherInternal().getRoot().getChild("second"));
    }

    @Test
    void registerAllClearsQueueEvenWhenSupplierFails() {
        CommandRegistry registry = new CommandRegistry();
        PaperCommandsMock commands = newCommands();

        registry.addCommand(c -> LiteralArgumentBuilder.<CommandSourceStack>literal("one").executes(ctx -> 1));
        registry.addCommand(c -> {
            throw new IllegalStateException("boom");
        });

        assertThrows(IllegalStateException.class, () -> registry.registerAll(commands));
        assertEquals(0, registry.pendingCount(), "queue should be empty after a failing registration");
        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("one"), "commands registered before the failure should remain registered");

        assertDoesNotThrow(() -> registry.registerAll(commands), "a second registerAll should be a no-op, not a re-registration of the failed queue");
    }

    @Test
    void registerAllRegistersAliases() {
        CommandRegistry registry = new CommandRegistry();
        PaperCommandsMock commands = newCommands();

        registry.addCommand(
                c -> LiteralArgumentBuilder.<CommandSourceStack>literal("main").executes(ctx -> 1),
                List.of("alias-a", "alias-b")
        );
        registry.registerAll(commands);

        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("main"));
        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("alias-a"));
        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("alias-b"));
    }
}
