package com.mtms.repository;

import com.mtms.domain.Alert;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


//@Repository
public interface AlertRepository extends CrudRepository<Alert, Integer> {/*
by default we have these two:
save(alert)=> addAlert()
findAll() => getAllAlerts()
*/
}