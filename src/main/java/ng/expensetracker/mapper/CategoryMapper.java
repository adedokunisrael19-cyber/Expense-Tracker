package ng.expensetracker.mapper;


import ng.expensetracker.data.model.Category;
import ng.expensetracker.dto.response.CategoryResponse;

public class CategoryMapper {

    private CategoryMapper() {

    }

    public static CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getCreatedAt()
        );
    }
}
