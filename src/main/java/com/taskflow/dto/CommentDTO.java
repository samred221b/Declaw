package com.taskflow.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Comment creation and update operations.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    /**
     * Content/text of the comment.
     *
     * @NotBlank ensures a non-empty comment body
     */
    @NotBlank(message = "Comment content is required")
    @Size(min = 1, max = 2000, message = "Comment must be between 1 and 2000 characters")
    private String content;

    /**
     * ID of the parent comment for nested replies (optional).
     */
    private Long parentId;

    // Enum for reply depth control to prevent infinite nesting
    @Builder.Default
    private Integer maxReplyDepth = 5;
}
