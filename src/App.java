import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.Simulator;
import utils.exceptions.QueueMovementException;
import utils.exceptions.TimeCounterException;

public class App {
    public static void main(String[] args) throws Exception {
        App.example1();
    }

    public static void example1() throws QueueMovementException {
        Simulator simulator = new Simulator();
        simulator.addTaskToGenerateProcesses(0, 4);
        simulator.addTaskToGenerateProcesses(10, 8);
        simulator.addTaskToGenerateProcesses(20, 16);

        try {
            simulator.start();
        } catch (TimeCounterException e1) {
            simulator.fowardStep();
        }

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        input = input.toUpperCase();

        while (!input.equals("Q")) {

            if (input.equals("A")) {
                System.out.println("Processos na fila (árvore):");
                simulator.showQueueTree();
                System.out.println();
            } else if (input.equals("L")) {
                System.out.println("Processos na fila (lista):");
                simulator.showQueueHeap();
            } else if (input.equals("H")) {
                System.out.println("Histórico de execuções:");
                simulator.showHistory();
            } else {
                try {
                    int steps = Integer.parseInt(input);
                    simulator.fowardSteps(steps);
                } catch (NumberFormatException e) {
                    simulator.fowardStep();
                }
            }

            input = scanner.nextLine();
            input = input.toUpperCase();
        }

        scanner.close();
    }

    public static void example2() {
        List<main.Process> javaList = new ArrayList<>();
        javaList.add(new main.Process(0));
        javaList.add(new main.Process(5));
        javaList.add(new main.Process(10));
    }
}
