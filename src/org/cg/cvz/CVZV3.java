package org.cg.cvz;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Score:  Rank: /5491
//class Player {
class CVZV3 {

    static class Ash extends Human {
        Ash(int x, int y) {
            super(x, y);
        }
    }

    static class Human extends Point {
        Human(int x, int y) {
            super(x, y);
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
        int xNext;
        int yNext;

        Zombie(int x, int y, int xNext, int yNext) {
            super(x, y);
            this.xNext = xNext;
            this.yNext = yNext;
        }
    }

    private static final int ASH_RANGE = 1999;
    private static final int ASH_SPEED = 999;
    private static final int ZOMBIE_SPEED = 401;

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

    public static void main(final String args[]) {
        final Scanner in = new Scanner(System.in);

        while (true) {
            final int x = in.nextInt();
            final int y = in.nextInt();
            Ash ash = new Ash(x, y);

            final int humanCount = in.nextInt();
//         final boolean target = false;
            List<Human> humans = new ArrayList<>();
            for (int i = 0; i < humanCount; i++) {
                final int humanId = in.nextInt();
                final int humanX = in.nextInt();
                final int humanY = in.nextInt();
                humans.add(new Human(humanX, humanY));
                // System.err.println("humanId:" + humanId + " x:" + humanX + "
                // y:" + humanY);
            }
            final int zombieCount = in.nextInt();
            List<Zombie> zombies = new ArrayList<>();
            for (int i = 0; i < zombieCount; i++) {
                final int zombieId = in.nextInt();
                final int zombieX = in.nextInt();
                final int zombieY = in.nextInt();
                final int zombieXNext = in.nextInt();
                final int zombieYNext = in.nextInt();
                zombies.add(new Zombie(zombieX, zombieY, zombieXNext, zombieYNext));
                // System.err.println("zombieId:" + zombieId + " x:" + zombieX +
                // " y:" + zombieY + " Xnext:" + zombieXNext
                // + " YNext:" + zombieYNext);
            }

            Point targetPoint = findTargetPoint(ash, humans, zombies);
//         System.err.println("tx:" + targetPoint.x + " ty:" + targetPoint.y);

            System.out.println(targetPoint.x + " " + targetPoint.y);
            targetPoint = null;
        }
    }

    private static Point findTargetPoint(Ash ash, List<Human> humans, List<Zombie> zombies) {
//        double nearest = Integer.MAX_VALUE;
        double farthest = Integer.MIN_VALUE;
        Point poi = null;

        for (Human human : humans) {
            Zombie zombie = findNearestZombie(human, zombies);
            double stepsZ2H = Math.ceil(zombie.distance(human) / ZOMBIE_SPEED);
            double stepsA2H = Math.floor(ash.distance(human) / ASH_SPEED);

            if (poi == null) {
                poi = new Point(human.x, human.y);
            }

            if (farthest < (stepsZ2H - stepsA2H) && (stepsA2H <= stepsZ2H)) {
                farthest = (stepsZ2H - stepsA2H);
//            if (nearest > (stepsZ2H - stepsA2H) && (stepsA2H <= stepsZ2H)) {
//                nearest = (stepsZ2H - stepsA2H);
                poi = new Point(zombie.x, zombie.y);
//				System.err.println("nearestPoint: " + poi.x + ", " + poi.y);
            }
        }
        return poi;
    }
}
