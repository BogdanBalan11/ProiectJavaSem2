package Proiect1.repositories;



import Proiect1.domain.Bill;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BillRepository extends CrudRepository<Bill, Long> {
    List<Bill> findByUserId(Long userId);
}
