package main;

import contracts.Orderable;
import structures.PriorityQueue;
import utils.exceptions.QueueIndexException;
import utils.exceptions.QueueMovementException;
import utils.exceptions.TaskException;
import utils.exceptions.TimeCounterException;

public class Simulator {

    /**
     * Task
     */
    private abstract class Task implements Orderable {

        protected int time;
        private boolean done = false;

        public Task(int time) throws TaskException {
            if (executionTime.isRunning() && executionTime.get() > time) {
                throw new TaskException("Não é possível agendar uma tarefa para um tempo passado.");
            }
            this.time = time;
        }

        public int getTime() {
            return this.time;
        }

        @Override
        public Integer getIdentifier() {
            return this.time;
        }

        public abstract void doTask();

        protected void markAsDone() {
            this.done = true;
        }

        protected void markAsUndone() {
            this.done = false;
        }

        public boolean isDone() {
            return this.done;
        }
    }

    /**
     * TaskToGenerateProcesses
     */
    public class TaskToGenerateProcesses extends Task {

        int quantity;

        public TaskToGenerateProcesses(int time, int quantity) throws TaskException {
            super(time);
            this.quantity = quantity;
        }

        /**
         * @return
         */
        private static SecondsCounter generateRemainingTime() {
            final int min = 1;
            final int max = 21;
            return new SecondsCounter((int) (Math.random() * (max - min)) + min);
        }

        @Override
        public void doTask() {
            logger.log(this.quantity + " processos foram gerados automaticamente.");

            for (int i = 0; i < this.quantity; i++) {
                SecondsCounter remainingTime = generateRemainingTime();
                Process newProcess = new Process(remainingTime.get());
                cpu.add(newProcess);
            }

            this.markAsDone();
        }

        @Override
        public void show() {
            System.out.println(this.toString());
        }

        public String toString() {
            return "Aos " + this.time + "s, " + this.quantity + " processos serão gerados automaticamente.";
        }

    }

    private CPU cpu;
    private Logger logger;
    private SecondsCounter executionTime;
    private PriorityQueue<Task> tasks = new PriorityQueue<>(true);

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

    public void start() throws TimeCounterException {
        // Imprimir log das atividades realizadas antes de iniciar a contagem
        if (!this.executionTime.isRunning()) {
            this.logger.report();
            System.out.println();
        }

        this.executionTime.start();
        this.logger.log("A simulação foi iniciada.");

        this.doTasks();

        this.cpu.doProcesses();

        this.logger.report();
    }

    public void fowardStep() {
        this.executionTime.increment(CPU.secondsPerStep);
        this.doTasks();
        this.cpu.doProcesses();
        this.logger.report();
    }

    public void fowardSteps(int steps) {
        for (int i = 0; i < steps; i++) {
            this.fowardStep();
            System.out.println();
        }
    }

    public void addTaskToGenerateProcesses(int second, int processesQuantity) {
        try {
            TaskToGenerateProcesses task = new TaskToGenerateProcesses(second, processesQuantity);

            try {
                this.tasks.insert(task);
                this.logger.log("Tarefa adicionada: " + task.toString());
            } catch (QueueMovementException e) {
                throw new TaskException("Não foi possível adicionar a tarefa.");
            }

        } catch (TaskException e) {
            this.logger.log(e.getMessage());
            e.printStackTrace();
        }
    }

    private void doTasks() {
        try {
            Task next = this.tasks.getElement(1);
            while (next.getTime() == this.executionTime.get()) {
                this.tasks.remove().doTask();
                next = this.tasks.getElement(1);
            }
        } catch (QueueIndexException e) {
            // Não há nenhuma tarefa agendada
        } catch (QueueMovementException e) {
            this.logger.log("Houve um erro ao descartar a tarefa.");
        }
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
}
