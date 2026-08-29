package org.coffeepop.betterPlugin.api.registry;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * A thread-safe, named registry for arbitrary values.
 *
 * @param <T> the value type
 */
public interface Registry<T> {

    /**
     * Registers a value under a key, replacing any previous value.
     *
     * @param key   the key
     * @param value the value
     */
    void register(String key, T value);

    /**
     * Returns the value registered under the key.
     *
     * @param key the key
     * @return the value, if present
     */
    Optional<T> get(String key);

    /**
     * Returns whether a value is registered under the key.
     *
     * @param key the key
     * @return {@code true} if present
     */
    boolean contains(String key);

    /**
     * Returns all registered keys.
     *
     * @return an immutable snapshot of the keys
     */
    Set<String> keys();

    /**
     * Returns all registered values.
     *
     * @return an immutable snapshot of the values
     */
    Collection<T> values();

    /**
     * Removes the value registered under the key.
     *
     * @param key the key
     * @return the removed value, if present
     */
    Optional<T> remove(String key);

    /**
     * Removes all entries.
     */
    void clear();
}
