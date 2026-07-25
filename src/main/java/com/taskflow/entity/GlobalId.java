package com.taskflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic ID entity for polymorphic identity mappings.
 */
@Entity
@Table(name = "global_ids")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class GlobalId {

    /**
     * Type of global resource (PROJECT or TASK).
     */
    @Enumerated(EnumType.STRING)
    private GlobalIdType type;

    /**
     * The actual ID value.
     */
    @Column(name = "value")
    private Long value;

    // Enum for global resource types
    public enum GlobalIdType {
        PROJECT,
        TASK
    }
}
