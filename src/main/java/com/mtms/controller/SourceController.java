package com.mtms.controller;

import com.mtms.model.Source;
import com.mtms.model.Track;
import com.mtms.service.SourceHealthService;
import com.mtms.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sources")
public class SourceController {
    @Autowired
    private SourceHealthService sources;

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public Boolean registerSource(@RequestParam("source_id") String source_id) throws Exception {
        return sources.registerSource(source_id);
    }

    @GetMapping("/list")
    public List<Source> getAllsources(){
        return sources.getAllSources();
    }

    @GetMapping("/search/{sourceId}")
    public Source searchSourceId(@PathVariable String sourceId){
        return sources.findSourceById(sourceId);
    }

    @GetMapping("/search/sequences")
    public Map<String, Long> getSourcesAndSequences(){
        return sources.getSourcesAndSequences();
    }

}
