package by.mark;

import java.security.NoSuchAlgorithmException;

public class Main {

    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        try {
            new RPSGame(args).run();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Failed to init MAC");
        }
    }
}
