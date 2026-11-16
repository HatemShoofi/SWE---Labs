package org.example;

/**
 * Login state of ONE email: how many wrong passwords in a row,
 * and until when it is blocked.
 *
 * Several threads can use the same UserState at the same time,
 * so every method is synchronized: only one thread can be inside
 * this object's methods at a time. This prevents lost updates.
 */
public class UserState {

    /** Wrong passwords since the last reset. */
    private int failures = 0;

    /** Time (in ms) when the block ends. 0 = not blocked. */
    private long blockedUntil = 0;

    /**
     * @return seconds left in the block, or 0 if not blocked.
     * If the block time already passed, the counter resets,
     * so the user gets n new attempts.
     */
    public synchronized long secondsLeft() {
        if (blockedUntil == 0) {
            return 0;
        }
        long now = System.currentTimeMillis();
        if (now >= blockedUntil) {      // block is over
            blockedUntil = 0;
            failures = 0;
            return 0;
        }
        return (blockedUntil - now + 999) / 1000;   // round up to whole seconds
    }

    /**
     * Records one wrong password.
     * If this is failure number n, the block starts (t seconds).
     *
     * @return attempts left; 0 means the email is now blocked
     */
    public synchronized int recordFailure(int maxFailures, int blockSeconds) {
        if (secondsLeft() > 0) {        // already blocked: not counted
            return 0;
        }
        failures++;
        if (failures >= maxFailures) {
            blockedUntil = System.currentTimeMillis() + blockSeconds * 1000L;
            return 0;
        }
        return maxFailures - failures;
    }

    /** Successful login: start counting from zero again. */
    public synchronized void reset() {
        failures = 0;
        blockedUntil = 0;
    }

    /** @return current number of failures (used by the stress test) */
    public synchronized int getFailures() {
        return failures;
    }
}
