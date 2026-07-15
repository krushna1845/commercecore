package com.krushna.commercecore.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recent_searches")
public class RecentSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String query;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "search_count")
    private int searchCount = 1;

    @Column(name = "last_searched_at")
    private LocalDateTime lastSearchedAt = LocalDateTime.now();

    public RecentSearch() {}

    public RecentSearch(String query, User user) {
        this.query = query;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getSearchCount() { return searchCount; }
    public void setSearchCount(int searchCount) { this.searchCount = searchCount; }

    public LocalDateTime getLastSearchedAt() { return lastSearchedAt; }
    public void setLastSearchedAt(LocalDateTime lastSearchedAt) { this.lastSearchedAt = lastSearchedAt; }
}
