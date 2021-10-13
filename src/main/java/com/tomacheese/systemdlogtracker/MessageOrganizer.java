package com.tomacheese.systemdlogtracker;

import java.util.LinkedList;
import java.util.TimerTask;

public class MessageOrganizer extends TimerTask {
    @Override
    public void run() {
        if (Main.queue.size() > 0) {
            LinkedList<String> messages = new LinkedList<>();
            while (true) {
                String line = Main.queue.poll();
                if (line == null)
                    break;
                if (messages.size() > 0 && (String.join("\n", messages).length() + line.length()) >= 1900) {
                    Main.SendMessage(String.join("\n", messages));
                    messages.clear();
                }
                if (line.length() >= 1900) {
                    if (messages.size() > 0) {
                        Main.SendMessage(String.join("\n", messages));
                        messages.clear();
                    }
                    int count = line.length() / 1900;
                    for (int i = 0; i <= count; i++) {
                        int end = Math.min((i + 1) * 1900, line.length());
                        String text = line.substring(i * 1900, end);
                        Main.SendMessage(text);
                    }
                }
                messages.add(line);
            }
            if (messages.size() > 0) {
                Main.SendMessage(String.join("\n", messages));
            }
        }
    }
}
