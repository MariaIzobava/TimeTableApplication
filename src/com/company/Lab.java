package com.company;

public class Lab extends Pair {
    private Group group;
    private Main.GroupPart subGroup = Main.GroupPart.FIRST;

    public Lab() {
        super();
        weight = 1.;
    }

    public void setSubGroup(Main.GroupPart subGroup) {
        this.subGroup = subGroup;
    }

    public Main.GroupPart getSubGroup() {
        return subGroup;
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
        return group.getNumberOfStudents() / 2;
    }

    public String write() {
        return super.write() + " ### Group - " + (group.getNumber() + 1) + " " + subGroup + " part";
    }

}
