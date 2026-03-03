package com.uni.project.service.impl;

import com.uni.project.exception.BodyParametersException;
import com.uni.project.exception.UserException;
import com.uni.project.mapper.BodyParametersMapper;
import com.uni.project.mapper.NutritionalValueMapper;
import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.response.BodyParametersResponse;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.entity.BodyParameters;
import com.uni.project.model.entity.NutritionalValue;
import com.uni.project.model.entity.Sex;
import com.uni.project.model.entity.User;
import com.uni.project.repository.BodyParametersRepository;
import com.uni.project.repository.UserRepository;
import com.uni.project.service.BodyParametersService;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BodyParametersServiceImpl implements BodyParametersService {
    private static final String BODY_PARAMETERS_FAIL_MESSAGE = "Body parameters not found by Id";
    private static final String USER_FAIL_MESSAGE = "User not found by Id";
    private final BodyParametersRepository bodyParametersRepository;
    private final UserRepository userRepository;
    private final BodyParametersMapper bodyParametersMapper;
    private final NutritionalValueMapper nutritionalValueMapper;

    @Override
    @Transactional
    public BodyParametersResponse bodyParametersCreate(BodyParametersRequest bodyParametersRequest) {
        User owner = userRepository.findById(bodyParametersRequest.getUserId())
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        BodyParameters bodyParameters = bodyParametersMapper.fromRequest(bodyParametersRequest);
        bodyParameters.setOwner(owner);
        BodyParameters savedBodyParameters = bodyParametersRepository.save(bodyParameters);
        return bodyParametersMapper.toResponse(savedBodyParameters);
    }

    @Override
    public BodyParametersResponse getBodyParametersById(Integer id) {
        BodyParameters bodyParameters = bodyParametersRepository.findById(id)
                .orElseThrow(() -> new BodyParametersException(BODY_PARAMETERS_FAIL_MESSAGE));
        return bodyParametersMapper.toResponse(bodyParameters);
    }

    @Override
    public List<BodyParametersResponse> getAllBodyParameters() {
        return bodyParametersMapper.toResponses(bodyParametersRepository.findAll());
    }

    @Override
    @Transactional
    public BodyParametersResponse bodyParametersUpdate(Integer id, BodyParametersRequest bodyParametersRequest) {
        BodyParameters bodyParameters = bodyParametersRepository.findById(id)
                .orElseThrow(() -> new BodyParametersException(BODY_PARAMETERS_FAIL_MESSAGE));
        User owner = userRepository.findById(bodyParametersRequest.getUserId())
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));

        bodyParameters.setOwner(owner);
        bodyParameters.setRecordDate(bodyParametersRequest.getRecordDate());
        bodyParameters.setSex(bodyParametersRequest.getSex());
        bodyParameters.setWeight(bodyParametersRequest.getWeight());
        bodyParameters.setHeight(bodyParametersRequest.getHeight());
        bodyParameters.setAge(bodyParametersRequest.getAge());
        bodyParameters.setChest(bodyParametersRequest.getChest());
        bodyParameters.setWaist(bodyParametersRequest.getWaist());
        bodyParameters.setHips(bodyParametersRequest.getHips());
        bodyParameters.setGoalNutritional(bodyParametersMapper
                .fromRequest(bodyParametersRequest)
                .getGoalNutritional());
        bodyParametersRepository.save(bodyParameters);

        return bodyParametersMapper.toResponse(bodyParameters);
    }

    @Override
    @Transactional
    public void bodyParametersDelete(Integer id) {
        BodyParameters bodyParameters = bodyParametersRepository.findById(id)
                .orElseThrow(() -> new BodyParametersException(BODY_PARAMETERS_FAIL_MESSAGE));
        bodyParametersRepository.delete(bodyParameters);
    }

    @Override
    public List<BodyParametersResponse> getAllBodyParametersByUserId(Integer userId) {
        return bodyParametersMapper.toResponses(bodyParametersRepository.findAllByOwnerIdOrderByRecordDateDesc(userId));
    }

    @Override
    public List<BodyParametersResponse> getAllBodyParametersByUserIdAndDate(Integer userId, LocalDate date) {
        return bodyParametersMapper.toResponses(bodyParametersRepository.findAllByOwnerIdAndRecordDate(userId, date));
    }

    @Override
    @Transactional
    public NutritionalValueResponse calculateNutritionalValueForUser(Integer idUser) {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new UserException(USER_FAIL_MESSAGE));
        BodyParameters measurements = getLatestBodyParameters(user);
        if (measurements == null
                || measurements.getWeight() == null
                || measurements.getHeight() == null
                || measurements.getAge() == null
                || measurements.getSex() == null) {
            throw new BodyParametersException("User measurements are incomplete for nutritional value calculation");
        }

        NutritionalValue goalNutritionalValue = getNutritionalValue(measurements);
        measurements.setGoalNutritional(goalNutritionalValue);
        bodyParametersRepository.save(measurements);

        return nutritionalValueMapper.toResponse(goalNutritionalValue);
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
}
