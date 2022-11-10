package contracts;

import utils.exceptions.EmptyListException;
import utils.exceptions.ItemNotFoundException;

public interface ListInterface <Type> {
    int addFirst(Type value);
    int addLast(Type value);
    int addAfter(Type value, int id) throws ItemNotFoundException;

    Type peekFirst();
    Type peekLast();

    Type search(int id) throws EmptyListException, ItemNotFoundException;
    Type searchByIndex(int index) throws EmptyListException;
    int searchIndexByData(Object object) throws EmptyListException;

    Type removeFirst() throws EmptyListException;
    Type removeLast() throws EmptyListException;
    Type remove(int id) throws ItemNotFoundException, EmptyListException;

    void show();
}
