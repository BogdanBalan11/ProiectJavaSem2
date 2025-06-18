package Proiect1.repositories;

import Proiect1.domain.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BillRepository extends CrudRepository<Bill, Long> {
    List<Bill> findByUserId(Long userId);
    Page<Bill> findByUserId(Long userId, Pageable pageable);

}
