package com.uni.project.mapper;

import com.uni.project.model.dto.request.BodyParametersRequest;
import com.uni.project.model.dto.response.BodyParametersResponse;
import com.uni.project.model.entity.BodyParameters;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = NutritionalValueMapper.class)
public interface BodyParametersMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "goalNutritional", source = "dailyGoal")
    BodyParameters fromRequest(BodyParametersRequest bodyParametersRequest);

    @Mapping(target = "userId", source = "owner.id")
    @Mapping(target = "dailyGoal", source = "goalNutritional")
    BodyParametersResponse toResponse(BodyParameters bodyParameters);

    List<BodyParametersResponse> toResponses(List<BodyParameters> bodyParametersList);
}
