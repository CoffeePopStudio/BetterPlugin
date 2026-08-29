package org.coffeepop.betterPlugin.internal.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import org.coffeepop.betterPlugin.api.exception.CommandException;
import org.coffeepop.betterPlugin.bootstrap.BetterPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.command.CommandSourceStackMock;
import org.mockbukkit.mockbukkit.command.brigadier.PaperCommandsMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandBuilderImplTest {

    private ServerMock server;
    private BetterPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        server = MockBukkit.getMock();
        plugin = MockBukkit.load(BetterPlugin.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void builderMethodsAreFluent() {
        CommandBuilderImpl builder = new CommandBuilderImpl();

        assertSame(builder, builder.name("cmd"));
        assertSame(builder, builder.permission("test.perm"));
        assertSame(builder, builder.aliases("alias1", "alias2"));
        assertSame(builder, builder.plugin(plugin));
        assertSame(builder, builder.description("desc"));
        assertSame(builder, builder.usage("/cmd"));
        assertSame(builder, builder.permissionMessage("no permission"));
        assertSame(builder, builder.playerOnly());
        assertSame(builder, builder.consoleOnly());
        assertSame(builder, builder.cooldown(Duration.ofSeconds(1)));
        assertSame(builder, builder.cooldownMessage("wait"));
        assertSame(builder, builder.then(LiteralArgumentBuilder.<CommandSourceStack>literal("sub")));
        assertSame(builder, builder.context(null));
        assertSame(builder, builder.tabCompleter(null));
    }

    @Test
    void registerRejectsNullName() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void registerRejectsEmptyName() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("");
        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void registerRejectsNullContext() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("cmd");
        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void registerRejectsBlankName() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("   ");
        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void registerRejectsBlankAlias() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("cmd");
        builder.aliases("   ");
        builder.executes(ctx -> 1);
        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void registerRejectsDuplicateRegistration() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("cmd");
        builder.executes(ctx -> 1);
        builder.register();
        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void registerRejectsContextWithoutExecutor() {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.register(LiteralArgumentBuilder.<CommandSourceStack>literal("nofunc"));
        ParseResults<CommandSourceStack> parsed = dispatcher.parse(
                "nofunc",
                CommandSourceStackMock.from(server.getConsoleSender())
        );
        CommandContext<CommandSourceStack> context = parsed.getContext().build("nofunc");

        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("cmd");
        builder.context(context);
        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void thenRejectsNullChild() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("cmd");
        assertThrows(NullPointerException.class, () -> builder.then(null));
    }

    @Test
    void cooldownRejectsNegative() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("cmd");
        assertThrows(IllegalArgumentException.class, () -> builder.cooldown(Duration.ofSeconds(-1)));
    }

    @Test
    void registerRejectsMixingArgumentsAndSubcommands() {
        CommandBuilderImpl builder = new CommandBuilderImpl();
        builder.name("cmd");
        builder.argument("value", IntegerArgumentType.integer());
        builder.then(LiteralArgumentBuilder.<CommandSourceStack>literal("sub"));
        builder.arguments((sender, command, label, args) -> true);

        assertThrows(CommandException.class, builder::register);
    }

    @Test
    void registerAddsCommandToRegistryAndExecutesIt() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CommandContext<CommandSourceStack> context = createContext("greet", executions);

        CommandBuilder.create()
                .name("greet")
                .context(context)
                .register();

        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcherInternal();

        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        int result = assertDoesNotThrow(() -> dispatcher.execute("greet", source));

        assertEquals(1, result);
        assertEquals(1, executions.get());
    }

    @Test
    void registerAppliesPermissionRequirement() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        CommandContext<CommandSourceStack> context = createContext("secret", executions);

        CommandBuilder.create()
                .name("secret")
                .permission("test.secret")
                .context(context)
                .register();

        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcherInternal();

        PlayerMock player = server.addPlayer();
        CommandSourceStack noPermissionSource = CommandSourceStackMock.from(player);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("secret", noPermissionSource));
        assertEquals(0, executions.get(), "command must not run without the required permission");

        player.addAttachment(plugin, "test.secret", true);
        CommandSourceStack withPermissionSource = CommandSourceStackMock.from(player);

        int result = assertDoesNotThrow(() -> dispatcher.execute("secret", withPermissionSource));
        assertEquals(1, result);
        assertEquals(1, executions.get());
    }

    @Test
    void registerRegistersAliases() {
        AtomicInteger executions = new AtomicInteger();
        CommandContext<CommandSourceStack> context = createContext("main", executions);

        CommandBuilder.create()
                .name("main")
                .aliases("alias-a", "alias-b")
                .context(context)
                .register();

        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);

        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("main"));
        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("alias-a"));
        assertNotNull(commands.getDispatcherInternal().getRoot().getChild("alias-b"));
    }

    @Test
    void registerThenDispatchThroughServerExecutesCommand() {
        AtomicInteger executions = new AtomicInteger();
        CommandContext<CommandSourceStack> context = createContext("hello", executions);

        CommandBuilder.create()
                .name("hello")
                .context(context)
                .register();

        boolean success = server.dispatchCommand(server.getConsoleSender(), "hello");

        assertTrue(success, "dispatchCommand should return true for a successfully executed command");
        assertEquals(1, executions.get());
    }

    @Test
    void executesCommandExecutorReceivesSenderAndArgs() throws Exception {
        AtomicReference<CommandSender> seenSender = new AtomicReference<>();
        AtomicReference<String[]> seenArgs = new AtomicReference<>();
        AtomicInteger executions = new AtomicInteger();

        CommandExecutor executor = (sender, command, label, args) -> {
            seenSender.set(sender);
            seenArgs.set(args);
            executions.incrementAndGet();
            return true;
        };

        CommandBuilder.create()
                .name("exec")
                .executes(executor)
                .register();

        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcherInternal();

        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        int rootResult = assertDoesNotThrow(() -> dispatcher.execute("exec", source));
        assertEquals(1, rootResult);
        assertEquals(1, executions.get());
        assertNotNull(seenSender.get());
        assertEquals(0, seenArgs.get().length);

        int argsResult = assertDoesNotThrow(() -> dispatcher.execute("exec one two", source));
        assertEquals(1, argsResult);
        assertEquals(2, executions.get());
        assertArrayEquals(new String[]{"one", "two"}, seenArgs.get());

        int quotedResult = assertDoesNotThrow(() -> dispatcher.execute("exec \"one two\"", source));
        assertEquals(1, quotedResult);
        assertEquals(3, executions.get());
        assertArrayEquals(new String[]{"one two"}, seenArgs.get(), "quoted arguments should arrive as a single argument");
    }

    @Test
    void executesRawBrigadierCommand() throws Exception {
        AtomicReference<CommandSender> seenSender = new AtomicReference<>();
        com.mojang.brigadier.Command<CommandSourceStack> rawCommand = ctx -> {
            seenSender.set(ctx.getSource().getSender());
            return 1;
        };

        CommandBuilder.create()
                .name("raw")
                .executes(rawCommand)
                .register();

        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcherInternal();

        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        int result = assertDoesNotThrow(() -> dispatcher.execute("raw", source));

        assertEquals(1, result);
        assertNotNull(seenSender.get());
        assertSame(server.getConsoleSender(), seenSender.get());
    }

    @Test
    void createWithPluginRegistersAndExecutes() throws Exception {
        AtomicInteger executions = new AtomicInteger();

        CommandBuilder.create(plugin)
                .name("owned")
                .executes((sender, command, label, args) -> {
                    executions.incrementAndGet();
                    return true;
                })
                .register();

        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcherInternal();

        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        int result = assertDoesNotThrow(() -> dispatcher.execute("owned", source));

        assertEquals(1, result);
        assertEquals(1, executions.get());
    }

    @Test
    void registerAppliesTabCompleter() throws Exception {
        AtomicInteger executions = new AtomicInteger();
        AtomicReference<Command> seenCommand = new AtomicReference<>();
        CommandContext<CommandSourceStack> context = createContext("complete", executions);

        TabCompleter completer = (sender, command, alias, args) -> {
            seenCommand.set(command);
            if (args.length == 1 && args[0].startsWith("a")) {
                return List.of("apple", "avocado");
            }
            return List.of();
        };

        CommandBuilder.create()
                .name("complete")
                .permission("test.complete")
                .aliases("c")
                .context(context)
                .tabCompleter(completer)
                .register();

        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);
        CommandDispatcher<CommandSourceStack> dispatcher = commands.getDispatcherInternal();

        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        Suggestions suggestions = dispatcher.getCompletionSuggestions(dispatcher.parse("complete a", source)).join();

        List<String> completions = suggestions.getList().stream().map(Suggestion::getText).toList();
        assertTrue(completions.contains("apple"));
        assertTrue(completions.contains("avocado"));

        assertNotNull(seenCommand.get(), "TabCompleter should receive the lightweight command adapter");
        assertEquals("complete", seenCommand.get().getName());
        assertEquals(List.of("c"), seenCommand.get().getAliases());
        assertEquals("test.complete", seenCommand.get().getPermission());

        int result = assertDoesNotThrow(() -> dispatcher.execute("complete hello", source));
        assertEquals(1, result);
        assertEquals(1, executions.get());
    }

    @Test
    @SuppressWarnings("deprecation") // Command#getPermissionMessage is deprecated but still exposed for adapter metadata
    void registerAppliesCommandMetadataToAdapter() throws Exception {
        AtomicReference<Command> seenCommand = new AtomicReference<>();

        CommandBuilder builder = CommandBuilder.create()
                .name("meta")
                .description("A meta command")
                .usage("/meta <value>")
                .permissionMessage("No permission!")
                .executes((sender, command, label, args) -> true)
                .tabCompleter((sender, command, label, args) -> {
                    seenCommand.set(command);
                    return List.of();
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        dispatcher.getCompletionSuggestions(dispatcher.parse("meta ", source)).join();

        assertNotNull(seenCommand.get());
        assertEquals("A meta command", seenCommand.get().getDescription());
        assertEquals("/meta <value>", seenCommand.get().getUsage());
        assertEquals("No permission!", seenCommand.get().getPermissionMessage());
    }

    @Test
    void registerPlayerOnlyRejectsConsole() throws Exception {
        AtomicInteger executions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("playeronly")
                .playerOnly()
                .executes((sender, command, label, args) -> {
                    executions.incrementAndGet();
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack consoleSource = CommandSourceStackMock.from(server.getConsoleSender());

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("playeronly", consoleSource));
        assertEquals(0, executions.get());

        PlayerMock player = server.addPlayer();
        CommandSourceStack playerSource = CommandSourceStackMock.from(player);
        int result = assertDoesNotThrow(() -> dispatcher.execute("playeronly", playerSource));

        assertEquals(1, result);
        assertEquals(1, executions.get());
    }

    @Test
    void registerConsoleOnlyRejectsPlayer() throws Exception {
        AtomicInteger executions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("consoleonly")
                .consoleOnly()
                .executes((sender, command, label, args) -> {
                    executions.incrementAndGet();
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        PlayerMock player = server.addPlayer();
        CommandSourceStack playerSource = CommandSourceStackMock.from(player);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("consoleonly", playerSource));
        assertEquals(0, executions.get());

        CommandSourceStack consoleSource = CommandSourceStackMock.from(server.getConsoleSender());
        int result = assertDoesNotThrow(() -> dispatcher.execute("consoleonly", consoleSource));

        assertEquals(1, result);
        assertEquals(1, executions.get());
    }

    @Test
    void registerCooldownBlocksRepeatedExecution() throws Exception {
        AtomicInteger executions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("cooldown")
                .cooldown(Duration.ofSeconds(60))
                .executes((sender, command, label, args) -> {
                    executions.incrementAndGet();
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        PlayerMock player = server.addPlayer();
        CommandSourceStack source = CommandSourceStackMock.from(player);

        int first = assertDoesNotThrow(() -> dispatcher.execute("cooldown", source));
        int second = assertDoesNotThrow(() -> dispatcher.execute("cooldown", source));

        assertEquals(1, first);
        assertEquals(0, second);
        assertEquals(1, executions.get());
    }

    @Test
    void registerSubCommands() throws Exception {
        AtomicInteger rootExecutions = new AtomicInteger();
        AtomicInteger subExecutions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("parent")
                .executes((sender, command, label, args) -> {
                    rootExecutions.incrementAndGet();
                    return true;
                })
                .then(LiteralArgumentBuilder
                        .<CommandSourceStack>literal("sub")
                        .executes(ctx -> {
                            subExecutions.incrementAndGet();
                            return 1;
                        }));

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());

        int rootResult = assertDoesNotThrow(() -> dispatcher.execute("parent", source));
        int subResult = assertDoesNotThrow(() -> dispatcher.execute("parent sub", source));

        assertEquals(1, rootResult);
        assertEquals(1, subResult);
        assertEquals(1, rootExecutions.get());
        assertEquals(1, subExecutions.get());
    }

    @Test
    void registerCombinesPermissionAndPlayerOnly() throws Exception {
        AtomicInteger executions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("secure")
                .permission("test.secure")
                .playerOnly()
                .executes((sender, command, label, args) -> {
                    executions.incrementAndGet();
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);

        PlayerMock player = server.addPlayer();
        CommandSourceStack noPermission = CommandSourceStackMock.from(player);
        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("secure", noPermission));
        assertEquals(0, executions.get(), "command must not run without the required permission");

        player.addAttachment(plugin, "test.secure", true);
        CommandSourceStack withPermission = CommandSourceStackMock.from(player);
        int result = assertDoesNotThrow(() -> dispatcher.execute("secure", withPermission));
        assertEquals(1, result);
        assertEquals(1, executions.get());

        CommandSourceStack console = CommandSourceStackMock.from(server.getConsoleSender());
        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("secure", console));
        assertEquals(1, executions.get(), "playerOnly must still apply when permission is granted");
    }

    @Test
    void registerPlayerOnlyAndConsoleOnlyRejectsEveryone() throws Exception {
        AtomicInteger executions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("nobody")
                .playerOnly()
                .consoleOnly()
                .executes((sender, command, label, args) -> {
                    executions.incrementAndGet();
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);

        CommandSourceStack console = CommandSourceStackMock.from(server.getConsoleSender());
        PlayerMock player = server.addPlayer();
        CommandSourceStack playerSource = CommandSourceStackMock.from(player);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("nobody", console));
        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("nobody", playerSource));
        assertEquals(0, executions.get(), "conflicting restrictions must block every sender");
    }

    @Test
    void registerCooldownDoesNotApplyToConsole() throws Exception {
        AtomicInteger executions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("consolecooldown")
                .cooldown(Duration.ofSeconds(60))
                .executes((sender, command, label, args) -> {
                    executions.incrementAndGet();
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());

        int first = assertDoesNotThrow(() -> dispatcher.execute("consolecooldown", source));
        int second = assertDoesNotThrow(() -> dispatcher.execute("consolecooldown", source));

        assertEquals(1, first);
        assertEquals(1, second);
        assertEquals(2, executions.get(), "cooldown should only affect players");
    }

    @Test
    void cooldownMessageIsConfigurable() throws Exception {
        PlayerMock player = server.addPlayer();

        CommandBuilder builder = CommandBuilder.create()
                .name("cooldownmsg")
                .cooldown(Duration.ofSeconds(60))
                .cooldownMessage("Still cooling down!")
                .executes((sender, command, label, args) -> true);

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(player);

        assertDoesNotThrow(() -> dispatcher.execute("cooldownmsg", source));
        int second = assertDoesNotThrow(() -> dispatcher.execute("cooldownmsg", source));

        assertEquals(0, second);
        assertEquals(Component.text("Still cooling down!"), player.nextComponentMessage());
    }

    @Test
    void permissionMessageIsSentWhenPermissionMissing() throws Exception {
        PlayerMock player = server.addPlayer();

        CommandBuilder builder = CommandBuilder.create()
                .name("secretmsg")
                .permission("test.secretmsg")
                .permissionMessage("No permission!")
                .executes((sender, command, label, args) -> true);

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(player);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("secretmsg", source));
        assertEquals(Component.text("No permission!"), player.nextComponentMessage());
    }

    @Test
    void registerSubCommandsBypassRootCooldown() throws Exception {
        AtomicInteger rootExecutions = new AtomicInteger();
        AtomicInteger subExecutions = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("cooldownparent")
                .cooldown(Duration.ofSeconds(60))
                .executes((sender, command, label, args) -> {
                    rootExecutions.incrementAndGet();
                    return true;
                })
                .then(LiteralArgumentBuilder
                        .<CommandSourceStack>literal("sub")
                        .executes(ctx -> {
                            subExecutions.incrementAndGet();
                            return 1;
                        }));

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        PlayerMock player = server.addPlayer();
        CommandSourceStack source = CommandSourceStackMock.from(player);

        int rootResult = assertDoesNotThrow(() -> dispatcher.execute("cooldownparent", source));
        int firstSub = assertDoesNotThrow(() -> dispatcher.execute("cooldownparent sub", source));
        int secondSub = assertDoesNotThrow(() -> dispatcher.execute("cooldownparent sub", source));

        assertEquals(1, rootResult);
        assertEquals(1, firstSub);
        assertEquals(1, secondSub);
        assertEquals(1, rootExecutions.get());
        assertEquals(2, subExecutions.get(), "root cooldown must not block subcommands");
    }

    @Test
    void registerThenIgnoresTabCompleter() throws Exception {
        AtomicBoolean completerCalled = new AtomicBoolean();

        CommandBuilder builder = CommandBuilder.create()
                .name("withchild")
                .executes((sender, command, label, args) -> true)
                .tabCompleter((sender, command, label, args) -> {
                    completerCalled.set(true);
                    return List.of("apple");
                })
                .then(LiteralArgumentBuilder
                        .<CommandSourceStack>literal("sub")
                        .executes(ctx -> 1));

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        dispatcher.getCompletionSuggestions(dispatcher.parse("withchild ", source)).join();

        assertFalse(completerCalled.get(), "tabCompleter must be ignored when child nodes are present");
    }

    @Test
    void registerContextExecutorHasHighestPriority() throws Exception {
        AtomicInteger contextExecutions = new AtomicInteger();
        AtomicInteger rawExecutions = new AtomicInteger();
        AtomicInteger bukkitExecutions = new AtomicInteger();
        CommandContext<CommandSourceStack> context = createContext("priority", contextExecutions);
        com.mojang.brigadier.Command<CommandSourceStack> rawCommand = ctx -> {
            rawExecutions.incrementAndGet();
            return 1;
        };
        CommandExecutor bukkitExecutor = (sender, command, label, args) -> {
            bukkitExecutions.incrementAndGet();
            return true;
        };

        CommandBuilder builder = CommandBuilder.create()
                .name("priority")
                .context(context)
                .executes(rawCommand)
                .executes(bukkitExecutor);

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        int result = assertDoesNotThrow(() -> dispatcher.execute("priority", source));

        assertEquals(1, result);
        assertEquals(1, contextExecutions.get());
        assertEquals(0, rawExecutions.get());
        assertEquals(0, bukkitExecutions.get());
    }

    @Test
    void argumentsExecutorReceivesTypedValues() throws Exception {
        AtomicInteger sum = new AtomicInteger();

        CommandBuilder builder = CommandBuilder.create()
                .name("sum")
                .argument("left", IntegerArgumentType.integer())
                .argument("right", IntegerArgumentType.integer())
                .arguments((sender, command, label, args) -> {
                    sum.set(args.getInt("left") + args.getInt("right"));
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        int result = assertDoesNotThrow(() -> dispatcher.execute("sum 20 22", source));

        assertEquals(1, result);
        assertEquals(42, sum.get());
    }

    @Test
    void argumentSuggestionsAreProvided() throws Exception {
        CommandBuilder builder = CommandBuilder.create()
                .name("pick")
                .argument("color", com.mojang.brigadier.arguments.StringArgumentType.word())
                .suggestions("red", "blue")
                .arguments((sender, command, label, args) -> true);

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        Suggestions suggestions = dispatcher.getCompletionSuggestions(dispatcher.parse("pick ", source)).join();

        List<String> texts = suggestions.getList().stream().map(Suggestion::getText).toList();
        assertTrue(texts.contains("red"));
        assertTrue(texts.contains("blue"));
    }

    @Test
    void optionalArgumentAllowsRootExecution() throws Exception {
        AtomicReference<String> seen = new AtomicReference<>();

        CommandBuilder builder = CommandBuilder.create()
                .name("warp")
                .argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
                .suggestions("home", "spawn")
                .optional()
                .arguments((sender, command, label, args) -> {
                    seen.set(args.contains("name") ? args.getString("name") : "home");
                    return true;
                });

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());

        assertDoesNotThrow(() -> dispatcher.execute("warp", source));
        assertEquals("home", seen.get());

        assertDoesNotThrow(() -> dispatcher.execute("warp spawn", source));
        assertEquals("spawn", seen.get());
    }

    @Test
    void suggestOnlinePlayersProvidesPlayerNames() throws Exception {
        server.addPlayer("alice");

        CommandBuilder builder = CommandBuilder.create()
                .name("msg")
                .argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
                .suggestOnlinePlayers()
                .arguments((sender, command, label, args) -> true);

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(server.getConsoleSender());
        Suggestions suggestions = dispatcher.getCompletionSuggestions(dispatcher.parse("msg ", source)).join();

        List<String> texts = suggestions.getList().stream().map(Suggestion::getText).toList();
        assertTrue(texts.contains("alice"));
    }

    @Test
    void customPlaceholderIsUsedInCooldownMessage() throws Exception {
        PlayerMock player = server.addPlayer();

        CommandBuilder builder = CommandBuilder.create()
                .name("cd")
                .cooldown(Duration.ofSeconds(60))
                .cooldownMessage("{prefix} wait {cooldown}")
                .placeholder("prefix", sender -> "[X] ")
                .executes((sender, command, label, args) -> true);

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(player);

        assertDoesNotThrow(() -> dispatcher.execute("cd", source));
        assertDoesNotThrow(() -> dispatcher.execute("cd", source));

        String message = PlainTextComponentSerializer.plainText().serialize(player.nextComponentMessage());
        assertTrue(message.startsWith("[X]") && message.contains("wait"), "custom prefix should be formatted, got: " + message);
    }

    @Test
    void messageFormatterOverridesFormatting() throws Exception {
        PlayerMock player = server.addPlayer();

        CommandBuilder builder = CommandBuilder.create()
                .name("fmt")
                .permission("test.fmt")
                .permissionMessage("denied")
                .messageFormatter((template, sender) -> "[" + template.toUpperCase() + "]")
                .executes((sender, command, label, args) -> true);

        CommandDispatcher<CommandSourceStack> dispatcher = registerAndGetDispatcher(builder);
        CommandSourceStack source = CommandSourceStackMock.from(player);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("fmt", source));
        assertEquals(Component.text("[DENIED]"), player.nextComponentMessage());
    }

    @Test
    void betterPluginRegistersInfoCommand() {
        PlayerMock player = server.addPlayer();

        boolean success = server.dispatchCommand(player, "betterplugin");

        assertTrue(success, "betterplugin command should be registered by BetterPlugin itself");
        String message = PlainTextComponentSerializer.plainText().serialize(player.nextComponentMessage());
        assertTrue(message.contains("BetterPlugin"), "info command should report the framework name, got: " + message);
    }

    private CommandDispatcher<CommandSourceStack> registerAndGetDispatcher(CommandBuilder builder) {
        builder.register();
        CommandRegistry registry = plugin.getCommandRegistry();
        PaperCommandsMock commands = PaperCommandsMock.INSTANCE;
        commands.newDispatcher();
        commands.setCurrentContext(plugin);
        registry.registerAll(commands);
        return commands.getDispatcherInternal();
    }

    private CommandContext<CommandSourceStack> createContext(String literal, AtomicInteger executions) {
        CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher<>();
        dispatcher.register(LiteralArgumentBuilder
                .<CommandSourceStack>literal(literal)
                .executes(ctx -> {
                    executions.incrementAndGet();
                    return 1;
                }));

        ParseResults<CommandSourceStack> parsed = dispatcher.parse(
                literal,
                CommandSourceStackMock.from(server.getConsoleSender())
        );
        return parsed.getContext().build(literal);
    }
}
