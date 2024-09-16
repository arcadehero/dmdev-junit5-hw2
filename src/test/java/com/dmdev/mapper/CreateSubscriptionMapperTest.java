package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

    @Test
    void shouldCreate_ifValidDto() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .name("Some name")
                .userId(123)
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .provider(Provider.APPLE.name())
                .build();
        Subscription expectedSubscription = Subscription.builder()
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE)
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .status(Status.ACTIVE)
                .build();

        Subscription actualSubscription = mapper.map(dto);

        assertThat(actualSubscription).isEqualTo(expectedSubscription);
    }
}