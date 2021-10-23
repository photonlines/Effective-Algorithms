import java.util.Arrays;

public class SuffixArray {

    // Contains our original word representation
    private final String word;

    private final int wordLength;

    // Contains the representation of our suffix array elements
    private Suffix[] suffixes;

    private static final int NO_ELEMENT_FOUND_INDEX = -1;

    // Compare the two input strings and return a positive integer if the first word
    // is greater than the second word, a negative integer if the second word is grater
    // than the first word, and 0 if the words are equal.
    private int compareStrings(String firstWord, String secondWord) {
        // Get the minimum / smallest length string
        int minLength = Math.min(firstWord.length(), secondWord.length());

        // Iterate over each element and compare the string character by character
        for (int i = 0; i < minLength; i++) {
            if (firstWord.charAt(i) < secondWord.charAt(i)) return -1;
            if (firstWord.charAt(i) > secondWord.charAt(i)) return +1;
        }

        // If our character by character comparison showed no difference, we simply
        // declare the longer length string as the winner
        return firstWord.length() - secondWord.length();

    }

    // Objects used to represent our suffix array elements
    private class Suffix implements Comparable<Suffix> {

        // The getIndex of our suffix element
        private final int index;

        // Simple constructor which initializes our suffix getIndex
        private Suffix( int index ) { this.index = index; }

        // Returns the length of our suffix element
        private int length() { return wordLength - index; }

        // Returns the character at getIndex i
        private char charAt(int i) { return word.charAt(index + i); }

        // Return a positive integer if our suffix is greater than the input suffix, and
        // negative integer it is smaller, and 0 if they are equal
        public int compareTo(Suffix otherSuffix) { return compareStrings(this.toString(), otherSuffix.toString()); }

        // Return the string representation of our suffix
        public String toString() {
            return word.substring(index);
        }

    }

    // Private constructor which initializes our suffix array
    private SuffixArray(String word) {

        // Initialize our word and word length variables
        this.word = word;
        this.wordLength = word.length();

        // Initialize our suffix array elements
        this.suffixes = new Suffix[wordLength];
        for (int i = 0; i < wordLength; i++)
            suffixes[i] = new Suffix(i);

        // Sort our suffix elements
        Arrays.sort(suffixes);

    }

    // Static factory method which returns a new suffix array instance containing a list of
    // suffix elements generated from the passed in word
    public static SuffixArray newInstance( String word ) { return new SuffixArray( word ); }

    // Return the length of our suffix array (ie. our word length)
    public int length() {
        return wordLength;
    }

    // Return the suffix index at i (ie. the ith smallest element)
    public int getIndex(int i) {
        if (i < 0 || i >= suffixes.length) return NO_ELEMENT_FOUND_INDEX;
        else return suffixes[i].index;
    }


    public int getLongestCommonPrefixLength(int i) {
        if (i < 1 || i >= suffixes.length) return NO_ELEMENT_FOUND_INDEX;
        else return getLongestCommonPrefixLength(suffixes[i], suffixes[i-1]);
    }

   // Get the longest common prefix length shared between suffix1 and suffix2
    private int getLongestCommonPrefixLength(Suffix suffix1, Suffix suffix2) {
        int smallestLength = Math.min(suffix1.length(), suffix2.length());
        for (int i = 0; i < smallestLength; i++) {
            if (suffix1.charAt(i) != suffix2.charAt(i)) return i;
        }
        return smallestLength;
    }

    // Get the suffic located at index i
    public String getSuffixAtIndex(int i) {
        if (i < 0 || i >= suffixes.length) return "";
        return suffixes[i].toString();
    }

    // Return the number of suffixes less than the input query
    public int getRank(String query) {

        // Initialize the value of our lower and upper index variables to the start and end
        // of the word
        int lowerIndex = 0;
        int upperIndex = wordLength - 1;

        // Keep cutting our search in half by comparing our query with the middle suffix
        while (lowerIndex <= upperIndex) {
            int middleIndex = lowerIndex + ((upperIndex - lowerIndex) / 2);
            int queryComparison = compareStrings(query, suffixes[middleIndex].toString());
            // If our query is less than the middle element, we adjust the upper index so that our
            // search is continued in the lower half of our suffix array
            if (queryComparison < 0) upperIndex = middleIndex - 1;
                // If our query is larger than the middle element, we adjust the lower index so that our
                // search is continued in the upper half of our suffix array
            else if (queryComparison > 0) lowerIndex = middleIndex + 1;
                // Otherwise, our elements query is equal to the suffix, so we return the middle index
            else return middleIndex;
        }
        // We return our lower element index, which will indicate the number of elements less than
        // our search query
        return lowerIndex;
    }

}