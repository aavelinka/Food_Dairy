package com.uni.project.service;


import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;

import java.util.List;

public interface NutritionalValueService {
    NutritionalValueResponse nutritionalValueCreate(NutritionalValueRequest nutritionalValueRequest);

    NutritionalValueResponse getNutritionalValueById(Integer id);

    List<NutritionalValueResponse> getAllNutritionalValues();

    NutritionalValueResponse nutritionalValueUpdate(Integer id, NutritionalValueRequest nutritionalValueRequest);

    void nutritionalValueDelete(Integer id);
}
