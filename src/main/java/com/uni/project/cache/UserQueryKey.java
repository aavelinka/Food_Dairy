package com.uni.project.cache;

import java.util.Objects;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record UserQueryKey(QueryType queryType, Integer ageSearch, int pageNumber, int pageSize, Sort sort) {
    public enum QueryType {
        ALL_USERS,
        USERS_BY_AGE_JPQL,
        USERS_BY_AGE_NATIVE
    }

    public static UserQueryKey forAllUsers(Pageable pageable) {
        return new UserQueryKey(
                QueryType.ALL_USERS,
                null,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    public static UserQueryKey forAgeJpql(Integer ageSearch, Pageable pageable) {
        return new UserQueryKey(
                QueryType.USERS_BY_AGE_JPQL,
                ageSearch,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    public static UserQueryKey forAgeNative(Integer ageSearch, Pageable pageable) {
        return new UserQueryKey(
                QueryType.USERS_BY_AGE_NATIVE,
                ageSearch,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort()
        );
    }

    @Override
    public Sort sort() {
        return sort == null ? Sort.unsorted() : sort;
    }

    public UserQueryKey {
        Objects.requireNonNull(queryType, "queryType must not be null");
    }
}
