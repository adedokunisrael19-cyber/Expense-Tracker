package ng.expensetracker.services;

import ng.expensetracker.data.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(Category category);
    Category getCategory(long id);
    List<Category> getAllCategories();
    void deleteCategory(long categoryId);
}
