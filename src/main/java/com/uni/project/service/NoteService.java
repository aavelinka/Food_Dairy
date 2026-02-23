package com.uni.project.service;

import com.uni.project.model.dto.request.NoteRequest;
import com.uni.project.model.dto.response.NoteResponse;

import java.time.LocalDate;
import java.util.List;

public interface NoteService {
    NoteResponse noteCreate(NoteRequest noteRequest);

    NoteResponse getNoteById(Integer id);

    List<NoteResponse> getAllNotes();

    NoteResponse noteUpdate(Integer id, NoteRequest noteRequest);

    void noteDelete(Integer id);

    List<NoteResponse> getAllNotesByUserId(Integer userId);

    List<NoteResponse> getAllNotesByDate(LocalDate dateSearch);

    List<NoteResponse> getAllNotesByMealId(Integer mealId);
}
