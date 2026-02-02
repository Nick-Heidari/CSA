/**
 * Project 3.1.5
 *
 * The Memory Game shows a random sequence of "memory strings" in a variety of buttons.
 * After watching the memory strings appear in the buttons one at a time, the
 * player recreates the sequence from memory.
 */
import java.util.Arrays;
import java.util.Collections;

public class MemoryGame {
    public static void main(String[] args) {
        int score = 0;
        int rounds = 0;

        // The "memory strings" - an array of at least 4 single character strings.
        String[] memory = {"a", "b", "c", "d"};

        // Create the game and gameboard. Randomized board with 3 buttons.
        MemoryGameGUI game = new MemoryGameGUI();
        game.createBoard(3, true);

        // Play the game until user wants to quit.
        boolean playAgain = true;
        while (playAgain) {
            rounds++;

            // Shuffle the memory strings into a random order
            Collections.shuffle(Arrays.asList(memory));

            // Play one sequence with a 0.5 second delay. Pass the array, not a String.
            String guess = game.playSequence(memory, 0.5);

            // If the guess is not null, determine a match
            if (guess != null) {
                // Cleanup the guess - replace commas and spaces with the empty string.
                guess = guess.replace(",", "").replace(" ", "");

                // Build the correct sequence string from the array to compare
                String randomSeq = String.join("", memory);

                // Check if all elements of the guess match the sequence
                boolean matched = guess.equals(randomSeq);

                // If match, signal a match, otherwise, try again.
                if (matched) {
                    game.matched();
                    score++;
                } else {
                    game.tryAgain();
                }
            }

            // Ask via GUI if the user wants to play another round
            playAgain = game.playAgain();
        }

        // When done playing, show score and end the game.
        game.showScore(score, rounds);
        game.quit();
    }
}
