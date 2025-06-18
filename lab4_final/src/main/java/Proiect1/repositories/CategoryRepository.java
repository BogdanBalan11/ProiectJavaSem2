package Proiect1.repositories;


import Proiect1.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Page<Category> findAll(Pageable pageable);
}
