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

    /**
     * Возвращает количество элементов (пар ключ-значение) в хеш-таблице.
     *
     * @return Количество элементов в хеш-таблице.
     */
    @Override
    public int size() {

        return size;
    }

    /**
     * Проверяет, пуста ли хеш-таблица, то есть, содержит ли она какие-либо элементы.
     *
     * @return {@code true}, если хеш-таблица не содержит элементов, иначе {@code false}.
     */
    @Override
    public boolean isEmpty() {

        return size() == 0;
    }

    /**
     * Возвращает текущую емкость хеш-таблицы. Если таблица не инициализирована,
     * возвращает значение емкости по умолчанию.
     *
     * @return Текущая емкость хеш-таблицы или емкость по умолчанию, если таблица не инициализирована.
     */
    private int capacity() {

        return table != null ? table.length : DEFAULT_INITIAL_CAPACITY;
    }

    /**
     * Вычисляет и возвращает индекс бакета в хеш-таблице для указанного хеш-кода элемента.
     * Использует остаток от деления хеш-кода на текущую емкость хеш-таблицы.
     *
     * @param hash Хеш-код элемента.
     * @return Индекс бакета в хеш-таблице.
     */
    int index(int hash) {

        return Math.floorMod(hash, capacity());
    }

    /**
     * Увеличивает размер хеш-таблицы, рехешируя ее элементы в новую таблицу
     * с увеличенной емкостью, выбранной из массива простых чисел PRIME_CAPACITIES.
     * Если текущая емкость достигла максимального значения, бросает исключение.
     *
     * @return Новая хеш-таблица с увеличенной емкостью.
     * @throws IllegalStateException Если текущая емкость достигла максимального значения.
     */
    private Node[] resize() {
        if (capacityIndex >= PRIME_CAPACITIES.length) {
            throw new IllegalStateException("Хеш-таблица достигла максимальной емкости.");
        }

        if (table != null) {
            capacityIndex++;
        }

        final int newCapacity = PRIME_CAPACITIES[capacityIndex];
        final Node[] newTable = (Node[]) new MyHashMap<?, ?>.Node[newCapacity];

        if (table == null) {
            return newTable;
        }

        for (Node bucket : table) {
            Node node = bucket;
            while (node != null) {
                final Node current = node;
                node = node.next;

                final int index = Math.floorMod(current.hash, newCapacity);
                current.next = newTable[index];
                newTable[index] = current;
            }
        }

        return newTable;
    }

    /**
     * Возвращает итератор для перебора элементов в хеш-таблице.
     * Итератор реализован в виде внутреннего класса EntryIterator.
     *
     * @return Итератор для перебора элементов хеш-таблицы.
     */
    @Override
    public Iterator<Entry<Key, Value>> iterator() {
        return new EntryIterator();
    }

    /**
     * Внутренний класс, реализующий интерфейс Iterator для перебора элементов
     * хеш-таблицы типа Entry (ключ-значение).
     */
    private class EntryIterator implements Iterator<Entry<Key, Value>> {
        /**
         * Индекс текущего бакета в хеш-таблице.
         */
        private int index = 0;

        /**
         * Текущий узел (Node) в текущем бакете хеш-таблицы.
         */
        private Node currentNode = table[0];

        {
            findNextNonNullNode();
        }

        /**
         * Приватный метод для поиска следующего ненулевого узла в хеш-таблице.
         * Устанавливает index и currentNode в соответствии с найденным узлом.
         */
        private void findNextNonNullNode() {
            while (currentNode == null && index < table.length - 1) {
                index++;
                currentNode = table[index];
            }
        }

        /**
         * Переопределение метода hasNext() интерфейса Iterator.
         * Проверяет наличие следующего элемента в итераторе.
         *
         * @return true, если есть следующий элемент, false в противном случае.
         */
        @Override
        public boolean hasNext() {
            return !(index == table.length - 1 && currentNode == null);
        }

        /**
         * Переопределение метода next() интерфейса Iterator.
         * Возвращает текущий узел и сдвигает указатель currentNode на следующий узел.
         *
         * @return Текущий узел (Entry) в итераторе.
         */
        @Override
        public Entry<Key, Value> next() {
            final Node current = currentNode;

            if (currentNode != null) {
                currentNode = currentNode.next;
            }
            findNextNonNullNode();

            return current;
        }
    }

    /**
     * Класс, представляющий узел в хеш-таблице MyHashMap.
     * Реализует интерфейс MyMap.Entry для предоставления
     * ключа, значения и связанных методов.
     */
    class Node implements MyMap.Entry<Key, Value> {
        /**
         * Ключ узла.
         */
        final Key key;

        /**
         * Хеш-код ключа узла.
         */
        final int hash;

        /**
         * Значение узла.
         */
        Value value;

        /**
         * Следующий узел в цепочке, используемой для
         * разрешения коллизий в хеш-таблице.
         */
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

        /**
         * Получает значение узла по указанному хеш-коду и ключу.
         * Если узел содержит указанный ключ, возвращает его значение,
         * в противном случае возвращает null.
         *
         * @param hash Хеш-код ключа.
         * @param key  Ключ для поиска.
         * @return Значение узла, если ключ найден; в противном случае null.
         */
        Value get(int hash, Key key) {
            Node node = findOrGetLast(hash, key);
            if (!node.isLast() || node.matches(hash, key)) {
                return node.value;
            }
            return null;
        }

        /**
         * Вставляет новый узел с указанным хеш-кодом, ключом и значением
         * в текущий узел или обновляет значение для существующего ключа.
         *
         * @param hash  Хеш-код ключа.
         * @param key   Ключ для вставки или обновления.
         * @param value Значение для вставки или обновления.
         */
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

        /**
         * Ищет узел с указанным хеш-кодом и ключом в списке узлов, начиная с текущего узла.
         * Если узел не найден, возвращает последний узел в списке. Если найден, возвращает найденный узел.
         *
         * @param hash Хеш-код ключа узла.
         * @param key  Ключ узла для поиска.
         * @return     Узел с указанным хеш-кодом и ключом, если найден, иначе последний узел в списке.
         */
        Node findOrGetLast(int hash, Key key) {
            Node node = this;
            while (!node.isLast() && !node.matches(hash, key)) {
                node = node.next;
            }
            return node;
        }

        /**
         * Проверяет, является ли текущий узел последним в списке.
         *
         * @return {@code true}, если узел последний, иначе {@code false}.
         */
        boolean isLast() {
            return next == null;
        }

        /**
         * Проверяет, совпадают ли хеш-код и ключ текущего узла с указанными значениями.
         *
         * @param hash Хеш-код для сравнения.
         * @param key  Ключ для сравнения.
         * @return     {@code true}, если хеш-код и ключ совпадают, иначе {@code false}.
         */
        boolean matches(int hash, Key key) {
            return this.hash == hash && this.key.equals(key);
        }

        /**
         * Возвращает ключ текущего узла.
         *
         * @return Ключ текущего узла.
         */
        @Override
        public Key getKey() {
            return key;
        }

        /**
         * Возвращает значение текущего узла.
         *
         * @return Значение текущего узла.
         */
        @Override
        public Value getValue() {
            return value;
        }

        /**
         * Устанавливает новое значение для текущего узла и возвращает предыдущее значение.
         *
         * @param value Новое значение для установки.
         * @return Предыдущее значение текущего узла.
         */
        @Override
        public Value setValue(Value value) {
            final Value previous = this.value;
            this.value = value;
            return previous;
        }

        /**
         * Проверяет, равен ли данный объект текущему узлу. В данном случае сравнение
         * происходит по хэш-кодам.
         *
         * @param obj Объект для сравнения.
         * @return {@code true}, если объекты равны, иначе {@code false}.
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return hash == node.hash;
        }

        /**
         * Возвращает хеш-код текущего узла.
         *
         * @return Хеш-код текущего узла.
         */
        @Override
        public int hashCode() {
            return hash;
        }
    }
}