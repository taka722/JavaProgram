package edu.uob;

import java.util.Objects;

public class TwoKey {
    private final String key1;
    private final String key2;

    public TwoKey(String key1, String key2) {
        this.key1 = key1;
        this.key2 = key2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TwoKey twoKey = (TwoKey) obj;
        return Objects.equals(key1, twoKey.key1) && Objects.equals(key2, twoKey.key2);
    }
}

