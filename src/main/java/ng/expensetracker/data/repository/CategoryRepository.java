
package ng.expensetracker.data.repository;

import ng.expensetracker.data.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
