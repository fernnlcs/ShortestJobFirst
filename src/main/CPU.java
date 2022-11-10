package main;

import contracts.Orderable;
import structures.PriorityQueue;
import structures.SinglyLinkedList;
import utils.exceptions.CPUIsBusyException;
import utils.exceptions.CPUNotRunningException;
import utils.exceptions.ItemNotFoundException;
import utils.exceptions.QueueIndexException;
import utils.exceptions.QueueMovementException;
import utils.exceptions.TimeCounterException;
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

            logger.log("Escolhido: " + this.process.toString() + ".");

            // Recupera o processo da zona de transição, se houver
            try {
                recoverFromTransitionZone();
            } catch (TransitionZoneException e) {
                // Do nothing
            }

            // Define o tempo restante como 3s ou o tempo do processo, se for menor
            this.remainingTime = Math.min(process.getRemainingTime(), CPU.secondsPerExecution);

            // Define o tempo de encerramento
            this.endTime = this.startTime + this.remainingTime;

            // Adiciona ao histórico
            history.addLast("[" + SecondsCounter.toReadableTime(this.startTime) + " - "
                    + SecondsCounter.toReadableTime(this.getEndTime()) + "]\n" + this.process.getName() + " ("
                    + this.process.getRemainingTime() + "s -> " + (this.process.getRemainingTime() - this.remainingTime)
                    + "s)");
            this.execute(secondsPerStep);
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
                    suspendToTransitionZone(process);
                } catch (TransitionZoneException e) {
                    logger.log("Erro: Não foi possível suspender " + this.process.toString() + ". " + e.getMessage());
                }
            } else {
                logger.log("Descarte: " + this.process.toString() + " foi encerrado corretamente.");
            }

            try {
                chooseProcessToExecute();
            } catch (CPUIsBusyException e) {
                logger.log("Erro: Não foi possível iniciar próximo processo automaticamente. " + e.getMessage());
            }
        }

        /**
         * @param seconds
         */
        public void execute(int seconds) {
            if (this.remainingTime > 0) {
                logger.log("Em execução: " + this.process.toString() + ". Interrompendo em "
                        + this.remainingTime + "s.");

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
    private Logger logger;
    private Executor executing = null;
    private Process transitionZone = null;
    private SecondsCounter executionTime;

    public static final int secondsPerStep = 1;
    public static final int secondsPerExecution = 3;

    public CPU(SecondsCounter timeCounter) {
        this.executionTime = timeCounter;
        this.logger = new Logger(timeCounter);
    }

    public CPU(Logger logger, SecondsCounter timeCounter) {
        this.logger = logger;
        this.executionTime = timeCounter;
    }

    /**
     * @return
     */
    public boolean isRunning() {
        return this.executionTime.isRunning();
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
            this.logger.log("Tarefa adicionada: " + task.toString());
        } catch (QueueMovementException e) {
            this.logger.log("Não foi possível adicionar a tarefa.");
        }
    }

    /**
     * @param name
     * @return
     */
    public Process add() {
        int remainingTime = CPU.generateRemainingTime();
        Process process = new Process(remainingTime);
        return this.add(process);
    }

    /**
     * @param process
     * @return
     */
    public Process add(Process process) {
        try {
            this.queue.insert(process);
        } catch (QueueMovementException e) {
            this.logger.log("Erro: " + e.getMessage());
        }

        this.logger.log("Processo adicionado à fila: " + process);
        return process;
    }

    /**
     * @param quantity
     */
    public void generateProcesses(int quantity) {
        this.logger.log(quantity + " processos foram gerados automaticamente.");
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
            logger.log("Suspenso: Assim que outro processo for escolhido, " + process.toString()
                    + " voltará para a lista.");
        } else {
            throw new TransitionZoneException(
                    "Já existe outro processo suspenso (" + this.transitionZone.toString() + ").");
        }
    }

    /**
     * @throws TransitionZoneException
     * 
     */
    private Process recoverFromTransitionZone() throws TransitionZoneException {
        if (this.transitionZone != null) {
            Process process = this.transitionZone;
            this.logger.log("Recuperação: " + process.toString() + " já pode voltar para a lista.");

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
    public Process getProcessToExecuteOld() throws CPUIsBusyException {

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
            this.executing = new Executor(current, this.executionTime.get());

            // Verifica se existe um processo suspenso, pendente de ser readicionado à lista
            try {
                this.recoverFromTransitionZone();
            } catch (TransitionZoneException e) {
                // Do nothing
            }

            return current;
        } catch (QueueMovementException e) {

            if (this.queue.size() > 0) {
                this.logger.log("Erro: Não foi possível pegar o próximo processo. " + e.getMessage());
            } else {
                this.logger.log("A CPU está livre.");
            }

        }

        return null;
    }

    /**
     * @return
     * @throws CPUIsBusyException
     */
    public Process chooseProcessToExecute() throws CPUIsBusyException {

        // Não prossegue caso já tenha outro processo em execução
        if (this.isBusy()) {
            throw new CPUIsBusyException("Não foi possível executar o próximo processo. A CPU está ocupada. Aguarde.");
        }

        // Se a lista estiver vazia, verifica se há algum processo suspenso para
        // executar.
        if (this.queue.size() == 0) {
            try {
                this.recoverFromTransitionZone();
            } catch (TransitionZoneException e) {
                this.logger.log("A CPU está livre.");
                return null;
            }
        }

        Process next;

        try {
            next = this.queue.remove();
        } catch (QueueMovementException e1) {
            this.logger.log("Erro: " + e1.getMessage());
            return null;
        }

        this.executing = new Executor(next, this.executionTime.get());
        return next;
    }

    /**
     * 
     */
    private void doTasks() {
        try {
            TaskToGenerate possibleGenerator = this.tasks.getElement(1);
            while (possibleGenerator.getIdentifier() == this.executionTime.get()) {
                this.generateProcesses(possibleGenerator.quantity);
                this.tasks.remove();
                possibleGenerator = this.tasks.getElement(1);
            }
        } catch (QueueIndexException e) {
            // Do nothing
        } catch (QueueMovementException e) {
            this.logger.log("Houve um erro ao descartar a tarefa.");
        }
    }

    /**
     * 
     */
    private void doProcesses() {
        try {
            // Executa o próximo processo
            this.chooseProcessToExecute();
        } catch (CPUIsBusyException e) {
            // Continua a execução do processo em andamento
            this.executing.execute(CPU.secondsPerStep);
        }
    }

    /**
     * 
     */
    public void start() {
        try {
            if (!this.isRunning()) {
                this.logger.report();
                System.out.println();
            }

            // Atualiza o tempo
            this.executionTime.start();
            this.logger.log("A CPU foi iniciada.");

            // Realiza as tarefas agendadas
            this.doTasks();

            // Mexe nos processos
            this.doProcesses();

            // Gera relatório
            this.logger.report();
        } catch (TimeCounterException e) {
            this.logger.log("Comando para iniciar negado. A CPU já está em execução.");
            this.logger.report();
        }
    }

    /**
     * @throws CPUIsBusyException
     * 
     */
    public void fowardStep() {
        // Inicia a CPU, se necessário
        if (!this.isRunning()) {
            this.start();
            this.logger.report();
            return;
        }

        // Atualiza o tempo
        try {
            this.executionTime.increment(CPU.secondsPerStep);
        } catch (TimeCounterException e) {
            // Do nothing
        }

        // Realiza as tarefas agendadas
        this.doTasks();

        // Mexe nos processos
        this.doProcesses();

        // Gera relatório
        this.logger.report();
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
     * @return
     * @throws CPUNotRunningException
     */
    public String getReadableExecutionTime() throws CPUNotRunningException {
        if (!this.isRunning()) {
            throw new CPUNotRunningException("A CPU não está em execução.");
        }

        return this.executionTime.toReadableTime();
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
            try {
                System.out.println(this.history.search(i) + "\n");
            } catch (ItemNotFoundException e) {
                // Do nothing
            }
        }
    }
}
