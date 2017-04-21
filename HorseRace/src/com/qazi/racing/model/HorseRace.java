package com.qazi.racing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HorseRace {
	static final int FINISH_LINE = 75;
	private List<Horse> horses = new ArrayList<Horse>();
	private ExecutorService exec = Executors.newCachedThreadPool();
	private CyclicBarrier barrier;

	public HorseRace(int nHorses, final int pause) {
		StringBuilder builder = new StringBuilder();
		for (int x = 0; x < FINISH_LINE; x++) {
			builder.append("=");
		}

		barrier = new CyclicBarrier(nHorses, () -> {
			System.out.println(builder.toString());
			for (Horse horse : horses) {
				System.out.println(horse.tracks());
			}
			for (Horse horse : horses)
				if (horse.getStrides() >= FINISH_LINE) {
					System.out.println(horse + "won!");
					exec.shutdownNow();
					printRunnerUp();
					return;
				}
			try {
				TimeUnit.MILLISECONDS.sleep(pause);
			} catch (InterruptedException e) {
				System.out.println("barrier-action sleep interrupted");
			}

		});

		for (int i = 0; i < nHorses; i++) {
			Horse horse = new Horse(barrier);
			horses.add(horse);
			exec.execute(horse);
		}

	}

	public void printRunnerUp() {
		horses.sort((Horse h1, Horse h2) -> h2.getStrides() - h1.getStrides());
		int previous = horses.get(0).getStrides();
		int position = 1;
		for (Horse horse : horses) {
			if ((horse.getStrides() < previous))
				position += 1;
			System.out.println(
					horse + ", Score:" + horse.getStrides() + ", position:" + position + getOrdinalFor(position));
			previous = horse.getStrides();

		}

	}

	public static String getOrdinalFor(int value) {
		int tenRemainder = value % 10;
		switch (tenRemainder) {
		case 1:
			return "st";
		case 2:
			return "nd";
		case 3:
			return "rd";
		default:
			return "th";
		}
	}

	public static void main(String[] args) {
		System.out.println("Starting Race");
		int nHorses = 10;
		int pause = 200;
		if (args.length > 0) { // Optional argument
			int n = new Integer(args[0]);
			nHorses = n > 0 ? n : nHorses;
		}
		if (args.length > 1) { // Optional argument
			int p = new Integer(args[1]);
			pause = p > -1 ? p : pause;
		}
		new HorseRace(nHorses, pause);

	}

}
