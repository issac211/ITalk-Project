package com.hit.dm;

import java.io.Serializable;

public class MatchResult<T> implements Serializable {
    private T item;
    private int[] indexes;

    public MatchResult(T item, int[] indexes) {
        this.item = item;
        this.indexes = indexes;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public int[] getIndexes() {
        return indexes;
    }

    public void setIndexes(int[] indexes) {
        this.indexes = indexes;
    }

    @Override
    public String toString() {
        return "MatchResult{" +
                "item=" + item +
                ", indexes=" + java.util.Arrays.toString(indexes) +
                '}';
    }
}
