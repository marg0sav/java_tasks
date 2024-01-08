package org.example;
public interface MyMap<Key, Value> {
    Value get(Key key);

    default boolean containsKey(Key key) {
        return get(key) != null;
    }

    void put(Key key, Value value);

    Value remove(Key key);

    int size();

    boolean isEmpty();

    interface Entry<Key, Value> {
        Key getKey();

        Value getValue();

        Value setValue(Value value);
    }
}
