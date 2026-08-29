package org.coffeepop.betterPlugin.api.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Executor for commands that declare typed arguments through
 * {@link CommandBuilder#argument(String, com.mojang.brigadier.arguments.ArgumentType)}.
 */
@FunctionalInterface
public interface CommandArgumentsExecutor {

    /**
     * Executes the command with parsed arguments.
     *
     * @param sender  the command sender
     * @param command the lightweight command adapter
     * @param label   the command label that was used
     * @param args    typed access to the parsed arguments
     * @return {@code true} for success, {@code false} for failure
     */
    boolean execute(CommandSender sender, Command command, String label, CommandArguments args);
}
