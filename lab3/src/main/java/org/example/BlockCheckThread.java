package org.example;

/**
 * A thread that checks if an email is blocked right now.
 *
 * Extends Thread (instead of implementing Runnable) because it is
 * a small, fixed task that LoginTask starts and then waits for
 * with join(); having start() and join() directly on it is simplest.
 */
public class BlockCheckThread extends Thread {

    private final AttemptTracker tracker;
    private final String email;

    /** The answer: seconds left in the block, 0 if not blocked. */
    private long secondsLeft;

    public BlockCheckThread(AttemptTracker tracker, String email) {
        super("BlockCheckThread");   // thread name (helps when debugging)
        this.tracker = tracker;
        this.email = email;
    }

    /** Runs in the new thread after start() is called. */
    @Override
    public void run() {
        secondsLeft = tracker.secondsLeft(email);
    }

    /**
     * Call only after join(): join() guarantees the thread finished
     * and that we see the value it wrote.
     *
     * @return seconds left in the block, 0 if not blocked
     */
    public long getSecondsLeft() {
        return secondsLeft;
    }
}
