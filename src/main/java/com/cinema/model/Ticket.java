package com.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {

    private final String ticketNumber;
    private final FilmSession session;
    private final int rowNum;
    private final int seatNum;
    private final BigDecimal price;
    private TicketStatus status = TicketStatus.AVAILABLE;
    private String customerLogin;
    private LocalDateTime soldAt;

    public Ticket(String ticketNumber, FilmSession session, int rowNum, int seatNum, BigDecimal price) {
        this.ticketNumber = ticketNumber;
        this.session = session;
        this.rowNum = rowNum;
        this.seatNum = seatNum;
        this.price = price;
    }

    public synchronized boolean trySell(String login, LocalDateTime when) {
        if (status != TicketStatus.AVAILABLE) {
            return false;
        }
        customerLogin = login;
        soldAt = when;
        status = TicketStatus.SOLD;
        return true;
    }

    public String getTicketNumber() { return ticketNumber; }
    public FilmSession getSession() { return session; }
    public String getSessionCode() { return session.getSessionCode(); }
    public int getRowNum() { return rowNum; }
    public int getSeatNum() { return seatNum; }
    public BigDecimal getPrice() { return price; }
    public TicketStatus getStatus() { return status; }
    public String getCustomerLogin() { return customerLogin; }
    public LocalDateTime getSoldAt() { return soldAt; }
}