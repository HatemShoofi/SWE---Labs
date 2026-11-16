package org.example;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps one UserState per email, and knows n and t.
 * The block is per EMAIL (not per window), so all login threads
 * share this one tracker.
 */
public class AttemptTracker {

    /** email -> its state. ConcurrentHashMap is safe for many threads. */
    private final ConcurrentHashMap<String, UserState> states = new ConcurrentHashMap<>();

    private final int maxFailures;   // n
    private final int blockSeconds;  // t

    public AttemptTracker(int maxFailures, int blockSeconds) {
        this.maxFailures = maxFailures;
        this.blockSeconds = blockSeconds;
    }

    /** @return seconds left in the block for this email, 0 if not blocked */
    public long secondsLeft(String email) {
        UserState state = states.get(email);
        if (state == null) {             // never failed -> not blocked
            return 0;
        }
        return state.secondsLeft();
    }

    /**
     * Records a wrong password for this email.
     * computeIfAbsent creates the UserState on the first failure,
     * atomically: two threads can never create two states for one email.
     *
     * @return attempts left; 0 = now blocked
     */
    public int recordFailure(String email) {
        UserState state = states.computeIfAbsent(email, e -> new UserState());
        return state.recordFailure(maxFailures, blockSeconds);
    }

    /** Successful login: reset this email's counter. */
    public void reset(String email) {
        UserState state = states.get(email);
        if (state != null) {
            state.reset();
        }
    }

    /** @return failures counted for this email (used by the stress test) */
    public int getFailures(String email) {
        UserState state = states.get(email);
        return state == null ? 0 : state.getFailures();
    }

    public int getMaxFailures()  { return maxFailures; }
    public int getBlockSeconds() { return blockSeconds; }
}
