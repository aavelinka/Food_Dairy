package com.uni.project.mapper;

import com.uni.project.model.dto.request.WaterIntakeRequest;
import com.uni.project.model.dto.response.WaterIntakeResponse;
import com.uni.project.model.entity.WaterIntake;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WaterIntakeMapper {
    @Mapping(target = "id", ignore = true)
    WaterIntake fromRequest(WaterIntakeRequest waterIntakeRequest);

    WaterIntakeResponse toResponse(WaterIntake waterIntake);

    List<WaterIntakeResponse> toResponses(List<WaterIntake> waterIntakes);
}
