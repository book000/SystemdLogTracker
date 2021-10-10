package com.tomacheese.systemdlogtracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private final Path path;
    private SendMode mode;
    private String discordToken;
    private long discordChannelId;
    private String discordWebhookUrl;
    private String slackWebhookUrl;
    private String arguments = "-a -o cat -f -n 0";
    private long sendInterval = 5000;

    public Config(Path path) {
        this.path = path;
        boolean bool = load();
        if(!bool){
            System.exit(1);
        }
    }

    public boolean load() {
        JSONObject object;
        try {
            object = new JSONObject(Files.readString(path));
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to read the configuration file.");
            return false;
        } catch (JSONException e) {
            System.out.println("[ERROR] The JSON of the configuration file is syntax error.");
            return false;
        }
        try {
            if (object.has("discordToken") && object.has("discordChannelId")) {
                // Discord Bot Mode
                mode = SendMode.DISCORD_BOT;
                discordToken = object.getString("discordToken");
                discordChannelId = object.getLong("discordChannelId");
            } else if (object.has("discordWebhookUrl")) {
                // Discord Webhook
                mode = SendMode.DISCORD_WEBHOOK;
                discordWebhookUrl = object.getString("discordWebhookUrl");
            } else if (object.has("slackWebhookUrl")) {
                // Slack Webhook
                mode = SendMode.SLACK_WEBHOOK;
                slackWebhookUrl = object.getString("slackWebhookUrl");
            } else {
                System.out.println("[ERROR] The service is not supported or the required settings were not found.");
                return false;
            }

            arguments = object.has("arguments") ? object.getString("arguments") : arguments;
            sendInterval = object.has("sendInterval") ? object.getLong("sendInterval") : sendInterval;
        } catch (JSONException e) {
            System.out.println("[ERROR] The setting information could not be read normally.");
            return false;
        }
        return true;
    }

    public SendMode getSendMode() {
        return mode;
    }

    public String getDiscordToken() {
        return discordToken;
    }

    public long getDiscordChannelId() {
        return discordChannelId;
    }

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public String getSlackWebhookUrl() {
        return slackWebhookUrl;
    }

    public String getJournalArguments() {
        return arguments;
    }

    public long getSendInterval() {
        return sendInterval;
    }

    public enum SendMode {
        DISCORD_BOT,
        DISCORD_WEBHOOK,
        SLACK_WEBHOOK
    }
}
