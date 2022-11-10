package structures;

import java.util.List;

import contracts.QueueInterface;
import utils.exceptions.EmptyListException;

public class Queue<Type> implements QueueInterface<Type> {

    private SinglyLinkedList<Type> list = new SinglyLinkedList<Type>();

    @Override
    public void push(Type value) {
        this.list.addLast(value);
    }

    @Override
    public Type pop() throws EmptyListException {
        return this.list.removeFirst();
    }

    @Override
    public Type peek() {
        return this.list.peekFirst();
    }

    @Override
    public boolean isEmpty() {
        return (this.size() == 0);
    }

    public int size() {
        return this.list.size();
    }

    @Override
    public void show() {
        this.list.show();
    }
    
    public List<Type> toArray() {
        return this.list.toArray();
    }
}
