package com.uni.project.service.impl;

import com.uni.project.cache.UserQueryKey;
import com.uni.project.cache.UserSearchCache;
import com.uni.project.exception.EmailAlreadyExistsException;
import com.uni.project.exception.UserException;
import com.uni.project.mapper.UserMapper;
import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import com.uni.project.model.entity.User;
import com.uni.project.repository.UserRepository;
import com.uni.project.service.UserService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private static final String USER_FAIL_MESSAGE = "User not found by Id";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final NutritionalGoalCalculator nutritionalGoalCalculator;
    private final UserSearchCache userSearchCache;

    @Override
    @Transactional
    public UserResponse userCreate(UserRequest userRequest) {
        User savedUser = saveUserWithInitialBodyParameters(userRequest);
        userSearchCache.clear();
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        return userMapper.toResponse(user);
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return getCachedUsersPage(
                UserQueryKey.forAllUsers(pageable),
                () -> userRepository.findAll(pageable),
                pageable
        );
    }

    @Override
    @Transactional
    public UserResponse userUpdate(Integer id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        validateEmailAvailability(userRequest.getEmail(), id);

        user.setName(userRequest.getName());
        user.setPassword(userRequest.getPassword());
        user.setEmail(userRequest.getEmail());
        user.setGoalType(userRequest.getGoalType());

        BodyParameters newBodyParameters = createHistoryBodyParameters(userRequest.getMeasurements(), user);
        if (user.getBodyParametersHistory() == null) {
            user.setBodyParametersHistory(new HashSet<>());
        }
        user.getBodyParametersHistory().add(newBodyParameters);

        User savedUser = userRepository.save(user);
        userSearchCache.clear();
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public void userDelete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        userRepository.delete(user);
        userSearchCache.clear();
    }

    @Override
    public List<UserResponse> getAllUsersByName(String nameSearch) {
        return userMapper.toResponses(userRepository.findAllByName(nameSearch));
    }

    @Override
    public List<UserResponse> getAllUsersBySex(Sex sexSearch) {
        return userMapper.toResponses(userRepository.findAllBySex(sexSearch));
    }

    @Override
    public Page<UserResponse> getAllUsersByAge(Integer ageSearch, Pageable pageable) {
        return getCachedUsersPage(
                UserQueryKey.forAgeJpql(ageSearch, pageable),
                () -> userRepository.findAllByAge(ageSearch, pageable),
                pageable
        );
    }

    @Override
    public Page<UserResponse> getAllUsersByAgeNative(Integer ageSearch, Pageable pageable) {
        return getCachedUsersPage(
                UserQueryKey.forAgeNative(ageSearch, pageable),
                () -> userRepository.findAllByAgeNative(ageSearch, pageable),
                pageable
        );
    }

    @Override
    public List<UserResponse> findAllWithMealsAndBodyParameters() {
        return userMapper.toResponses(userRepository.findAllWithMealsAndBodyParameters());
    }

    private Page<UserResponse> toResponsePage(Page<User> usersPage, Pageable pageable) {
        List<UserResponse> content = usersPage.getContent().stream()
                .map(userMapper::toResponse)
                .toList();
        return new PageImpl<>(content, pageable, usersPage.getTotalElements());
    }

    private Page<UserResponse> getCachedUsersPage(
            UserQueryKey key,
            java.util.function.Supplier<Page<User>> pageSupplier,
            Pageable pageable
    ) {
        Optional<Page<UserResponse>> cached = userSearchCache.get(key);
        if (cached.isPresent()) {
            return cached.get();
        }

        Page<UserResponse> mappedPage = toResponsePage(pageSupplier.get(), pageable);
        userSearchCache.put(key, mappedPage);
        return mappedPage;
    }

    private User saveUserWithInitialBodyParameters(UserRequest userRequest) {
        validateEmailAvailability(userRequest.getEmail(), null);
        User user = userMapper.fromRequest(userRequest);
        BodyParameters initialBodyParameters = createInitialBodyParameters(userRequest.getMeasurements(), user);
        user.setBodyParametersHistory(new HashSet<>(Set.of(initialBodyParameters)));
        return userRepository.save(user);
    }

    private void validateEmailAvailability(String email, Integer currentUserId) {
        if (email == null || !userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        if (currentUserId != null) {
            User existingUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
            if (email.equalsIgnoreCase(existingUser.getEmail())) {
                return;
            }
        }

        throw new EmailAlreadyExistsException("Email already exists");
    }

    private BodyParameters createInitialBodyParameters(BodyParametersRequest request, User owner) {
        BodyParameters bodyParameters = toBodyParameters(request, owner);
        bodyParameters.setGoalNutritional(nutritionalGoalCalculator.calculate(bodyParameters, owner.getGoalType()));
        bodyParameters.setAutoCalculated(true);
        return bodyParameters;
    }

    private BodyParameters createHistoryBodyParameters(BodyParametersRequest request, User owner) {
        BodyParameters bodyParameters = toBodyParameters(request, owner);
        BodyParameters latest = getLatestBodyParameters(owner);

        if (latest == null || latest.getGoalNutritional() == null) {
            bodyParameters.setGoalNutritional(nutritionalGoalCalculator.calculate(bodyParameters, owner.getGoalType()));
            bodyParameters.setAutoCalculated(true);
            return bodyParameters;
        }

        bodyParameters.setGoalNutritional(copyNutritionalValue(latest.getGoalNutritional()));
        bodyParameters.setAutoCalculated(Boolean.TRUE.equals(latest.getAutoCalculated()));
        return bodyParameters;
    }

    private BodyParameters toBodyParameters(BodyParametersRequest request, User owner) {
        BodyParameters bodyParameters = new BodyParameters();
        bodyParameters.setRecordDate(request.getRecordDate());
        bodyParameters.setSex(request.getSex());
        bodyParameters.setWeight(request.getWeight());
        bodyParameters.setHeight(request.getHeight());
        bodyParameters.setAge(request.getAge());
        bodyParameters.setChest(request.getChest());
        bodyParameters.setWaist(request.getWaist());
        bodyParameters.setHips(request.getHips());
        bodyParameters.setOwner(owner);
        return bodyParameters;
    }

    private BodyParameters getLatestBodyParameters(User user) {
        if (user.getBodyParametersHistory() == null || user.getBodyParametersHistory().isEmpty()) {
            return null;
        }

        return user.getBodyParametersHistory().stream()
                .filter(Objects::nonNull)
                .max(Comparator
                        .comparing(BodyParameters::getRecordDate, Comparator.nullsLast(LocalDate::compareTo))
                        .thenComparing(BodyParameters::getId, Comparator.nullsFirst(Integer::compareTo)))
                .orElse(null);
    }

    private NutritionalValue copyNutritionalValue(NutritionalValue source) {
        return new NutritionalValue(
                source.getCalories(),
                source.getProteins(),
                source.getFats(),
                source.getCarbohydrates()
        );
    }
}
