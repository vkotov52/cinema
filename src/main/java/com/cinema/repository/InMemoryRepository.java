package com.cinema.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;

/** Хранилище в памяти. Одна реализация на все сущности. */
public class InMemoryRepository<T, K extends Comparable<K>> implements EntityRepository<T, K> {

    private final Map<K, T> storage = new ConcurrentSkipListMap<>();
    private final Function<T, K> keyExtractor;

    public InMemoryRepository(Function<T, K> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    @Override
    public T save(T entity) {
        storage.put(keyExtractor.apply(entity), entity);
        return entity;
    }

    @Override
    public Optional<T> findByKey(K key) {
        return Optional.ofNullable(storage.get(key));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteByKey(K key) {
        return storage.remove(key) != null;
    }
}
