package com.uni.project.service.impl;

import com.uni.project.exception.NoteException;
import com.uni.project.mapper.NoteMapper;
import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;
import com.uni.project.model.entity.Meal;
import com.uni.project.model.entity.Note;
import com.uni.project.model.entity.User;
import com.uni.project.repository.MealRepository;
import com.uni.project.repository.NoteRepository;
import com.uni.project.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final MealRepository mealRepository;

    private final NoteMapper noteMapper;

    @Override
    @Transactional
    public NoteResponse noteCreate(NoteRequest noteRequest) {
        User user = getUser(noteRequest.getUserId());
        Meal meal = getMeal(noteRequest.getMealId());
        Note note = noteRepository
                .save(noteMapper.fromRequest(noteRequest, user, meal));

        return noteMapper.toResponse(note);
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
        User user = getUser(noteRequest.getUserId());
        Meal meal = getMeal(noteRequest.getMealId());
        note.setUser(user);
        note.setMeal(meal);
        note.setDate(noteRequest.getDate());
        note.setNotes(noteRequest.getNotes());
        noteRepository.save(note);

        return noteMapper.toResponse(note);
    }

    @Override
    @Transactional
    public void noteDelete(Integer id) {
        noteRepository.deleteById(id);
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

    private User getUser(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoteException("User not found by Id"));
    }

    private Meal getMeal(Integer mealId) {
        if (mealId == null) {
            return null;
        }
        return mealRepository.findById(mealId)
                .orElseThrow(() -> new NoteException("Meal not found by Id"));
    }
}
