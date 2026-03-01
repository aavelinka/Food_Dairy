package com.uni.project.controller;

import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;
import com.uni.project.service.NoteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/note")
@AllArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<NoteResponse> noteCreate(@Valid @RequestBody NoteRequest noteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.noteCreate(noteRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable Integer id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteResponse> noteUpdate(@PathVariable Integer id,
                                                   @Valid @RequestBody NoteRequest noteRequest) {
        return ResponseEntity.ok(noteService.noteUpdate(id, noteRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> noteDelete(@PathVariable Integer id) {
        noteService.noteDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date")
    public ResponseEntity<List<NoteResponse>> getAllNotesByDate(@RequestParam LocalDate dateSearch) {
        return ResponseEntity.ok(noteService.getAllNotesByDate(dateSearch));
    }

    @GetMapping("/meal_note")
    public ResponseEntity<List<NoteResponse>> getAllNotesByMeal(@RequestParam("mealId") Integer mealId) {
        return ResponseEntity.ok(noteService.getAllNotesByMealId(mealId));
    }
}
