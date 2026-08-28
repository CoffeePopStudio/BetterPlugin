package org.coffeepop.betterPlugin.api.exception;

/**
 * Thrown when a command cannot be registered because the builder is in an invalid state.
 */
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
