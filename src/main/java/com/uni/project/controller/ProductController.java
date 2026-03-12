package com.uni.project.controller;

import com.uni.project.controller.api.ProductControllerApi;
import com.uni.project.model.dto.request.ProductRequest;
import com.uni.project.model.dto.response.ProductResponse;
import com.uni.project.service.ProductService;
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
@RequestMapping("/api/products")
@AllArgsConstructor
@Validated
public class ProductController implements ProductControllerApi {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> productCreate(@Valid @RequestBody
                                                         ProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.productCreate(productRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> productUpdate(
            @PathVariable @Positive Integer id,
            @Valid @RequestBody ProductRequest productRequest
    ) {
        return ResponseEntity.ok(productService.productUpdate(id, productRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> productDelete(@PathVariable @Positive Integer id) {
        productService.productDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/name")
    public ResponseEntity<List<ProductResponse>> getAllProductsByName(@RequestParam @NotBlank String nameSearch) {
        return ResponseEntity.ok(productService.getAllProductsByName(nameSearch));
    }

    @GetMapping("/meal_list")
    public ResponseEntity<List<ProductResponse>> getAllProductsByMeal(
            @RequestParam("mealId") @Positive Integer mealId
    ) {
        return ResponseEntity.ok(productService.getAllProductsByMealId(mealId));
    }
}
