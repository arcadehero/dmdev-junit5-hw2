package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    void dtoValid() {
        var validDto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(validDto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void userIdNull() {
        var nullIdDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Some name")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(nullIdDto);
        Error error = actualResult.getErrors().get(0);

        assertThat(error.getCode()).isEqualTo(100);
        assertThat(error.getMessage()).isEqualTo("userId is invalid");
    }

    @Test
    void nameBlank() {
        var blankNameDto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now())
                .build();

        ValidationResult actualResult = validator.validate(blankNameDto);
        Error error = actualResult.getErrors().get(0);

        assertThat(error.getCode()).isEqualTo(101);
        assertThat(error.getMessage()).isEqualTo("name is invalid");
    }


    @Test
    void providerNull() {
        var providerNullDto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("Some name")
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(providerNullDto);
        Error error = actualResult.getErrors().get(0);

        assertThat(error.getCode()).isEqualTo(102);
        assertThat(error.getMessage()).isEqualTo("provider is invalid");
    }

    @Test
    void expirationsDateBeforeNow() {
        var expirationDateBeforeDto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().minus(1, ChronoUnit.MINUTES))
                .build();

        ValidationResult actualResult = validator.validate(expirationDateBeforeDto);
        Error error = actualResult.getErrors().get(0);

        assertThat(error.getCode()).isEqualTo(103);
        assertThat(error.getMessage()).isEqualTo("expirationDate is invalid");
    }

    @Test
    void expirationsDateNull() {
        var expirationDateNullDto = CreateSubscriptionDto.builder()
                .userId(123)
                .name("Some name")
                .provider(Provider.APPLE.name())
                .expirationDate(null)
                .build();

        ValidationResult actualResult = validator.validate(expirationDateNullDto);
        Error error = actualResult.getErrors().get(0);

        assertThat(error.getCode()).isEqualTo(103);
        assertThat(error.getMessage()).isEqualTo("expirationDate is invalid");
    }

    @Test
    void badNameId() {
        var badDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(badDto);
        List<Integer> errorCodes = actualResult.getErrors().stream().map(Error::getCode).toList();

        assertThat(errorCodes).contains(100, 101);
    }
}
