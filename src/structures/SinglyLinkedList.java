package structures;

import java.util.ArrayList;
import java.util.List;

import contracts.ListInterface;
import utils.exceptions.EmptyListException;
import utils.exceptions.ItemNotFoundException;
import utils.exceptions.StructureException;

public class SinglyLinkedList<Type extends Object> implements ListInterface<Type> {

    public Node head = null;
    public Node tail = null;
    public int nextId = 0;
    public int size = 0;

    /**
     * Node
     */
    public class Node {
    
        int id = nextId++;
        Type data;
        Node next = null;

        public Node(Type data) {
            this.data = data;
        }
        
    }
    
    public int size() {
        return this.size;
    }

    @Override
    public int addFirst(Type value) {
        Node oldHead = this.head;
        Node newHead = new Node(value);

        if (oldHead == null) {
            // Primeiro elemento de todos
            this.head = newHead;
            this.tail = newHead;
        } else {
            // Já tem outros
            newHead.next = oldHead;
            this.head = newHead;
        }

        // Incrementar tamanho
        this.size += 1;

        return newHead.id;
    }

    @Override
    public int addLast(Type value) {
        Node oldTail = this.tail;
        Node newTail = new Node(value);

        if (oldTail == null) {
            // Primeiro elemento de todos
            this.head = newTail;
            this.tail = newTail;
        } else {
            // Já tem outros
            oldTail.next = newTail;
            this.tail = newTail;
        }

        // Incrementar tamanho
        this.size += 1;

        return newTail.id;
    }

    @Override
    public int addAfter(Type value, int id) throws ItemNotFoundException {
        Node target = this.searchNode(id);

        if (target == null) {
            throw new StructureException("Erro: não encontrei o id " + id);
        }

        Node nextAnyway = target.next;
        Node newNode = new Node(value);

        target.next = newNode;
        newNode.next = nextAnyway;

        this.size += 1;

        return newNode.id;
    }

    @Override
    public Type peekFirst() {
        if (this.head == null) {
            return null;
        }

        return this.head.data;
    }

    @Override
    public Type peekLast() {
        if (this.tail == null) {
            return null;
        }
        
        return this.tail.data;
    }

    private Node searchNode(int id) throws ItemNotFoundException{
        Node current = this.head;

        while (current != null) {
            if (current.id == id) {
                return current;
            }
            current = current.next;
        }

        throw new ItemNotFoundException("O item não foi encontrado.");
    }

    public Type searchByIndex(int index) {
        Node current = this.head;

        for (int i = 0; i < index && i < this.size(); i++) {
            current = current.next;
        }

        return current.data;
    }

    public int searchIndexByData(Object object) {
        Node current = this.head;
        int index = 0;

        while (current != null) {
            if (current.data.equals(object)) {
                return index;
            }
            current = current.next;
            index++;
        }

        return -1;
    }

    @Override
    public Type search(int id) throws ItemNotFoundException {
        Node result = this.searchNode(id);

        if (result != null) {
            return result.data;
        }

        return null;
    }

    @Override
    public Type removeFirst() throws EmptyListException {
        Node oldHead = this.head;
        
        if (oldHead == null) {
            throw new EmptyListException("Lista vazia. Nada removido.");
        }

        Node newHead = oldHead.next;
        this.head = newHead;

        if (newHead == null) {
            // Se é o único elemento, a cauda também recebe o valor nulo
            this.tail = newHead;
        } else {
            // Isola o item removido
            oldHead.next = null;
        }

        this.size -= 1;

        return oldHead.data;
    }

    @Override
    public Type removeLast() throws EmptyListException {
        Node oldTail = this.tail;

        if (oldTail == null) {
            throw new EmptyListException("Lista vazia. Nada removido.");
        }

        if (oldTail == this.head) {
            // Se é o único elemento
            this.head = null;
            this.tail = null;

            return oldTail.data;
        }

        Node newTail = this.head;

        while (newTail.next.id != oldTail.id) {
            newTail = newTail.next;
        }

        this.tail = newTail;
        newTail.next = null;

        this.size -= 1;

        return oldTail.data;
    }

    private Node searchNodeBefore(int id) {
        Node before = null;
        Node current = this.head;

        while (current != null) {
            if (current.id == id) {
                return before;
            }
            before = current;
            current = current.next;
        }

        return null;
    }

    @Override
    public Type remove(int id) throws ItemNotFoundException, EmptyListException{
        Node removable = this.searchNode(id);

        if (removable.id == this.head.id) {
            return this.removeFirst();
        }

        if (removable.id == this.tail.id) {
            return this.removeLast();
        }

        Node previous = this.searchNodeBefore(id);

        // Isolando item
        previous.next = removable.next;

        this.size -= 1;

        return removable.data;
    }

    @Override
    public void show() {
        Node current = this.head;

        if (current == null) {
            System.out.println("Lista vazia.\n");
        } else {
            System.out.println(this.size + " itens\n");
            do {
                System.out.println("[#" + current.id + "]");
                System.out.println(current.data.toString());
                System.out.println("");

                current = current.next;
            } while (current != null);
        }
    }
    
    public List<Type> toArray() {
        List<Type> result = new ArrayList<Type>();

        for (Node current = this.head; current != null; current = current.next) {
            result.add(current.data);
        }

        return result;
    }
}
