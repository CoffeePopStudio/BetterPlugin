package org.coffeepop.betterPlugin.api.registry;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default thread-safe {@link Registry} implementation backed by a
 * {@link ConcurrentHashMap}.
 *
 * @param <T> the value type
 */
public final class SimpleRegistry<T> implements Registry<T> {

    private final Map<String, T> entries = new ConcurrentHashMap<>();

    @Override
    public void register(String key, T value) {
        entries.put(
                Objects.requireNonNull(key, "key"),
                Objects.requireNonNull(value, "value")
        );
    }

    @Override
    public Optional<T> get(String key) {
        return Optional.ofNullable(entries.get(key));
    }

    @Override
    public boolean contains(String key) {
        return entries.containsKey(key);
    }

    @Override
    public Set<String> keys() {
        return Set.copyOf(entries.keySet());
    }

    @Override
    public Collection<T> values() {
        return List.copyOf(entries.values());
    }

    @Override
    public Optional<T> remove(String key) {
        return Optional.ofNullable(entries.remove(key));
    }

    @Override
    public void clear() {
        entries.clear();
    }
}
