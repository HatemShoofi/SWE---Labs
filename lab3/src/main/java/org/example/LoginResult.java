package org.example;

/**
 * The answer LoginTask sends back to the controller.
 * One of four kinds:
 *   SUCCESS         - email + password are correct
 *   WRONG_DETAILS   - email doesn't exist (not tracked)
 *   WRONG_PASSWORD  - email exists, wrong password (attemptsLeft is set)
 *   BLOCKED         - email is blocked (secondsLeft is set)
 */
public class LoginResult {

    /** The four possible outcomes of a login. */
    public enum Type { SUCCESS, WRONG_DETAILS, WRONG_PASSWORD, BLOCKED }

    private final Type type;
    private final String email;
    private final int attemptsLeft;
    private final long secondsLeft;

    /** Private: use the static methods below to create results. */
    private LoginResult(Type type, String email, int attemptsLeft, long secondsLeft) {
        this.type = type;
        this.email = email;
        this.attemptsLeft = attemptsLeft;
        this.secondsLeft = secondsLeft;
    }

    public static LoginResult success(String email) {
        return new LoginResult(Type.SUCCESS, email, 0, 0);
    }

    public static LoginResult wrongDetails() {
        return new LoginResult(Type.WRONG_DETAILS, null, 0, 0);
    }

    public static LoginResult wrongPassword(int attemptsLeft) {
        return new LoginResult(Type.WRONG_PASSWORD, null, attemptsLeft, 0);
    }

    public static LoginResult blocked(long secondsLeft) {
        return new LoginResult(Type.BLOCKED, null, 0, secondsLeft);
    }

    public Type getType()         { return type; }
    public String getEmail()      { return email; }
    public int getAttemptsLeft()  { return attemptsLeft; }
    public long getSecondsLeft()  { return secondsLeft; }
}
