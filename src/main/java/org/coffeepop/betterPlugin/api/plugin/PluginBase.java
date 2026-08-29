package org.coffeepop.betterPlugin.api.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.coffeepop.betterPlugin.api.command.CommandBuilder;
import org.coffeepop.betterPlugin.api.event.ListenerRegistry;
import org.coffeepop.betterPlugin.api.registry.ModuleRegistry;
import org.coffeepop.betterPlugin.api.registry.Registry;
import org.coffeepop.betterPlugin.api.registry.SimpleRegistry;
import org.coffeepop.betterPlugin.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.ApiStatus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Plugin entry base class for BetterPlugin users.
 * <p>
 * Subclasses override {@link #onPluginEnable()} / {@link #onPluginDisable()}
 * instead of {@code onEnable()} / {@code onDisable()}, so the framework can
 * guarantee cleanup (for example, cancelling owned tasks) on disable.
 */
public abstract class PluginBase extends JavaPlugin {

    private final List<BukkitTask> ownedTasks = new CopyOnWriteArrayList<>();
    private final List<Runnable> pendingWhenReady = new ArrayList<>();
    private final SimpleRegistry<Object> registry = new SimpleRegistry<>();
    private final TaskScheduler taskScheduler = new TaskScheduler(this);
    private final ListenerRegistry listenerRegistry = new ListenerRegistry(this);
    private final ModuleRegistry moduleRegistry = new ModuleRegistry(this);
    private volatile boolean serverReady;

    @Override
    public final void onEnable() {
        getServer().getScheduler().runTaskLater(this, () -> {
            List<Runnable> tasks;
            synchronized (pendingWhenReady) {
                serverReady = true;
                tasks = List.copyOf(pendingWhenReady);
                pendingWhenReady.clear();
            }
            for (Runnable task : tasks) {
                try {
                    task.run();
                } catch (RuntimeException e) {
                    getLogger().log(java.util.logging.Level.WARNING, "A runWhenReady task failed", e);
                }
            }
        }, 1L);
        onPluginEnable();
    }

    @Override
    public final void onDisable() {
        for (BukkitTask task : ownedTasks) {
            task.cancel();
        }
        ownedTasks.clear();
        taskScheduler.cancelAll();
        listenerRegistry.unregisterAll();
        moduleRegistry.disableAll();
        onPluginDisable();
    }

    /**
     * Called when the plugin is enabled. Override this instead of
     * {@code onEnable()}.
     */
    @ApiStatus.OverrideOnly
    protected void onPluginEnable() {
    }

    /**
     * Called when the plugin is disabled. Override this instead of
     * {@code onDisable()}.
     */
    @ApiStatus.OverrideOnly
    protected void onPluginDisable() {
    }

    /**
     * Returns the plugin's logger. Equivalent to {@link #getLogger()}, kept
     * as a short alias for convenience.
     */
    protected java.util.logging.Logger log() {
        return getLogger();
    }

    /**
     * Reads a {@code String} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected String configString(String path, String defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getString(path, defaultValue) : defaultValue;
    }

    /**
     * Reads an {@code int} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected int configInt(String path, int defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getInt(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a {@code long} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected long configLong(String path, long defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getLong(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a {@code double} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected double configDouble(String path, double defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getDouble(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a {@code boolean} from the plugin config, falling back to
     * {@code defaultValue} when the path is not set.
     */
    protected boolean configBoolean(String path, boolean defaultValue) {
        var config = getConfig();
        return config.isSet(path) ? config.getBoolean(path, defaultValue) : defaultValue;
    }

    /**
     * Reads a string list from the plugin config. Returns an empty list when
     * the path is not set.
     */
    protected java.util.List<String> configStringList(String path) {
        var config = getConfig();
        if (!config.isSet(path)) {
            return java.util.List.of();
        }
        return config.getStringList(path);
    }

    /**
     * Copies a file from the plugin jar into the plugin data folder only if it
     * does not already exist. Useful for shipping default resource files other
     * than {@code config.yml}, such as {@code messages.yml}.
     *
     * @param path the resource path inside the jar, e.g. {@code "messages.yml"}
     */
    protected void saveDefaultResource(String path) {
        saveResource(path, false);
    }

    /**
     * Reloads the plugin config and calls {@link #onConfigReload()}.
     * <p>
     * Defaults are re-applied only if the file is missing; user changes are
     * kept.
     */
    protected void reloadPluginConfig() {
        try (InputStream ignored = getResource("config.yml")) {
            if (ignored != null) {
                saveDefaultConfig();
            }
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Failed to inspect config.yml resource", e);
        }
        reloadConfig();
        onConfigReload();
    }

    /**
     * Called after {@link #reloadPluginConfig()} has refreshed the config.
     * Override this to rebuild settings or scheduled tasks.
     */
    protected void onConfigReload() {
    }

    /**
     * Runs a task on the main thread now. The returned task is owned by the
     * plugin and is cancelled automatically on disable.
     */
    protected BukkitTask runSync(Runnable task) {
        return track(getServer().getScheduler().runTask(this, task));
    }

    /**
     * Runs a task on an asynchronous thread now. The returned task is owned
     * by the plugin and is cancelled automatically on disable.
     */
    protected BukkitTask runAsync(Runnable task) {
        return track(getServer().getScheduler().runTaskAsynchronously(this, task));
    }

    /**
     * Runs a task on the main thread after {@code delayTicks} ticks. The
     * returned task is owned by the plugin and is cancelled automatically on
     * disable.
     */
    protected BukkitTask runSyncLater(Runnable task, long delayTicks) {
        return track(getServer().getScheduler().runTaskLater(this, task, delayTicks));
    }

    /**
     * Runs a repeating task on the main thread. The returned task is owned
     * by the plugin and is cancelled automatically on disable.
     */
    protected BukkitTask runSyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(getServer().getScheduler().runTaskTimer(this, task, delayTicks, periodTicks));
    }

    /**
     * Runs a repeating asynchronous task. The returned task is owned by the
     * plugin and is cancelled automatically on disable.
     */
    protected BukkitTask runAsyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(getServer().getScheduler().runTaskTimerAsynchronously(this, task, delayTicks, periodTicks));
    }

    /**
     * Whether the server has finished starting. Returns {@code true} once the
     * first tick after enable has passed.
     */
    protected boolean isServerReady() {
        return serverReady;
    }

    /**
     * Runs the task once the server is ready (the first tick after enable).
     * If the server is already ready, the task runs immediately on the calling
     * thread.
     */
    protected void runWhenReady(Runnable task) {
        if (task == null) {
            throw new NullPointerException("task");
        }
        boolean runNow;
        synchronized (pendingWhenReady) {
            runNow = serverReady;
            if (!runNow) {
                pendingWhenReady.add(task);
            }
        }
        if (runNow) {
            task.run();
        }
    }

    private BukkitTask track(BukkitTask task) {
        ownedTasks.removeIf(BukkitTask::isCancelled);
        ownedTasks.add(task);
        return task;
    }

    /**
     * Returns a {@link CommandBuilder} bound to this plugin. Shortcut for
     * {@code CommandBuilder.create(this)}.
     */
    protected CommandBuilder command() {
        return CommandBuilder.create(this);
    }

    /**
     * Returns the plugin's shared thread-safe registry.
     * <p>
     * Use it to register services, handlers, or any value that other code in
     * the plugin should be able to look up by name.
     *
     * @param <T> the value type
     * @return the shared registry
     */
    @SuppressWarnings("unchecked")
    protected <T> Registry<T> registry() {
        return (Registry<T>) registry;
    }

    /**
     * Returns the plugin's shared {@link TaskScheduler}. Tasks created through
     * it are cancelled automatically when the plugin is disabled.
     *
     * @return the shared scheduler
     */
    protected TaskScheduler tasks() {
        return taskScheduler;
    }

    /**
     * Returns the plugin's shared {@link ListenerRegistry}. Listeners are
     * unregistered automatically when the plugin is disabled.
     *
     * @return the shared listener registry
     */
    protected ListenerRegistry listeners() {
        return listenerRegistry;
    }

    /**
     * Returns the plugin's shared {@link ModuleRegistry}. All enabled modules
     * are disabled automatically when the plugin is disabled.
     *
     * @return the shared module registry
     */
    protected ModuleRegistry modules() {
        return moduleRegistry;
    }
}
