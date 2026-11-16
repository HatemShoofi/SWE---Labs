package org.example;

import javafx.application.Platform;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * Proves that the synchronized methods in UserState don't lose updates.
 * Started by the "Run concurrency test" button on the login screen.
 *
 * Test (with n = 10,000):
 *   1. 20 threads run at the same time against the SAME email,
 *      each records 499 failures -> 9,980 failures in total.
 *      Expected: the counter is exactly 9,980 (no lost updates).
 *   2. 19 more failures (9,999 in total) must NOT block.
 *   3. The 20th extra failure (10,000 = n) MUST block.
 *
 * Experiment: remove "synchronized" from UserState, run again, and
 * the counter ends up below 9,980 because some count++ updates are lost.
 */
public class ConcurrencyTest implements Runnable {

    private static final int THREADS = 20;
    private static final int FAILURES_PER_THREAD = 499;
    private static final int N = 10_000;
    private static final String EMAIL = "stress.test@example.com";

    /** Receives the report (runs on the JavaFX thread). */
    private final Consumer<String> onDone;

    public ConcurrencyTest(Consumer<String> onDone) {
        this.onDone = onDone;
    }

    @Override
    public void run() {
        String report;
        try {
            report = runTest();
        } catch (InterruptedException e) {
            return;   // program is closing
        }
        System.out.println(report);
        Platform.runLater(() -> onDone.accept(report));
    }

    /**
     * Runs the test on its own tracker (so it doesn't touch real users).
     *
     * @return a short report with PASS / FAIL for each check
     */
    public static String runTest() throws InterruptedException {
        AttemptTracker tracker = new AttemptTracker(N, 60);

        // The latch makes all 20 threads start at the same moment,
        // so they really compete for the same counter.
        CountDownLatch startSignal = new CountDownLatch(1);
        Thread[] threads = new Thread[THREADS];

        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                try {
                    startSignal.await();
                } catch (InterruptedException e) {
                    return;
                }
                for (int k = 0; k < FAILURES_PER_THREAD; k++) {
                    tracker.recordFailure(EMAIL);
                }
            }, "StressThread-" + i);
            threads[i].start();
        }

        long startTime = System.currentTimeMillis();
        startSignal.countDown();          // GO
        for (Thread t : threads) {
            t.join();                     // wait for all 20 to finish
        }
        long elapsed = System.currentTimeMillis() - startTime;

        int expected = THREADS * FAILURES_PER_THREAD;   // 9,980
        int counted = tracker.getFailures(EMAIL);

        // 19 more failures: none of them should block
        boolean blockedTooEarly = false;
        for (int i = 0; i < 19; i++) {
            if (tracker.recordFailure(EMAIL) == 0) {
                blockedTooEarly = true;
            }
        }

        // The 20th: this is failure number n, so it must block
        boolean blockedOnTime = tracker.recordFailure(EMAIL) == 0;

        boolean noLostUpdates = counted == expected;
        boolean passed = noLostUpdates && !blockedTooEarly && blockedOnTime;

        return "Concurrency test (" + THREADS + " threads x " + FAILURES_PER_THREAD
                + ", n=" + N + ", " + elapsed + " ms)\n"
                + "Counted " + counted + " of " + expected + " failures, lost "
                + (expected - counted) + ": " + (noLostUpdates ? "PASS" : "FAIL") + "\n"
                + "19 more failures do not block: " + (!blockedTooEarly ? "PASS" : "FAIL") + "\n"
                + "20th failure blocks: " + (blockedOnTime ? "PASS" : "FAIL") + "\n"
                + "RESULT: " + (passed ? "PASS" : "FAIL");
    }
}
