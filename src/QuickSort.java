import java.util.*;
import java.util.concurrent.*;

/**
 * Algorithm: Quick Sort
 * Time Complexity  - Average Case: O(n log(n)); Worst Case: O(n^2)
 * Space Complexity - Worst Case: O(log(n))
 * Use Cases:       - When number of elements isn't huge and in need of a generally efficient sorting algorithm
 *                  - When you want to take advantage of cpu parallelization (divide and conquer algorithm)
 */
public class QuickSort {

    // Parameter options which can be used to specify whether to use thread parallelization to sort
    // each sublist to speed up the sorting speed for large data sets.
    public enum Parallelize {YES, NO};

    // The minimum number of elements which need to be sorted in order for the algorithm to use parallelization.
    private static final int MIN_PARALLELIZE_LIMIT = 256000;

    private QuickSort() { }

    /**
     * Using quick sort, sort a list of elements and decide whether to use parallelizatoin to perform the sorting based
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
     * Find the parallelization threshold and sort a list of comparable elements according to this
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
     * Using quick sort, sort a list of elements and decide whether to use parallelizatoin to perform the sorting based
     * on the passed in threshold parameter.
     */
    private static <T extends Comparable<T>> void sort(List<T> list, int threshold ) throws InterruptedException, ExecutionException {
        if (list.size() <= 1) return;
        // Pick a partition element and divide the original list into two - a left list which will hold all elements
        // less than or equal to our partition element, and a right list which will hold all items larger than this
        // element.
        List<T> leftList = new ArrayList<T>();
        List<T> rightList = new ArrayList<T>();
        // Divide the list into the left and right partition sublists and save the returned partition element.
        T partitionElement = partition(list, leftList, rightList);
        // Whenever half the list size is less than the threshold limit, use regular sequential processing to
        // recursively sort each half.
        if (list.size() / 2 < threshold ) {
            sort(leftList, threshold );
            sort(rightList, threshold );
        } else {
            // If half the list size is over or equal to the threshold limit, then add two new threads to the
            // thread pool
            ExecutorService executor = Executors.newFixedThreadPool( 2 );
            // Add each task to recursively sort each half of the list to a list of tasks available to execute
            List<Callable<Void>> tasks = new ArrayList<Callable<Void>>( 2 );
            addSortingTask(tasks, leftList, threshold );
            addSortingTask(tasks, rightList, threshold );
            // Invoke all sorting tasks in task list and wait for all of them to complete
            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> future : futures) {
                future.get();
            }
            // Initiate a shutdown so that no new tasks can be accepted
            executor.shutdown();
        }
        // Merge the different list partitions by combining the left list, partition element, and right list
        List<T> mergedList = new ArrayList<T>();
        mergedList.addAll(leftList);
        mergedList.add(partitionElement);
        mergedList.addAll(rightList);
        // Copy the merged list elements to the passed in list, so that all elements in list are in sorted order
        Collections.copy(list, mergedList);
    }

    /**
     * Add a new task to the passed in task list which recursively calls our sort method to sort the passed in list
     * according to the specified threshold value.
     */
    private static <T extends Comparable<T>> void addSortingTask(List<Callable<Void>> tasks, List<T> list, int threshold ) {
        tasks.add(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                sort(list, threshold);
                return null;
            }
        });
    }

    /**
     * Set a partition element to be the first element of the list, and create two sub-lists using this partition.
     * The left sub-list will contain all elements less than or equal to our partition, while the right sub-list
     * will contain all the larger elements. Return the partition element.
     */
    private static <T extends Comparable<T>> T partition(List<T> list, List<T> leftList, List<T> rightList) {
        // Set the partition element to be the first element in our input list
        T partitionElement = list.get(0);
        // If our input list has only one element, return the element as no partitioning is necessary
        if (list.size() == 1) return partitionElement;
        // Iterate through each element other than the first element in our list and add each element less than or
        // equal than the partition element to the left list while adding all greater elements to the right list.
        for (T element : list.subList(1, list.size()) ) {
            if (element.compareTo(partitionElement) <= 0) {
                leftList.add(element);
            } else {
                rightList.add(element);
            }
        }
        // Return the chosen partition element, which we can use later to merge all of our elements together.
        return partitionElement;
    }
}
