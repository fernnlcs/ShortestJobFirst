package contracts;

public interface QueueInterface<Type> {
    void push(Type value);
    Type pop();

    Type peek();

    boolean isEmpty();

    void show();
}
