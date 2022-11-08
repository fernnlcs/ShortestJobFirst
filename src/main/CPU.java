package main;

import contracts.Orderable;
import structures.PriorityQueue;
import structures.Queue;
import structures.SinglyLinkedList;
import utils.exceptions.CPUIsBusyException;
import utils.exceptions.CPUNotRunningException;
import utils.exceptions.QueueIndexException;
import utils.exceptions.QueueMovementException;
import utils.exceptions.TransitionZoneException;

public class CPU {

    class TaskToGenerate implements Orderable {
        int second;
        int quantity;

        /**
         * @param seconds
         * @param quantity
         */
        public TaskToGenerate(int seconds, int quantity) {
            this.second = seconds;
            this.quantity = quantity;
        }

        @Override
        public Integer getIdentifier() {
            return this.second;
        }

        @Override
        public void show() {
            System.out.println(this.toString());
        }

        public String toString() {
            return "Aos " + this.second + "s, " + this.quantity + " processos serão gerados automaticamente.";
        }
    }

    class Executor {
        Process process;
        Integer startTime;
        Integer endTime;
        Integer remainingTime;

        /**
         * @param process
         * @param startTime
         */
        public Executor(Process process, Integer startTime) {
            // Define os atributos
            this.process = process;
            this.startTime = startTime;

            // Define o tempo restante como 3s ou o tempo do processo, se for menor
            if (process.getRemainingTime() < CPU.secondsPerExecution) {
                this.remainingTime = process.getRemainingTime();
            } else {
                this.remainingTime = CPU.secondsPerExecution;
            }

            // Define o tempo de encerramento
            this.endTime = this.startTime + this.remainingTime;

            // Adiciona ao histórico
            history.addLast("[" + CPU.secondsToReadableTime(this.startTime) + " - "
                    + CPU.secondsToReadableTime(this.getEndTime()) + "]\n" + this.process.getName() + " ("
                    + this.process.getRemainingTime() + "s -> " + (this.process.getRemainingTime() - this.remainingTime)
                    + "s)");

            log("Lançado: " + this.process.getName() + ".");
            log("Mensagem: \"" + this.process.getMessage() + "\".");
            this.execute(1);
        }

        /**
         * @return
         */
        public Integer getEndTime() {
            return this.endTime;
        }

        /**
         * @param seconds
         */
        public void decrement(int seconds) {
            this.remainingTime -= seconds;
            this.process.decrementRemainingTime(seconds);
        }

        /**
         * 
         */
        public void interrupt() {
            executing = null;

            if (this.process.getRemainingTime() > 0) {
                try {
                    log("Pausa: " + this.process.getName() + " interrompido.");
                    suspendToTransitionZone(process);
                } catch (TransitionZoneException e) {
                    log("Erro: Não foi possível suspender " + this.process.getName() + ". " + e.getMessage());
                }
            } else {
                log("Descarte: " + this.process.getName() + " foi encerrado corretamente.");
            }

            try {
                getProcessToExecute();
            } catch (CPUIsBusyException e) {
                log("Erro: Não foi possível iniciar próximo processo automaticamente. " + e.getMessage());
            }
        }

        /**
         * @param seconds
         */
        public void execute(int seconds) {
            if (this.remainingTime >= 0) {
                log("Em execução: " + this.process.getName() + ". Tempo cedido: "
                        + this.remainingTime + "s. O processo ainda requer " + this.process.getRemainingTime()
                        + "s.");

                if (this.remainingTime > 0) {
                    this.decrement(seconds);
                } else {
                    this.interrupt();
                }

            } else {
                this.interrupt();
            }

        }
    }

    private PriorityQueue<Process> queue = new PriorityQueue<>(true);
    private PriorityQueue<TaskToGenerate> tasks = new PriorityQueue<>(true);
    private SinglyLinkedList<String> history = new SinglyLinkedList<>();
    private Queue<String> logger = new Queue<>();
    private Executor executing = null;
    private Process transitionZone = null;
    private Integer executionTime = null;
    private Integer nextId = 0;

    public static final int secondsPerStep = 1;
    public static final int secondsPerExecution = 3;

    /**
     * 
     */
    public CPU() {
    }

    /**
     * @return
     */
    public boolean isRunning() {
        return this.executionTime != null;
    }

    /**
     * @return
     */
    public boolean isBusy() {
        return (this.executing != null);
    }

    /**
     * @return
     */
    private int generateId() {
        int result = this.nextId;
        this.nextId += 1;
        return result;
    }

    /**
     * @return
     */
    private static int generateRemainingTime() {
        final int min = 1;
        final int max = 21;
        return (int) (Math.random() * (max - min)) + min;
    }

    /**
     * @param second
     * @param quantity
     */
    public void addTask(int second, int quantity) {
        try {
            TaskToGenerate task = new TaskToGenerate(second, quantity);
            this.tasks.insert(task);
            this.log("Tarefa adicionada: " + task.toString());
        } catch (QueueMovementException e) {
            this.log("Não foi possível adicionar a tarefa.");
        }
    }

    /**
     * @param name
     * @return
     */
    public Process add() {
        int id = this.generateId();
        int remainingTime = CPU.generateRemainingTime();

        Process process = new Process(id, remainingTime);

        try {
            this.queue.insert(process);
        } catch (QueueMovementException e) {
            this.log("Erro: " + e.getMessage());
        }

        this.log("Processo adicionado: " + process);
        return process;
    }

    /**
     * @param process
     * @return
     */
    public Process add(Process process) {
        try {
            this.queue.insert(process);
        } catch (QueueMovementException e) {
            this.log("Erro: " + e.getMessage());
        }

        this.log("Processo adicionado: " + process);
        return process;
    }

