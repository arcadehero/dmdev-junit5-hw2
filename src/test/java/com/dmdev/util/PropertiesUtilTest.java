package com.dmdev.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PropertiesUtilTest {

    @MethodSource("getPropertyArguments")
    @ParameterizedTest
    void propertiesSuccessfullyDone_ifPropertiesProvided(String key, String expectedResult) {
        String actualResult = PropertiesUtil.get(key);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void shouldReturnNull_ifUnknownKeyProvided() {
        String actualResult = PropertiesUtil.get("123");
        assertThat(actualResult).isNull();
    }

    public static Stream<Arguments> getPropertyArguments() {
        return Stream.of(
                arguments("db.user", "sa"),
                arguments("db.password", "")
        );
    }
}