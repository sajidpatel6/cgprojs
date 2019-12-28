package org.cg.cvz;

//public class CVZV1 {
//
//}

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class CVZV1 {

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

   private static final int ASH_RANGE = 1990;

   private static Point findFarthestHuman(List<Zombie> zombies, List<Human> humans) {
      double farthest = Integer.MIN_VALUE;
      Point farthestPoint = null;

      for (Zombie zombie : zombies) {
         double nearest = Integer.MAX_VALUE;
         Human humanN = null;
         for (Human human : humans) {
            if (nearest > zombie.distance(human)) {
               nearest = zombie.distance(human);
               humanN = human;
            }
         }
         if (farthest < nearest) {
            farthest = nearest;
            farthestPoint = new Point(humanN.x, humanN.y);
         }
      }
      return farthestPoint;
   }

   private static Point findNearestHuman(Ash ash, List<Human> humans, List<Zombie> zombies) {
      double nearest = Integer.MAX_VALUE;
      Point nearestPoint = null;

      for (Human human : humans) {
         Zombie zombie = findNearestZombie(human, zombies);
         double factor = ash.distance(human) / zombie.distance(human);
         if (nearest > factor) {
            nearest = factor;
            nearestPoint = new Point(human.x, human.y);
         }
      }
      return nearestPoint;
   }

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

   private static Point findNearestZombies(Ash ash, Point targetPoint, List<Zombie> zombies) {
      double nearest = Integer.MAX_VALUE;
      Point nearestPoint = null;
      if (targetPoint != null) {
         for (Zombie zombie : zombies) {
            Point newPoint = targetPoint.midWay(zombie, ASH_RANGE / targetPoint.distance(zombie));
            System.err.println("dist: " + targetPoint.distance(zombie));
            System.err.println("new: " + newPoint.x + " " + newPoint.y);

            if (nearest > newPoint.distance(ash)) {
               nearest = newPoint.distance(ash);
               nearestPoint = newPoint;
            }
         }
      }
      System.err.println("near: " + nearestPoint.x + " " + nearestPoint.y);
      return nearestPoint;
   }

   public static void main(final String args[]) {
      final Scanner in = new Scanner(System.in);

      while (true) {
         final int x = in.nextInt();
         final int y = in.nextInt();
         Ash ash = new Ash(x, y);

         final int humanCount = in.nextInt();
         final boolean target = false;
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

         Point targetPoint1 = findNearestHuman(ash, humans, zombies);
         // // Point targetPoint = findFarthestHuman(zombies, humans);
         // System.err.println("tx:" + targetPoint1.x + " ty:" + targetPoint1.y);
          Point targetPoint = findNearestZombies(ash, targetPoint1, zombies);
        //  Point targetPoint = findNearestZombie(ash, zombies);
         if (targetPoint == null) {
            targetPoint = targetPoint1;
         }
         System.err.println("tx:" + targetPoint.x + " ty:" + targetPoint.y);

         System.out.println(targetPoint.x + " " + targetPoint.y);
         targetPoint = null;
      }
   }
}