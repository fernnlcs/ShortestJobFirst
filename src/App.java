import java.io.FileNotFoundException;
import java.util.Scanner;

import main.Simulator;
import utils.exceptions.QueueMovementException;

public class App {
    public static void main(String[] args) throws Exception {
        App.example1();
    }

    public static void example1() throws QueueMovementException {
        Simulator simulator = new Simulator();

        try {
            simulator.importTasks("assets/tasks.txt");
        } catch (FileNotFoundException e) {
            // Do nothing
        }

        simulator.start();
        simulator.interact(new Scanner(System.in));
    }
}
