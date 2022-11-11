package main;

import contracts.TimeCounter;
import structures.Queue;
import utils.exceptions.EmptyListException;

public class Logger {

    private Queue<String> queue = new Queue<>();
    private TimeCounter counter;
    private TimeCounter estimator = null;

    public Logger(TimeCounter counter) {
        this.counter = counter;
    }

    public Logger(TimeCounter counter, TimeCounter estimator) {
        this.counter = counter;
        this.estimator = estimator;
    }

    /**
     * @param information
     */
    public void log(String information) {
        this.queue.push(information);
    }

    /**
     * 
     */
    public void report() {
        String label = "[" + this.counter.toReadableTime() + "]";

        if (this.estimator != null && this.estimator.isRunning() && this.estimator.get() > 0) {
            double proportion = (double) this.counter.get() / (double) this.estimator.get();
            proportion *= 100;
            proportion = Math.round(proportion);
            label += " " + (int) proportion + "%";
        }

        System.out.println(label);

        while (this.queue.size() > 0) {
            try {
                System.out.println("> " + this.queue.pop().trim());
            } catch (EmptyListException e) {
                // Do nothing
            }
        }
    }

}
