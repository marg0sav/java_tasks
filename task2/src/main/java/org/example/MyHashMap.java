package org.example;
import java.util.Iterator;

public class MyHashMap<Key, Value> implements MyMap<Key, Value>, Iterable<MyMap.Entry<Key, Value>> {

    /**
     * Массив простых чисел, используемых для выбора начальной емкости хеш-таблицы.
     * Выбор простых чисел помогает уменьшить коллизии и обеспечивает эффективное
     * распределение данных по бакетам.
     */
    private static final int[] PRIME_CAPACITIES = {
            17, 31, 61, 127, 257, 509, 1021, 2053, 4099, 8209, 16411, 32771, 65537,
            131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 16777259,
            33554467, 67108879, 134217757, 268435459, 536870923, 1073741827
    };

    /**
     * Начальная емкость хеш-таблицы по умолчанию.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = PRIME_CAPACITIES[0];
    /**
     * Коэффициент загрузки (load factor) определяет, насколько "загруженной"
     * может быть таблица, прежде чем будет автоматически увеличена в размере
     * (рехеширована).
     */
    private static final double LOAD_FACTOR = 0.75;

    /**
     * Текущий размер хеш-таблицы, который представляет собой количество
     * элементов в таблице.
     */
    int size;
    /**
     * Массив бакетов, представляющих собой хранение элементов в хеш-таблице.
     */
    Node[] table;
    /**
     * Индекс, используемый для выбора текущей емкости из массива
     * простых чисел при изменении размера хеш-таблицы.
     */
    int capacityIndex = 0;

    public MyHashMap() {
    }

    /**
     * Получает значение, связанное с указанным ключом, из хеш-таблицы.
     *
     * @param key  Ключ, по которому производится поиск значения.
     * @return     Значение, связанное с указанным ключом, или null, если такого ключа нет
     *             или таблица пуста.
     */
    @Override
    public Value get(Key key) {
        if (table == null) {
            return null;
        }

        final int hash = key.hashCode();
        final Node bucket = table[index(hash)];

        if (bucket == null) {
            return null;
        } else {
            return bucket.get(hash, key);
        }
    }

    /**
     * Вставляет элемент в хеш-таблицу или обновляет значение для существующего ключа.
     * Если текущий размер хеш-таблицы превышает установленный коэффициент загрузки,
     * выполняется операция рехеширования для увеличения её размера.
     *
     * @param key   Ключ, который следует вставить или обновить.
     * @param value Значение, связанное с указанным ключом.
     */
    @Override
    public void put(Key key, Value value) {
        if (table == null || size >= LOAD_FACTOR * capacity()) {
            table = resize();
        }

        final int hash = key.hashCode();
        final int index = index(hash);

        if (table[index] == null) {
            table[index] = new Node(hash, key, value);
            size++;
            return;
        }

        table[index].put(hash, key, value);
    }


    /**
     * Удаляет элемент из хеш-таблицы по указанному ключу.
     *
     * @param key Ключ элемента, который требуется удалить.
     * @return Значение, связанное с удаленным ключом, или null, если ключ не найден
     *         или таблица пуста.
     */
    @Override
    public Value remove(Key key) {
        if (table == null) {
            return null;
        }

        final int hash = key.hashCode();
        final int index = index(hash);
        Node current = table[index];

        if (current == null) {
            return null;
        }

        if (current.matches(hash, key)) {
            table[index] = current.next;
            size--;
            return current.value;
        }

        while (!current.isLast() && !current.next.matches(hash, key)) {
            current = current.next;
        }

        if (!current.next.isLast()) {
            size--;
            Value previous = current.next.value;
            current.next = current.next.next;
            return previous;
        }

        return null;
    }

    /*public Value remove(Key key) {
        if (table == null) {
            return null;
        }
        final int hash = key.hashCode();
        final int index = index(hash);
        if (table[index] == null) {
            return null;
        }
        if (table[index].matches(hash, key)) {
            final Value previous = table[index].value;
            table[index] = table[index].next;
            size--;
            return previous;
        }
        if (table[index].isLast()) {
            return null;
        }
        return table[index].remove(hash, key);
    }
     */

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    private int capacity() {
        return table != null ? table.length : DEFAULT_INITIAL_CAPACITY;
    }

    int index(int hash) {
        return Math.floorMod(hash, capacity());
    }

    private Node[] resize() {
        if (capacityIndex >= PRIME_CAPACITIES.length) {
            return table;
        }
        if (table != null) {
            capacityIndex++;
        }
        final int newCapacity = PRIME_CAPACITIES[capacityIndex];
        @SuppressWarnings({"unchecked"})
        final var newTable = (Node[]) new MyHashMap<?, ?>.Node[newCapacity];
        if (table == null) {
            return newTable;
        }
        for (Node bucket : table) {
            for (Node node = bucket, next; node != null; node = next) {
                next = node.next;
                final int index = Math.floorMod(node.hash, newCapacity);
                node.next = newTable[index];
                newTable[index] = node;
            }
        }
        return newTable;
    }

    @Override
    public Iterator<Entry<Key, Value>> iterator() {
        return new Iterator<>() {
            int index = 0;
            Node node = table[0];

            {
                ensureBegin();
            }

            private void ensureBegin() {
                while (node == null && index < table.length - 1) {
                    index++;
                    node = table[index];
                }
                assert !hasNext() || node != null;
            }

            @Override
            public boolean hasNext() {
                return !(index == table.length - 1 && node == null);
            }

            @Override
            public Entry<Key, Value> next() {
                final Node current = node;

                if (node != null) {
                    node = node.next;
                }
                ensureBegin();

                return current;
            }
        };
    }

    class Node implements MyMap.Entry<Key, Value> {
        final Key key;
        final int hash;
        Value value;
        Node next;

        Node(int hash, Key key, Value value, Node next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        Node(int hash, Key key, Value value) {
            this(hash, key, value, null);
        }

        Value get(int hash, Key key) {
            Node node = findOrGetLast(hash, key);
            if (!node.isLast() || node.matches(hash, key)) {
                return node.value;
            }
            return null;
        }

        void put(int hash, Key key, Value value) {
            Node node = this;
            while (!node.isLast() && !node.matches(hash, key)) {
                node = node.next;
            }
            if (!node.isLast() || node.matches(hash, key)) {
                node.value = value;
                return;
            }
            node.next = new Node(hash, key, value);
            size++;
        }

        Value remove(int hash, Key key) {
            Node node = this;
            while (!node.next.isLast() && !node.next.matches(hash, key)) {
                node = node.next;
            }
            if (node.next.isLast() && !node.next.matches(hash, key)) {
                return null;
            }
            size--;
            Value previous = node.next.value;
            node.next = node.next.next;
            return previous;
        }

        Node findOrGetLast(int hash, Key key) {
            Node node = this;
            while (!node.isLast() && !node.matches(hash, key)) {
                node = node.next;
            }
            return node;
        }

        boolean isLast() {
            return next == null;
        }

        boolean matches(int hash, Key key) {
            return this.hash == hash && this.key.equals(key);
        }

        @Override
        public Key getKey() {
            return key;
        }

        @Override
        public Value getValue() {
            return value;
        }

        @Override
        public Value setValue(Value value) {
            final Value previous = this.value;
            this.value = value;
            return previous;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return hash == node.hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
