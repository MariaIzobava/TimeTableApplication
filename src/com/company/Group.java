package com.company;

public class Group {

    private int course;
    private int number;
    private int numberOfStudents;

    public Group(int course, int number, int numberOfStudents) {
        this.course = course;
        this.number = number;
        this.numberOfStudents = numberOfStudents;
    }

    public int getCourse() {
        return course;
    }

    public int getNumber() {
        return number;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }
}
