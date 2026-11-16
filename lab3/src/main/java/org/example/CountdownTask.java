package org.example;

import javafx.application.Platform;

import java.util.function.LongConsumer;

/**
 * Live countdown shown while an email is blocked.
 *
 * Runs in its own thread so it can sleep() between ticks without
 * freezing the window. Every second it asks the JavaFX thread
 * (Platform.runLater) to show the seconds left, and when the time
 * is up it tells the controller to clear the message and enable
 * the inputs again.
 *
 * Implements Runnable because it is a job given to new Thread(...);
 * a new one is created for every block.
 */
public class CountdownTask implements Runnable {

    private final long seconds;

    /** Shows "X seconds left" (runs on the JavaFX thread). */
    private final LongConsumer onTick;

    /** Called once when the block is over (runs on the JavaFX thread). */
    private final Runnable onFinished;

    public CountdownTask(long seconds, LongConsumer onTick, Runnable onFinished) {
        this.seconds = seconds;
        this.onTick = onTick;
        this.onFinished = onFinished;
    }

    @Override
    public void run() {
        long end = System.currentTimeMillis() + seconds * 1000L;
        try {
            while (true) {
                long msLeft = end - System.currentTimeMillis();
                if (msLeft <= 0) {
                    break;
                }
                long secondsLeft = (msLeft + 999) / 1000;   // round up
                Platform.runLater(() -> onTick.accept(secondsLeft));

                // Sleep until the next whole second
                long sleepMs = msLeft - (secondsLeft - 1) * 1000L;
                Thread.sleep(sleepMs);
            }
            Platform.runLater(onFinished);
        } catch (InterruptedException e) {
            // The window was closed during the countdown: stop quietly
        }
    }
}
