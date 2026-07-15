package com.krushna.commercecore.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class RecentSearchStore {
    private final Deque<String> recent = new ArrayDeque<>();
    private final Map<String, Integer> counts = new ConcurrentHashMap<>();
    private final int MAX_RECENT = 50;

    public synchronized void record(String q) {
        if (q == null || q.isBlank()) return;
        recent.remove(q);
        recent.addFirst(q);
        while (recent.size() > MAX_RECENT) recent.removeLast();
        counts.merge(q, 1, Integer::sum);
    }

    public synchronized List<String> recent(int limit) {
        return recent.stream().limit(limit).collect(Collectors.toList());
    }

    public List<Map.Entry<String,Integer>> popular(int limit) {
        return counts.entrySet().stream()
                .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
