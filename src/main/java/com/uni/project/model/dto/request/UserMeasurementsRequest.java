package com.uni.project.model.dto.request;

import com.uni.project.model.entity.BodyParameters;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMeasurementsRequest {
    @NotNull
    @Valid
    private BodyParameters measurements;
}
