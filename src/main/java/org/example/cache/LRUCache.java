package org.example.cache;

import java.util.HashMap;

/**
 * LRUCache implementation using a HashMap for O(1) key lookups and a Doubly
 * Linked List (DLL) for O(1) order tracking and eviction.
 * * Time Complexity:
 * - get(key): O(1) average time
 * - put(key, value): O(1) average time
 */
class LRUCache {

    // 1. Define the Node for the Doubly Linked List
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        public Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    // Instance variables
    private final int capacity;
    private final HashMap<Integer, Node> cacheMap;

    // Dummy head and tail nodes to simplify insertion and removal operations
    private final Node head;
    private final Node tail;

    /**
     * Initializes the LRU Cache with a given positive capacity.
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cacheMap = new HashMap<>(capacity);

        // Initialize dummy head and tail nodes
        this.head = new Node(0, 0); // key and value are arbitrary for dummy nodes
        this.tail = new Node(0, 0);

        // Link head and tail together initially
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Gets the value associated with the key. If found, marks the item as MRU.
     * @param key The key to look up.
     * @return The value if the key exists, otherwise -1.
     */
    public int get(int key) {
        // O(1) lookup in HashMap
        Node node = cacheMap.get(key);

        if (node == null) {
            return -1;
        }

        // Move the accessed node to the head of the list (MRU)
        moveToHead(node);
        return node.value;
    }

    /**
     * Updates the value of an existing key or adds a new key-value pair.
     * Handles capacity eviction if necessary.
     * @param key The key to insert or update.
     * @param value The value associated with the key.
     */
    public void put(int key, int value) {
        Node node = cacheMap.get(key);

        if (node != null) {
            // Case 1: Key exists (Update)
            node.value = value;
            moveToHead(node); // Update its usage to MRU
        } else {
            // Case 2: Key does not exist (Insert)
            Node newNode = new Node(key, value);
            cacheMap.put(key, newNode);
            addToHead(newNode);

            // Check for capacity constraints
            if (cacheMap.size() > capacity) {
                // Evict the LRU item (the node just before the dummy tail)
                Node lruNode = tail.prev;

                // Remove from DLL and HashMap
                removeNode(lruNode);
                cacheMap.remove(lruNode.key);
            }
        }
    }

    /* --- Doubly Linked List Helper Methods (O(1) operations) --- */

    /**
     * Removes a given node from its current position in the DLL.
     */
    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

        // Relink the previous and next nodes
        prev.next = next;
        next.prev = prev;
    }

    /**
     * Inserts a node right after the dummy head (MRU position).
     */
    private void addToHead(Node node) {
        // New node's next is current head's next
        node.next = head.next;
        // New node's prev is the head
        node.prev = head;

        // Update the surrounding nodes
        head.next.prev = node;
        head.next = node;
    }

    /**
     * Moves an existing node from its current position to the head (MRU).
     */
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    /* --- Main method for testing the example case --- */
    public static void main(String[] args) {
        System.out.println("Testing LRUCache (Capacity 2):");
        LRUCache lRUCache = new LRUCache(2);

        // lRUCache.put(1, 1); // cache is {1=1}
        lRUCache.put(1, 1);
        System.out.println("put(1, 1)");

        // lRUCache.put(2, 2); // cache is {1=1, 2=2}
        lRUCache.put(2, 2);
        System.out.println("put(2, 2)");

        // lRUCache.get(1); // return 1. 1 becomes MRU. cache is {2=2, 1=1}
        System.out.println("get(1): " + lRUCache.get(1)); // Expected: 1

        // lRUCache.put(3, 3); // LRU key was 2, evicts key 2, cache is {1=1, 3=3}
        lRUCache.put(3, 3);
        System.out.println("put(3, 3)");

        // lRUCache.get(2); // returns -1 (not found)
        System.out.println("get(2): " + lRUCache.get(2)); // Expected: -1

        // lRUCache.put(4, 4); // LRU key was 1, evicts key 1, cache is {3=3, 4=4}
        lRUCache.put(4, 4);
        System.out.println("put(4, 4)");

        // lRUCache.get(1); // return -1 (not found)
        System.out.println("get(1): " + lRUCache.get(1)); // Expected: -1

        // lRUCache.get(3); // return 3. 3 becomes MRU. cache is {4=4, 3=3}
        System.out.println("get(3): " + lRUCache.get(3)); // Expected: 3

        // lRUCache.get(4); // return 4. 4 becomes MRU. cache is {3=3, 4=4}
        System.out.println("get(4): " + lRUCache.get(4)); // Expected: 4
    }
}

