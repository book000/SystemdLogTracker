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
        double after = sendNext();
        CompletableFuture.delayedExecutor((long) (after * 1000), TimeUnit.MILLISECONDS).execute(this);
    }

    /**
     * キューの先頭 1 件を送信する。送信に失敗したメッセージは先頭に戻し、次回の送信で再送する。
     *
     * @return 次の送信までの待ち秒数
     */
    double sendNext() {
        String line = Main.messages.poll();
        if (line == null) {
            return 1;
        }
        try {
            double after = 1;
            switch (Main.getConfig().getSendMode()) {
                case DISCORD_BOT -> after = discordSendMessage("```" + line + "```");
                case DISCORD_WEBHOOK -> after = discordWebhookSendMessage("```" + line + "```");
                case SLACK_WEBHOOK -> after = slackWebhookSendMessage("```" + line + "```");
            }
            // 送信メソッドは成功時のみ 0 を返す。失敗時は順序を保つため先頭に戻す
            if (after > 0) {
                Main.messages.addFirst(line);
            }
            return after;
        } catch (RuntimeException e) {
            System.out.println("[ERROR] sendNext failed: " + e.getMessage());
            Main.messages.addFirst(line);
            return 1;
        }
    }

    /**
     * 429 の待ち時間ヘッダを秒数に変換する。
     * 0 を返すと成功と区別できなくなるため、値が無い、数値でない、有限でない、0 以下の場合は 1 を返す。
     */
    static double retryDelay(String header) {
        if (header == null) {
            return 1;
        }
        try {
            double value = Double.parseDouble(header);
            return Double.isFinite(value) && value > 0 ? value : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
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
                    return retryDelay(response.header("X-RateLimit-Reset-After"));
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
                    return retryDelay(response.header("X-RateLimit-Reset-After"));
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
                    return retryDelay(response.header("Retry-After"));
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
