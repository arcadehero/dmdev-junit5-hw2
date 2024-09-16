package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SubscriptionDaoIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription("alex1", 1));
        Subscription subscription2 = subscriptionDao.insert(getSubscription("alex2", 2));
        Subscription subscription3 = subscriptionDao.insert(getSubscription("alex3", 3));

        List<Subscription> actualSubscriptions = subscriptionDao.findAll();

        assertThat(actualSubscriptions).hasSize(3);
        List<Integer> subscriptionIds = actualSubscriptions.stream().map(Subscription::getId).toList();
        assertThat(subscriptionIds).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription("alex1", 1));

        Optional<Subscription> actualResult = subscriptionDao.findById(subscription1.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription1);
    }

    @Test
    void deleteExistingEntity() {
        Subscription subscription1 = subscriptionDao.insert(getSubscription("alex1", 1));

        boolean actualResult = subscriptionDao.delete(subscription1.getUserId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNonExistingEntity() {
        subscriptionDao.insert(getSubscription("alex1", 1));

        boolean actualResult = subscriptionDao.delete(12345);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        Subscription subscription1 = getSubscription("alex1", 1);
        subscriptionDao.insert(subscription1);
        subscription1.setName("new_alex");

        subscriptionDao.update(subscription1);

        Subscription updatedSubscription = subscriptionDao.findByUserId(1).get(0);
        assertThat(updatedSubscription).isEqualTo(subscription1);
    }

    @Test
    void insert() {
        Subscription subscription1 = getSubscription("alex1", 1);

        Subscription actualResult = subscriptionDao.insert(subscription1);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByUserId() {
        Subscription subscription1 = getSubscription("alex1", 1);

        Subscription actualResult = subscriptionDao.insert(subscription1);

        assertThat(actualResult.getUserId()).isEqualTo(subscription1.getUserId());
    }

    private Subscription getSubscription(String name, Integer userId) {
        return Subscription.builder()
                .userId(userId)
                .name(name)
                .provider(Provider.APPLE)
                .status(Status.ACTIVE)
                .expirationDate(Instant.parse("2024-12-16T10:15:30.00Z"))
                .build();

    }
}