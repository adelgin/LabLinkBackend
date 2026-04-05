package org.example.lablinkbackend.domain.publication.model.dto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.lablinkbackend.domain.user.model.entity.User;
import java.io.Serializable;

@Data
@Entity
@Table(name = "publication_authors")
@IdClass(PublicationAuthor.PublicationAuthorId.class)
public class PublicationAuthor {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", nullable = false)
    private Publication publication;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "author_order", nullable = false)
    private Integer authorOrder;

    @Column(name = "is_corresponding_author")
    private Boolean isCorrespondingAuthor = false;

    @Data
    @EqualsAndHashCode
    public static class PublicationAuthorId implements Serializable {
        private Long publication;
        private Long user;
    }
}
