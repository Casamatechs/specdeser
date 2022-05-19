package kr.sanchez.specdeser.core.jakarta;

public class ContextStack<T> {

    private final T[] stackArray;
    private int array_pointer;

    public ContextStack() {
        this.stackArray = (T[]) new Object[128];
        this.array_pointer = -1;
    }

    T pop() {
        if (this.array_pointer >= 0) return this.stackArray[this.array_pointer--];
        return null;
    }

    void push(T elem) {
        if (this.array_pointer < this.stackArray.length-1) this.stackArray[++this.array_pointer] = elem;
    }

    T peek() {
        if (this.array_pointer >= 0) return this.stackArray[this.array_pointer];
        return null;
    }

    boolean isEmpty() {
        return this.array_pointer == -1;
    }
}
