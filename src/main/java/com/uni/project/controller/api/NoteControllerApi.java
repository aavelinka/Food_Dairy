package com.uni.project.controller.api;

import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Notes", description = "Operations with notes attached to meals")
public interface NoteControllerApi {
    @Operation(summary = "Create note")
    @ApiResponse(responseCode = "201", description = "Note created")
    @BadRequestApiResponse
    @ConflictApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<NoteResponse> noteCreate(@Valid NoteRequest noteRequest);

    @Operation(summary = "Get note by id")
    @ApiResponse(responseCode = "200", description = "Note found")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<NoteResponse> getNoteById(@Parameter(description = "Note id") @Positive Integer id);

    @Operation(summary = "Get all notes")
    @ApiResponse(responseCode = "200", description = "Notes returned")
    @InternalServerErrorApiResponse
    ResponseEntity<List<NoteResponse>> getAllNotes();

    @Operation(summary = "Update note")
    @ApiResponse(responseCode = "200", description = "Note updated")
    @BadRequestApiResponse
    @ConflictApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<NoteResponse> noteUpdate(
            @Parameter(description = "Note id") @Positive Integer id,
            @Valid NoteRequest noteRequest
    );

    @Operation(summary = "Delete note")
    @ApiResponse(responseCode = "204", description = "Note deleted")
    @BadRequestApiResponse
    @NotFoundApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<Void> noteDelete(@Parameter(description = "Note id") @Positive Integer id);

    @Operation(summary = "Find notes by meal date")
    @ApiResponse(responseCode = "200", description = "Notes returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<NoteResponse>> getAllNotesByDate(@Parameter(description = "Meal date") LocalDate dateSearch);

    @Operation(summary = "Find notes by meal id")
    @ApiResponse(responseCode = "200", description = "Notes returned")
    @BadRequestApiResponse
    @InternalServerErrorApiResponse
    ResponseEntity<List<NoteResponse>> getAllNotesByMeal(
            @Parameter(description = "Meal id") @Positive Integer mealId
    );
}
