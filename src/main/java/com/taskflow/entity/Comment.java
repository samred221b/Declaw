package com.taskflow.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Comment entity representing discussions and notes on Tasks.
 */
@Entity
@Table(name = "comments")
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class Comment implements Serializable {

    /**
     * Unique identifier for the comment.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Content/text of the comment.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * User who authored this comment.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    /**
     * Associated task that this comment belongs to.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false, updatable = false)
    private Task task;

    /**
     * Parent ID for nested/reply comments (optional).
     */
    @Column(name = "parent_id", nullable = true)
    private Long parentId;

    /**
     * Timestamp when the comment was created.
     */
    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    /**
     * Timestamp of the last update to the comment (for edits).
     */
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    // Constructors for JPA
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content.substring(0, Math.min(content.length(), 50)) + '\'' +
                ", user='" + (user != null ? user.getUsername() : "null") + '\'' +
                '}';
    }
}
