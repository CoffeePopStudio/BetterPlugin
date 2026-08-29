package org.coffeepop.betterPlugin.api.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Schedules tasks on behalf of a plugin and cancels them together.
 * <p>
 * Tasks created through this class are tracked automatically; call
 * {@link #cancelAll()} on plugin disable (or simply keep the instance for the
 * plugin's lifetime).
 */
public final class TaskScheduler {

    private final JavaPlugin plugin;
    private final List<BukkitTask> ownedTasks = new CopyOnWriteArrayList<>();

    /**
     * Creates a scheduler owned by the given plugin.
     *
     * @param plugin the plugin that owns the tasks
     */
    public TaskScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs a task on the main thread now.
     */
    public BukkitTask runSync(Runnable task) {
        return track(plugin.getServer().getScheduler().runTask(plugin, task));
    }

    /**
     * Runs a task on an asynchronous thread now.
     */
    public BukkitTask runAsync(Runnable task) {
        return track(plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task));
    }

    /**
     * Runs a task on the main thread after {@code delayTicks} ticks.
     */
    public BukkitTask runSyncLater(Runnable task, long delayTicks) {
        return track(plugin.getServer().getScheduler().runTaskLater(plugin, task, delayTicks));
    }

    /**
     * Runs a repeating task on the main thread.
     */
    public BukkitTask runSyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(plugin.getServer().getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks));
    }

    /**
     * Runs a repeating asynchronous task.
     */
    public BukkitTask runAsyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks));
    }

    /**
     * Cancels every task still owned by this scheduler and clears the list.
     */
    public void cancelAll() {
        for (BukkitTask task : ownedTasks) {
            task.cancel();
        }
        ownedTasks.clear();
    }

    private BukkitTask track(BukkitTask task) {
        ownedTasks.removeIf(BukkitTask::isCancelled);
        ownedTasks.add(task);
        return task;
    }
}
