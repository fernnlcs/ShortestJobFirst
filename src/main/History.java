package main;

import structures.SinglyLinkedList;
import utils.exceptions.ItemNotFoundException;

public class History {

    /**
     * Event
     */
    public class Event {

        private int startTime;
        private int duration;
        private String description;

        public Event(int duration, String description) {
            this.startTime = counter.get();
            this.duration = duration;
            this.description = description;
        }

        private int calculateEndTime() {
            return startTime + duration;
        }

        @Override
        public String toString() {
            String result = "";
            result += "[";
            result += SecondsCounter.toReadableTime(this.startTime);
            result += " - ";
            result += SecondsCounter.toReadableTime(this.calculateEndTime());
            result += "]\n" ;
            result += this.description;

            return result;
        }
    }

    SinglyLinkedList<Event> events = new SinglyLinkedList<>();
    SecondsCounter counter;

    public History(SecondsCounter counter) {
        this.counter = counter;
    }

    public void add(int duration, String description) {
        Event event = new Event(duration, description);
        events.addLast(event);
    }

    public void show() {
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        String result = "HISTÓRICO\n\n";

        for (int i = 0; i < this.events.size(); i++) {
            try {
                result += this.events.search(i) + "\n\n";
            } catch (ItemNotFoundException e) {
                // Do nothing
            }
        }

        return result;
    }
}
