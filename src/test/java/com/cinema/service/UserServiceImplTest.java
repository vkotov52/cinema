package com.cinema.service;

import com.cinema.model.CustomerUser;
import com.cinema.model.ManagerUser;
import com.cinema.model.User;
import com.cinema.model.UserRole;
import com.cinema.repository.TicketRepository;
import com.cinema.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserServiceImpl.class)
class UserServiceImplTest {

    @MockitoBean
    private UserRepository users;

    @MockitoBean
    private TicketRepository tickets;

    @Autowired
    private UserService service;

    /** Проверяет, что при регистрации создаётся конкретный подкласс пользователя, соответствующий переданной роли. */
    @Test
    void registerCreatesSubclassMatchingRole() {
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.register(UserRole.MANAGER, "m1", "pw", "Петров", "Пётр", "p@p.ru");

        assertInstanceOf(ManagerUser.class, user);
        assertEquals(UserRole.MANAGER, user.getRole());
    }

    /** Проверяет отказ в регистрации пользователя с уже существующим логином. */
    @Test
    void registerRejectsDuplicateLogin() {
        when(users.existsById("m1")).thenReturn(true);

        assertThrows(
                IllegalStateException.class,
                () -> service.register(UserRole.MANAGER, "m1", "pw", "Петров", "Пётр", "p@p.ru")
        );
        verify(users, never()).save(any());
    }

    /** Проверяет валидацию входных данных: отсутствие роли при регистрации должно приводить к ошибке. */
    @Test
    void registerRejectsMissingRole() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.register(null, "m1", "pw", "Петров", "Пётр", "p@p.ru")
        );
    }

    /** Проверяет, что обновление профиля изменяет персональные данные, но сохраняет исходную роль пользователя. */
    @Test
    void updateChangesProfileButKeepsRole() {
        User user = new CustomerUser("m1", "pw", "Иванов", "Иван", "i@i.ru");
        when(users.findById("m1")).thenReturn(Optional.of(user));
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = service.update("m1", "pw1", "Сидоров", "Иван", "s@i.ru");

        assertEquals("Сидоров", result.getLastName());
        assertEquals("s@i.ru", result.getEmail());
        assertEquals(UserRole.CUSTOMER, result.getRole());
    }

    /** Проверяет запрет удаления пользователя, у которого есть купленные билеты. */
    @Test
    void deleteRejectsUserWithTickets() {
        User user = new CustomerUser("u", "p", "Иванов", "Иван", "i@i.ru");
        when(users.findById("u")).thenReturn(Optional.of(user));
        when(tickets.existsByCustomer_Login("u")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> service.delete("u"));
        verify(users, never()).delete(any());
    }
}