package com.krushna.commercecore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "search_synonyms")
public class SearchSynonym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Column(nullable = false)
    private String synonyms;

    public SearchSynonym() {}

    public SearchSynonym(String keyword, String synonyms) {
        this.keyword = keyword;
        this.synonyms = synonyms;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getSynonyms() { return synonyms; }
    public void setSynonyms(String synonyms) { this.synonyms = synonyms; }
}
