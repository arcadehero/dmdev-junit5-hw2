package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Mock
    private Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void shouldCancelActiveSubscription_whenSubscriptionExists() {
        Subscription someSubscription = Subscription.builder()
                .id(1)
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();
        doReturn(Optional.of(someSubscription)).when(subscriptionDao).findById(any());
        when(subscriptionDao.update(any(Subscription.class))).then(AdditionalAnswers.returnsFirstArg());

        subscriptionService.cancel(someSubscription.getId());

        assertThat(someSubscription.getStatus()).isEqualTo(Status.CANCELED);
        verify(subscriptionDao).update(someSubscription);
    }

    @Test
    void shouldThrowException_whenCancelNotActiveSubscription() {
        Subscription expiredSubscription = Subscription.builder()
                .id(1)
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(Status.EXPIRED)
                .build();
        doReturn(Optional.of(expiredSubscription)).when(subscriptionDao).findById(any());

        SubscriptionException subscriptionException = assertThrows(SubscriptionException.class,
                () -> subscriptionService.cancel(expiredSubscription.getId()));

        String actualMessage = subscriptionException.getMessage();
        assertThat(actualMessage).isEqualTo(String.format("Only active subscription %d can be canceled",
                expiredSubscription.getId()));
    }

    @Test
    void shouldExpireSubscription_whenSubscriptionExists() {
        Subscription activeSubscription = Subscription.builder()
                .id(1)
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .status(Status.ACTIVE)
                .build();
        doReturn(Optional.of(activeSubscription)).when(subscriptionDao).findById(any());
        when(subscriptionDao.update(any(Subscription.class))).then(AdditionalAnswers.returnsFirstArg());

        subscriptionService.expire(activeSubscription.getId());

        assertThat(activeSubscription.getStatus()).isEqualTo(Status.EXPIRED);
        verify(subscriptionDao).update(activeSubscription);
    }

    @Test
    void shouldThrowException_whenExpireAlreadyExpiredSubscription() {
        Subscription expiredSubscription = Subscription.builder()
                .id(1)
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE)
                .expirationDate(Instant.now().minus(30, ChronoUnit.DAYS))
                .status(Status.EXPIRED)
                .build();
        doReturn(Optional.of(expiredSubscription)).when(subscriptionDao).findById(any());

        SubscriptionException subscriptionException = assertThrows(SubscriptionException.class,
                () -> subscriptionService.expire(expiredSubscription.getId()));

        assertThat(subscriptionException.getMessage()).isEqualTo(String.format("Subscription %d has already expired",
                expiredSubscription.getId()));
    }

    @Test
    void shouldCreateSubscription() {
        CreateSubscriptionDto fineSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .build();
        Subscription subscription = Subscription.builder()
                .id(1)
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE)
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .build();
        Subscription updatedSubscription = Subscription.builder()
                .id(1)
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE)
                .status(Status.ACTIVE)
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .build();
        doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(fineSubscriptionDto);
        doReturn(Collections.singletonList(subscription)).when(subscriptionDao).findByUserId(fineSubscriptionDto.getUserId());
        doReturn(updatedSubscription).when(subscriptionDao).upsert(any(Subscription.class));

        Subscription actualSubscription = subscriptionService.upsert(fineSubscriptionDto);

        assertThat(actualSubscription).isEqualTo(subscription);
        assertThat(actualSubscription.getStatus()).isEqualTo(Status.ACTIVE);
        verify(subscriptionDao).upsert(subscription);
    }

    @Test
    void shouldThrowException_WhenDtoInvalid() {
        CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .build();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of(100, "message"));
        doReturn(validationResult).when(createSubscriptionValidator).validate(dto);

        assertThrows(ValidationException.class, () -> subscriptionService.upsert(dto));
        verifyNoInteractions(subscriptionDao, createSubscriptionMapper);
    }
}