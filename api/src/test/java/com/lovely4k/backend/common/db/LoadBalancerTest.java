package com.lovely4k.backend.common.db;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;

class LoadBalancerTest {

    @DisplayName("데이터 베이스의 가중치에 따라 해당 가중치 비율에 맞는 db를 반환한다.")
    @Test
    void weightRoundRobin() {
        int totalCalls = 10000; // 총 호출 횟수
        Map<DatabaseType, Long> typeCounts = new EnumMap<>(DatabaseType.class);

        // 각 DatabaseType에 대해 호출 횟수를 카운트
        for (int i = 0; i < totalCalls; i++) {
            DatabaseType selectedType = LoadBalancer.weightRoundRobin();
            typeCounts.put(selectedType, typeCounts.getOrDefault(selectedType, 0L) + 1);
        }

        // 모든 DatabaseType에 대해 기대되는 비율과 실제 비율을 검증
        int totalWeight = Arrays.stream(DatabaseType.values()).mapToInt(DatabaseType::getWeight).sum();

        for (DatabaseType type : DatabaseType.values()) {
            double expectedRatio = (double) type.getWeight() / totalWeight;
            double actualRatio = (double) typeCounts.getOrDefault(type, 0L) / totalCalls;
            assertThat(actualRatio)
                .as(type.name() + " 선택 비율 검증")
                .isCloseTo(expectedRatio, within(0.05));
        }
    }

}