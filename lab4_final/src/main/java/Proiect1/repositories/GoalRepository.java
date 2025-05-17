package Proiect1.repositories;


import Proiect1.domain.Goal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoalRepository extends CrudRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
}
