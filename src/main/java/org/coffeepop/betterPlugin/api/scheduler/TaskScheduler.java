package org.coffeepop.betterPlugin.api.scheduler;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Schedules tasks on behalf of a plugin and cancels them together.
 * <p>
 * Tasks created through this class are tracked automatically; call
 * {@link #cancelAll()} on plugin disable. After {@link #cancelAll()} the
 * scheduler is considered closed and will immediately cancel any task created
 * through it.
 */
public final class TaskScheduler {

    private final JavaPlugin plugin;
    private final List<BukkitTask> ownedTasks = new CopyOnWriteArrayList<>();
    private final Object lock = new Object();
    private boolean closed;

    /**
     * Creates a scheduler owned by the given plugin.
     *
     * @param plugin the plugin that owns the tasks
     */
    public TaskScheduler(JavaPlugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /**
     * Runs a task on the main thread now.
     */
    public BukkitTask runSync(Runnable task) {
        return track(plugin.getServer().getScheduler().runTask(plugin, Objects.requireNonNull(task, "task")));
    }

    /**
     * Runs a task on an asynchronous thread now.
     */
    public BukkitTask runAsync(Runnable task) {
        return track(plugin.getServer().getScheduler().runTaskAsynchronously(plugin, Objects.requireNonNull(task, "task")));
    }

    /**
     * Runs a task on the main thread after {@code delayTicks} ticks.
     */
    public BukkitTask runSyncLater(Runnable task, long delayTicks) {
        return track(plugin.getServer().getScheduler().runTaskLater(plugin, Objects.requireNonNull(task, "task"), delayTicks));
    }

    /**
     * Runs a repeating task on the main thread.
     */
    public BukkitTask runSyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(plugin.getServer().getScheduler().runTaskTimer(plugin, Objects.requireNonNull(task, "task"), delayTicks, periodTicks));
    }

    /**
     * Runs a repeating asynchronous task.
     */
    public BukkitTask runAsyncTimer(Runnable task, long delayTicks, long periodTicks) {
        return track(plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, Objects.requireNonNull(task, "task"), delayTicks, periodTicks));
    }

    /**
     * Cancels every task still owned by this scheduler and closes it.
     * <p>
     * After this method returns, any task scheduled through this instance is
     * cancelled immediately.
     */
    public void cancelAll() {
        synchronized (lock) {
            closed = true;
            for (BukkitTask task : ownedTasks) {
                task.cancel();
            }
            ownedTasks.clear();
        }
    }

    private BukkitTask track(BukkitTask task) {
        synchronized (lock) {
            if (closed) {
                task.cancel();
                return task;
            }
            ownedTasks.removeIf(BukkitTask::isCancelled);
            ownedTasks.add(task);
        }
        return task;
    }
}
