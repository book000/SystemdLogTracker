package com.tomacheese.systemdlogtracker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SenderTest {
    @BeforeEach
    void setUp(@TempDir Path dir) throws IOException {
        Path path = dir.resolve("config.json");
        Files.writeString(path, "{\"discordWebhookUrl\": \"https://example.invalid/hook\"}");
        Main.setConfig(new Config(path));
        Main.messages.clear();
    }

    @AfterEach
    void tearDown() {
        Main.messages.clear();
    }

    private static Sender senderReturning(double result) {
        return senderDoing(() -> result);
    }

    private static Sender senderDoing(Supplier<Double> action) {
        return new Sender() {
            @Override
            double discordWebhookSendMessage(String content) {
                return action.get();
            }
        };
    }

    @Test
    void rateLimitedMessageStaysAtHeadOfQueue() {
        Main.messages.add("A");

        double after = senderReturning(60).sendNext();

        assertEquals(60.0, after);
        assertEquals(List.of("A"), List.copyOf(Main.messages));
    }

    @Test
    void otherFailureKeepsMessageInQueue() {
        Main.messages.add("A");

        double after = senderReturning(1).sendNext();

        assertEquals(1.0, after);
        assertEquals(List.of("A"), List.copyOf(Main.messages));
    }

    @Test
    void successfulMessageIsRemovedFromQueue() {
        Main.messages.add("A");

        double after = senderReturning(0).sendNext();

        assertEquals(0.0, after);
        assertEquals(List.of(), List.copyOf(Main.messages));
    }

    @Test
    void failureKeepsQueueOrder() {
        Main.messages.add("A");
        Main.messages.add("B");

        senderReturning(1).sendNext();

        assertEquals(List.of("A", "B"), List.copyOf(Main.messages));
    }

    @Test
    void emptyQueueWaitsOneSecond() {
        double after = senderReturning(0).sendNext();

        assertEquals(1.0, after);
        assertEquals(List.of(), List.copyOf(Main.messages));
    }

    @Test
    void retryDelayFallsBackToOneForUnusableValues() {
        for (String header : new String[]{null, "0", "-1", "abc", "NaN", "Infinity"}) {
            assertEquals(1.0, Sender.retryDelay(header), "header=" + header);
        }
    }

    @Test
    void retryDelayUsesPositiveValue() {
        assertEquals(2.5, Sender.retryDelay("2.5"));
    }

    @Test
    void runtimeExceptionKeepsMessageAndDoesNotPropagate() {
        Main.messages.add("A");
        Sender sender = senderDoing(() -> {
            throw new IllegalArgumentException("bad url");
        });

        double after = assertDoesNotThrow(sender::sendNext);

        assertEquals(1.0, after);
        assertEquals(List.of("A"), List.copyOf(Main.messages));
    }
}
