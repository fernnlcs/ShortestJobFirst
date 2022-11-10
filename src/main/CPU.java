package main;

import structures.PriorityQueue;
import structures.SinglyLinkedList;
import utils.exceptions.CPUIsBusyException;
import utils.exceptions.ItemNotFoundException;
import utils.exceptions.QueueMovementException;
import utils.exceptions.TransitionZoneException;

public class CPU {

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
    public boolean isBusy() {
        return (this.executing != null);
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

        this.logger.log("Processo adicionado à lista: " + process);
        return process;
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
            this.logger.log("Liberado: " + process.toString() + " já pode voltar para a lista.");

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
    public void doProcesses() {
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
