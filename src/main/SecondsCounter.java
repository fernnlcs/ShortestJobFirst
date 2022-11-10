package main;

import utils.exceptions.TimeCounterException;

public class SecondsCounter {
    // null significa que o contador ainda está em preparação
    private Integer seconds = null;

    /**
     * @param total
     * @return
     */
    public static String toReadableTime(int total) {
        int seconds = total % 60;
        int minutes = (total / 60) % 60;
        int hours = total / 3600;

        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);
    }

    public SecondsCounter() {
        // Permanece null
    }

    public SecondsCounter(Integer startAt) {
        // Pode ser null sim
        this.seconds = startAt;
    }

    private void assertThatIsRunning() throws TimeCounterException {
        if (!this.isRunning()) {
            throw new TimeCounterException("O contador de segundos ainda não começou.");
        }
    }

    private void assertThatIsNotRunning() throws TimeCounterException {
        if (this.isRunning()) {
            throw new TimeCounterException("O contador de segundos já começou.");
        }
    }

    public boolean isRunning() {
        return this.seconds != null;
    }

    public Integer get() {
        // Pode retornar null
        return this.seconds;
    }

    /**
     * @throws TimeCounterException
     */
    public void start() throws TimeCounterException {
        this.start(0);
    }

    /**
     * @param startAt
     * @throws TimeCounterException
     */
    public void start(Integer startAt) throws TimeCounterException {
        this.assertThatIsNotRunning();

        if (startAt == null) {
            startAt = 0;
        }

        this.seconds = startAt;
    }

    /**
     * @param seconds
     * @throws TimeCounterException
     */
    public void increment(int seconds) throws TimeCounterException {
        this.assertThatIsRunning();
        this.seconds += seconds;
    }

    public void decrement(int seconds) throws TimeCounterException {
        this.assertThatIsRunning();

        if (seconds > this.seconds) {
            throw new TimeCounterException(
                    "O contador de tempo só está com " + this.get() + "s. Impossível decrementar " + seconds + "s");
        } else {
            this.seconds -= seconds;
        }
    }

    public String toReadableTime() {
        try {
            assertThatIsRunning();
            return SecondsCounter.toReadableTime(this.seconds);
        } catch (TimeCounterException e) {
            return "Contador de tempo ainda não iniciado.";
        }
    }

    @Override
    public String toString() {
        return this.seconds + "s";
    }
}
