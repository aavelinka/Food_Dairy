package com.uni.project.cache;

import com.uni.project.model.dto.response.UserResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class UserSearchCache {
    private final InMemoryPageCache<UserQueryKey, UserResponse> cache = new InMemoryPageCache<>();

    public Optional<Page<UserResponse>> get(UserQueryKey key) {
        return cache.get(key);
    }

    public void put(UserQueryKey key, Page<UserResponse> value) {
        cache.put(key, value);
    }

    public void clear() {
        cache.clear();
    }
}
