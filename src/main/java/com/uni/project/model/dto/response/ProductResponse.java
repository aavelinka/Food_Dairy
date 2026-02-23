package com.uni.project.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer id;

    private String name;

    private Integer nutritionalValue100gId;

    private List<Integer> mealIds;
}
