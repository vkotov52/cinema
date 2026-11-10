package com.cinema.model;

public class Hall {

    private final String hallCode;
    private final String name;
    private final int rowsCount;
    private final int seatsPerRow;

    public Hall(String hallCode, String name, int rowsCount, int seatsPerRow) {
        if (rowsCount <= 0 || seatsPerRow <= 0) {
            throw new IllegalArgumentException("Число рядов и мест должно быть больше нуля");
        }
        this.hallCode = hallCode;
        this.name = name;
        this.rowsCount = rowsCount;
        this.seatsPerRow = seatsPerRow;
    }

    public String getHallCode() {
        return hallCode;
    }

    public String getName() {
        return name;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public int capacity() {
        return rowsCount * seatsPerRow;
    }
}
