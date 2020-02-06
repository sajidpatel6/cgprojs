package org.cg.mm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

// Gold 97/238
//class Player {
public class MMV1 {

    private static final int COUNTER_DIVIDER = 50;

    enum Action {
        SKILL, MOVE, WAIT;
    }

    static class Destroyer extends Looter {
        Destroyer(int player, int x, int y) {
            super(LOOTER_DESTROYER, player, x, y);

            mass = DESTROYER_MASS;
            skillCost = DESTROYER_SKILL_COST;
            skillRange = DESTROYER_SKILL_RANGE;
            skillActive = DESTROYER_SKILL_ACTIVE;
        }
    }

    static class DestroyerSkillEffect extends SkillEffect {

        DestroyerSkillEffect(int type, int x, int y, double radius, int duration, int order, Destroyer destroyer) {
            super(type, x, y, radius, duration, order, destroyer);
        }
    }

    static class Doof extends Looter {
        Doof(int player, int x, int y) {
            super(LOOTER_DOOF, player, x, y);

            mass = DOOF_MASS;
            skillCost = DOOF_SKILL_COST;
            skillRange = DOOF_SKILL_RANGE;
            skillActive = DOOF_SKILL_ACTIVE;
        }
    }

    static class DoofSkillEffect extends SkillEffect {

        DoofSkillEffect(int type, int x, int y, double radius, int duration, int order, Doof doof) {
            super(type, x, y, radius, duration, order, doof);
        }
    }

    static abstract class Looter extends Unit {
        int skillCost;
        double skillRange;
        boolean skillActive;

        int player;

        Looter(int type, int player, int x, int y) {
            super(type, x, y);

            this.player = player;

            radius = LOOTER_RADIUS;
        }

        public int getPlayerIndex() {
            return player;
        }
    }

    static class MyPlayer {
        int index;

        MyPlayer(int index) {
            this.index = index;
        }
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        double distance(Point p) {
            return Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
        }

        boolean isInRange(Point p, double range) {
            return p != this && distance(p) <= range;
        }

        public Point midWay(Point wreck, double ratio) {
            double d = distance(wreck);

            if (d < EPSILON) {
                return this;
            }

            int dx = wreck.x - x;
            int dy = wreck.y - y;

            return new Point((int) (this.x + dx * ratio), (int) (this.y + dy * ratio));
        }

    }

    static class Reaper extends Looter {
        Reaper(int player, int x, int y) {
            super(LOOTER_REAPER, player, x, y);

            mass = REAPER_MASS;
            skillCost = REAPER_SKILL_COST;
            skillRange = REAPER_SKILL_RANGE;
            skillActive = REAPER_SKILL_ACTIVE;
        }
    }

    static class ReaperSkillEffect extends SkillEffect {
        ReaperSkillEffect(int type, int x, int y, double radius, int duration, int order, Reaper reaper) {
            super(type, x, y, radius, duration, order, reaper);
        }
    }

    static abstract class SkillEffect extends Point {
        int id;
        int type;
        double radius;
        int duration;
        int order;
        boolean known;
        Looter looter;

        SkillEffect(int type, int x, int y, double radius, int duration, int order, Looter looter) {
            super(x, y);

            this.type = type;
            this.radius = radius;
            this.duration = duration;
            this.looter = looter;
            this.order = order;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SkillEffect other = (SkillEffect) obj;
            if (id != other.id) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            return result;
        }
    }

    static class Tanker extends Unit {
        int water;
        int size;
        MyPlayer player;

        Tanker(int size, MyPlayer player) {
            super(TYPE_TANKER, 0, 0);

            this.player = player;
            this.size = size;

            water = TANKER_EMPTY_WATER;
            mass = TANKER_EMPTY_MASS + TANKER_MASS_BY_WATER * water;
            radius = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * size;
        }
    }

    static abstract class Unit extends Point {
        int type;
        int id;
        int vx;
        int vy;
        double radius;
        double mass;

        Unit(int type, int x, int y) {
            super(x, y);
            this.type = type;
            vx = 0;
            vy = 0;
        }

