package structures;

import java.util.ArrayList;
import java.util.List;

import contracts.Orderable;
import utils.MovementType;
import utils.exceptions.QueueIndexException;
import utils.exceptions.QueueMovementException;

public class PriorityQueue<TypeQ extends Orderable> {

    private List<TypeQ> heap = new ArrayList<>();

    private boolean descending = false;

    /**
     * 
     */
    public PriorityQueue() {
    }

    /**
     * @param descending
     */
    public PriorityQueue(boolean descending) {
        this.descending = descending;
    }

    /**
     * @param heap
     * @throws QueueMovementException
     */
    public PriorityQueue(List<TypeQ> heap) throws QueueMovementException {
        this.setHeap(heap);
    }

    /**
     * @param heap
     * @param descending
     * @throws QueueMovementException
     */
    public PriorityQueue(List<TypeQ> heap, boolean descending) throws QueueMovementException {
        this.descending = descending;
        this.setHeap(heap);
    }

    /**
     * @param heap
     * @throws QueueMovementException
     */
    public void setHeap(List<TypeQ> heap) throws QueueMovementException {
        this.heap = heap;
        this.sort();
    }

    /**
     * @return
     */
    public int size() {
        return this.heap.size();
    }

    /**
     * @param bigger
     * @param smaller
     * @return
     */
    private boolean isBiggerThan(TypeQ bigger, TypeQ smaller) {
        return bigger.getIdentifier() > smaller.getIdentifier();
    }

    private boolean isSmallerThan(TypeQ bigger, TypeQ smaller) {
        return bigger.getIdentifier() < smaller.getIdentifier();
    }

    private boolean compare(TypeQ bigger, TypeQ smaller) {
        if (this.descending) {
            return this.isSmallerThan(bigger, smaller);
        } else {
            return this.isBiggerThan(bigger, smaller);
        }
    }

    /**
     * @param order
     * @return
     */
    public boolean elementExists(int order) {
        order -= 1;
        return (order < this.heap.size()) && order >= 0;
    }

    /**
     * @param order
     * @throws QueueIndexException
     */
    public void assertThatElementExists(int order) throws QueueIndexException {
        if (!this.elementExists(order)) {
            throw new QueueIndexException("Não existe um " + order + "º elemento nessa lista.");
        }
    }

    /**
     * @param order
     * @return
     * @throws QueueIndexException
     */
    public TypeQ getElement(int order) throws QueueIndexException {
        this.assertThatElementExists(order);

        int index = order - 1;
        return this.heap.get(index);
    }

    /**
     * @param order
     * @param element
     * @throws QueueIndexException
     */
    public void setElement(int order, TypeQ element) throws QueueIndexException {
        this.assertThatElementExists(order);

        int index = order - 1;
        this.heap.set(index, element);
    }

    /**
     * @param order
     * @return
     */
    public int elementHeight(int order) throws QueueIndexException {
        if (order <= 0) {
            throw new QueueIndexException("A ordem do elemento precisa ser maior que zero.");
        }
        return (int) Math.floor(Math.log((double) order) / Math.log(2));
    }

    /**
     * @param item
     * @throws QueueMovementException
     */
    public void insert(TypeQ item) throws QueueMovementException {
        this.heap.add(item);
        try {
            this.moveUp(this.heap.size());
        } catch (QueueIndexException e) {
            throw new QueueMovementException("Ocorreu um erro ao inserir o item.");
        }
    }

    /**
     * @return
     * @throws QueueMovementException
     */
    public TypeQ remove() throws QueueMovementException {
        if (this.heap.size() > 0) {
            try {
                TypeQ toRemove = this.heap.get(0);

                this.setElement(1, this.heap.get(this.heap.size() - 1));
                this.heap.remove(this.heap.size() - 1);

                if (this.heap.size() > 0) {
                    this.moveDown(1);
                }

                return toRemove;
            } catch (QueueIndexException e) {
                throw new QueueMovementException("Não foi possível remover o item. Talvez a lista já esteja vazia.");
            }
        } else {
            throw new QueueMovementException("A lista já está vazia.");
        }
    }

    /**
     * @param order
     * @throws QueueIndexException
     */
    public void moveUp(int order) throws QueueIndexException {
        this.assertThatElementExists(order);

        TypeQ current = this.getElement(order);
        int parentOrder = order / 2;

        if (this.elementExists(parentOrder)) {
            TypeQ parent = this.getElement(parentOrder);

            if (this.compare(current, parent)) {
                this.setElement(order, parent);
                this.setElement(parentOrder, current);

                this.moveUp(parentOrder);
            }
        }
    }

    /**
     * @param order
     * @throws QueueIndexException
     */
    public void moveDown(int order) throws QueueIndexException {
        this.assertThatElementExists(order);

        TypeQ current = this.getElement(order);

        int firstChildOrder = order * 2;
        int secondChildOrder = order * 2 + 1;

        try {
            this.assertThatElementExists(firstChildOrder);

            TypeQ firstChild = this.getElement(firstChildOrder);
            TypeQ childToSwitch = firstChild;
            int childToSwitchOrder = firstChildOrder;

            if (this.elementExists(secondChildOrder)) {
                TypeQ secondChild = this.getElement(secondChildOrder);

                if (this.compare(secondChild, firstChild)) {
                    childToSwitch = secondChild;
                    childToSwitchOrder = secondChildOrder;
                }
            }

            if (this.compare(childToSwitch, current)) {
                this.setElement(order, childToSwitch);
                this.setElement(childToSwitchOrder, current);

                this.moveDown(childToSwitchOrder);
            }
        } catch (QueueIndexException e) {
            return;
        }
    }

    /**
     * @throws QueueMovementException
     */
    public void sort() throws QueueMovementException {
        try {
            for (int i = this.heap.size() / 2; i > 0; i--) {
                this.moveDown(i);
            }
        } catch (QueueIndexException e) {
            throw new QueueMovementException("Ocorreu um erro ao ordenar a lista.", MovementType.SORT);
        }
    }

    /**
     * 
     */
    public void show() {
        System.out.println(this.toTree());
    }

    /**
     * @param num
     * @return
     */
    private String spaces(int num) {
        String result = "";

        for (int i = 0; i < num; i++) {
            result += " ";
        }

        return result;
    }

    /**
     * @param num
     * @return
     */
    private int coef(int num) {
        if (num < 0) {
            return 0;
        }
        return (this.coef(num - 1) * 2 + 1);
    }

    /**
     * @return
     */
    public String toTree() {
        String result = "";

        try {
            final int blockSize = 5;
            final int maxHeight = this.elementHeight(this.heap.size());
            int height = this.elementHeight(this.heap.size());
            for (int i = this.heap.size(); i > 0; i--) {
                int newHeight = this.elementHeight(i);
                int order = maxHeight - newHeight;
                TypeQ element = this.getElement(i);

                if (newHeight != height) {
                    height = newHeight;
                    result = this.spaces(this.coef(order - 2) * blockSize) + result;
                    result = "\n" + result;
                }
                result = "[" + String.format("%02d", element.getIdentifier()) + "s]" + this.spaces(this.coef(order) * blockSize) + result;
            }
            result = this.spaces(this.coef(maxHeight - 1) * blockSize) + result;
        } catch (QueueIndexException exception) {
            return "[Lista vazia]";
        }

        return result;
    }

    @Override
    public String toString() {
        String result = "";

        for (TypeQ type : heap) {
            result += type.toString();
            result += "\n";
        }

        return result;
    }
}
