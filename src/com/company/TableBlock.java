package com.company;

import java.util.ArrayList;

class TableBlock implements GenericIterable {

    private GenericTuple<Pair, Pair>[] groups;
    private ArrayList<GenericTuple<Pair, String> > pairs = new ArrayList<>();

    public TableBlock(int k) {
        groups = new GenericTuple[k];
    }

    private boolean doesPairFitsForGroup(Pair pair, int group) {
        return (groups[group] == null)
                || (groups[group].getSecond() == null && (pair instanceof Lab) && ((Lab) pair).getSubGroup().equals(Main.GroupPart.SECOND))
                || (groups[group].getFirst() == null && (pair instanceof Lab) && ((Lab) pair).getSubGroup().equals(Main.GroupPart.FIRST));
    }

    public boolean fits(Pair pair) {
        GenericIterator<Pair> it = new GenericIterator(pair);
        while (it.hasNext()) {
            int group = (int)it.next();
            if (!doesPairFitsForGroup(pair, group)) return false;
        }
        return true;
    }

    public int getNumberOfIterableElements() {
        return pairs.size();
    }

    public Object getIterableElement(int i) {
        return pairs.get(i);
    }

    public boolean groupHasLecture(int group) {
        return (groups[group] != null && groups[group].getFirst() != null && groups[group].getFirst() instanceof Lecture);
    }

    private void addPair(Pair pair, int group) {
        if (!(pair instanceof Lab)) {
            groups[group] = new GenericTuple<>(pair, pair);
        }
        else  {
            if (groups[group] == null)
                groups[group] = new GenericTuple<>(null, null);
            if (((Lab) pair).getSubGroup().equals(Main.GroupPart.SECOND))
                groups[group].setSecond(pair);
            else
                groups[group].setFirst(pair);
        }
    }

    public boolean hasPair(Pair pair) {
        for (GenericTuple<Pair, String> cur : pairs) {
            if (cur.getFirst().getPairNumber() == pair.getPairNumber())
                return true;
        }
        return false;
    }

    public void add(Pair pair, String roomNumber) {
        GenericIterator<Pair> it = new GenericIterator(pair);
        pairs.add(new GenericTuple<>(pair, roomNumber));
        while (it.hasNext()) {
            int group = (int)it.next();
            addPair(pair, group);
        }
    }

    boolean isPairShiftToRight(Pair pair, int group) {
        return (groups[group].getFirst() == null || !groups[group].getFirst().equals(pair));
    }

}