        double speed() {
            return Math.sqrt(vx * vx + vy * vy);
        }
    }

    static class Wreck extends Point {
        int id = -1;
        double radius;
        int water;
        boolean known;
        MyPlayer player;

        Wreck(int x, int y, int water, double radius) {
            super(x, y);

            this.radius = radius;
            this.water = water;
        }

        public boolean inSkillEffect(List<SkillEffect> skillEffects) {
            if (skillEffects == null) {
                return false;
            }
            for (SkillEffect skillEffect : skillEffects) {
                if (this.isInRange(skillEffect, DOOF_SKILL_RADIUS)) { // Centers almost matching
                    return true;
                }
            }
            return false;
        }

        public boolean isOverlapping(final Wreck other) {
            return Math.hypot(this.x - other.x, this.y - other.y) < this.radius + other.radius - LOOTER_RADIUS / 2;
        }

        public Wreck midWay(Wreck wreck, double ratio) {
            double d = distance(wreck);

            if (d < EPSILON) {
                return this;
            }

            double dx = wreck.x - x;
            double dy = wreck.y - y;

            return new Wreck((int) (this.x + dx * ratio), (int) (this.y + dy * ratio), this.water, this.radius);
        }
    }

    static int LOOTER_COUNT = 3;
    static boolean REAPER_SKILL_ACTIVE = true;
    static boolean DESTROYER_SKILL_ACTIVE = true;
    static boolean DOOF_SKILL_ACTIVE = true;
    static double MAP_RADIUS = 6000.0;
    static int TANKERS_BY_PLAYER;
    static int TANKERS_BY_PLAYER_MIN = 1;
    static int TANKERS_BY_PLAYER_MAX = 3;
    static double WATERTOWN_RADIUS = 3000.0;
    static int TANKER_THRUST = 500;
    static double TANKER_EMPTY_MASS = 2.5;
    static double TANKER_MASS_BY_WATER = 0.5;
    static double TANKER_RADIUS_BASE = 400.0;
    static double TANKER_RADIUS_BY_SIZE = 50.0;
    static int TANKER_EMPTY_WATER = 1;
    static int TANKER_MIN_SIZE = 4;
    static int TANKER_MAX_SIZE = 10;
    static double TANKER_MIN_RADIUS = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * TANKER_MIN_SIZE;
    static double TANKER_MAX_RADIUS = TANKER_RADIUS_BASE + TANKER_RADIUS_BY_SIZE * TANKER_MAX_SIZE;
    static double TANKER_SPAWN_RADIUS = 8000.0;
    static int TANKER_START_THRUST = 2000;
    static int MAX_THRUST = 300;
    static int MAX_RAGE = 300;
    static int WIN_SCORE = 50;
    static double REAPER_MASS = 0.5;
    static int REAPER_SKILL_DURATION = 3;
    static int REAPER_SKILL_COST = 30;
    static int REAPER_SKILL_ORDER = 0;
    static double REAPER_SKILL_RANGE = 2000.0;
    static double REAPER_SKILL_RADIUS = 1000.0;
    static double REAPER_SKILL_MASS_BONUS = 10.0;
    static double DESTROYER_MASS = 1.5;
    static int DESTROYER_SKILL_DURATION = 1;
    static int DESTROYER_SKILL_COST = 60;
    static int DESTROYER_SKILL_ORDER = 2;
    static double DESTROYER_SKILL_RANGE = 2000.0;
    static double DESTROYER_SKILL_RADIUS = 1000.0;
    static int DESTROYER_NITRO_GRENADE_POWER = 1000;
    static double DOOF_MASS = 1.0;
    static double DOOF_RAGE_COEF = 1.0 / 100.0;
    static int DOOF_SKILL_DURATION = 3;
    static int DOOF_SKILL_COST = 30;
    static int DOOF_SKILL_ORDER = 1;
    static double DOOF_SKILL_RANGE = 2000.0;
    static double DOOF_SKILL_RADIUS = 1000.0;
    static double LOOTER_RADIUS = 400.0;
    static int LOOTER_REAPER = 0;
    static int LOOTER_DESTROYER = 1;
    static int LOOTER_DOOF = 2;
    static int TYPE_TANKER = 3;
    static int TYPE_WRECK = 4;
    static int TYPE_REAPER_SKILL_EFFECT = 5;
    static int TYPE_DOOF_SKILL_EFFECT = 6;
    static int TYPE_DESTROYER_SKILL_EFFECT = 7;
    static double EPSILON = 0.00001;

