package com.company;

public class ArrayIterator {
    class ArrayException extends Exception {}
    private String[] data;
    private int it = 0;
    private int l;
    public ArrayIterator(String[] data) {
        this.data = data;
        l = data.length;
    }

    public boolean hasNext() {
        return it < l;
    }

    public String next() {
        try {
            if (it == l) throw new ArrayException();
            it++;
            return data[it-1];
        }
        catch (ArrayException e) {
            System.out.println("No more Elements in array!");
            return "";
        }
    }

}