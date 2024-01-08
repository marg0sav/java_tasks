package org.example;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.Iterator;

class MyHashMapTest {

    @org.junit.jupiter.api.Test
    void get() {
        MyHashMap<String, Integer> myMap = new MyHashMap<>();
        HashMap<String, Integer> hashMap = new HashMap<>();

        myMap.put("key1", 42);
        hashMap.put("key1", 42);

        assertEquals(hashMap.get("key1"), myMap.get("key1"));
        assertNull(myMap.get("nonexistentKey"));
    }

    @org.junit.jupiter.api.Test
    void put() {
        MyHashMap<String, Integer> myMap = new MyHashMap<>();
        HashMap<String, Integer> hashMap = new HashMap<>();

        myMap.put("key1", 42);
        hashMap.put("key1", 42);

        assertEquals(hashMap.get("key1"), myMap.get("key1"));

        myMap.put("key1", 100);
        hashMap.put("key1", 100);

        assertEquals(hashMap.get("key1"), myMap.get("key1"));

        myMap.put("key2", 200);
        hashMap.put("key2", 200);

        assertEquals(hashMap.get("key2"), myMap.get("key2"));
    }

    @org.junit.jupiter.api.Test
    void remove() {
        MyHashMap<String, Integer> myMap = new MyHashMap<>();
        HashMap<String, Integer> hashMap = new HashMap<>();

        myMap.put("key1", 42);
        hashMap.put("key1", 42);

        assertEquals(hashMap.remove("key1"), myMap.remove("key1"));
        assertNull(myMap.get("key1"));

        assertEquals(hashMap.remove("nonexistentKey"), myMap.remove("nonexistentKey"));
        assertNull(myMap.get("nonexistentKey"));
    }

    @org.junit.jupiter.api.Test
    void size() {
        MyHashMap<String, Integer> myMap = new MyHashMap<>();
        HashMap<String, Integer> hashMap = new HashMap<>();

        assertEquals(hashMap.size(), myMap.size());

        for(int i=0;i<100;i++)
        {
            myMap.put("key"+i,i);
            hashMap.put("key"+i,i);

        }
        assertEquals(hashMap.size(), myMap.size());

        myMap.remove("key51");
        hashMap.remove("key51");

        assertEquals(hashMap.size(), myMap.size());

        for(int i=25;i<50;i++)
        {
            myMap.remove("key"+i);
            hashMap.remove("key"+i);
        }
        assertEquals(hashMap.size(), myMap.size());
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        MyHashMap<String, Integer> myMap = new MyHashMap<>();
        HashMap<String, Integer> hashMap = new HashMap<>();

        assertEquals(hashMap.isEmpty(), myMap.isEmpty());

        myMap.put("key1", 42);
        hashMap.put("key1", 42);

        assertEquals(hashMap.isEmpty(), myMap.isEmpty());

        myMap.remove("key1");
        hashMap.remove("key1");

        assertEquals(hashMap.isEmpty(), myMap.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void iterator() {
        MyHashMap<String, Integer> myMap = new MyHashMap<>();
        HashMap<String, Integer> hashMap = new HashMap<>();

        myMap.put("key1", 42);
        hashMap.put("key1", 42);

        myMap.put("key2", 100);
        hashMap.put("key2", 100);

        myMap.put("key3", 200);
        hashMap.put("key3", 200);

        Iterator<MyMap.Entry<String, Integer>> iterator = myMap.iterator();

        while (iterator.hasNext()) {
            MyMap.Entry<String, Integer> entry = iterator.next();
            assertTrue(hashMap.containsKey(entry.getKey()));
            assertEquals(hashMap.get(entry.getKey()), entry.getValue());
        }
    }
}