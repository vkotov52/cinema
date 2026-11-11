package com.cinema.repository;

import com.cinema.model.Ticket;
import com.cinema.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/** Репозиторий для работы с сущностью Ticket. */
public interface TicketRepository extends JpaRepository<Ticket, String> {

    /** Возвращает все билеты заданного сеанса. */
    List<Ticket> findBySession_SessionCode(String sessionCode);

    /** Возвращает билеты заданного сеанса с указанным статусом (например, только свободные). */
    List<Ticket> findBySession_SessionCodeAndStatus(String sessionCode, TicketStatus status);

    /** Возвращает билет по сеансу, ряду и месту, или empty, если такого места нет. */
    Optional<Ticket> findBySession_SessionCodeAndRowNumAndSeatNum(String sessionCode, int rowNum, int seatNum);

    /** Возвращает все билеты, купленные пользователем с заданным логином. */
    List<Ticket> findByCustomer_Login(String login);

    /** Проверяет, есть ли у пользователя с заданным логином хотя бы один купленный билет (для запрета удаления). */
    boolean existsByCustomer_Login(String login);

    /** Возвращает все билеты с заданным статусом (например, все свободные или все проданные). */
    List<Ticket> findByStatus(TicketStatus status);

    /** Возвращает количество билетов с заданным статусом. */
    long countByStatus(TicketStatus status);

    /** Вычисляет суммарную выручку сеанса по проданным билетам. Возвращает 0, если продаж не было. */
    @Query("""
            select coalesce(sum(t.price), 0)
            from Ticket t
            where t.session.sessionCode = :code
              and t.status = com.cinema.model.TicketStatus.SOLD
            """)
    BigDecimal revenueBySession(@Param("code") String code);

    /** Возвращает количество проданных билетов по каждому фильму, отсортированное по убыванию. */
    @Query("""
            select t.session.film.title, count(t)
            from Ticket t
            where t.status = com.cinema.model.TicketStatus.SOLD
            group by t.session.film.title
            order by count(t) desc
            """)
    List<Object[]> soldCountByFilm();
}