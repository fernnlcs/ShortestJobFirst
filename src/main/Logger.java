package main;

import structures.Queue;
import utils.exceptions.EmptyListException;

public class Logger {

    private Queue<String> queue = new Queue<>();
    private SecondsCounter counter;

    public Logger(SecondsCounter counter) {
        this.counter = counter;
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
        System.out.println("[" + this.counter.toReadableTime() + "]");

        while (this.queue.size() > 0) {
            try {
                System.out.println("> " + this.queue.pop().trim());
            } catch (EmptyListException e) {
                // Do nothing
            }
        }
    }

}
