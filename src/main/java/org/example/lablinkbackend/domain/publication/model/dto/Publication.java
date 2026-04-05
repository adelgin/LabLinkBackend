package org.example.lablinkbackend.domain.publication.model.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.example.lablinkbackend.domain.user.model.entity.User;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity(name = "publications")
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doi;
    private String journalTitle;
    private String source;

    @Column(nullable = false)
    private String title;

    private String description;
    private String url;
    private Integer year;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // Связь с авторами
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<PublicationAuthor> authors = new ArrayList<>();

    public void addAuthor(User user, Integer order, Boolean isCorresponding) {
        PublicationAuthor author = new PublicationAuthor();
        author.setPublication(this);
        author.setUser(user);
        author.setAuthorOrder(order);
        author.setIsCorrespondingAuthor(isCorresponding);
        this.authors.add(author);
    }

    public void removeAuthor(User user) {
        this.authors.removeIf(author -> author.getUser().equals(user));
    }

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
