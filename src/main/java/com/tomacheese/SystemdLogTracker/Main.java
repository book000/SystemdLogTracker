package com.tomacheese.SystemdLogTracker;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Config config;
	public static Queue<String> queue = new ArrayDeque<>();
    public static Queue<String> messages = new ArrayDeque<>();

	public static void main(String[] args) {
        Path path = Path.of(args.length >= 1 ? String.join(" ", args) : "config.json");
		config = new Config(path);

        System.out.println("[INFO] Mode: " + config.getSendMode().name());

		List<String> command = new LinkedList<>();
		command.add("journalctl");
		command.addAll(List.of(config.getJournalArguments().split(" ")));
		System.out.println("[INFO] Run command: " + String.join(" ", command));

		new Tracker(command).start();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new MessageOrganizer(), 0, config.getSendInterval());
        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(new Sender());
	}

	public static void SendMessage(String text) {
		if (text.length() >= 2000) {
			System.out.println("[ERROR] line message too long: " + text.length() + "\n" + text);
            return;
		}
        messages.add(text);
	}

    public static Config getConfig() {
        return config;
    }
}
