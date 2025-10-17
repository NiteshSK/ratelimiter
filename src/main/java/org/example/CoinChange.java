package org.example;

import java.util.Arrays;

/**
 * Solves the Coin Change problem (Minimum Number of Coins) using
 * Recursion with Memoization (Top-Down Dynamic Programming).
 * * Time Complexity: O(amount * len(coins)) - Due to memoization.
 * Space Complexity: O(amount) - For the memoization table and recursion stack.
 */
public class CoinChange {

    // Constant representing an unreachable state (or "infinity").
    // We use a large integer and check for it to avoid overflow when adding 1.
    private static final int UNREACHABLE = Integer.MAX_VALUE - 1;

    /**
     * Entry point for the recursive coin change calculation.
     * Initializes the memoization table and calls the recursive helper.
     * * @param coins A list of available coin denominations.
     * @param amount The target amount to make change for.
     * @return The minimum number of coins, or -1 if the amount cannot be made.
     */
    public static int coinChange(int[] coins, int amount) {
        if (amount < 0) {
            return -1;
        }
        if (amount == 0) {
            return 0;
        }

        // Initialize memoization table with -1 to signify "not yet computed"
        // The size is amount + 1 to store results from 0 to 'amount'.
        int[] memo = new int[amount + 1];
        Arrays.fill(memo, -1);

        int result = solve(coins, amount, memo);

        // If the result is the unreachable constant, return -1.
        return result == UNREACHABLE ? -1 : result;
    }

    /**
     * Recursive helper function with Memoization.
     * * @param coins Available coin denominations.
     * @param remainingAmount The amount currently being solved for.
     * @param memo The memoization table.
     * @return The minimum coins for remainingAmount, or UNREACHABLE.
     */
    private static int solve(int[] coins, int remainingAmount, int[] memo) {
        // Base Case 1: Success! 0 coins are needed for an amount of 0.
        if (remainingAmount == 0) {
            return 0;
        }

        // Base Case 2: Failure! Cannot use a negative amount.
        // This case is actually caught by the subsequent recursive checks,
        // but for safety, we return the failure indicator.
        if (remainingAmount < 0) {
            return UNREACHABLE;
        }

        // Memoization Check: If result is already computed, return it immediately.
        if (memo[remainingAmount] != -1) {
            return memo[remainingAmount];
        }

        int minCoins = UNREACHABLE;

        // Recursive Step: Try every coin to see which one yields the best result.
        for (int coin : coins) {

            // Recursively call for the smaller subproblem
            int result = solve(coins, remainingAmount - coin, memo);

            // If the subproblem was solvable (result is not UNREACHABLE)
            if (result != UNREACHABLE) {
                // Update minCoins with the current best path: 1 (current coin) + result
                minCoins = Math.min(minCoins, 1 + result);
            }
        }

        // Store the calculated minimum into the memoization table before returning.
        memo[remainingAmount] = minCoins;
        return minCoins;
    }

    // --- Example Usage (main method remains for testing) ---
    public static void main(String[] args) {
        int targetAmount1 = 11;
        int[] availableCoins1 = {1, 2, 5};
        int minCoins1 = coinChange(availableCoins1, targetAmount1);

        System.out.println("Coin denominations: " + Arrays.toString(availableCoins1));
        System.out.println("Target amount: " + targetAmount1);
        System.out.println("Minimum coins required: " + minCoins1);
        // Expected Output: 3 (e.g., two 5s and one 1)

        System.out.println("-".repeat(20));

        int targetAmount2 = 7;
        int[] availableCoins2 = {2, 3, 5};
        int minCoins2 = coinChange(availableCoins2, targetAmount2);

        System.out.println("Coin denominations: " + Arrays.toString(availableCoins2));
        System.out.println("Target amount: " + targetAmount2);
        System.out.println("Minimum coins required: " + minCoins2);
        // Expected Output: 2 (e.g., one 5 and one 2)

        System.out.println("-".repeat(20));

        int targetAmount3 = 3;
        int[] availableCoins3 = {2};
        int minCoins3 = coinChange(availableCoins3, targetAmount3);

        System.out.println("Coin denominations: " + Arrays.toString(availableCoins3));
        System.out.println("Target amount: " + targetAmount3);
        System.out.println("Minimum coins required: " + minCoins3);
        // Expected Output: -1 (Cannot make 3 using only 2s)
    }
}

