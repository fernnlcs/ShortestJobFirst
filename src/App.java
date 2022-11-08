import java.util.Scanner;

import main.CPU;
import utils.exceptions.QueueMovementException;

public class App {
    public static void main(String[] args) throws Exception {
        App.example1();
    }

    public static void example1() throws QueueMovementException {
        System.out.println(">>> Algoritmo SJF <<<\n");
        System.out.println("Tecle ENTER para avançar 01 segundo.");
        System.out.println("Digite um número para avançar os segundos correspondentes.");
        System.out.println("Digite 'q' ou 'Q' para parar.");
        System.out.println("Digite 'a' ou 'A' para ver os processos em forma de árvore.");
        System.out.println("Digite 'l' ou 'L' para ver os processos em forma de heap.");
        System.out.println("Digite 'h' ou 'H' para ver o histórico de execuções.");
        System.out.println();

        CPU cpu = new CPU();
        cpu.addTask(0, 4);
        cpu.addTask(10, 8);
        cpu.addTask(20, 16);
        cpu.start();

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        input = input.toUpperCase();

        while (!input.equals("Q")) {

            if (input.equals("A")) {
                System.out.println("Processos na fila (árvore):");
                cpu.showQueueTree();
                System.out.println();
            } else if (input.equals("L")) {
                System.out.println("Processos na fila (lista):");
                cpu.showQueueHeap();
            } else if (input.equals("H")) {
                System.out.println("Histórico de execuções:");
                cpu.showHistory();
            } else {
                try {
                    int steps = Integer.parseInt(input);
                    cpu.fowardSteps(steps);
                } catch (NumberFormatException e) {
                    cpu.fowardStep();
                }
            }

            input = scanner.nextLine();
            input = input.toUpperCase();
        }

        scanner.close();
    }

}