    static double MIN_IMPULSE = 30.0;

    static double IMPULSE_COEFF = 0.5;

    final static Point WATERTOWN = new Point(0, 0);

    private static List<Wreck> addOverlapping(List<Wreck> wrecks) {
        List<Wreck> newWrecks = new ArrayList<>();
        Map<Wreck, List<Wreck>> overlapMap = new HashMap<>();
        for (int i = 0; i < wrecks.size(); i++) {
            final Wreck wreck = wrecks.get(i);
            for (int j = i + 1; j < wrecks.size(); j++) {
                final Wreck other = wrecks.get(j);
                if (wreck.isOverlapping(other)) {
                    List<Wreck> overlaps;
                    if (overlapMap.containsKey(wreck)) {
                        overlaps = overlapMap.get(wreck);
                    } else {
                        overlaps = new ArrayList<>();
                    }

                    overlaps.add(other);
                    overlapMap.put(wreck, overlaps);
                }
            }
        }

        Set<Entry<Wreck, List<Wreck>>> entrySet = overlapMap.entrySet();
        for (Entry<Wreck, List<Wreck>> entry : entrySet) {
            List<Wreck> oWrecks = entry.getValue();
            wrecks.remove(entry.getKey());
            wrecks.removeAll(oWrecks);
            oWrecks.add(entry.getKey());
            newWrecks.add(combinedWreck(oWrecks));
        }
        newWrecks.addAll(wrecks);
        return newWrecks;
    }

    private static Wreck combinedWreck(List<Wreck> oWrecks) {
        int x = 0;
        int y = 0;
        int water = 0;

        for (Wreck oWreck : oWrecks) {
            x += oWreck.x;
            y += oWreck.y;
            water += oWreck.water;
        }

        return new Wreck(x / oWrecks.size(), y / oWrecks.size(), water, oWrecks.get(0).radius);
    }

    private static Wreck findNearestWreck(List<Reaper> reapers, List<Destroyer> destroyers, List<Doof> doofs,
            List<Wreck> wrecks, List<Tanker> tankers, List<SkillEffect> skillEffects) {
        Wreck nearestWreck = null;
        Wreck otherWreck = null;
        double minDistFactor = Double.MAX_VALUE;
        double minDistOther = Double.MAX_VALUE;

        Reaper myReaper = getRUnit(reapers, 0);
        Doof myDoof = getDoUnit(doofs, 0);
        Doof doof1 = getDoUnit(doofs, 1);
        Doof doof2 = getDoUnit(doofs, 2);

        for (Wreck wreck : wrecks) {
            double dist = wreck.distance(myReaper);
            double distE1 = wreck.distance(getRUnit(reapers, 1));
            double distE2 = wreck.distance(getRUnit(reapers, 2));
            double reaperFactor;
            double minval = Math.min(distE1, distE2);
            if (minval < EPSILON) {
                reaperFactor = 100 * dist / EPSILON;
            } else {
                reaperFactor = 100 * dist / minval;
            }

            double doofFactor = 1;
            double minDoofDist = myDoof.distance(wreck);
            minDoofDist = doof1.distance(wreck) < minDoofDist ? doof1.distance(wreck) : minDoofDist;
            minDoofDist = doof2.distance(wreck) < minDoofDist ? doof2.distance(wreck) : minDoofDist;
            doofFactor = 100 / minDoofDist;

            double waterFactor = 100 / wreck.water / wreck.water;

            double distFactor = doofFactor * reaperFactor * waterFactor;

            if (wreck.inSkillEffect(skillEffects) && minDistOther > distFactor) {
                minDistOther = distFactor;
                otherWreck = wreck;
            } else if (minDistFactor > distFactor) {
                nearestWreck = wreck;
                minDistFactor = distFactor;
            }
        }
        if (nearestWreck == null && otherWreck != null) {
            nearestWreck = otherWreck;
        }

        if (nearestWreck != null) {
            System.err.println("Selected Wreck: " + nearestWreck.id);
        }
        return nearestWreck;
    }

