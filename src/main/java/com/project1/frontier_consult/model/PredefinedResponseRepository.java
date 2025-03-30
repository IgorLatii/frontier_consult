package com.project1.frontier_consult.model;

//import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface PredefinedResponseRepository extends CrudRepository<PredefinedResponse, Long> {

    Optional<PredefinedResponse> findByCommandAndLanguage(String command, String language);
}
