package contracts;

import utils.exceptions.EmptyListException;

public interface QueueInterface<Type> {
    void push(Type value);
    Type pop() throws EmptyListException;

    Type peek();

    boolean isEmpty();

    void show();
}
