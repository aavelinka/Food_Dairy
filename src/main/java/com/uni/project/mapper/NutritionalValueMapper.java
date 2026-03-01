package com.uni.project.mapper;

import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.NutritionalValueResponse;
import com.uni.project.model.entity.NutritionalValue;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NutritionalValueMapper {
    NutritionalValueResponse toResponse(NutritionalValue nutritionalValue);

    List<NutritionalValueResponse> toResponses(List<NutritionalValue> values);

    NutritionalValue fromRequest(NutritionalValueRequest nutritionalValueRequest);
}