    /**
     * @param quantity
     */
    public void generateProcesses(int quantity) {
        this.log(quantity + " processos foram gerados automaticamente.");
        for (int i = 0; i < quantity; i++) {
            this.add();
        }
    }

    /**
     * @param process
     * @throws TransitionZoneException
     */
    private void suspendToTransitionZone(Process process) throws TransitionZoneException {
        if (this.transitionZone == null) {
            this.transitionZone = process;
            log("Suspenso: Assim que outro processo for escolhido, " + process.getName() + " voltará para a lista.");
        } else {
            throw new TransitionZoneException(
                    "Já existe outro processo suspenso (" + this.transitionZone.getName() + ").");
        }
    }

    /**
     * @throws TransitionZoneException
     * 
     */
    private Process recoverFromTransitionZone() throws TransitionZoneException {
        if (this.transitionZone != null) {
            Process process = this.transitionZone;
            this.log("Recuperação: " + process.getName() + " já pode voltar para a lista.");

            this.add(process);
            this.transitionZone = null;

            return process;
        } else {
            throw new TransitionZoneException(
                    "Não tem nenhum processo suspenso.");
        }
    }

    /**
     * @return
     * @throws CPUIsBusyException
     */
    public Process getProcessToExecute() throws CPUIsBusyException {

        // Interrompe a ação, caso já tenha outro processo em execução
        if (this.isBusy()) {
            throw new CPUIsBusyException("Não foi possível executar o próximo processo. A CPU está ocupada. Aguarde.");
        }

        // Se a lista estiver vazia, verifica se há algum processo suspenso para
        // executar.
        if (this.queue.size() <= 0) {
            try {
                this.recoverFromTransitionZone();
            } catch (TransitionZoneException e) {
                // Do nothing
            }
        }

        try {
            // Põe o próximo processo em execução
            Process current;
            current = this.queue.remove();
            this.executing = new Executor(current, this.executionTime);

            // Verifica se existe um processo suspenso, pendente de ser readicionado à lista
            try {
                this.recoverFromTransitionZone();
            } catch (TransitionZoneException e) {
                // Do nothing
            }

            return current;
        } catch (QueueMovementException e) {

            if (this.queue.size() > 0) {
                this.log("Erro: Não foi possível pegar o próximo processo. " + e.getMessage());
            } else {
                this.log("A CPU está livre.");
            }

        }

        return null;
    }

    /**
     * 
     */
    private void doTasks() {
        try {
            TaskToGenerate possibleGenerator = this.tasks.getElement(1);
            while (possibleGenerator.getIdentifier() == this.executionTime) {
                this.generateProcesses(possibleGenerator.quantity);
                this.tasks.remove();
                possibleGenerator = this.tasks.getElement(1);
            }
        } catch (QueueIndexException e) {
            // Do nothing
        } catch (QueueMovementException e) {
            this.log("Houve um erro ao descartar a tarefa.");
        }
    }

    /**
     * 
     */
    private void doProcesses() {
        if (this.isBusy()) {
            // Continua a execução do processo em andamento
            this.executing.execute(CPU.secondsPerStep);
        } else {
            // Executa o próximo processo
            try {
                this.getProcessToExecute();
            } catch (CPUIsBusyException e) {
                this.log("Erro: " + e.getMessage());
            }
        }
    }

    /**
     * 
     */
    public void start() {
        // Observa se a CPU já foi iniciada.
        if (this.isRunning()) {
            this.log("Comando para iniciar negado. A CPU já está em execução.");
            return;
        }

        this.report();
        System.out.println();

        // Atualiza o tempo
        this.executionTime = 0;
        this.log("A CPU foi iniciada.");

        // Realiza as tarefas agendadas
        this.doTasks();

        // Mexe nos processos
        this.doProcesses();

        // Gera relatório
        this.report();
    }

    /**
     * @throws CPUIsBusyException
     * 
     */
    public void fowardStep() {
        // Inicia a CPU, se necessário
        if (!this.isRunning()) {
            this.start();
            this.report();
            return;
        }

        // Atualiza o tempo
        this.executionTime += CPU.secondsPerStep;

        // Realiza as tarefas agendadas
        this.doTasks();

        // Mexe nos processos
        this.doProcesses();

        // Gera relatório
        this.report();
    }

    /**
     * @param quantity
     */
    public void fowardSteps(int quantity) {
        for (int i = 0; i < quantity; i++) {
            this.fowardStep();
            System.out.println();
        }
    }

    /**
     * @param information
     */
    private void log(String information) {
        this.logger.push(information);
    }

    /**
     * 
     */
    public void report() {
        try {
            System.out.println("[" + this.getReadableExecutionTime() + "]");

        } catch (CPUNotRunningException e) {
            System.out.println("[Antes de a CPU iniciar]");
        }

        while (this.logger.size() > 0) {
            System.out.println("> " + this.logger.pop().trim());
        }
    }

    /**
     * @return
     * @throws CPUNotRunningException
     */
    public String getReadableExecutionTime() throws CPUNotRunningException {
        if (!this.isRunning()) {
            throw new CPUNotRunningException("A CPU não está em execução.");
        }

        return CPU.secondsToReadableTime(this.executionTime);
    }

    /**
     * @param total
     * @return
     */
    private static String secondsToReadableTime(int total) {
        int seconds = total % 60;
        int minutes = (total / 60) % 60;
        int hours = total / 3600;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);
    }

    /**
     * 
     */
    public void showQueueTree() {
        this.queue.show();
    }

    /**
     * 
     */
    public void showQueueHeap() {
        System.out.println(this.queue.toString());
    }

    /**
     * 
     */
    public void showHistory() {
        for (int i = 0; i < this.history.size(); i++) {
            System.out.println(this.history.search(i) + "\n");
        }
    }
}