    private static Destroyer findNearPotentialDestroyer(Reaper reaper, List<Destroyer> destroyers, List<Tanker> tankers,
            boolean meAttacking) {
        // find destroyer which has tanker nearest to it
        Destroyer target = null;
        double leastDist = Double.MAX_VALUE;
        for (Destroyer destroyer : destroyers) {
            if (destroyer.player != 0 || destroyer.player == 0 && !meAttacking) {
                for (Tanker tanker : tankers) {
                    double distTank = destroyer.distance(tanker) * reaper.distance(tanker);
                    if (distTank < leastDist && tanker.isInRange(WATERTOWN, MAP_RADIUS)) {
                        target = destroyer;
                        leastDist = distTank;
                    }
                }
            }
        }

        return target;
    }

    private static Tanker findTanker(List<Destroyer> destroyers, List<Tanker> tankers, List<Reaper> reapers,
            boolean meAttacking) {
        Tanker selTanker = null;
        double leastDist = Double.MAX_VALUE;
        Reaper myReaper = getRUnit(reapers, 0);
        Reaper reaper1 = getRUnit(reapers, 1);
        Reaper reaper2 = getRUnit(reapers, 2);

        for (Destroyer destroyer : destroyers) {
            if (destroyer.player != 0 || destroyer.player == 0 && !meAttacking) {
                for (Tanker tanker : tankers) {
                    double distFac1 = destroyer.distance(tanker) * myReaper.distance(tanker)
                            / Math.min(reaper1.distance(tanker), reaper2.distance(tanker));

                    if (distFac1 < leastDist && tanker.isInRange(WATERTOWN, MAP_RADIUS)
                            && destroyer.distance(tanker) < REAPER_SKILL_RANGE) {
                        selTanker = tanker;
                        leastDist = distFac1;
                    }
                }
            }
        }

        if (selTanker != null) {
            System.err.println("TankerR : " + selTanker.id);
        }

        return selTanker;
    }

    private static Wreck findWreckNearEnemy(Reaper reaper, List<Wreck> wrecks) {
        Wreck nearestWreck = null;
        double minDistance = Double.MAX_VALUE;

        for (Wreck wreck : wrecks) {
            double dist = wreck.distance(reaper);

            if (minDistance > dist && dist < wreck.radius + reaper.radius) {
                minDistance = dist;
                nearestWreck = wreck;
            }
        }

        return nearestWreck;
    }

    private static Doof getDoUnit(List<Doof> doofs, int index) {
        Doof returnVal = null;

        for (Doof doof : doofs) {
            if (doof.getPlayerIndex() == index) {
                returnVal = doof;
                break;
            }
        }
        return returnVal;
    }

    private static Destroyer getDUnit(List<Destroyer> destroyers, int index) {
        Destroyer returnVal = null;

        for (Destroyer destroyer : destroyers) {
            if (destroyer.getPlayerIndex() == index) {
                returnVal = destroyer;
                break;
            }
        }
        return returnVal;
    }

    private static Reaper getEnemy(List<Reaper> reapers, int i) {
        Reaper reaper = null;

        for (Reaper reaperV : reapers) {
            if (reaperV.getPlayerIndex() == i) {
                reaper = reaperV;
                break;
            }
        }
        return reaper;
    }

