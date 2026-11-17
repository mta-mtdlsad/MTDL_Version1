package com.mtms.service;

import com.mtms.domain.Path;
import com.mtms.repository.PathRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PathService {
    private final PathRepository pathRepository;
    private volatile List<Path> cachedPaths = Collections.emptyList();

    public PathService(PathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    @PostConstruct
    public void refreshPathCache() {
        List<Path> paths = new ArrayList<>();
        pathRepository.findAll().forEach(paths::add);
        this.cachedPaths = Collections.unmodifiableList(paths);
    }

    public List<Path> getAllPaths() {
        if (cachedPaths.isEmpty()) {
            refreshPathCache();
        }
        return cachedPaths;
    }

    public Optional<Path> getPathById(long pathId) {
        return getAllPaths().stream().filter(p -> p.getPathId() == pathId).findFirst();
    }

    public Long findMatchingPathIdForCoordinates(double latitude, double longitude, double maxDeviationKm) {
        for (Path path : getAllPaths()) {
            if (path.isTrackOnPath(latitude, longitude, maxDeviationKm)) {
                return path.getPathId();
            }
        }
        return null;
    }
}
