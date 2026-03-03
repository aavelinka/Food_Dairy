package com.uni.project.service.impl;

import com.uni.project.exception.UserException;
import com.uni.project.mapper.NutritionalValueMapper;
import com.uni.project.mapper.UserMapper;
import com.uni.project.model.dto.request.UserCompositeRequest;
import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.UserResponse;
import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.UserRepository;
import com.uni.project.service.UserService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MealRepository mealRepository;
    private final UserMapper userMapper;
    private final NutritionalValueMapper nutritionalValueMapper;
    private static final String USER_FAIL_MESSAGE = "User not found by Id";

    @Override
    @Transactional
    public UserResponse userCreate(UserRequest userRequest) {
        User user = userMapper.fromRequest(userRequest);
        user.setMealsPlan(new ArrayList<>());
        user.setBodyParametersHistory(new LinkedHashSet<>());
        BodyParameters bodyParameters = buildBodyParametersRecord(
                user,
                userRequest.getMeasurements(),
                toGoalFromRequest(userRequest)
        );
        if (bodyParameters != null) {
            user.getBodyParametersHistory().add(bodyParameters);
        }
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponses(userRepository.findAll());
    }

    @Override
    @Transactional
    public UserResponse userUpdate(Integer id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        user.setName(userRequest.getName());
        user.setPassword(userRequest.getPassword());
        user.setEmail(userRequest.getEmail());

        BodyParameters bodyParameters = buildBodyParametersRecord(
                user,
                userRequest.getMeasurements(),
                toGoalFromRequest(userRequest)
        );
        if (bodyParameters != null) {
            if (user.getBodyParametersHistory() == null) {
                user.setBodyParametersHistory(new LinkedHashSet<>());
            }
            user.getBodyParametersHistory().add(bodyParameters);
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void userDelete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        userRepository.delete(user);
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
    public List<UserResponse> getAllUsersByAge(Integer ageSearch) {
        return userMapper.toResponses(userRepository.findAllByAge(ageSearch));
    }

    @Override
    public List<UserResponse> findAllWithMealsAndBodyParameters() {
        List<User> userList = userRepository.findAllWithMealsAndBodyParameters();
        return userMapper.toResponses(userList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UserResponse createUserWithoutGoalAndNoteNoTx(UserCompositeRequest userRequest) {
        return createCompositeInternal(userRequest);
    }

    @Override
    @Transactional
    public UserResponse createUserWithGoalAndNoteTx(UserCompositeRequest userRequest) {
        return createCompositeInternal(userRequest);
    }

    private UserResponse createCompositeInternal(UserCompositeRequest request) {
        User user = userMapper.fromRequest(request);

        if (user.getMealsPlan() == null) {
            user.setMealsPlan(new ArrayList<>());
        }
        if (user.getBodyParametersHistory() == null) {
            user.setBodyParametersHistory(new LinkedHashSet<>());
        }

        BodyParameters bodyParameters = buildBodyParametersRecord(
                user,
                request.getMeasurements(),
                toGoalFromRequest(request)
        );

        if (bodyParameters != null) {
            user.getBodyParametersHistory().add(bodyParameters);
        }

        user = userRepository.save(user);

        if (request.isFailAfterUser()) {
            throw new UserException("Forced error after saving user and daily goal");
        }

        Note note = new Note();
        note.setNotes(new ArrayList<>(request.getNotes()));

        Meal meal = new Meal();
        meal.setName(request.getMealName());
        meal.setDate(request.getMealDate());
        meal.setAuthor(user);
        meal.setRecipe(note);

        note.setMeal(meal);

        meal = mealRepository.save(meal);

        user.getMealsPlan().add(meal);

        return userMapper.toResponse(user);
    }

    private NutritionalValue toGoalFromRequest(UserRequest userRequest) {
        if (userRequest.getDailyGoal() == null) {
            return null;
        }
        return nutritionalValueMapper.fromRequest(userRequest.getDailyGoal());
    }

    private BodyParameters buildBodyParametersRecord(User user,
                                                     BodyParameters source,
                                                     NutritionalValue goalOverride) {
        if (source == null) {
            return null;
        }

        BodyParameters bodyParameters = new BodyParameters();
        bodyParameters.setRecordDate(source.getRecordDate() == null ? LocalDate.now() : source.getRecordDate());
        bodyParameters.setSex(source.getSex());
        bodyParameters.setWeight(source.getWeight());
        bodyParameters.setHeight(source.getHeight());
        bodyParameters.setAge(source.getAge());
        bodyParameters.setChest(source.getChest());
        bodyParameters.setWaist(source.getWaist());
        bodyParameters.setHips(source.getHips());
        bodyParameters.setGoalNutritional(goalOverride == null ? source.getGoalNutritional() : goalOverride);
        bodyParameters.setOwner(user);
        return bodyParameters;
    }

}
