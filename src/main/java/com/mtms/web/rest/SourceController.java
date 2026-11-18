package com.mtms.web.rest;

import com.mtms.domain.Source;
import com.mtms.repository.SourceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/sources")
public class SourceController {

    private final SourceRepository sourceRepository;

    public SourceController(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @GetMapping("/list")
    public List<Source> listSources() {
        List<Source> sources = new ArrayList<>();
        sourceRepository.findAll().forEach(sources::add);
        return sources;
    }
}
