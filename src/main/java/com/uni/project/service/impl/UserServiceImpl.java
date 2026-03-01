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
import com.uni.project.model.entity.Product;
import com.uni.project.model.entity.Sex;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.NutritionalValueRepository;
import com.uni.project.repository.UserRepository;
import com.uni.project.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final NutritionalValueRepository nutritionalValueRepository;
    private final UserMapper userMapper;
    private final NutritionalValueMapper nutritionalValueMapper;
    private static final String USER_FAIL_MESSAGE = "User not found by Id";

    @Override
    @Transactional
    public UserResponse userCreate(UserRequest userRequest) {
        NutritionalValue dailyGoal = getDailyGoal(userRequest.getDailyGoalId());
        List<Meal> meals = getMeals(userRequest.getMealIds());
        User user = userMapper.fromRequest(userRequest, dailyGoal, new ArrayList<>());
        replaceMeals(user, meals);
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
        return toResponses(userRepository.findAll());
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

        if (userRequest.getDailyGoalId() != null) {
            NutritionalValue dailyGoal = getDailyGoal(userRequest.getDailyGoalId());
            user.setDailyGoal(dailyGoal);
        }
        if (userRequest.getMealIds() != null) {
            List<Meal> meals = getMeals(userRequest.getMealIds());
            replaceMeals(user, meals);
        }

        userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void userDelete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));

        if (user.getMealsPlan() != null) {
            for (Meal meal : new ArrayList<>(user.getMealsPlan())) {
                unlinkMealProducts(meal);
            }
        }

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
        return toResponses(userRepository.findAllByName(nameSearch));
    }

    @Override
    public List<UserResponse> getAllUsersBySex(Sex sexSearch) {
        return toResponses(userRepository.findAllBySex(sexSearch));
    }

    @Override
    public List<UserResponse> getAllUsersByAge(Integer ageSearch) {
        return toResponses(userRepository.findAllByAge(ageSearch));
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
        nutritionalValueRepository.save(goalNutritionalValue);
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
        return toResponses(userList);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public UserResponse createUserWithGoalAndNoteNoTx(UserCompositeRequest userRequest) {
        return createCompositeInternal(userRequest);
    }

    @Override
    @Transactional
    public UserResponse createUserWithGoalAndNoteTx(UserCompositeRequest userRequest) {
        return createCompositeInternal(userRequest);
    }

    private List<UserResponse> toResponses(List<User> users) { //надо вынести в маппер
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    private NutritionalValue getDailyGoal(Integer dailyGoalId) {
        if (dailyGoalId == null) {
            return null;
        }
        return nutritionalValueRepository.findById(dailyGoalId)
                .orElseThrow(() -> new UserException("Nutritional Value not found by Id"));
    }

    private List<Meal> getMeals(List<Integer> mealIds) {
        if (mealIds == null || mealIds.isEmpty()) {
            return List.of();
        }
        List<Meal> meals = mealRepository.findAllById(mealIds);
        if (meals.size() != mealIds.size()) {
            throw new UserException("Some meals not found");
        }
        return meals;
    }

    private void unlinkMealProducts(Meal meal) {
        if (meal == null || meal.getProductList() == null) {
            return;
        }

        for (Product product : new ArrayList<>(meal.getProductList())) {
            if (product.getMealList() != null) {
                product.getMealList().remove(meal);
            }
        }
        meal.getProductList().clear();
    }

    private void replaceMeals(User user, List<Meal> meals) {
        if (user.getMealsPlan() == null) {
            user.setMealsPlan(new ArrayList<>());
        }

        List<Meal> sourceMeals = meals == null
                ? List.of()
                : meals.stream().filter(Objects::nonNull).toList();
        Set<Integer> targetIds = sourceMeals.stream()
                .map(Meal::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Integer userId = user.getId();
        List<Meal> mealsToRemove = user.getMealsPlan().stream()
                .filter(currentMeal -> currentMeal.getId() == null
                        || !targetIds.contains(currentMeal.getId()))
                .toList();

        for (Meal mealToRemove : mealsToRemove) {
            Integer authorId = mealToRemove.getAuthor() == null
                    ? null
                    : mealToRemove.getAuthor().getId();
            if (Objects.equals(authorId, userId)) {
                mealToRemove.setAuthor(null);
            }
        }
        user.getMealsPlan().removeAll(mealsToRemove);

        for (Meal meal : sourceMeals) {
            Integer authorId = meal.getAuthor() == null
                    ? null
                    : meal.getAuthor().getId();
            if (!Objects.equals(authorId, userId)) {
                meal.setAuthor(user);
            }
            if (!user.getMealsPlan().contains(meal)) {
                user.getMealsPlan().add(meal);
            }
        }
    }

    private UserResponse createCompositeInternal(UserCompositeRequest request) {
        NutritionalValue dailyGoal = new NutritionalValue();
        dailyGoal.setCalories(request.getDailyGoalCalories());
        dailyGoal.setProteins(request.getDailyGoalProteins());
        dailyGoal.setFats(request.getDailyGoalFats());
        dailyGoal.setCarbohydrates(request.getDailyGoalCarbohydrates());
        nutritionalValueRepository.save(dailyGoal);

        User user = userRepository.save(
                userMapper.fromRequest(request, dailyGoal, new ArrayList<>())
        );

        user.setDailyGoal(dailyGoal);
        userRepository.save(user);

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
