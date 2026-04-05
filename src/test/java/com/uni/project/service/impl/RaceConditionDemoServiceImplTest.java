package com.uni.project.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.uni.project.model.dto.response.RaceConditionDemoResponse;
import org.junit.jupiter.api.Test;

class RaceConditionDemoServiceImplTest {
    private final RaceConditionDemoServiceImpl raceConditionDemoService = new RaceConditionDemoServiceImpl();

    @Test
    void runAtomicDemoShouldReachExpectedValue() {
        RaceConditionDemoResponse response = raceConditionDemoService.runAtomicDemo(50, 1000);

        assertEquals("atomic", response.getMode());
        assertEquals(50, response.getThreadCount());
        assertEquals(1000, response.getIncrementsPerThread());
        assertEquals(50000L, response.getExpected());
        assertEquals(50000L, response.getActual());
        assertEquals(0L, response.getLostUpdates());
    }

    @Test
    void runUnsafeDemoShouldReturnConsistentResultSnapshot() {
        RaceConditionDemoResponse response = raceConditionDemoService.runUnsafeDemo(50, 1000);

        assertEquals("unsafe", response.getMode());
        assertEquals(50, response.getThreadCount());
        assertEquals(1000, response.getIncrementsPerThread());
        assertEquals(50000L, response.getExpected());
        assertTrue(response.getActual() <= response.getExpected());
        assertEquals(response.getExpected() - response.getActual(), response.getLostUpdates());
    }
}