    private static Reaper getRUnit(List<Reaper> reapers, int index) {
        Reaper returnVal = null;

        for (Reaper reaper : reapers) {
            if (reaper.getPlayerIndex() == index) {
                returnVal = reaper;
                break;
            }
        }
        return returnVal;
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int doofSkillCountDown = 0;
        int reaperSkillCountDown = 0;
        int counter = 0;
        while (true) {
            counter++;
            int myScore = in.nextInt();
            int enemyScore1 = in.nextInt();
            int enemyScore2 = in.nextInt();
            int myRage = in.nextInt();
            int enemyRage1 = in.nextInt();
            int enemyRage2 = in.nextInt();
            int unitCount = in.nextInt();
            List<Wreck> wrecks = new ArrayList<>();
            List<Tanker> tankers = new ArrayList<>();
            List<Reaper> reapers = new ArrayList<>();
            List<Destroyer> destroyers = new ArrayList<>();
            List<Doof> doofs = new ArrayList<>();
            List<SkillEffect> skillEffects = new ArrayList<>();

            for (int i = 0; i < unitCount; i++) {
                int unitId = in.nextInt();
                int unitType = in.nextInt();
                int player = in.nextInt();
                float mass = in.nextFloat();
                int radius = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int vx = in.nextInt();
                int vy = in.nextInt();
                int extra = in.nextInt();
                int extra2 = in.nextInt();

                MyPlayer playerO = new MyPlayer(player);
                if (unitType == TYPE_WRECK) {
                    Wreck wreck = new Wreck(x, y, extra, radius);
                    wreck.id = unitId;
                    wreck.player = playerO;

                    wrecks.add(wreck);
                } else if (unitType == TYPE_TANKER) {
                    Tanker tanker = new Tanker((int) ((radius - TANKER_RADIUS_BASE) / TANKER_RADIUS_BY_SIZE), playerO);
                    tanker.id = unitId;
                    tanker.mass = mass;
                    tanker.radius = radius;
                    tanker.x = x;
                    tanker.y = y;
                    tanker.vx = vx;
                    tanker.vy = vy;
                    tanker.water = extra;
                    tanker.size = extra2;

                    tankers.add(tanker);
                } else if (unitType == LOOTER_REAPER) {
                    Reaper reaper = new Reaper(player, x, y);
                    reaper.id = unitId;
                    reaper.mass = mass;
                    reaper.radius = radius;
                    reaper.vx = vx;
                    reaper.vy = vy;

                    reapers.add(reaper);
                } else if (unitType == LOOTER_DESTROYER) {
                    Destroyer destroyer = new Destroyer(player, x, y);
                    destroyer.id = unitId;
                    destroyer.mass = mass;
                    destroyer.radius = radius;
                    destroyer.vx = vx;
                    destroyer.vy = vy;

                    destroyers.add(destroyer);
                } else if (unitType == LOOTER_DOOF) {
                    Doof doof = new Doof(player, x, y);
                    doof.id = unitId;
                    doof.mass = mass;
                    doof.radius = radius;
                    doof.vx = vx;
                    doof.vy = vy;

                    doofs.add(doof);
                } else if (unitType == TYPE_DOOF_SKILL_EFFECT) {
                    DoofSkillEffect doofSE = new DoofSkillEffect(TYPE_DOOF_SKILL_EFFECT, x, y, radius,
                            DOOF_SKILL_DURATION, DOOF_SKILL_ORDER, null);
                    skillEffects.add(doofSE);
                } else {
                    System.err.println("4 unitId = " + unitId + " unitType = " + unitType + " player = " + player
                            + " mass = " + mass + " radius = " + radius + " x = " + x + " y = " + y + " vx = " + vx
                            + " vy = " + vy + " extra = " + extra + " extra2 = " + extra2);
                }
            }

            Doof doof = getDoUnit(doofs, 0);
            Doof doof1 = getDoUnit(doofs, 1);
            Doof doof2 = getDoUnit(doofs, 2);

            Reaper reaper = getRUnit(reapers, 0);
            Reaper enemy, otherEnemy;
            boolean isOtherEnemyAhead = false;
            if (enemyScore1 > enemyScore2) {
                enemy = getEnemy(reapers, 1);
                otherEnemy = getEnemy(reapers, 2);
                isOtherEnemyAhead = enemyScore2 > myScore || Math.abs(enemyScore2 - myScore) < 5;
            } else {
                enemy = getEnemy(reapers, 2);
                otherEnemy = getEnemy(reapers, 1);
                isOtherEnemyAhead = enemyScore1 > myScore || Math.abs(enemyScore1 - myScore) < 5;
            }

            Destroyer destroyer = getDUnit(destroyers, 0);

            // Tanker tankerD = findTanker(destroyer, tankers);
            Tanker tankerR = findTanker(destroyers, tankers, reapers, myScore > counter / COUNTER_DIVIDER);

            wrecks = addOverlapping(wrecks);
            Wreck selWreck = findNearestWreck(reapers, destroyers, doofs, wrecks, tankers, skillEffects);
            // Random r = new Random();

            if (selWreck != null) {
                System.out.println(selWreck.x - reaper.vx + " " + (selWreck.y - reaper.vy) + " " + MAX_THRUST);
            } else {
                Destroyer otherDest = findNearPotentialDestroyer(reaper, destroyers, tankers,
                        myScore > counter / COUNTER_DIVIDER// wrecks.size() >= MIN_WRECKS
                );

                if (tankerR != null) {
                    System.out.println(tankerR.x + " " + tankerR.y + " " + MAX_THRUST);
                } else if (otherDest != null) {
                    System.out.println(otherDest.x - reaper.vx + " " + (otherDest.y - reaper.vy) + " " + MAX_THRUST);
                } else {
                    if (doof1.distance(reaper) < doof2.distance(reaper) && enemyRage1 > DOOF_SKILL_COST) {
                        System.out.println(doof1.x - reaper.vx + " " + (doof1.y - reaper.vy) + " " + MAX_THRUST);
                    } else if (enemyRage2 > DOOF_SKILL_COST) {
                        System.out.println(doof2.x - reaper.vx + " " + (doof2.y - reaper.vy) + " " + MAX_THRUST);
                    } else {
                        System.out
                                .println(WATERTOWN.x - reaper.vx + " " + (WATERTOWN.y - reaper.vy) + " " + MAX_THRUST);
                    }
                }
            }

            Wreck enemyWreck = findWreckNearEnemy(enemy, wrecks);
            Wreck otherEnemyWreck = findWreckNearEnemy(otherEnemy, wrecks);

            if (enemyWreck != null && enemyWreck.isInRange(destroyer, DESTROYER_SKILL_RANGE)) {
                System.out
                        .println(enemyWreck.x + destroyer.vx + " " + (enemyWreck.y + destroyer.vy) + " " + MAX_THRUST);
            } else if (enemyWreck != null && enemy.isInRange(enemyWreck, enemyWreck.radius + enemy.radius)) {
                System.out.println(enemy.x + " " + enemy.y + " " + MAX_THRUST);
            } else {
                System.out.println(enemy.x + enemy.vx + " " + (enemy.y + enemy.vy) + " " + MAX_THRUST);
            }

            if (enemyWreck != null && enemyWreck.isInRange(doof, DOOF_SKILL_RANGE)
                    && !reaper.isInRange(enemyWreck, enemyWreck.radius) && myRage > DOOF_SKILL_COST
                    && doofSkillCountDown == 0) {
                System.out.println("SKILL " + enemyWreck.x + " " + enemyWreck.y);
                doofSkillCountDown = DOOF_SKILL_DURATION;
            } else if (otherEnemyWreck != null && otherEnemyWreck.isInRange(doof, DOOF_SKILL_RANGE)
                    && !reaper.isInRange(otherEnemyWreck, otherEnemyWreck.radius) && myRage > DOOF_SKILL_COST
                    && doofSkillCountDown == 0 && isOtherEnemyAhead) {
                System.out.println("SKILL " + otherEnemyWreck.x + " " + otherEnemyWreck.y);
                doofSkillCountDown = DOOF_SKILL_DURATION;
            } else {
                System.out.println(enemy.x + enemy.vx + " " + (enemy.y + enemy.vy) + " " + MAX_THRUST);
            }

            if (doofSkillCountDown > 0) {
                doofSkillCountDown--;
            }

            if (reaperSkillCountDown > 0) {
                reaperSkillCountDown--;
            }
        }
    }
}
