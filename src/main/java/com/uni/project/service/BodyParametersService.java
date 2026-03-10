package com.uni.project.service;

import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.BodyParametersResponse;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import java.time.LocalDate;
import java.util.List;

public interface BodyParametersService {
    BodyParametersResponse bodyParametersCreate(BodyParametersRequest bodyParametersRequest);

    BodyParametersResponse getBodyParametersById(Integer id);

    List<BodyParametersResponse> getAllBodyParameters();

    BodyParametersResponse bodyParametersUpdate(Integer id, BodyParametersRequest bodyParametersRequest);

    void bodyParametersDelete(Integer id);

    List<BodyParametersResponse> getAllBodyParametersByUserId(Integer userId);

    List<BodyParametersResponse> getAllBodyParametersByUserIdAndDate(Integer userId, LocalDate date);

    NutritionalValueResponse calculateNutritionalValueForUser(Integer id);

    NutritionalValueResponse setManualNutritionalValue(Integer bodyParametersId, NutritionalValueRequest request);
}
