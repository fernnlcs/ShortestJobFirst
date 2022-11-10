import java.util.Scanner;

import main.Simulator;
import utils.exceptions.QueueMovementException;

public class App {
    public static void main(String[] args) throws Exception {
        App.example1();
    }

    public static void example1() throws QueueMovementException {
        Simulator simulator = new Simulator();

        simulator.addTaskToGenerateProcesses(0, 4);
        simulator.addTaskToGenerateProcesses(10, 8);
        simulator.addTaskToGenerateProcesses(20, 16);

        simulator.start();
        simulator.interact(new Scanner(System.in));
    }
}
