package contracts;

public interface TimeCounter {

    void start();
    boolean isRunning();

    void increment(int seconds);
    void decrement(int seconds);

    Integer get();
    String toReadableTime();
    
}
