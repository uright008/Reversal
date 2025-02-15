package cn.stars.reversal.ui.atmoic.island;

import lombok.Getter;

@Getter
public class AtomicTask {
    private final Runnable task;
    private final long delay;
    private final int priority;

    public AtomicTask(Runnable task, long delay, int priority) {
        this.task = task;
        this.delay = delay;
        this.priority = priority;
    }

    public AtomicTask(Runnable task, long delay) {
        this.task = task;
        this.delay = delay;
        this.priority = 0;
    }

    public AtomicTask(Runnable task) {
        this.task = task;
        this.delay = 1000;
        this.priority = 0;
    }
}
