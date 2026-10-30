package ng.expensetracker.data.repository;

import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.TransactionType;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)

class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testThatICanSaveAndRetrieve(){
        Category category = new Category("Salary", TransactionType.INCOME);
        Category savedCategory = categoryRepository.save(category);
        assertThat(categoryRepository.findById(savedCategory.getId()));
    }


}