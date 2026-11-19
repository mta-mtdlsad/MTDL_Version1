package com.mtms.repository;

import com.mtms.domain.Path;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PathRepository extends CrudRepository<Path, Long> {
}
