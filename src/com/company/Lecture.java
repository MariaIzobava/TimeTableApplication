package com.company;

import java.util.ArrayList;

public class Lecture extends Pair {

    private ArrayList<Group> groups = new ArrayList<>();

    public Lecture() {
        super();
        weight = 3.;
    }

    @Override
    public void addGroup(Group group) {
        groups.add(group);
    }

    @Override
    public Object getIterableElement(int i) {
        return groups.get(i).getNumber();
    }

    @Override
    public int getNumberOfIterableElements() {
        return groups.size();
    }

    @Override
    public int getNumberOfStudents() {
        int numberOfStudents = 0;
        for (Group group : groups)
            numberOfStudents += group.getNumberOfStudents();
        return numberOfStudents;
    }

    private String writeGroups() {
        StringBuffer s = new StringBuffer();
        s.append("[");
        for (Group group : groups) {
            s.append(group.getNumber() + 1);
            s.append(" ");
        }
        s.append("]");
        return s.toString();
    }

    public String write() {
        return super.write() + " ### Group - " + writeGroups();
    }

}
