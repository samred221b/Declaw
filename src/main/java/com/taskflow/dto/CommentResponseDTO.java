package com.taskflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Comment representation in API responses.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDTO {

    /**
     * Unique identifier for the comment.
     */
    private Long id;

    /**
     * Content/text of the comment.
     */
    @Builder.Default
    private String content = "";

    /**
     * Username of the user who authored this comment.
     */
    @Builder.Default
    private String username = "";

    /**
     * ID of the task this comment belongs to.
     */
    private Long taskId;

    /**
     * ID of parent comment for nested replies (if any).
     */
    private Long parentId;

    // Enum for reply depth control
    @Builder.Default
    private Integer maxReplyDepth = 5;
}
