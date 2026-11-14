package com.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** JPA-сущность билета на сеанс. Таблица: ticket. Уникальное ограничение: один и тот же ряд и место не могут повторяться в рамках одного сеанса. */
@Entity
@Table(
        name = "ticket",
        uniqueConstraints = @UniqueConstraint(columnNames = {"session_code", "row_num", "seat_num"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    /** Первичный ключ — уникальный номер билета (например, TKT-000001). */
    @Id
    @Column(name = "ticket_number", length = 12)
    @NotBlank
    private String ticketNumber;

    /** Сеанс, на который выдан билет. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "session_code", nullable = false)
    private FilmSession session;

    /** Номер ряда, начиная с 1. */
    @Column(name = "row_num", nullable = false)
    @Min(1)
    private int rowNum;

    /** Номер места в ряду, начиная с 1. */
    @Column(name = "seat_num", nullable = false)
    @Min(1)
    private int seatNum;

    /** Статус билета (AVAILABLE, RESERVED, SOLD). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TicketStatus status = TicketStatus.AVAILABLE;

    /** Цена билета. */
    @Setter
    @Column(nullable = false, precision = 8, scale = 2)
    @DecimalMin("0.00")
    private BigDecimal price;

    /** Покупатель билета. Null, пока билет не продан. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_login")
    private User customer;

    /** Дата и время продажи билета. Null, пока билет не продан. */
    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    /** Конструктор для создания нового билета на заданное место сеанса. */
    public Ticket(String ticketNumber, FilmSession session, int rowNum, int seatNum, BigDecimal price) {
        this.ticketNumber = ticketNumber;
        this.session = session;
        this.rowNum = rowNum;
        this.seatNum = seatNum;
        this.price = price;
    }

    /** Пытается продать билет: если статус AVAILABLE, устанавливает покупателя, время и меняет статус на SOLD. Возвращает true при успехе. */
    public boolean trySell(LocalDateTime when, User user) {
        if (status != TicketStatus.AVAILABLE) {
            return false;
        }
        customer = user;
        soldAt = when;
        status = TicketStatus.SOLD;
        return true;
    }

    /** Возвращает код сеанса билета. */
    public String getSessionCode() {
        return session == null ? null : session.getSessionCode();
    }

    /** Возвращает логин покупателя. */
    public String getCustomerLogin() {
        return customer == null ? null : customer.getLogin();
    }
}