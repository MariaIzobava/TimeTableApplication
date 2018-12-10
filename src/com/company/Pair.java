package com.company;

import org.apache.poi.ss.formula.functions.T;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class Pair implements Cloneable, GenericIterable {

    private static final int TIMES = 5;
    private static final int DAYS = 6;

    private int course;
    private String subject;
    private String teacher;
    private int pairNumber;

    // 1 = wish
    // 0 = natural
    // -1 = don't wish
    private Integer[][] preference = new Integer[TIMES][DAYS];
    private static Double[][] dayWeight = new Double[TIMES][DAYS];
    private static Double[][] dayOnDemandIndex = new Double[TIMES][DAYS];
    private static Double[][] notDayOnDemandIndex = new Double[TIMES][DAYS];
//    private static Double

    protected Double weight;

    public static void initialize() {
        try {
            String csvFile1 = "wishList.csv";
            String csvFile2 = "notWishList.csv";
            BufferedReader br1 = new BufferedReader(new FileReader(csvFile1));
            BufferedReader br2 = new BufferedReader(new FileReader(csvFile2));
            String line1, line2;
            for (int i = 0; i < TIMES; i++) {
                line1 = br1.readLine();
                line2 = br2.readLine();
                String[] data1 = line1.split(" ");
                String[] data2 = line2.split(" ");
                for (int j = 0; j < DAYS; j++) {
                    dayOnDemandIndex[i][j] = Double.parseDouble(data1[j]);
                    notDayOnDemandIndex[i][j] = Double.parseDouble(data2[j]);
                }
            }

            String csvFile = "dayWeight.csv";
            BufferedReader br3 = new BufferedReader(new FileReader(csvFile));
            String line;

            for (int i = 0; i < TIMES; i++) {
                line = br3.readLine();
                String[] data = line.split(" ");
                for (int j = 0; j < DAYS; j++)
                    dayWeight[i][j] = Double.parseDouble(data[j]);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Pair() {
        for (int i = 0; i < TIMES; i++)
            for (int j=0; j < DAYS; j++) {
                preference[i][j] = 0;
        }

    }

    public abstract void addGroup(Group group);

    public abstract int getNumberOfIterableElements();
    public abstract int getNumberOfStudents();
    public abstract Object getIterableElement(int i);

    public Pair clone() throws CloneNotSupportedException  {

        return (Pair) super.clone();
    }

    public String write() {
        return pairNumber + "   Course - " + (course + 1) + " ### Subject - " + subject + " ### Teacher - " + teacher;
    }

    public void setCourse(int course) {
        this.course = course;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setPairNumber(int pairNumber) {
        this.pairNumber = pairNumber;
    }

    public int getPairNumber() {
        return pairNumber;
    }

    public void setPreference(int time, int day, int val) {
        recalculateWeight(time, day, val);
        preference[time][day] = val;
    }

    public int getCourse() {
        return course;
    }

    public String getSubject() {
        return subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getOccupationQuality(int time, int day) {
        return  dayWeight[time][day] + ((preference[time][day] == 1) ? dayOnDemandIndex[time][day] :
                (preference[time][day] == -1) ? -notDayOnDemandIndex[time][day] : 0);
    }

    private void recalculateWeight(int time, int day, int val) {
        weight -= (preference[time][day] == 1) ? dayOnDemandIndex[time][day] :
                (preference[time][day] == -1) ? notDayOnDemandIndex[time][day] : 0;
        weight += (val == 1) ? dayOnDemandIndex[time][day] :
                (val == -1) ? notDayOnDemandIndex[time][day] : 0;
    }

    public Boolean suits(int roomNumber) {
        return Main.rooms.get(roomNumber).getSecond() >= getNumberOfStudents();
    }

}
