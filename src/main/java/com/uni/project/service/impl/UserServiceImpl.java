package com.uni.project.service.impl;

import com.uni.project.exception.UserException;
import com.uni.project.mapper.NutritionalValueMapper;
import com.uni.project.mapper.UserMapper;
import com.uni.project.model.dto.request.UserCompositeRequest;
import com.uni.project.model.dto.request.UserMeasurementsRequest;
import com.uni.project.model.dto.request.UserRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
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
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
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
        user.setMeasurements(userRequest.getMeasurements());
        if (userRequest.getDailyGoal() != null) {
            user.setDailyGoal(nutritionalValueMapper.fromRequest(userRequest.getDailyGoal()));
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
    @Transactional
    public UserResponse measurementsUpdate(Integer id, UserMeasurementsRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        user.setMeasurements(userRequest.getMeasurements());
        userRepository.save(user);
        return userMapper.toResponse(user);
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
    @Transactional(readOnly = true)
    public NutritionalValueResponse calculateNutritionalValueForUser(Integer idUser) {
        User user = userRepository.findById(idUser).
                orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        BodyParameters measurements = user.getMeasurements();
        if (measurements == null
                || measurements.getWeight() == null
                || measurements.getHeight() == null
                || measurements.getAge() == null
                || measurements.getSex() == null) {
            throw new UserException("User measurements are incomplete for nutritional value calculation");
        }
        NutritionalValue goalNutritionalValue = getNutritionalValue(measurements);
        user.setDailyGoal(goalNutritionalValue);
        userRepository.save(user);

        return nutritionalValueMapper.toResponse(goalNutritionalValue);
    }

    @NonNull
    private static NutritionalValue getNutritionalValue(BodyParameters measurements) {
        NutritionalValue goalNutritionalValue = new NutritionalValue();
        double baseValues = (measurements.getWeight() * 10
                + measurements.getHeight() * 6.25
                - measurements.getAge() * 5);
        baseValues = (measurements.getSex() == Sex.FEMALE)
                ? (baseValues - 161)
                : (baseValues + 5);
        goalNutritionalValue.setCalories(baseValues);
        goalNutritionalValue.setProteins(baseValues * 0.25);
        goalNutritionalValue.setFats(baseValues * 0.2);
        goalNutritionalValue.setCarbohydrates(baseValues * 0.55);
        return goalNutritionalValue;
    }

    @Override
    public List<UserResponse> findAllWithMeals() {
        List<User> userList = userRepository.findAllWithMeals();
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
        NutritionalValue dailyGoal = nutritionalValueMapper.fromRequest(request.getDailyGoal());
        User user = userMapper.fromRequest(request);
        user.setDailyGoal(dailyGoal);
        user.setMealsPlan(new ArrayList<>());
        user = userRepository.save(user);

        if (request.isFailAfterUser()) {
            throw new UserException("Forced error after saving user and daily goal");
        }

        Note note = new Note();
        note.setNotes(request.getNoteTexts());

        Meal meal = new Meal();
        meal.setName("Meal note");
        meal.setDate(request.getNoteDate());
        meal.setAuthor(user);
        meal.setRecipe(note);
        mealRepository.save(meal);
        if (user.getMealsPlan() != null) {
            user.getMealsPlan().add(meal);
        }

        return userMapper.toResponse(user);
    }
}
