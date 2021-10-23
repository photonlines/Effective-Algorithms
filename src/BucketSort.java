import java.util.*;

/**
 * Algorithm: Bucket Sort
 * Time Complexity  - Average Case: O(n+k); Worst Case: O(n^2)
 * Space Complexity - Worst Case: O(n)
 * Use Cases:       - When the input is uniformly distributed over a range
 *                  - When the importance of time complexity trumps space complexity
 *                  - Note: the speed of the sort depends on the passed in comparator strategy. If the strategy is
 *                    the default one, our speed will resemble the quick sort speed, as we will only have 2-3 buckets.
 *                    If the comparator strategy returns a varied range of values and the elements are uniformly
 *                    distributed, then our bucket size will increase with improved sorting speed but increased need
 *                    for space.
 */
public class BucketSort {

    private static final int ZERO = 0;

    private BucketSort () { }

    /**
     * Using bucket sort, sort a list of elements using the passed in comparator strategy. The number of buckets is
     * dependent on the range returned by the passed in comparator - the larger the range of values, the more buckets
     * are used to partition our elements.
     */
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        // Validate the passed in parameters
        if (list == null || list.size() <= 1) return;
        if (comparator == null) return;
        // Retrieve a list of buckets to process/sort
        List<List<T>> buckets = getBucketList(list, comparator);
        // Iterate through each bucket and re-insert the sorted bucket elements into our list
        int currentIndex = ZERO;
        for (List<T> bucket : buckets) {
            // Sort the bucket elements using our comparator strategy
            Collections.sort(bucket, comparator);
            // Insert each sorted element into our original list
            for ( T element : bucket ) {
                list.set(currentIndex++, element );
            }
        }
    }

    /**
     * Create and return a list of buckets which are ordered such that the first bucket contains all of the minimum
     * valued elements and each subsequent bucket contains elements in the next range. The number of buckets is directly
     * proportional to the range of values returned by the passed in comparator strategy.
     */
    private static  <T> List<List<T>> getBucketList(List<T> list, Comparator<T> comparator) {
        // Used to hold the maximum and minimum elements in our list.
        T minElement = list.get(ZERO);
        T maxElement = list.get(ZERO);
        // Fetch the minimum and maximum elements from the list
        for (T element : list.subList(1, list.size())) {
           if (comparator.compare(minElement, element) > ZERO) {
               minElement = element;
           }
           if (comparator.compare(maxElement, element) < ZERO) {
               maxElement = element;
           }
        }
        // Declare a new list of lists to hold our buckets
        List<List<T>> buckets = new ArrayList<List<T>>();
        // Calculate the total number of buckets we will need to partition the elements into by making a bucket for
        // each integer value in between the range within [ minimum element ... maximum element ]
        int numberOfBuckets = comparator.compare(maxElement, minElement) + 1;
        for (int i = 0; i < numberOfBuckets; i++) {
           buckets.add(i, new ArrayList<T>());
        }
        // Iterate through each element in the list and calculate and place it in an appropriate bucket
        for (T element : list) {
            int bucketNumber = comparator.compare(element, minElement);
            buckets.get(bucketNumber).add(element);
        }
        // Return the constructed list of buckets containing our partitioned elements
        return buckets;
    }

    private static Random RAND = new Random(42);

    public static List<Integer> createRandomList(int length) {

        List<Integer> randList = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            randList.add(RAND.nextInt(1000000));
        }
        return randList;
    }

    private static  <T extends Comparable<T>> boolean isSorted(List<T> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0) return false;
        }
        return true;
    }

    public static void main(String[] args) throws Throwable {
        int LENGTH = 1000;
        int RUNS   =  14;

        for (int i = 1; i <= RUNS; i++) {
            List<Integer> testList = createRandomList(LENGTH);

            long startTime = System.currentTimeMillis();
            sort(testList, new Comparator<Integer>() {
                public int compare(Integer a, Integer b) {
                    return a - b;
                }
            } );
            long endTime = System.currentTimeMillis();

            System.out.printf("%10d elements  =>  %6d ms \n", LENGTH, endTime - startTime);
            System.out.println(isSorted(testList));

            LENGTH *= 2;
        }
    }
}
