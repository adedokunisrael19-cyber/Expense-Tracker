package ng.expensetracker.services;

import lombok.RequiredArgsConstructor;
import ng.expensetracker.data.model.Category;
import ng.expensetracker.data.repository.CategoryRepository;
import ng.expensetracker.data.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private  final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategory(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found")
        );
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(long categoryId) {
      Category category =  categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));

        boolean isReferenced = (boolean) transactionRepository.existsByCategory_Id(categoryId);
        if (isReferenced) {
            throw new IllegalStateException("Cannot delete category because it is referenced by a transaction");
        }

        categoryRepository.delete(category);
    }
}
