package com.cinema.service;

import com.cinema.model.AdminUser;
import com.cinema.model.CustomerUser;
import com.cinema.model.ManagerUser;
import com.cinema.model.Ticket;
import com.cinema.model.User;
import com.cinema.model.UserRole;
import com.cinema.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final EntityRepository<User, String> users;
    private final EntityRepository<Ticket, String> tickets;

    public UserServiceImpl(EntityRepository<User, String> users, EntityRepository<Ticket, String> tickets) {
        this.users = users;
        this.tickets = tickets;
    }

    @Override
    public synchronized User register(UserRole role, String login, String password, String lastName,
                                      String firstName, String email) {
        if (users.findByKey(login).isPresent()) {
            throw new IllegalStateException("Пользователь уже существует: " + login);
        }
        User user = switch (role) {
            case ADMIN -> new AdminUser(login, password, lastName, firstName, email);
            case MANAGER -> new ManagerUser(login, password, lastName, firstName, email);
            case CUSTOMER -> new CustomerUser(login, password, lastName, firstName, email);
        };
        return users.save(user);
    }

    @Override
    public List<User> findAll() {
        return users.findAll();
    }

    @Override
    public List<User> findByRole(UserRole role) {
        List<User> result = new ArrayList<>();
        for (User user : users.findAll()) {
            if (user.getRole() == role) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public synchronized void delete(String login) {
        if (users.findByKey(login).isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден: " + login);
        }
        for (Ticket ticket : tickets.findAll()) {
            if (login.equals(ticket.getCustomerLogin())) {
                throw new IllegalStateException("Нельзя удалить пользователя " + login + ": у него есть билеты");
            }
        }
        users.deleteByKey(login);
    }
}
