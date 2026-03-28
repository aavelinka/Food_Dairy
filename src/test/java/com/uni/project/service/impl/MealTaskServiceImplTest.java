package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uni.project.cache.MealTaskRegistry;
import com.uni.project.exception.TaskNotFoundException;
import com.uni.project.model.dto.request.MealRequest;
import com.uni.project.model.dto.request.NutritionalValueRequest;
import com.uni.project.model.dto.response.MealTaskCreatedResponse;
import com.uni.project.model.dto.response.MealTaskStatusResponse;
import com.uni.project.model.dto.response.MealTaskStatisticsResponse;
import com.uni.project.model.task.MealTaskState;
import com.uni.project.model.task.MealTaskStatus;
import com.uni.project.service.MealTaskStatisticsService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MealTaskServiceImplTest {
    @Mock
    private MealTaskRegistry mealTaskRegistry;

    @Mock
    private MealTaskAsyncExecutor mealTaskAsyncExecutor;

    @Mock
    private MealTaskStatisticsService mealTaskStatisticsService;

    @InjectMocks
    private MealTaskServiceImpl mealTaskService;

    @Test
    void startBulkTxTaskShouldCreateTaskAndScheduleAsyncExecution() {
        MealRequest request = buildMealRequest();
        when(mealTaskRegistry.create(any(UUID.class), eq(1)))
                .thenAnswer(invocation -> MealTaskState.pending(
                        invocation.getArgument(0),
                        invocation.getArgument(1)
                ));

        MealTaskCreatedResponse response = mealTaskService.startBulkTxTask(List.of(request), null);

        ArgumentCaptor<UUID> taskIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(mealTaskRegistry).create(taskIdCaptor.capture(), eq(1));
        verify(mealTaskStatisticsService).onTaskSubmitted(1);
        verify(mealTaskAsyncExecutor).createBulkTx(eq(taskIdCaptor.getValue()), eq(List.of(request)), isNull());
        assertEquals(taskIdCaptor.getValue(), response.getTaskId());
        assertEquals(MealTaskStatus.PENDING, response.getStatus());
    }

    @Test
    void getTaskStatusShouldReturnMappedStatus() {
        UUID taskId = UUID.randomUUID();
        MealTaskState taskState = MealTaskState.pending(taskId, 2);
        taskState.markRunning();
        when(mealTaskRegistry.findById(taskId)).thenReturn(Optional.of(taskState));

        MealTaskStatusResponse response = mealTaskService.getTaskStatus(taskId);

        assertEquals(taskId, response.getTaskId());
        assertEquals(MealTaskStatus.RUNNING, response.getStatus());
        assertEquals(2, response.getTotalItems());
    }

    @Test
    void getTaskStatusShouldThrowWhenTaskMissing() {
        UUID taskId = UUID.randomUUID();
        when(mealTaskRegistry.findById(taskId)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> mealTaskService.getTaskStatus(taskId)
        );

        assertEquals("Task not found by Id", exception.getMessage());
    }

    @Test
    void getTaskStatisticsShouldReturnStatisticsSnapshot() {
        MealTaskStatisticsResponse expectedResponse = new MealTaskStatisticsResponse(2, 1, 1, 0, 5L);
        when(mealTaskStatisticsService.getStatistics()).thenReturn(expectedResponse);

        MealTaskStatisticsResponse actualResponse = mealTaskService.getTaskStatistics();

        assertEquals(expectedResponse, actualResponse);
    }

    private MealRequest buildMealRequest() {
        return new MealRequest(
                "Lunch",
                LocalDate.of(2026, 3, 18),
                new NutritionalValueRequest(500.0, 30.0, 15.0, 55.0),
                null,
                List.of()
        );
    }
}
