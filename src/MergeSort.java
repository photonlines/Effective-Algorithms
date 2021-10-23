import java.util.*;
import java.util.concurrent.*;

/**
 * Algorithm: Marge Sort
 * Time Complexity  - Average Case: O(n log(n)); Worst Case: O(n log(n))
 * Space Complexity - Worst Case: O(n)
 * Use Cases:       - In instances where data to be sorted can only be efficiently accessed sequentially
 *                  - When you want to take advantage of cpu parallelization; divide and conquer algorithm
 *                  - When sorting linked lists
 */
public class MergeSort {

    // Parameter option which can be used to specify whether to use thread parallelization
    public enum Parallelize {YES, NO};

    // The minimum number of elements which need to be sorted in order for the algorithm to use parallelization
    private static final int MIN_PARALLELIZE_LIMIT = 128000;

    private static final int ZERO = 0;

    private MergeSort () { };

    /**
     * Using merge sort, sort a list of elements and decide whether to use parallelizatoin to perform the sorting based
     * on the minimum parallelization limit.
     */
    public static <T extends Comparable<T>> void sort(List<T> list) throws ExecutionException, InterruptedException {
        if (list == null || list.size() <= 1) return;
        if (list.size() > MIN_PARALLELIZE_LIMIT) {
            sort(list, Parallelize.YES);
        } else {
            sort(list, Parallelize.NO);
        }

    }

    /**
     * Using merge sort, find the parallelization threshold and sort a list of comparable elements according to this
     * threshold limit. By threshold, we mean the minimum amount of list elements needed in the collection
     * in order for the algorithm to perform parallel processing.
     */
    public static <T extends Comparable<T>> void sort(List<T> list, Parallelize parallelize) throws ExecutionException, InterruptedException {
        if (list == null || list.size() <= 1) return;
        int threshold = getThreshold(list.size(),  parallelize);
        sort(list, threshold);
    }

    /**
     * Determine the parallelization threshold according to the list size and parallelize parameter.
     * If we want to parallelize, we count the number of available cores, and set the threshold to be approximately
     * equal to the total list size divided by the number of cpu cores for even load distribution.
     * If we do not want to parallelize, we simply set the threshold to the total list size, so that no parallel
     * processing is ever performed.
     */
    private static <T extends Comparable<T>> int getThreshold(int listSize, Parallelize parallelize) {
        if (parallelize == Parallelize.YES) {
            int numberOfCores = Runtime.getRuntime().availableProcessors();
            return listSize / numberOfCores - 1;
        } else {
            return listSize;
        }
    }

    /**
     * Using merge sort, sort a list of elements and decide whether to use parallelizatoin to perform the sorting based
     * on the passed in threshold parameter.
     */
    private static <T extends Comparable<T>> void sort(List<T> list, int threshold ) throws InterruptedException, ExecutionException {
        if (list.size() <= 1) return;
        // Split the list into two halves
        int halfSize = list.size() / 2;
        List<T> leftList = new ArrayList<T>(list.subList(0, halfSize));
        List<T> rightList = new ArrayList<T>(list.subList(halfSize, list.size()));

        // Whenever half the list size is less than the threshold limit, use regular sequential processing to
        // recursively sort each half.
        if (halfSize < threshold ) {
            sort(leftList, threshold );
            sort(rightList, threshold );
        } else {

            // If the split list size is over or equal to the threshold limit, then add two new threads to the
            // thread pool
            ExecutorService executor = Executors.newFixedThreadPool( 2 );
            // Add each task to recursively sort each half of the list to a list of tasks available to execute
            List<Callable<Void>> tasks = new ArrayList<Callable<Void>>( 2 );
            addSortingTask(tasks, leftList, threshold );
            addSortingTask(tasks, rightList, threshold);

            // Invoke all sorting tasks in task list and wait for all of them to complete
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
            // Initiate a shutdown so that no new tasks can be accepted
            executor.shutdown();
        }

        // Merge the two sorted sub-lists into one ordered list
        List<T> mergedList = merge(leftList, rightList);
        // Copy the merged list elements to the passed in list, so that all elements in list are in sorted order
        Collections.copy(list, mergedList);

    }

    // Add a new task to the passed in task list which recursively calls our sort method to sort the passed in list
    // according to the specified threshold value.
    private static <T extends Comparable<T>> void addSortingTask(List<Callable<Void>> tasks, List<T> list, int threshold ) {
        tasks.add(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                sort(list, threshold);
                return null;
            }
        });
    }

    // Merge the two passed in sorted lists in order so that the results is sorted and return the result
    private static final <T extends Comparable<T>> List<T> merge(List<T> leftList, List<T> rightList) {
        List<T> result = new ArrayList<T>();
        // Iterate through each sublist in order from lowest element to highest, and add the lowest valued element
        // from the lists to add to our results array
        while (leftList.size() > ZERO && rightList.size() > ZERO) {
            if (leftList.get(ZERO).compareTo(rightList.get(ZERO)) <= ZERO) {
                result.add(leftList.remove(ZERO));
            } else {
                result.add(rightList.remove(ZERO));
            }
        }
        // Add all of the elements remaining in the left sub-list to our results
        while (leftList.size() > ZERO) result.add(leftList.remove(ZERO));
        // Add all of the elements remaining in the right sub-list to our results
        while (rightList.size() > ZERO) result.add(rightList.remove(ZERO));

        return result;
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
        int RUNS   =  10;

        for (int i = 1; i <= RUNS; i++) {
            List<Integer> testList = createRandomList(LENGTH);

            long startTime = System.currentTimeMillis();
            sort(testList, Parallelize.NO);
            long endTime = System.currentTimeMillis();

            System.out.printf("%10d elements  =>  %6d ms \n", LENGTH, endTime - startTime);
            System.out.println(isSorted(testList));
            LENGTH *= 2;
        }
    }



}
