package com.tomacheese.systemdlogtracker;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Sender implements Runnable {
    @Override
    public void run() {
        String line = Main.messages.poll();
        if (line == null) {
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(this);
            return;
        }
        double after = 1;
        switch (Main.getConfig().getSendMode()) {
            case DISCORD_BOT -> after = discordSendMessage("```" + line + "```");
            case DISCORD_WEBHOOK -> after = discordWebhookSendMessage("```" + line + "```");
            case SLACK_WEBHOOK -> after = slackWebhookSendMessage("```" + line + "```");
        }

        CompletableFuture.delayedExecutor((long) (after * 1000), TimeUnit.MILLISECONDS).execute(this);
    }

    double discordSendMessage(String content) {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        String url = "https://discord.com/api/channels/%s/messages".formatted(Main.getConfig().getDiscordChannelId());
        JSONObject object = new JSONObject();
        object.put("content", content);
        RequestBody requestBody = RequestBody.create(object.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .header("Authorization", "Bot " + Main.getConfig().getDiscordToken())
            .post(requestBody)
            .build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return 0;
                }
                if (response.code() == 429) {
                    // Rate limit
                    String rateLimitResetAfterStr = response.header("X-RateLimit-Reset-After");
                    if (rateLimitResetAfterStr == null) {
                        return 1;
                    }
                    try {
                        return Double.parseDouble(rateLimitResetAfterStr);
                    } catch (NumberFormatException e) {
                        return 1;
                    }
                } else {
                    System.out.println("[ERROR] discordSendMessage failed: " + response.code() + " " + Objects.requireNonNull(response.body()).string());
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] discordSendMessage failed: " + e.getMessage());
        }
        return 1;
    }

    double discordWebhookSendMessage(String content) {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        String url = Main.getConfig().getDiscordWebhookUrl();
        JSONObject object = new JSONObject();
        object.put("content", content);
        RequestBody requestBody = RequestBody.create(object.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .post(requestBody)
            .build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return 0;
                }
                if (response.code() == 429) {
                    // Rate limit
                    String rateLimitResetAfterStr = response.header("X-RateLimit-Reset-After");
                    if (rateLimitResetAfterStr == null) {
                        return 1;
                    }
                    try {
                        return Double.parseDouble(rateLimitResetAfterStr);
                    } catch (NumberFormatException e) {
                        return 1;
                    }
                } else {
                    System.out.println("[ERROR] discordWebhookSendMessage failed: " + response.code() + " " + Objects.requireNonNull(response.body()).string());
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] discordWebhookSendMessage failed: " + e.getMessage());
        }
        return 1;
    }

    double slackWebhookSendMessage(String content) {
        OkHttpClient client = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
        String url = Main.getConfig().getSlackWebhookUrl();
        JSONObject object = new JSONObject();
        object.put("text", content);
        RequestBody requestBody = RequestBody.create(object.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .post(requestBody)
            .build();
        try {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return 0;
                }
                if (response.code() == 429) {
                    // Rate limit
                    String retryAfterStr = response.header("Retry-After");
                    if (retryAfterStr == null) {
                        return 1;
                    }
                    try {
                        return Double.parseDouble(retryAfterStr);
                    } catch (NumberFormatException e) {
                        return 1;
                    }
                } else {
                    System.out.println("[ERROR] slackWebhookSendMessage failed: " + response.code() + " " + Objects.requireNonNull(response.body()).string());
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] slackWebhookSendMessage failed: " + e.getMessage());
        }
        return 1;
    }
}
