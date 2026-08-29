package org.coffeepop.betterPlugin.api.command;

/**
 * Typed access to parsed command arguments.
 * <p>
 * Argument names match the names passed to
 * {@link CommandBuilder#argument(String, com.mojang.brigadier.arguments.ArgumentType)}.
 */
public interface CommandArguments {

    /**
     * Returns whether the named argument was present in the command input.
     * Useful for optional arguments.
     *
     * @param name the argument name
     * @return {@code true} if the argument was provided
     */
    boolean contains(String name);

    /**
     * Returns the argument as a string.
     *
     * @param name the argument name
     * @return the parsed value
     * @throws IllegalArgumentException if the argument is missing
     */
    String getString(String name);

    /**
     * Returns the argument as an {@code int}.
     *
     * @param name the argument name
     * @return the parsed value
     * @throws IllegalArgumentException if the argument is missing or not an integer
     */
    int getInt(String name);

    /**
     * Returns the argument as a {@code double}.
     *
     * @param name the argument name
     * @return the parsed value
     * @throws IllegalArgumentException if the argument is missing or not a number
     */
    double getDouble(String name);

    /**
     * Returns the argument as a {@code boolean}.
     *
     * @param name the argument name
     * @return the parsed value
     * @throws IllegalArgumentException if the argument is missing or not a boolean
     */
    boolean getBoolean(String name);
}
