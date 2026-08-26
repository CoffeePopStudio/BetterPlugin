package org.coffeepop.betterPlugin.api.exception;

import org.jetbrains.annotations.ApiStatus;

/**
 * Thrown when a command cannot be registered because the builder is in an invalid state.
 * <p>
 * This API is still experimental and may change in future versions.
 */
@ApiStatus.Experimental
public class CommandException extends RuntimeException {

    /**
     * Creates a new command exception with the given message.
     *
     * @param message the detail message
     */
    public CommandException(String message) {
        super(message);
    }
}
