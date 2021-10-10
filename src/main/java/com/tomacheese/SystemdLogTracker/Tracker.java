package com.tomacheese.SystemdLogTracker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Tracker extends Thread {
	List<String> command;

	public Tracker(List<String> command) {
		this.command = command;
	}

	@Override
	public void run() {
		Process p = null;
		try {
            System.out.println(command);
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);

			System.out.println("Tracker start.");
			p = pb.start();

			InputStream is = p.getInputStream();
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader buff = new BufferedReader(reader);

			while (p.isAlive()) {
				if (!buff.ready()) {
					continue;
				}
				String line = buff.readLine();
				if (line == null) {
					continue;
				}
				System.out.println(line);
				Main.queue.add(line);
			}
			new MessageOrganizer().run();
			int exitCode = p.exitValue();
			System.out.println("Tracker exited. ExitCode: " + exitCode);
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Tracking failed: " + e.getMessage());
			e.printStackTrace();
			if (p != null && p.isAlive()) {
				p.destroy();
			}
			System.exit(1);
		}
	}
}
