package Proiect1.repositories;


import Proiect1.domain.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoalRepository extends CrudRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
    Page<Goal> findByUserId(Long userId, Pageable pageable);
}
