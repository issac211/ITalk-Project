package com.hit.dm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchResult<T> implements Serializable {
    private final List<MatchResult<T>> matches;
    private String pattern; // The pattern that was searched for

    public SearchResult(String pattern) {
        this.matches = new ArrayList<>();
        setPattern(pattern);
    }

    public List<MatchResult<T>> getMatches() {
        return matches;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void addMatch(T matchObject, int[] matchIndexes) {
        matches.add(new MatchResult<>(matchObject, matchIndexes));
    }

    public boolean hasMatches() {
        return matches != null && !matches.isEmpty();
    }

    public int countMatches() {
        int matchesNumber = 0;
        if (matches != null) {
            for (MatchResult<T> mr : matches)
                for (int _ : mr.getIndexes())
                    matchesNumber++;
        }
        return matchesNumber;
    }

    @Override
    public String toString() {
        return "SearchResult{" +
                "matches=" + matches +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
