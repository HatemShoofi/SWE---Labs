package org.example;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * A message that LoginTask puts into the failure queue when an
 * existing email typed a wrong password.
 *
 * It carries its own small reply queue (size 1): the FailureWorker
 * that handles this attempt puts the answer there, and LoginTask
 * waits on it with take(). This way LoginTask knows immediately
 * whether THIS failure caused a block.
 */
public class FailedAttempt {

    /** The email that typed a wrong password. */
    private final String email;

    /** Where the FailureWorker puts the answer (holds exactly one result). */
    private final ArrayBlockingQueue<LoginResult> reply = new ArrayBlockingQueue<>(1);

    public FailedAttempt(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    /** @return the reply queue (FailureWorker puts, LoginTask takes) */
    public ArrayBlockingQueue<LoginResult> getReply() {
        return reply;
    }
}
