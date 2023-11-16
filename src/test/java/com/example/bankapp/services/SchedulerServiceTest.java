package com.example.bankapp.services;

import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
class SchedulerServiceTest {

    @SpyBean
    private SchedulerService schedulerService;

    @Test
    void processScheduledTasksTest() {
        await().atMost(Duration.FIVE_SECONDS)
                .untilAsserted(() -> verify(schedulerService,times(1)).processScheduledTasks());

    }
}
