package org.coffeepop.betterPlugin.api.scheduler;

import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.plugin.PluginMock;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskSchedulerTest {

    private ServerMock server;
    private PluginMock plugin;
    private TaskScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        server = MockBukkit.getMock();
        plugin = MockBukkit.createMockPlugin();
        scheduler = new TaskScheduler(plugin);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void runSyncTimerRunsAndIsCancelledByCancelAll() {
        AtomicInteger runs = new AtomicInteger();
        BukkitTask task = scheduler.runSyncTimer(runs::incrementAndGet, 0L, 1L);

        server.getScheduler().performTicks(3);
        assertTrue(runs.get() > 0, "timer should run while ticks pass");

        scheduler.cancelAll();

        assertTrue(task.isCancelled(), "cancelAll should cancel owned tasks");
        assertFalse(runs.get() > 100, "timer should stop after cancelAll");
    }

    @Test
    void cancelAllClearsFinishedTasks() {
        AtomicInteger runs = new AtomicInteger();
        BukkitTask task = scheduler.runSyncLater(runs::incrementAndGet, 1L);

        server.getScheduler().performTicks(1);
        assertTrue(runs.get() == 1, "one-shot task should run");

        scheduler.cancelAll();
        assertTrue(task.isCancelled(), "finished one-shot tasks should be cancelled/cleared");
    }

    @Test
    void tasksScheduledAfterCancelAllAreCancelledImmediately() {
        scheduler.cancelAll();

        BukkitTask task = scheduler.runSync(() -> {
        });

        assertTrue(task.isCancelled(), "scheduler is closed after cancelAll, so new tasks must not run");
    }
}
