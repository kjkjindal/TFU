package app.model.protocol;

import app.model.protocol.commands.CommandExecutionException;
import app.model.protocol.Cycle;
import app.model.protocol.commands.CommandExecutionException;
import javafx.scene.control.Alert;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project: FluidXMan
 * Author: alexskrynnyk
 * Date: 1/24/17
 */
public class ProtocolTaskThread extends Thread {

    private AtomicBoolean run = new AtomicBoolean(true);
    private Cycle cycle;
    private final Object lock = new Object();

    public ProtocolTaskThread() {
        super("ProtocolTaskThread");
    }

    public void run(Cycle newCycle) {
        synchronized(this.lock) {
            System.out.println("Got the lock. Assign new task and notify task thread");
            if(this.cycle != null) {
                throw new IllegalStateException("Already running a task!");
            }
            this.cycle = newCycle;
            this.lock.notifyAll();
        }
    }

    public void stopTaskThread() {
        synchronized (this.lock) {
            System.out.println("Stop task thread");
            this.run.set(false);
        }
    }

    public void run() {
        boolean ran = false;
        while (this.run.get()) {
            System.out.println("In task thread, look for new tasks");
            synchronized (this.lock) {
                System.out.println("Got the lock. Check whether it needs to run any task");
                try {
                    waitForCycle();
                    ran = executeCycle();
                } catch (CommandExecutionException e) {
                    System.out.println("Error while executing the Runnable: " + e);
                    throw new RuntimeException(e.getMessage(), e);
                } finally {
                    System.out.println("in finally");
                    cleanupCycle();
                    if (ran) {
                        ran = false;
                    }
                }
            }
        }
        System.out.println("Task thread is down");
    }

    private boolean executeCycle() throws CommandExecutionException {
        if (this.cycle == null) {
            return false;
        }
        System.out.println("Got a new task to run");
        System.out.println("Run the task");
        this.cycle.execute();
        return true;
    }

    private void cleanupCycle() {
        synchronized (this.lock) {
            System.out.println("Task execution over");
            this.cycle = null;
        }
    }

    private void waitForCycle() {
        while (this.cycle == null && run.get()) {
            System.out.println("No task, wait for 200ms and then check again");
            try {
                this.lock.wait(200);
            } catch (InterruptedException e) {
                System.out.println("Task thread was interrupted." + e);
            }
        }
    }

}
