package main;

import contracts.Orderable;

public class Process implements Orderable {

    private int id;
    private String name;
    private int remainingTime;
    private String message;

    /**
     * @param id
     * @param remainingTime
     * @param name
     */
    public Process(int id, int remainingTime) {
        this.id = id;
        this.name = "Processo #" + id;
        this.remainingTime = remainingTime;
        this.message = "Oi, sou o " + this.getName();
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
    public String getMessage() {
        return message;
    }

    /**
     * @return
     */
    public int getId() {
        return id;
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
