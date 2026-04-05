package com.uni.project.service;

import com.uni.project.model.dto.response.RaceConditionDemoResponse;

public interface RaceConditionDemoService {
    RaceConditionDemoResponse runUnsafeDemo(int threadCount, int incrementsPerThread);

    RaceConditionDemoResponse runAtomicDemo(int threadCount, int incrementsPerThread);
}
