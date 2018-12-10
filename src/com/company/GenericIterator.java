package com.company;

public class GenericIterator<T extends GenericIterable> {

    private T block;
    private int k;
    public GenericIterator(T block) {
        this.block = block;
        k = block.getNumberOfIterableElements();
    }

    public boolean hasNext() {
        return k > 0;
    }

    public Object next() {
   
        if (k == 0) {
            System.out.println("NO MORE ELEMENTS!");
            return null;
        }
        k--;
        return block.getIterableElement(k);
        
    }

}
