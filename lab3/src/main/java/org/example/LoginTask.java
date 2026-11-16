package org.example;

import javafx.application.Platform;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * The job that runs ONE login attempt, in its own thread
 * (never on the JavaFX thread, so the window never freezes).
 *
 * Implements Runnable (instead of extending Thread) because it is
 * a job, not a thread: the controller hands it to new Thread(...)
 * on every click. This keeps "what to do" separate from "who runs
 * it", and leaves the class free to extend another class.
 *
 * Flow:
 *   1. Start BlockCheckThread and wait for it (join).
 *      Blocked -> BLOCKED, without checking the password.
 *   2. Check email + password with the Lab 1 code.
 *      Correct -> reset the counter, SUCCESS.
 *   3. Email doesn't exist -> WRONG_DETAILS (not tracked).
 *   4. Email exists, wrong password -> put a FailedAttempt in the
 *      queue and wait (take) for a FailureWorker's answer.
 *   5. Send the result to the controller with Platform.runLater.
 */
public class LoginTask implements Runnable {

    private final String email;
    private final String password;
    private final UserRepository repository;
    private final AttemptTracker tracker;
    private final BlockingQueue<FailedAttempt> failureQueue;

    /** What to do with the result (runs on the JavaFX thread). */
    private final Consumer<LoginResult> onDone;

    public LoginTask(String email, String password, UserRepository repository,
                     AttemptTracker tracker, BlockingQueue<FailedAttempt> failureQueue,
                     Consumer<LoginResult> onDone) {
        this.email = email;
        this.password = password;
        this.repository = repository;
        this.tracker = tracker;
        this.failureQueue = failureQueue;
        this.onDone = onDone;
    }

    @Override
    public void run() {
        LoginResult result;
        try {
            result = login();
        } catch (InterruptedException e) {
            return;   // the program is closing: stop quietly
        }
        // Only the JavaFX thread may touch the screen, so we hand the
        // result over to it instead of updating the label from here.
        Platform.runLater(() -> onDone.accept(result));
    }

    /** The login steps (runs in this background thread). */
    LoginResult login() throws InterruptedException {
        // 1. Is the email blocked? Ask BlockCheckThread and wait for it.
        BlockCheckThread check = new BlockCheckThread(tracker, email);
        check.start();
        check.join();
        if (check.getSecondsLeft() > 0) {
            return LoginResult.blocked(check.getSecondsLeft());
        }

        // 2. Lab 1 validation: User's constructor throws an exception
        //    if the email or password format is invalid.
        boolean validFormat = true;
        try {
            new User(email, password);
        } catch (IllegalArgumentException e) {
            validFormat = false;   // can't be a correct login
        }

        if (validFormat && repository.isValidLogin(email, password)) {
            tracker.reset(email);
            return LoginResult.success(email);
        }

        // 3. Unknown email: generic message, no tracking.
        if (!repository.emailExists(email)) {
            return LoginResult.wrongDetails();
        }

        // 4. Known email, wrong password: hand it to a FailureWorker
        //    through the BlockingQueue and wait for its answer.
        FailedAttempt attempt = new FailedAttempt(email);
        failureQueue.put(attempt);          // a FailureWorker wakes up
        return attempt.getReply().take();   // sleep until it answers
    }
}
