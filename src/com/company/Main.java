package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class GenericTuple<T1, T2> {

    T1 key;
    T2 val;

    public GenericTuple(T1 key, T2 val) {
        this.key = key;
        this.val = val;
    }

    public T1 getFirst() {
        return key;
    }

    public T2 getSecond() {
        return val;
    }

    public void setFirst(T1 key) {
        this.key = key;
    }

    public void setSecond(T2 val) {
        this.val = val;
    }
}


public class Main {


    public static void fillGroups() {
        String csvFile = "groups.csv";
        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            for (int i = 0 ; i < COURSES; i++) {
                for (int j = 0; j < GROUPS; j++) {
                    line = br.readLine();
                    String[] data = line.split(";");
                    groups.add(new Group(i, Integer.parseInt(data[0]) - 1,  Integer.parseInt(data[1])));
                }
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void fillRooms() {
        String csvFile = "rooms.csv";
        BufferedReader br = null;
        String line = "";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                rooms.add(new GenericTuple(data[0], Integer.parseInt(data[1])));
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void sortPairs() {
        pairs.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return -o1.getWeight().compareTo(o2.getWeight());
            }
        });


    }

    // no repetitions for each pair per day
    // no more than 2 lectures per day
    private static boolean pairFitsDayRequirements(int course, int day, Pair pair) {
        int numberOfLectures = 0;
        for (int i = 0; i < TIMES; i++) {
            if (timetable[course][day][i].hasPair(pair))
                return false;
            GenericIterator<Pair> it = new GenericIterator(pair);
            while (it.hasNext()) {
                int group = (int)it.next();
                if (timetable[course][day][i].groupHasLecture(group)) {
                    numberOfLectures++;
                    break;
                }
            }
        }

        return !(pair instanceof Lecture && numberOfLectures > 1);
    }

    private static boolean pairFitsDay(int course, int day, int time, int teacher_id, Pair pair) {
        return (timetable[course][day][time].fits(pair) &&
                pairFitsDayRequirements(course, day, pair) &&
                teaching[teacher_id][day][time] == null);
    }

    private static int findBestRoomForPair(int day, int time, Pair pair, int roomNumber) {
        for (int room = 0; room < rooms.size(); room++) {
            if (roomsOccupied[room][day][time] == null  && pair.suits(room))
            {
                roomNumber = room;
            }
        }
        return roomNumber;
    }

    private static void brootForse() {
        for (Pair pair : pairs) {

            int course = pair.getCourse();
            int teacher_id = teachers.size() - 1 - teachers.headSet(pair.getTeacher()).size();
            // result day & time
            GenericTuple<Integer, Integer> resultTimeMoment = new GenericTuple<>(-1, -1);
            int resultRoom = -1;
            Double resultOccupationQuality = -1.;

            for (int j = 0; j < DAYS; j++) {
                int startingPair = (course > 1) ? 3 : 0;
                int finishingPair = (course > 1) ? TIMES : 5;

                for (int k = startingPair; k < finishingPair; k++) {
                    int roomNumberFound = -1;
                    if (pairFitsDay(course, j, k, teacher_id, pair)) {
                        roomNumberFound = findBestRoomForPair(j, k, pair, roomNumberFound);
                        if (roomNumberFound != -1) {
                            Double occupationQuality = pair.getOccupationQuality(k - startingPair, j);
                            if (occupationQuality.compareTo(resultOccupationQuality) > 0) {
                                resultOccupationQuality = occupationQuality;
                                resultRoom = roomNumberFound;
                                resultTimeMoment.setFirst(j);
                                resultTimeMoment.setSecond(k);
                            }
                        }
                    }
                }
            }

            if (resultRoom != -1) {
                timetable[course][resultTimeMoment.getFirst()][resultTimeMoment.getSecond()].add(pair, rooms.get(resultRoom).getFirst());
                teaching[teacher_id][resultTimeMoment.getFirst()][resultTimeMoment.getSecond()] = pair;
                roomsOccupied[resultRoom][resultTimeMoment.getFirst()][resultTimeMoment.getSecond()] = pair;
            } else {
                System.out.println("No place for pair " + pair.write() + ". Please, set it manually");
            }

        }
    }

    private static void initialize() {
        for (int i = 0; i < COURSES; i++) {
            for (int j = 0; j < DAYS; j++) {
                for (int k = 0; k < TIMES; k++) {
                    timetable[i][j][k] = new TableBlock(GROUPS);
                }
            }
        }
    }

    private static void createTable() {
        for (int i = 0; i < COURSES; i++) {
            for (int j = 0; j < DAYS; j++) {
                for (int k = 0; k < TIMES; k++) {
                    TimetableGenerator.fillRowWithPairs(timetable[i][j][k], j, k);
                }
            }
        }
    }

    public static void printPairs() {

        for (Pair pair : pairs) {
            System.out.println(pair.write());
        }
    }


    public static void main(String[] args) {
        // initialization block
        Pair.initialize();
        initialize();
        fillGroups();
        TimetableReader.readPairs();
        TimetableGenerator.initialize();
        fillRooms();
        // sorting pairs according their weights
        sortPairs();
        // greedy algorithm
        brootForse();
        // filling timetable
        createTable();
        TimetableGenerator.sink();

    }

    static final int COURSES = 4;
    static final int GROUPS = 13;
    static final int DAYS = 6;
    static final int TIMES = 8;
    static final int TEACHERS = 400;
    static final int ROOMS = 29;

    public static int TOTAL_NUMBER_OF_PAIRS = 0;

    enum GroupPart {
        FIRST,
        SECOND
    }

    public static ArrayList<Pair> pairs = new ArrayList<>();
    public static ArrayList<Group> groups = new ArrayList<>();
    private static TableBlock[][][] timetable = new TableBlock[COURSES][DAYS][TIMES];
    private static Pair[][][] teaching = new Pair[TEACHERS][DAYS][TIMES];
    private static Pair[][][] roomsOccupied = new Pair[ROOMS][DAYS][TIMES];
    public static ArrayList<GenericTuple<String, Integer> > rooms = new ArrayList<>();
    public static TreeSet<String> subjects = new TreeSet<>();
    public static TreeSet<String> teachers = new TreeSet<>();
}
