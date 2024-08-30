package ascii_art;

// Written bt Avinoam Nukrai, Hebrew U 2021 Winter

import java.util.HashSet;


// Solution for the algorithms problems in the exercise
public class Algorithms {


     /**
     * Checks if there is array's length is bigger than 0 or 1 and can hold a double
     * @param numList - a list of numbers (int [])
     * @return true if the array is ok, false eitherwise.
     */
     private static boolean findDuplicateValidate(int[] numList){
        return numList.length != 0 && numList.length != 1;
     }

    /**
     * A list of numbers from 1-n with one number doubled based on the
     * tortoise and the hare algorithm.
     * @param numList - the numbers list (int [])
     * @return the doubled number.
     */
    public static int findDuplicate(int[] numList) {
        if(!findDuplicateValidate(numList)) return -1;
        int tortoiseVal = numList[0];
        int hereVal = numList[numList[0]];
        while (tortoiseVal != hereVal) {
            tortoiseVal = numList[tortoiseVal];
            hereVal = numList[numList[hereVal]];
        }
        hereVal = 0;
        while (tortoiseVal != hereVal) {
            tortoiseVal = numList[tortoiseVal];
            hereVal = numList[hereVal];
        }
        return hereVal;
    }

    /**
     * Calculates the mors code of every word in the word's list and returns the
     * number of unique codes.
     * @param words - a list of words (String[])
     * @return the number of unique mors codes (int)
     */
    public static int uniqueMorseRepresentations(String[] words) {
        int charMin = (int) 'a';
        String[] morsCode = new String[]{".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
                "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-", "..-",
                "...-", ".--", "-..-", "-.--", "--.."};
        final HashSet<String> wordsInMors = new HashSet<>();
        for (String word : words) {
            StringBuilder wordInMors = new StringBuilder();
            for (int ind = 0; ind < word.length(); ind++) {
                String item = morsCode[(int) word.charAt(ind) - charMin];
                wordInMors.append(item);
            }
            wordsInMors.add(wordInMors.toString());
        }
        return wordsInMors.size();
    }
}
