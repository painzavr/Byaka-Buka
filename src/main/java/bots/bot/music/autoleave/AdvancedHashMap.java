package bots.bot.music.autoleave;

import java.util.HashMap;

public class AdvancedHashMap<K, V1, V2> {
    private final HashMap<K, Pair<V1, V2>> map;

    public AdvancedHashMap() {
        this.map = new HashMap<>();
    }

    public void put(K key, V1 value1, V2 value2) {
        map.put(key, new Pair<>(value1, value2));
    }

    public Pair<V1, V2> get(K key) {
        return map.get(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public void remove(K key) {
        map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public void updateValues(K key, V1 newValue1, V2 newValue2) {
        if (map.containsKey(key)) {
            Pair<V1, V2> currentValues = map.get(key);
            currentValues.setValue1(newValue1);
            currentValues.setValue2(newValue2);
        } else {
            // Handle the case where the key is not present (optional)
            System.out.println("Key not found: " + key);
        }
    }

    public static class Pair<V1, V2> {
        private V1 value1;
        private V2 value2;

        public Pair(V1 value1, V2 value2) {
            this.value1 = value1;
            this.value2 = value2;
        }

        public V1 getValue1() {
            return value1;
        }

        public void setValue1(V1 value1) {
            this.value1 = value1;
        }

        public V2 getValue2() {
            return value2;
        }

        public void setValue2(V2 value2) {
            this.value2 = value2;
        }
    }

}
