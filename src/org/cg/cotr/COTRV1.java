package org.cg.cotr;

import java.util.Scanner;
import java.util.Stack;


public class COTRV1 {
//class Player {

   static int invokeLeftCount = 0;
   static int invokeRightCount = 0;

   static int checkCurrentRune(char c, char runeState, String allValues, int moves,
         StringBuffer currSB) {
      int avLen = allValues.length();
      final int cIdx = allValues.indexOf(c);
      final int runeIdx = allValues.indexOf(runeState);
      final int mid = avLen / 2;
      int diff = cIdx - runeIdx;
      if (diff > 0 && diff < mid || diff < 0 && avLen + diff < mid) {
         if (diff < 0) {
            diff = diff + avLen;
            diff = Math.abs(diff);
         }

         for (int i = 0; i < diff; i++) {
            currSB.append("+");
         }
         // System.out.println("runeS: '" + runeState + "' runeI: " + runeIdx + " --> C:
         // " + c + " cIdx : " + cIdx
         // + " increment " + diff + " " + currSB.toString());
         return diff + moves;
      } else {
         if (diff > 0) {
            diff -= avLen;
            diff = Math.abs(diff);
         }
         if (diff < 0) {
            diff = Math.abs(diff);
         }

         for (int i = 0; i < diff; i++) {
            currSB.append("-");
         }

         // System.out.println("runeS: '" + runeState + "' runeI: " + runeIdx + " --> C:
         // " + c + " cIdx : " + cIdx
         // + " decrement " + diff + " " + currSB.toString());
         return diff + moves;
      }
   }

   private static int checkLeftRunes(char c, StringBuffer runeStates, int runeIndex,
         String allValues, int moves, StringBuffer leftSB) {
      // System.out.println("going left ");
      invokeLeftCount++;
      final StringBuffer newCurrSB = new StringBuffer();
      newCurrSB.append(leftSB.toString());
      final int currSteps = checkCurrentRune(c, runeStates.charAt(runeIndex), allValues, moves,
            newCurrSB);

      int leftIndex = runeIndex - 1;
      if (leftIndex < 0) {
         leftIndex = runeStates.length() - 1;
      }
      int leftSteps = Integer.MAX_VALUE;
      final StringBuffer newleftSB = new StringBuffer();
      newleftSB.append(leftSB.toString());

      // System.out.println("invokeLeftCount : " + invokeLeftCount + " moves: " +
      // moves);
      if (invokeLeftCount < 14) {
         leftSteps = checkLeftRunes(c, runeStates, leftIndex, allValues, moves + 1,
               newleftSB.append("<"));
      }
      if (currSteps < leftSteps) {
         leftSB.setLength(0);
         leftSB.append(newCurrSB);
         return currSteps;
      } else {
         leftSB.setLength(0);
         leftSB.append(newleftSB);
         return leftSteps;
      }
   }

   private static int checkRightRunes(char c, StringBuffer runeStates, int runeIndex,
         String allValues, int moves, StringBuffer rightSB) {
      // System.out.println("going right ");
      invokeRightCount++;
      final StringBuffer newCurrSB = new StringBuffer();
      newCurrSB.append(rightSB.toString());
      final int currSteps = checkCurrentRune(c, runeStates.charAt(runeIndex), allValues, moves,
            newCurrSB);

      int rightIndex = runeIndex + 1;
      if (rightIndex > runeStates.length() - 1) {
         rightIndex = 0;
      }
      int rightSteps = Integer.MAX_VALUE;
      final StringBuffer newRightSB = new StringBuffer();
      newRightSB.append(rightSB.toString());

      // System.out.println("invokeRightCount : " + invokeRightCount + " moves: " +
      // moves);
      if (invokeRightCount < 14) {
         rightSteps = checkRightRunes(c, runeStates, rightIndex, allValues, moves + 1,
               newRightSB.append(">"));
      }
      if (currSteps < rightSteps) {
         rightSB.setLength(0);
         rightSB.append(newCurrSB);
         return currSteps;
      } else {
         rightSB.setLength(0);
         rightSB.append(newRightSB);
         return rightSteps;
      }
   }

