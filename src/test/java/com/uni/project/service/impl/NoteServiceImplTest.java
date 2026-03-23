package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.exception.NoteConflictException;
import com.uni.project.mapper.NoteMapper;
import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.NoteRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {
    @Mock
    private NoteRepository noteRepository;

    @Mock
    private MealRepository mealRepository;

    @Mock
    private NoteMapper noteMapper;

    @InjectMocks
    private NoteServiceImpl noteService;

    @Test
    void noteCreateShouldAttachNoteToMeal() {
        NoteRequest request = buildRequest(10);
        Meal meal = buildMeal(10);
        Note note = buildNote(5, meal);
        NoteResponse expectedResponse = new NoteResponse();

        when(mealRepository.findById(10)).thenReturn(Optional.of(meal));
        when(noteMapper.fromRequest(request, meal)).thenReturn(note);
        when(mealRepository.save(same(meal))).thenReturn(meal);
        when(noteMapper.toResponse(note)).thenReturn(expectedResponse);

        NoteResponse actualResponse = noteService.noteCreate(request);

        assertSame(expectedResponse, actualResponse);
        assertSame(note, meal.getRecipe());
    }

    @Test
    void noteCreateShouldThrowWhenMealAlreadyHasRecipe() {
        NoteRequest request = buildRequest(11);
        Meal meal = buildMeal(11);
        meal.setRecipe(buildNote(8, meal));

        when(mealRepository.findById(11)).thenReturn(Optional.of(meal));

        assertThrows(NoteConflictException.class, () -> noteService.noteCreate(request));
    }

    @Test
    void getNoteByIdShouldReturnMappedResponse() {
        Note note = buildNote(1, null);
        NoteResponse expectedResponse = new NoteResponse();

        when(noteRepository.findById(1)).thenReturn(Optional.of(note));
        when(noteMapper.toResponse(note)).thenReturn(expectedResponse);

        NoteResponse actualResponse = noteService.getNoteById(1);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void getAllNotesShouldReturnMappedResponses() {
        List<Note> notes = List.of(buildNote(1, null));
        List<NoteResponse> expectedResponses = List.of(new NoteResponse());

        when(noteRepository.findAll()).thenReturn(notes);
        when(noteMapper.toResponses(notes)).thenReturn(expectedResponses);

        List<NoteResponse> actualResponses = noteService.getAllNotes();

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void noteUpdateShouldMoveNoteToAnotherMeal() {
        NoteRequest request = buildRequest(2);
        Meal currentMeal = buildMeal(1);
        Meal targetMeal = buildMeal(2);
        Note note = buildNote(5, currentMeal);
        NoteResponse expectedResponse = new NoteResponse();

        currentMeal.setRecipe(note);

        when(noteRepository.findById(5)).thenReturn(Optional.of(note));
        when(mealRepository.findById(2)).thenReturn(Optional.of(targetMeal));
        when(mealRepository.save(same(currentMeal))).thenReturn(currentMeal);
        when(mealRepository.save(same(targetMeal))).thenReturn(targetMeal);
        when(noteMapper.toResponse(note)).thenReturn(expectedResponse);

        NoteResponse actualResponse = noteService.noteUpdate(5, request);

        assertSame(expectedResponse, actualResponse);
        assertNull(currentMeal.getRecipe());
        assertSame(note, targetMeal.getRecipe());
        assertSame(targetMeal, note.getMeal());
        assertEquals(request.getNotes(), note.getNotes());
    }

    @Test
    void noteUpdateShouldThrowWhenTargetMealHasAnotherNote() {
        NoteRequest request = buildRequest(3);
        Meal currentMeal = buildMeal(1);
        Meal targetMeal = buildMeal(3);
        Note note = buildNote(5, currentMeal);
        Note anotherNote = buildNote(9, targetMeal);

        targetMeal.setRecipe(anotherNote);

        when(noteRepository.findById(5)).thenReturn(Optional.of(note));
        when(mealRepository.findById(3)).thenReturn(Optional.of(targetMeal));

        assertThrows(NoteConflictException.class, () -> noteService.noteUpdate(5, request));
    }

    @Test
    void noteDeleteShouldUnlinkNoteFromMeal() {
        Meal meal = buildMeal(4);
        Note note = buildNote(7, meal);
        meal.setRecipe(note);

        when(noteRepository.findById(7)).thenReturn(Optional.of(note));

        noteService.noteDelete(7);

        assertNull(meal.getRecipe());
        verify(mealRepository).save(same(meal));
        verify(noteRepository, never()).delete(note);
    }

    @Test
    void noteDeleteShouldDeleteNoteWithoutMeal() {
        Note note = buildNote(8, null);
        when(noteRepository.findById(8)).thenReturn(Optional.of(note));

        noteService.noteDelete(8);

        verify(noteRepository).delete(note);
    }

    @Test
    void getAllNotesByDateShouldReturnMappedResponses() {
        LocalDate date = LocalDate.of(2026, 3, 18);
        List<Note> notes = List.of(buildNote(1, null));
        List<NoteResponse> expectedResponses = List.of(new NoteResponse());

        when(noteRepository.findAllByDate(date)).thenReturn(notes);
        when(noteMapper.toResponses(notes)).thenReturn(expectedResponses);

        List<NoteResponse> actualResponses = noteService.getAllNotesByDate(date);

        assertEquals(expectedResponses, actualResponses);
    }

    @Test
    void getAllNotesByMealIdShouldReturnMappedResponses() {
        List<Note> notes = List.of(buildNote(1, null));
        List<NoteResponse> expectedResponses = List.of(new NoteResponse());

        when(noteRepository.findAllByMealId(10)).thenReturn(notes);
        when(noteMapper.toResponses(notes)).thenReturn(expectedResponses);

        List<NoteResponse> actualResponses = noteService.getAllNotesByMealId(10);

        assertEquals(expectedResponses, actualResponses);
    }

    private NoteRequest buildRequest(Integer mealId) {
        return new NoteRequest(mealId, List.of("Step 1", "Step 2"));
    }

    private Meal buildMeal(Integer id) {
        Meal meal = new Meal();
        meal.setId(id);
        meal.setDate(LocalDate.of(2026, 3, 18));
        return meal;
    }

    private Note buildNote(Integer id, Meal meal) {
        Note note = new Note();
        note.setId(id);
        note.setMeal(meal);
        note.setNotes(List.of("Initial"));
        return note;
    }
}
