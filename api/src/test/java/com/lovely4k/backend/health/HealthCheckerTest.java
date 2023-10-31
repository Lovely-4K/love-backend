package com.lovely4k.backend.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class HealthCheckerTest {

    @DisplayName("Health Controller의  profile 메서드를 통해 profile 조회를 할 수 있다.")
    @Test
    void real_profile() {
        // given
        String expectedProfile = "prod1";
        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.addActiveProfile(expectedProfile);
        mockEnvironment.addActiveProfile("oauth");
        mockEnvironment.addActiveProfile("real-db");

        HealthChecker healthChecker = new HealthChecker(mockEnvironment);

        // when
        String profile = healthChecker.profile();

        // then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    @DisplayName("real profile이 존재하지 않는다면 첫번째 profile이 조회된다.")
    @Test
    void no_real_profile() {
        // given
        String expectedProfile = "oauth";
        MockEnvironment mockEnvironment = new MockEnvironment();
        mockEnvironment.addActiveProfile(expectedProfile);
        mockEnvironment.addActiveProfile("real-db");

        HealthChecker healthChecker = new HealthChecker(mockEnvironment);

        // when
        String profile = healthChecker.profile();

        // then
        assertThat(profile).isEqualTo(expectedProfile);
    }

    @DisplayName("active profile이 없다면 default가 조회된다.")
    @Test
    void default_profile() {
        // given
        String expectedProfile = "default";
        MockEnvironment mockEnvironment = new MockEnvironment();

        HealthChecker healthChecker = new HealthChecker(mockEnvironment);

        // when
        String profile = healthChecker.profile();

        // then
        assertThat(profile).isEqualTo(expectedProfile);
    }
}
