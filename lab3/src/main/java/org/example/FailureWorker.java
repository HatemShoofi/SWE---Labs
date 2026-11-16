package org.example;

import java.util.concurrent.BlockingQueue;

/**
 * A background worker (two of them run) that handles wrong-password
 * events.
 *
 * It waits on a BlockingQueue: take() puts the thread to sleep until
 * an event arrives, so there is no busy wait (no loop that keeps
 * checking and burns CPU). For each event it updates the counter in
 * the AttemptTracker and sends the answer back on the event's reply
 * queue.
 *
 * Extends Thread because it is a long-lived thread with its own
 * identity (name, daemon flag) that runs for the whole program;
 * App creates it once and starts it directly.
 *
 * It is a daemon thread: it never stops the program from closing.
 */
public class FailureWorker extends Thread {

    private final BlockingQueue<FailedAttempt> queue;
    private final AttemptTracker tracker;

    public FailureWorker(String name, BlockingQueue<FailedAttempt> queue, AttemptTracker tracker) {
        super(name);
        this.queue = queue;
        this.tracker = tracker;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                // Sleeps here until LoginTask puts an event in the queue
                FailedAttempt attempt = queue.take();

                // Count the failure (synchronized inside UserState)
                int attemptsLeft = tracker.recordFailure(attempt.getEmail());

                LoginResult answer;
                if (attemptsLeft == 0) {
                    answer = LoginResult.blocked(tracker.secondsLeft(attempt.getEmail()));
                } else {
                    answer = LoginResult.wrongPassword(attemptsLeft);
                }

                // Wake up the LoginTask that is waiting for this answer
                attempt.getReply().put(answer);
            }
        } catch (InterruptedException e) {
            // interrupt() was called because the program is closing: just end
        }
    }
}
