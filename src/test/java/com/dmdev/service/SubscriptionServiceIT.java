package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionService subscriptionService;
    private SubscriptionDao subscriptionDao;

    @BeforeEach
    void init() {
        subscriptionDao = SubscriptionDao.getInstance();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                Clock.fixed(Instant.now(), ZoneId.systemDefault())
        );
    }

    @Test
    void upsert(){
        CreateSubscriptionDto fineSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .build();

        Subscription actualResult = subscriptionService.upsert(fineSubscriptionDto);

        assertThat(actualResult.getUserId()).isEqualTo(fineSubscriptionDto.getUserId());
    }
}