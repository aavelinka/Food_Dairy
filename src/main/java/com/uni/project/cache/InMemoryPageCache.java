package com.uni.project.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class InMemoryPageCache<K, V> {
    private final Map<K, Page<V>> cache = new HashMap<>();

    public synchronized Optional<Page<V>> get(K key) {
        return Optional.ofNullable(cache.get(key));
    }

    public synchronized void put(K key, Page<V> value) {
        cache.put(key, copyPage(value));
    }

    public synchronized void clear() {
        cache.clear();
    }

    private Page<V> copyPage(Page<V> source) {
        return new PageImpl<>(List.copyOf(source.getContent()), source.getPageable(), source.getTotalElements());
    }
}
