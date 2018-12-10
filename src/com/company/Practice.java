package com.company;

public class Practice extends Pair {

    private Group group;

    public Practice() {
        super();
        weight = 2.;
    }

    @Override
    public void addGroup(Group group) {
        this.group = group;
    }

    @Override
    public Object getIterableElement(int i) {
        return group.getNumber();
    }

    @Override
    public int getNumberOfIterableElements() {
        return 1;
    }

    @Override
    public int getNumberOfStudents() {
        return group.getNumberOfStudents();
    }

    public String write() {
        return super.write() + " ### Group - " + (group.getNumber() + 1);
    }

}
