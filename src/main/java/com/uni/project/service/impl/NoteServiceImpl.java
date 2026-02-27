package com.uni.project.service.impl;

import com.uni.project.exception.NoteException;
import com.uni.project.mapper.NoteMapper;
import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.NoteRepository;
import com.uni.project.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final MealRepository mealRepository;

    private final NoteMapper noteMapper;

    @Override
    @Transactional
    public NoteResponse noteCreate(NoteRequest noteRequest) {
        Meal meal = getMeal(noteRequest.getMealId());
        if (meal.getRecipe() != null) {
            throw new NoteException("Meal already has a note");
        }

        Note note = noteMapper.fromRequest(noteRequest, meal);
        meal.setRecipe(note);
        Meal savedMeal = mealRepository.save(meal);

        return noteMapper.toResponse(savedMeal.getRecipe());
    }

    @Override
    public NoteResponse getNoteById(Integer id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteException("Note is not found by Id"));

        return noteMapper.toResponse(note);
    }

    @Override
    public List<NoteResponse> getAllNotes() {
        return toResponses(noteRepository.findAll());
    }

    @Override
    @Transactional
    public NoteResponse noteUpdate(Integer id, NoteRequest noteRequest) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteException("Note is not found by Id"));
        Meal targetMeal = getMeal(noteRequest.getMealId());
        Meal currentMeal = note.getMeal();
        note.setNotes(noteRequest.getNotes());

        if (targetMeal.getRecipe() != null && !targetMeal.getRecipe().getId().equals(note.getId())) {
            throw new NoteException("Target meal already has another note");
        }

        if (currentMeal != null && !currentMeal.getId().equals(targetMeal.getId())) {
            currentMeal.setRecipe(null);
            mealRepository.save(currentMeal);
        }

        note.setMeal(targetMeal);
        targetMeal.setRecipe(note);
        mealRepository.save(targetMeal);

        return noteMapper.toResponse(note);
    }

    @Override
    @Transactional
    public void noteDelete(Integer id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new NoteException("Note is not found by Id"));
        Meal meal = note.getMeal();
        if (meal == null) {
            noteRepository.delete(note);
            return;
        }

        meal.setRecipe(null);
        mealRepository.save(meal);
    }

    @Override
    public List<NoteResponse> getAllNotesByUserId(Integer userId) {
        return toResponses(noteRepository.findAllByUserId(userId));
    }

    @Override
    public List<NoteResponse> getAllNotesByDate(LocalDate dateSearch) {
        return toResponses(noteRepository.findAllByDate(dateSearch));
    }

    @Override
    public List<NoteResponse> getAllNotesByMealId(Integer mealId) {
        return toResponses(noteRepository.findAllByMealId(mealId));
    }

    private List<NoteResponse> toResponses(List<Note> notes) {
        return notes.stream()
                .map(noteMapper::toResponse)
                .toList();
    }

    private Meal getMeal(Integer mealId) {
        return mealRepository.findById(mealId)
                .orElseThrow(() -> new NoteException("Meal not found by Id"));
    }
}
