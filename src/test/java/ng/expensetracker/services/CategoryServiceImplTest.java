package ng.expensetracker.services;

import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.model.TransactionType;
import ng.expensetracker.data.repository.CategoryRepository;
import ng.expensetracker.data.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private  CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void shouldSaveCategory() {
        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        when(categoryRepository.save(category))
                .thenReturn(category);

        Category result = categoryService.createCategory(category);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Groceries");
        assertThat(result.getType()).isEqualTo(TransactionType.EXPENSE);

        verify(categoryRepository).save(category);

    }
    @Test
    void shouldThrowExceptionWhenCategoryDoesNotExist() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategory(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(categoryRepository).findById(1L);
    }


    // TEST 4: RETRIEVE ALL CATEGORIES

    @Test
    void shouldRetrieveAllCategories() {

        List<Category> categories = List.of(
                new Category("Groceries", TransactionType.EXPENSE),
                new Category("Salary", TransactionType.INCOME)
        );

        when(categoryRepository.findAll())
                .thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(categories);

        verify(categoryRepository).findAll();
    }


    // TEST 5: DELETE CATEGORY SUCCESSFULLY

    @Test
    void shouldDeleteCategoryWhenNotReferenced() {

        Category category = new Category(
                "Transportation",
                TransactionType.EXPENSE
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(transactionRepository.existsByCategory_Id(1L))
                .thenReturn(false);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).findById(1L);
        verify(transactionRepository).existsByCategory_Id(1L);
        verify(categoryRepository).delete(category);
    }


    // TEST 6: PREVENT DELETING REFERENCED CATEGORY

    @Test
    void shouldNotDeleteCategoryWhenReferencedByTransaction() {

        Category category = new Category(
                "Groceries",
                TransactionType.EXPENSE
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(transactionRepository.existsByCategory_Id(1L))
                .thenReturn(true);

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("referenced by a transaction");

        verify(categoryRepository, never()).delete(any(Category.class));
    }


    // TEST 7: PREVENT DELETING NONEXISTENT CATEGORY

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentCategory() {

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.deleteCategory(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Category not found");

        verify(transactionRepository, never())
                .existsByCategory_Id(anyLong());

        verify(categoryRepository, never()).delete(any(Category.class));
    }


}