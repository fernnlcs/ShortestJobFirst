package main;

import contracts.Orderable;
import structures.Queue;
import utils.exceptions.EmptyListException;

public class Process implements Orderable {

    private int id;
    private String name;
    private int remainingTime;

    private static int nextId = 0;
    private static Queue<String> examplesOfNames = null;

    /**
     * @param remainingTime
     */
    public Process(int remainingTime) {
        this.id = Process.getNextId();
        this.remainingTime = remainingTime;

        if (Process.examplesOfNames == null) {
            Process.examplesOfNames = ProcessNameQueueGenerator.get();
        }

        this.setName();
    }

    private static int getNextId() {
        return Process.nextId++;
    }

    @Override
    public Integer getIdentifier() {
        return this.getRemainingTime();
    }

    /**
     * @return
     */
    public Integer getRemainingTime() {
        return remainingTime;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public int getId() {
        return id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setName() {
        try {
            this.name = Process.examplesOfNames.pop();
        } catch (EmptyListException e) {
            this.setName("Process #" + this.getId());
        }
    }

    /**
     * @param seconds
     */
    public void decrementRemainingTime(int seconds) {
        this.remainingTime -= seconds;
    }

    @Override
    public void show() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        String result = "[" + this.getRemainingTime().toString() + "s] " + this.getName();
        return result;
    }

}
