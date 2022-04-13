package com.tomacheese.systemdlogtracker;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {
    private static Config config;
    public static final Queue<String> queue = new ArrayDeque<>();
    public static final Queue<String> messages = new ArrayDeque<>();

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

    public static boolean containsItemFromArray(String inputString, String[] items) {
        return Arrays.stream(items).anyMatch(inputString::contains);
    }

    public static void SendMessage(String text) {
        if (text.length() >= 2000) {
            System.out.println("[ERROR] line message too long: " + text.length() + "\n" + text);
            return;
        }
        
        if (containsItemFromArray(text, config.getFilteredWords())) {
            System.out.println("Message matched filtered word, ignoring");
            return;
        }
        messages.add(text);
    }

    public static Config getConfig() {
        return config;
    }
}