   private static int findMostOptimalCommands(char c, StringBuffer runeStates, int runeIndexIn,
         StringBuffer sb2, String allValues) {

      // final int runeIndex = i % runeStates.length();
      // final char runeState = runeStates.charAt(runeIndex);
      // System.out.println("runeState[" + runeIndex + "] : " + runeState);

      final StringBuffer currSB = new StringBuffer();
      final StringBuffer leftSB = new StringBuffer();
      final StringBuffer rightSB = new StringBuffer();
      final int currSteps = checkCurrentRune(c, runeStates.charAt(runeIndexIn), allValues, 0,
            currSB);

      int leftSteps = Integer.MAX_VALUE;
      int leftIndex = runeIndexIn - 1;
      if (leftIndex < 0) {
         leftIndex = runeStates.length() - 1;
      }
      leftSteps = checkLeftRunes(c, runeStates, leftIndex, allValues, 1, leftSB.append("<"));

      int rightSteps = Integer.MAX_VALUE;
      int rightIndex = runeIndexIn + 1;
      if (rightIndex > runeStates.length() - 1) {
         rightIndex = 0;
      }
      if (rightIndex < runeStates.length()) {
         rightSteps = checkRightRunes(c, runeStates, rightIndex, allValues, 1, rightSB.append(">"));
      }

      // System.out.println("c: '" + c + "' runeState: " +
      // runeStates.charAt(runeIndexIn));
      int runeIndexOut;
      if (currSteps < leftSteps && currSteps < rightSteps) {
         // System.out.println("currSteps: " + currSteps + " currSB: " + currSB);
         sb2.append(currSB);
         runeIndexOut = runeIndexIn;
         // System.out.println("Rune Index unchanged at " + runeIndexOut);
      } else if (leftSteps < rightSteps) {
         // System.out.println("leftSteps: " + leftSteps + " leftSB: " + leftSB);
         sb2.append(leftSB);
         final String leftStr = leftSB.toString();
         final int count = leftStr.length() - leftStr.replace("<", "").length();
         runeIndexOut = runeIndexIn - count;
         while (runeIndexOut < 0) {
            runeIndexOut = runeStates.length() + runeIndexOut;
         }
         // System.out.println("Rune Index decreased to " + runeIndexOut);
      } else {
         // System.out.println("rightSteps: " + rightSteps + " rightSB: " + rightSB);
         sb2.append(rightSB);
         final String rightStr = rightSB.toString();
         final int count = rightStr.length() - rightStr.replace(">", "").length();
         runeIndexOut = runeIndexIn + count;
         while (runeIndexOut >= runeStates.length()) {
            runeIndexOut = runeIndexOut - runeStates.length();
         }
         // System.out.println("Rune Index increased to " + runeIndexOut);
      }

      // System.out.println("Rune States : " + runeStates);
      runeStates.setCharAt(runeIndexOut, c);
      // System.out.println("Rune States : " + runeStates);
      return runeIndexOut;
   }

   public static void main(String args[]) {
      final Scanner in = new Scanner(System.in);
      String magicPhrase = in.nextLine();
      // final String magicPhrase = "THIS IS A TEST SENTENCE LONG ENOUGH TO HAVE
      // CROSSED ALL THE RUNES HA HA HA";
      // final String magicPhrase = "GUZ MUG ";
      // String magicPhrase = "GUZ MUG ZOG GUMMOG ZUMGUM ZUM MOZMOZ MOG ZOGMOG
      // GUZMUGGUM";
      // String magicPhrase = "AHTAI";

      final StringBuffer sb = new StringBuffer();
      final StringBuffer sb2 = new StringBuffer();
      final StringBuffer runeStates = new StringBuffer("                              ");

      // Write an action using System.out.println()
      // To debug: System.err.println("Debug messages...");
      final String allValues = new String(" ABCDEFGHIJKLMNOPQRSTUVWXYZ");
      int runeIndex = 0;
      for (int i = 0; i < magicPhrase.length(); i++) {
         // if (i > 0) {
         // sb.append(">");
         // }
         sb2.setLength(0);

         final char c = magicPhrase.toUpperCase().charAt(i);

         // sb2 =
         runeIndex = findMostOptimalCommands(c, runeStates, runeIndex, sb2, allValues);
         // System.out.println("###########################################################################");
         sb.append(sb2.append(".").toString());
         // runeStates.setCharAt(runeIndex, c);
         // System.out.println("runeStates char : " + c + " at pos : " + runeIndex + "
         // Actually it is "
         // + runeStates.charAt(runeIndex));
      }

      System.out.println(sb.toString());
      System.out.println(magicPhrase);
      testString(sb.toString());
   }

   private static void testString(String string) {
      Stack<Integer> stack = new Stack<>();
      final StringBuffer runeStates = new StringBuffer("                              ");
      int currentPos = 0;

      for (int i = 0; i < string.length(); i++) {
         char currCmd = string.charAt(i);
         switch (currCmd) {
         case '>':
            currentPos++;
            if (currentPos == runeStates.length()) {
               currentPos = 0;
            }
            break;
         case '<':
            currentPos--;
            if (currentPos < 0) {
               currentPos = runeStates.length() - 1;
            }
            break;
         case '+':
            if (runeStates.charAt(currentPos) == ' ') {
               runeStates.setCharAt(currentPos, 'A');
            } else {
               char newChar = (char) (runeStates.charAt(currentPos) + 1);
               if (newChar > 'Z') {
                  newChar = ' ';
               }
               runeStates.setCharAt(currentPos, newChar);
            }
            break;
         case '-':
            if (runeStates.charAt(currentPos) == ' ') {
               runeStates.setCharAt(currentPos, 'Z');
            } else {
               char newChar = (char) (runeStates.charAt(currentPos) - 1);
               if (newChar < 'A') {
                  newChar = ' ';
               }
               runeStates.setCharAt(currentPos, newChar);
            }
            break;
         case '[':
            stack.push(i);
            System.out.print(runeStates.charAt(currentPos));
            break;
         case ']':
            System.out.print(runeStates.charAt(currentPos));
            break;
         case '.':
            System.out.print(runeStates.charAt(currentPos));
         }
      }

   }
}
