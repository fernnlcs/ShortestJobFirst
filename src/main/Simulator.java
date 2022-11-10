package main;

public class Simulator {

    private CPU cpu;
    private Logger logger;
    private SecondsCounter executionTime;

    public Simulator() {
        this.executionTime = new SecondsCounter();
        this.logger = new Logger(this.executionTime);
        this.cpu = new CPU(logger, executionTime);

        this.present();
    }

    public Simulator(CPU cpu) {
        this.cpu = cpu;
        this.executionTime = new SecondsCounter();
        this.logger = new Logger(this.executionTime);

        this.present();
    }

    /**
     * 
     */
    public void present() {
        System.out.println(">>> Algoritmo SJF <<<\n");
        System.out.println("Tecle ENTER para avançar 01 segundo.");
        System.out.println("Digite um número para avançar os segundos correspondentes.");
        System.out.println("Digite 'q' ou 'Q' para parar.");
        System.out.println("Digite 'a' ou 'A' para ver os processos em forma de árvore.");
        System.out.println("Digite 'l' ou 'L' para ver os processos em forma de heap.");
        System.out.println("Digite 'h' ou 'H' para ver o histórico de execuções.");
        System.out.println();
    }

    public void showQueueTree() {
        this.cpu.showQueueTree();
    }

    public void showQueueHeap() {
        this.cpu.showQueueHeap();
    }

    public void showHistory() {
        this.cpu.showHistory();
    }

    public void fowardSteps(int steps) {
        this.cpu.fowardSteps(steps);
    }

    public void fowardStep() {
        this.cpu.fowardStep();
    }

    public void addTask(int second, int processesQuantity) {
        this.cpu.addTask(second, processesQuantity);
    }

    public void start() {
        this.cpu.start();
    }
}
