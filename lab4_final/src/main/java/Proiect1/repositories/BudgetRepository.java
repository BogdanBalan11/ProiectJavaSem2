package Proiect1.repositories;


import Proiect1.domain.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BudgetRepository extends CrudRepository<Budget, Long> {
    List<Budget> findByUsersId(Long userId);
    Page<Budget> findByUsersId(Long userId, Pageable pageable);
}
