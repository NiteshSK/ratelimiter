package org.example;

import java.util.Arrays;

/**
 * Finds the maximum product of three numbers in a given integer array.
 * The maximum product can be formed in one of two ways:
 * 1. The product of the three largest numbers.
 * 2. The product of the two smallest (most negative) numbers and the largest number.
 * * Time Complexity: O(N log N) due to sorting, where N is the length of the array.
 * Space Complexity: O(log N) or O(N), depending on the sorting algorithm used by the JVM.
 */
public class MaximumProduct {

    /**
     * Finds the maximum product of three numbers in the array.
     * @param nums The input integer array.
     * @return The maximum product.
     */
    public int maximumProduct(int[] nums) {
        if (nums == null || nums.length < 3) {
            // Check for valid input size, though constraints suggest nums.length >= 3.
            throw new IllegalArgumentException("Array must contain at least three numbers.");
        }

        // Step 1: Sort the array in ascending order.
        // O(N log N) complexity.
        Arrays.sort(nums);

        int n = nums.length;

        // Use 'long' for intermediate products to prevent potential integer overflow,
        // as the maximum possible product (1000^3 = 10^9) is near the edge of a 32-bit integer.

        // Candidate 1: Product of the three largest numbers (nums[N-1], nums[N-2], nums[N-3]).
        // This is the solution if the array contains no negative numbers or the negative
        // numbers are small.
        long product1 = (long) nums[n - 1] * nums[n - 2] * nums[n - 3];

        // Candidate 2: Product of the two smallest (most negative) numbers (nums[0], nums[1])
        // and the largest number (nums[N-1]).
        // This handles the crucial case where two large negative numbers multiply to a large positive value.
        long product2 = (long) nums[0] * nums[1] * nums[n - 1];

        // Step 3: Return the maximum of the two candidates.
        return (int) Math.max(product1, product2);
    }

    // --- Example Usage ---
    public static void main(String[] args) {
        MaximumProduct mp = new MaximumProduct();

        // Example 1: Standard positive case
        int[] nums1 = {1, 2, 3};
        System.out.println("Nums: " + Arrays.toString(nums1) + ", Max Product: " + mp.maximumProduct(nums1));

        // Example 2: Negative numbers dominate (Case 2: (-10)*(-5)*3 = 150)
        int[] nums2 = {-10, -5, 1, 2, 3};
        System.out.println("Nums: " + Arrays.toString(nums2) + ", Max Product: " + mp.maximumProduct(nums2));

        // Example 3: All negative numbers (Case 1: (-1)*(-2)*(-3) = -6)
        int[] nums3 = {-1, -2, -3, -4, -5};
        System.out.println("Nums: " + Arrays.toString(nums3) + ", Max Product: " + mp.maximumProduct(nums3));
    }
}

