package by.mark;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

public class RPSGame {

    private static final int KEY_LENGTH = 16;
    private static final String SAMPLE_CASE = "Example:\n\tRock Paper Scissors Dragon Fly";

    private final SecureRandom random;
    private final MessageDigest digest;
    private String[] params;

    public RPSGame(String[] params) throws NoSuchAlgorithmException {
        this.params = params;
        this.random = new SecureRandom();
        this.digest = MessageDigest.getInstance("SHA3-256");
    }

    public void start(Scanner in) {
        while (!areValidParams(params)) {
            System.out.print("Game moves: ");
            params = in.nextLine().split("\\s+");
        }

        // key
        byte[] key = new byte[KEY_LENGTH];
        random.nextBytes(key);

        // choice
        // key (bytes) -> to int -> % param.len + 1 <=> (1...param.len)
        int pcChoice = (Math.abs(ByteBuffer.wrap(key).getInt()) % params.length) + 1;
        String hMAC = bytesToHex(digest.digest(key));
        System.out.println("\nHMAC:\n\t" + hMAC);

        showListOfChoices(params);
        String userChoiceTmp = in.nextLine();
        while (!isValidChoice(userChoiceTmp, params.length)) {
            showListOfChoices(params);
            userChoiceTmp = in.nextLine();
        }

        int userChoice = Integer.parseInt(userChoiceTmp);
        if (userChoice == 0) {
            System.out.println("\nBye!");
            return;
        }

        System.out.println("Your move: " + params[userChoice - 1]);
        System.out.println("Computer move: " + params[pcChoice - 1]);

        if (pcChoice == userChoice) {
            System.out.println("Draw! What a great game!");
        } else if (userWins(userChoice, pcChoice, params.length)) {
            System.out.println("Gratz! You are the winner!");
        } else {
            System.out.println("Unfortunately, you've lost!");
        }

        // Fair-play :)
        System.out.println("\nHMAC Key:\n\t" + bytesToHex(key));
    }

    public void run() {
        try(Scanner in = new Scanner(System.in)) {
            start(in);
        }
    }

    private boolean userWins(int userChoice, int pcChoice, int movesCount) {
        int tmpBound = (userChoice + movesCount / 2) % (movesCount);
        int bound = tmpBound == 0 ? movesCount : tmpBound;
        if (bound > userChoice) {
            return (pcChoice > userChoice && pcChoice <= bound);
        } else {
            return (pcChoice > userChoice || pcChoice <= bound);
        }
    }

    private boolean isValidChoice(String s, int paramsCount) {
        if (!StringUtils.isNumeric(s)) {
            System.out.println("Please enter numeric. Try again...");
            System.out.println("Range: [0, " + paramsCount + "]");
            return false;
        }

        int tmp = Integer.parseInt(s);
        if (tmp < 0 || tmp > paramsCount) {
            System.out.println("Out of range. Try again...");
            System.out.println("Range: [0, " + paramsCount + "]");
            return false;
        }
        return true;
    }

    private void showListOfChoices(String[] params) {
        System.out.println("\nYour choice?");
        for (int i = 0; i < params.length; i++)
            System.out.println((i + 1) + ". " + params[i]);
        System.out.println("0. [Exit]");
        System.out.print("Please enter: ");
    }

    private boolean areValidParams(String[] params) {
        if (params == null || params.length == 0) {
            System.out.println("No params found! Try again...");
            System.out.println(SAMPLE_CASE);
            return false;
        }

        if ((params.length & 1) == 0 || params.length < 3) {
            System.out.println("Incorrect number of params! Try again...");
            System.out.println(SAMPLE_CASE);
            return false;
        }

        if (containsSameParams(params)) {
            System.out.println("Identical params found! Try again...");
            System.out.println(SAMPLE_CASE);
            return false;
        }

        return true;
    }

    private boolean containsSameParams(String[] params) {
        for (int i = 0; i < params.length; i++)
            for (int j = 0; j < params.length; j++)
                if (i != j)
                    if (params[i].equals(params[j]))
                        return true;
        return false;
    }

    private String bytesToHex(byte[] hash) {
        // byte = 2 * hex
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            // 0xFF & b needed to cut extra one's in neg num;
            String hex = Integer.toHexString(0xFF & b).toUpperCase();
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
