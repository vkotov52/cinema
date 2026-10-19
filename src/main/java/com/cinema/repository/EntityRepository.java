package com.cinema.repository;

import java.util.List;
import java.util.Optional;

/** Базовый интерфейс для работы с хранилищем данных. */
public interface EntityRepository<T, K> {

    T save(T entity);

    Optional<T> findByKey(K key);

    List<T> findAll();

    boolean deleteByKey(K key);
}
