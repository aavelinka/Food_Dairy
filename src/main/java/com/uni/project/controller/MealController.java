package com.uni.project.controller;

import com.uni.project.controller.api.MealControllerApi;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.response.MealResponse;
import com.uni.project.service.MealService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/meal")
@AllArgsConstructor
@Validated
public class MealController implements MealControllerApi {
    private final MealService mealService;

    @GetMapping("/{id}")
    public ResponseEntity<MealResponse> getMealById(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @GetMapping
    public ResponseEntity<List<MealResponse>> getAllMeals() {
        return ResponseEntity.ok(mealService.getAllMeals());
    }
    
    @PostMapping
    public ResponseEntity<MealResponse> mealCreate(@Valid @RequestBody MealRequest mealRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mealService.mealCreate(mealRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealResponse> mealUpdate(
            @PathVariable @Positive Integer id,
            @Valid @RequestBody MealRequest mealRequest
    ) {
        return ResponseEntity.ok(mealService.mealUpdate(id, mealRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> mealDelete(@PathVariable @Positive Integer id) {
        mealService.mealDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name")
    public ResponseEntity<List<MealResponse>> getAllMealsByName(@RequestParam @NotBlank String nameSearch) {
        return ResponseEntity.ok(mealService.getAllMealsByName(nameSearch));
    }

    @GetMapping("/author")
    public ResponseEntity<List<MealResponse>> getAllMealsByAuthor(
            @RequestParam("authorId") @Positive Integer authorId
    ) {
        return ResponseEntity.ok(mealService.getAllMealsByAuthorId(authorId));
    }

    @GetMapping("/product_list")
    public ResponseEntity<List<MealResponse>> getAllMealsByProductList(
            @RequestParam("productIds") List<@Positive Integer> productIds
    ) {
        return ResponseEntity.ok(mealService.getAllMealsByProductIds(productIds));
    }
}
