package org.example;

/**
 * Solves the "Minimum Size Subarray Sum" problem using Binary Search on the Answer.
 * The goal is to find the minimum possible maximum size (penalty) after at most maxOperations.
 * * Time Complexity: O(N * log(Max_Value)), where N is nums.length and Max_Value is 10^9.
 * Space Complexity: O(1)
 */
public class MinimumSize {

    /**
     * Calculates the minimum number of operations required to ensure all bags
     * have a size less than or equal to 'targetPenalty'.
     * * The formula for operations required to reduce a bag of size 'num' to
     * pieces of size at most 'P' is: ceil(num / P) - 1.
     * In integer arithmetic, this is equivalent to: (num - 1) / P.
     * * @param nums The array of bag sizes.
     * @param targetPenalty The maximum allowed size for any bag (P).
     * @return The total number of operations required as a long.
     */
    private long calculateRequiredOperations(int[] nums, long targetPenalty) {
        long totalOps = 0;

        for (int num : nums) {
            // We only need to split the bag if its size is greater than the target penalty.
            if (num > targetPenalty) {
                // The (num - 1) / targetPenalty formula correctly calculates ceil(num / P) - 1
                // using integer division.
                totalOps += (num - 1) / targetPenalty;
            }
        }
        return totalOps;
    }

    /**
     * Finds the minimum possible penalty after performing at most maxOperations.
     */
    public int minimumSize(int[] nums, int maxOperations) {
        // Step 1: Define the search space [low, high]

        // low: Minimum possible penalty is 1 (since bag size must be positive).
        long low = 1;

        // high: Maximum possible penalty is the size of the largest initial bag.
        long high = 0;
        for (int num : nums) {
            high = Math.max(high, num);
        }

        // minPenalty stores the best valid answer found so far. Initialized to the worst case.
        long minPenalty = high;

        // Step 2: Binary Search on the penalty (the answer)
        while (low <= high) {
            // Mid is the potential maximum penalty (P) we are testing.
            long mid = low + (high - low) / 2;

            // Calculate operations required to achieve 'mid' as the maximum penalty.
            long totalOps = calculateRequiredOperations(nums, mid);

            // Step 3: Check the condition
            if (totalOps <= maxOperations) {
                // Case 1: The penalty 'mid' is achievable (we have enough operations).
                // This is a possible answer. We save it and try to achieve an even smaller penalty.
                minPenalty = mid;
                high = mid - 1;
            } else {
                // Case 2: The penalty 'mid' is too aggressive (requires too many operations).
                // We must accept a larger penalty to reduce the required operations.
                low = mid + 1;
            }
        }

        // The result is the minimum penalty found.
        return (int) minPenalty;
    }

    // --- Example Usage ---
    public static void main(String[] args) {
        MinimumSize solver = new MinimumSize();

        // Example 1: nums = [9], maxOperations = 2, Output: 3
        int[] nums1 = {9};
        int maxOps1 = 2;
        System.out.println("Input: [9], Ops: 2 -> Min Penalty: " + solver.minimumSize(nums1, maxOps1));
        // Explanation: P=3 requires (9-1)/3 = 2 ops. Achievable.

        // Example 2: nums = [2,4,8,2], maxOperations = 4, Output: 2
        int[] nums2 = {2, 4, 8, 2};
        int maxOps2 = 4;
        System.out.println("Input: [2,4,8,2], Ops: 4 -> Min Penalty: " + solver.minimumSize(nums2, maxOps2));
        // Explanation: P=2 requires (4-1)/2 + (8-1)/2 = 1 + 3 = 4 ops. Achievable.

        // Custom Example: nums = [7, 2, 4], maxOperations = 3, Output: 3
        int[] nums3 = {7, 2, 4};
        int maxOps3 = 3;
        System.out.println("Input: [7,2,4], Ops: 3 -> Min Penalty: " + solver.minimumSize(nums3, maxOps3));
        // Explanation: P=3 requires (7-1)/3 + (4-1)/3 = 2 + 1 = 3 ops. Achievable.
    }
}
