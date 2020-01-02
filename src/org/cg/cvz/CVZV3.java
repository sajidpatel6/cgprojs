package org.cg.cvz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

// Score:   Rank:  /5494
//class Player {
class CVZV3 {

	static class Ash extends Human {
		Ash(int x, int y) {
			super(-1, x, y);
		}
	}

	static class Human extends Point {
		int id;

		Human(int id, int x, int y) {
			super(x, y);
			this.id = id;
		}
	}

	static class Point {
		int x;
		int y;

		public Point() {
		}

		Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		double distance(Point p) {
			return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
		}

		boolean isInRange(Point p, double range) {
			return p != this && distance(p) <= range;
		}

		public Point midWay(Point point2, double ratio) {
			double d = distance(point2);

			if (d < 0.0001) {
				return this;
			}

			double dx = point2.x - x;
			double dy = point2.y - y;

			return new Point((int) (x + dx * ratio), (int) (y + dy * ratio));
		}
	}

	static class Zombie extends Point {
		int id;
		int xNext;
		int yNext;

		Zombie(int id, int x, int y, int xNext, int yNext) {
			super(x, y);
			this.id = id;
			this.xNext = xNext;
			this.yNext = yNext;
		}
	}

	private static final int ASH_RANGE = 2000;
	private static final int ASH_SPEED = 1000;
	private static final int ZOMBIE_SPEED = 400;

	private static Zombie findNearestZombie(Human human, List<Zombie> zombies) {
		double nearest = Integer.MAX_VALUE;
		Zombie nearestZombie = null;

		for (Zombie zombie : zombies) {
			double factor = zombie.distance(human);
			if (nearest > factor) {
				nearest = factor;
				nearestZombie = zombie;
			}
		}
		return nearestZombie;
	}

	private static Human findNearestHuman(Zombie zombie, List<Human> humans) {
		double nearest = Integer.MAX_VALUE;
		Human nearestHuman = null;

		for (Human human : humans) {
			double factor = human.distance(zombie);
			if (nearest > factor) {
				nearest = factor;
				nearestHuman = human;
			}
		}
		return nearestHuman;
	}

	public static void main(final String args[]) {
		final Scanner in = new Scanner(System.in);

		while (true) {
			final int x = in.nextInt();
			final int y = in.nextInt();
			Ash ash = new Ash(x, y);

			final int humanCount = in.nextInt();
			List<Human> humans = new ArrayList<>();
			for (int i = 0; i < humanCount; i++) {
				final int humanId = in.nextInt();
				final int humanX = in.nextInt();
				final int humanY = in.nextInt();
				humans.add(new Human(humanId, humanX, humanY));
			}
			final int zombieCount = in.nextInt();
			List<Zombie> zombies = new ArrayList<>();
			for (int i = 0; i < zombieCount; i++) {
				final int zombieId = in.nextInt();
				final int zombieX = in.nextInt();
				final int zombieY = in.nextInt();
				final int zombieXNext = in.nextInt();
				final int zombieYNext = in.nextInt();
				zombies.add(new Zombie(zombieId, zombieX, zombieY, zombieXNext, zombieYNext));
			}

			Point targetPoint = findHumansAtRisk(ash, humans, zombies);
//         System.err.println("tx:" + targetPoint.x + " ty:" + targetPoint.y);

			System.out.println(targetPoint.x + " " + targetPoint.y);
			targetPoint = null;
		}
	}

	static class SortHumans implements Comparator<Human> {
		List<Zombie> zombies;

		SortHumans(List<Zombie> zombies) {
			this.zombies = zombies;
		}

		public int compare(Human a, Human b) {
			Zombie az = findNearestZombie(a, zombies);
			int ad = (int) Math.floor(az.distance(a) / ZOMBIE_SPEED);
			Zombie bz = findNearestZombie(b, zombies);
			int bd = (int) Math.floor(bz.distance(b) / ZOMBIE_SPEED);

			if ((ad - bd) == 0) {
				return (az.id - bz.id);
			} else
				return (ad - bd);
		}
	}

	private static Point findHumansAtRisk(Ash ash, List<Human> origHumans, List<Zombie> zombies) {
		List<Human> humans = new ArrayList<Human>();

		for (Zombie zombie : zombies) {
			Human human = findNearestHuman(zombie, origHumans);
			if (!humans.contains(human)) {
				humans.add(human);
			}
		}

		Collections.sort(humans, new SortHumans(zombies));

		Point poi = null;
		for (int i = 0; i < humans.size(); i++) {
			Human human = humans.get(i);
			Zombie zombie = findNearestZombie(human, zombies);
			double stepsZ2H = Math.ceil(zombie.distance(human) / ZOMBIE_SPEED);
			double stepsA2H = Math.floor((ash.distance(human) - ASH_RANGE) / ASH_SPEED);

			System.err.println("Human: " + human.x + ", " + human.y + " Zombie: " + zombie.x + ", " + zombie.y);
			System.err.println("stepsZ2H: " + stepsZ2H + " stepsA2H: " + stepsA2H);

			if (stepsA2H <= stepsZ2H) {
				poi = new Point(zombie.xNext, zombie.yNext);
				break;
			}
		}

		if (poi == null) {
			Zombie zombie = findNearestZombie(ash, zombies);
			poi = new Point(zombie.xNext, zombie.yNext);
		}
		System.err.println("poi: " + poi.x + ", " + poi.y);

		return poi;
	}
}
