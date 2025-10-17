package org.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCacheWithLinkedHashMap {

    private int capacity;

    private final LinkedHashMap<Integer, Integer> cache;

    public LRUCacheWithLinkedHashMap(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {

            // Override removeEldestEntry to handle LRU eviction automatically.
            @Override
            protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
                // Return true if the size exceeds capacity, triggering the removal
                // of the eldest (LRU) entry, which is the head of the access-order DLL.
                return size() > LRUCacheWithLinkedHashMap.this.capacity;
            }
        };
    }

    public int get(int key) {

        Integer value = cache.get(key);

        if (value == null) {
            return -1;
        }
        return value;

    }

    public void put(int key, int value) {

        cache.put(key, value);

    }

    public static void main(String[] args) {
        System.out.println("Testing LRUCache (Capacity 2):");
        LRUCacheWithLinkedHashMap lRUCache = new LRUCacheWithLinkedHashMap(2);

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
