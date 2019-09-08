package com.tomacheese.SystemdLogTracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.Timer;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.webhook.WebhookClient;
import net.dv8tion.jda.webhook.WebhookClientBuilder;
import net.dv8tion.jda.webhook.WebhookMessage;
import net.dv8tion.jda.webhook.WebhookMessageBuilder;

public class Main {
	private static JDA jda = null;
	private static String webhookUrl = null;
	private static String webhookAvatarUrl = null;
	private static String webhookBotName = "SystemdLogTracker";
	private static TextChannel channel = null;
	public static Queue<String> queue = new ArrayDeque<>();

	public static void main(String[] args) {
		File f = new File("conf.properties");
		if (args.length == 1) {
			f = new File(args[0]);
			System.out.println("Custom config file: " + f.getAbsolutePath());
		}

		Properties props = new Properties();
		if (!f.exists()) {
			props.setProperty("discordToken", "-----");
			props.setProperty("channelId", "-----");
			props.setProperty("webhookUrl", "-----");
			props.setProperty("webhookAvatarUrl", "-----");
			props.setProperty("webhookBotName", "SystemdLogTracker");
			props.setProperty("arguments", "-a -o cat -f -n 0");
			try {
				props.store(new FileOutputStream(f), null);
			} catch (IOException e) {
				System.out.println("An error occurred when generating the config file.");
				System.out.println(e.getClass().getName());
				System.out.println(e.getMessage());
				System.exit(1);
				return;
			}
			System.out.println("A config file has been generated. (" + f.getAbsolutePath() + ")");
			System.out.println("Please edit config file and start again.");
			System.exit(1);
			return;
		}
		try {
			props.load(new FileInputStream(f));
		} catch (IOException e) {
			System.out.println("An error occurred when load the config file.");
			System.out.println(e.getClass().getName());
			System.out.println(e.getMessage());
			System.exit(1);
			return;
		}
		if (props.containsKey("discordToken") && !props.getProperty("discordToken").equals("-----")) {
			if (!props.containsKey("channelId") || props.getProperty("channelId").equals("-----")) {
				System.out.println("An error occurred.");
				System.out.println("channelId is not defined in the config file.");
				System.exit(1);
				return;
			}
			try {
				jda = new JDABuilder(AccountType.BOT)
						.setAudioEnabled(false)
						.setAutoReconnect(true)
						.setBulkDeleteSplittingEnabled(false)
						.setToken(props.getProperty("discordToken").trim())
						.setContextEnabled(false)
						.build().awaitReady();
			} catch (Exception e) {
				System.out.println("An error occurred while logging into Discord.");
				System.out.println(e.getClass().getName());
				System.out.println(e.getMessage());
				System.exit(1);
				return;
			}
			System.out.println("Successfully logged into Discord.");
			System.out.println("Mode: Bot");

			try {
				channel = jda.getTextChannelById(props.getProperty("channelId"));
			} catch (NumberFormatException e) {
				System.out.println("An error occurred.");
				System.out.println("The specified channel ID cannot be parsed.");
				System.out.println(e.getClass().getName());
				System.out.println(e.getMessage());
				System.exit(1);
				return;
			}
			if (channel == null) {
				System.out.println("An error occurred.");
				System.out.println("The specified channel cannot be found.");
				System.exit(1);
				return;
			}
		} else if (props.containsKey("webhookUrl") && !props.getProperty("webhookUrl").equals("-----")) {
			if (!props.containsKey("webhookBotName") || props.getProperty("webhookBotName").equals("-----")) {
				System.out.println("An error occurred.");
				System.out.println("channelId is not defined in the config file.");
				System.exit(1);
				return;
			}
			System.out.println("Mode: Webhook");
			webhookUrl = props.getProperty("webhookUrl");
			webhookBotName = props.getProperty("webhookBotName");
			if (props.containsKey("webhookAvatarUrl") && !props.getProperty("webhookAvatarUrl").equals("-----"))
				webhookAvatarUrl = props.getProperty("webhookAvatarUrl");
		} else {
			System.out.println("An error occurred.");
			System.out.println("discordToken or webhookBotName is not defined in the config file.");
			System.exit(1);
			return;
		}

		List<String> command = new LinkedList<>();
		//command.add("/bin/sh");
		//command.add("-c");
		command.add("journalctl");
		command.addAll(Arrays.asList(props.getProperty("arguments").split(" ")));
		System.out.println("Run command: " + String.join(" ", command));
		new Tracker(command).start();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new Sender(), 0, 1000);
	}

	public static JDA getJda() {
		return jda;
	}

	public static String getWebhookUrl() {
		return webhookUrl;
	}

	public static String getWebhookBotName() {
		return webhookBotName;
	}

	public static TextChannel getChannel() {
		return channel;
	}

	public static void SendMessage(String text) {
		if (text.length() >= 2000) {
			System.out.println("SendError: nagasugi " + text.length() + "\n" + text);
		}
		if (channel != null) {
			channel.sendMessage(text).queue(null, v -> {
				System.out.println("Failed to send message: " + text + " (" + v.getMessage() + ")");
			});
		} else if (webhookUrl != null) {
			WebhookClient client = new WebhookClientBuilder(webhookUrl).build();
			WebhookMessageBuilder builder = new WebhookMessageBuilder()
					.setUsername(webhookBotName)
					.setContent(text);
			if (webhookAvatarUrl != null) {
				builder.setAvatarUrl(webhookAvatarUrl);
			}

			WebhookMessage message = builder.build();
			client.send(message).exceptionally(v -> {
				System.out.println("Failed to send message: " + text + " (" + v.getMessage() + ")");
				return null;
			});
			client.close();
		}
	}
}
