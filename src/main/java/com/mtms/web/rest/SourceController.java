package com.mtms.web.rest;

import com.mtms.model.Source;
import com.mtms.service.SourceHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sources")
public class SourceController {
    @Autowired
    private SourceHealthService sources;

    /*
     * Registers a new data source in the database and
     *      in-memory health monitor (POST /api/sources/create).
     *
     * @param source_id The unique identifier of the source to register.
     * @return true once registration succeeds.
     * @throws Exception if database registration fails.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public Boolean registerSource(@RequestParam("source_id") String source_id) throws Exception {
        return sources.registerSource(source_id);
    }

    /*
     * Retrieves all registered data sources and their health metrics (GET /api/sources/list).
     *
     * @return A list of all Source entities.
     */
    @GetMapping("/list")
    public List<Source> getAllSources(){
        return sources.getAllSources();
    }

    /*
     * Looks up a specific data source by its unique source ID (GET /api/sources/search/{sourceId}).
     *
     * @param sourceId The unique source identifier path variable.
     * @return The matching Source entity.
     */
    @GetMapping("/search/{sourceId}")
    public Source searchSourceId(@PathVariable String sourceId){
        return sources.findSourceById(sourceId);
    }

    /*
     * Retrieves a mapping of all registered source IDs to their
     *      highest recorded sequence numbers (GET /api/sources/search/sequences).
     *
     * @return A Map where keys are source IDs and values are highest sequence numbers.
     */
    @GetMapping("/search/sequences")
    public Map<String, Long> getSourcesAndSequences(){
        return sources.getSourcesAndSequences();
    }

}
