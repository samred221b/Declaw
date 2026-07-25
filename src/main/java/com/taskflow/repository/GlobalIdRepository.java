package com.taskflow.repository;

import com.taskflow.entity.GlobalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for GlobalId entities (polymorphic ID mapping).
 */
@Repository
public interface GlobalIdRepository extends JpaRepository<GlobalId, Long> {

    /**
     * Find a global ID by type and value.
     *
     * @param type the resource type (PROJECT or TASK)
     * @param value the actual ID value to search for
     * @return an Optional containing the GlobalId if found, empty otherwise
     */
    Optional<GlobalId> findByTypeAndValue(GlobalId.GlobalIdType type, Long value);
}
