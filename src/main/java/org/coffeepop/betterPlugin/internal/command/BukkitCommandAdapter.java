package org.coffeepop.betterPlugin.internal.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Lightweight Bukkit {@link Command} adapter used only to provide command metadata
 * (name, aliases, permission) to a {@link org.bukkit.command.TabCompleter}.
 * <p>
 * Actual command execution is handled by the Brigadier command tree, so this adapter's
 * {@link #execute(CommandSender, String, String[])} is intentionally a no-op.
 */
@ApiStatus.Internal
public final class BukkitCommandAdapter extends Command {

    /**
     * Creates an adapter for the command being registered.
     *
     * @param name       the command name
     * @param aliases    the command aliases
     * @param permission the required permission, or {@code null} if none
     */
    public BukkitCommandAdapter(String name, List<String> aliases, String permission) {
        super(name, "BetterPlugin command", "/" + name, aliases);
        if (permission != null && !permission.isEmpty()) {
            setPermission(permission);
        }
    }

    @Override
    public boolean execute(@NonNull CommandSender sender, @NonNull String commandLabel, String @NonNull [] args) {
        // Brigadier handles execution; this adapter is only for tab-completion metadata.
        return false;
    }
}
