package com.taskflow.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for global ID lookups (polymorphic ID mapping).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/global-ids")
public class GlobalIdController {

    private final com.taskflow.repository.GlobalIdRepository globalIdRepository;

    /**
     * Retrieve a global ID by type and value.
     *
     * @param typeId the resource type (PROJECT or TASK)
     * @param idValue the actual ID value to look up
     * @return ResponseEntity containing the GlobalId if found, 404 otherwise
     */
    @GetMapping("/byType/{typeId}/byValue/{idValue}")
    public ResponseEntity<GlobalId> getByTypeAndValue(
            @PathVariable com.taskflow.entity.GlobalId.GlobalIdType typeId,
            @PathVariable Long idValue) {
        GlobalId globalId = globalIdRepository.findByTypeAndValue(typeId, idValue)
                .orElse(null);
        return globalId != null ? new ResponseEntity<>(globalId, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
