package BinDev.ExpensesTracker.service;

import BinDev.ExpensesTracker.entity.ExpenseCategory;
import BinDev.ExpensesTracker.entity.User;
import BinDev.ExpensesTracker.enums.CategoryName;
import BinDev.ExpensesTracker.repository.ExpenseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ExpenseCategoryRepository categoryRepository;

    // Créer les catégories par défaut pour un nouvel utilisateur
    @Transactional
    public void createDefaultCategoriesForUser(User user) {
        YearMonth now = YearMonth.now();

        for (CategoryName categoryName : CategoryName.values()) {
            ExpenseCategory category = ExpenseCategory.builder()
                    .user(user)
                    .name(categoryName)
                    .monthlyLimit(new BigDecimal("50"))
                    .month(now.getMonthValue())
                    .year(now.getYear())
                    .isModified(false)
                    .isActive(true)
                    .build();

            categoryRepository.save(category);
        }
    }

    // Créer les catégories pour le nouveau mois (appelé par Scheduler le 1er du mois)
    @Transactional
    public void createCategoriesForNewMonth(User user, Integer newMonth, Integer newYear) {
        Integer previousMonth = newMonth == 1 ? 12 : newMonth - 1;
        Integer previousYear = newMonth == 1 ? newYear - 1 : newYear;

        List<ExpenseCategory> previousCategories = categoryRepository
                .findByUserAndMonthAndYear(user, previousMonth, previousYear);

        for (ExpenseCategory previous : previousCategories) {
            boolean exists = categoryRepository.existsByUserAndNameAndMonthAndYear(
                    user, previous.getName(), newMonth, newYear);

            if (!exists) {
                ExpenseCategory newCategory = ExpenseCategory.builder()
                        .user(user)
                        .name(previous.getName())
                        .monthlyLimit(previous.getMonthlyLimit())
                        .month(newMonth)
                        .year(newYear)
                        .isModified(false)
                        .isActive(true)
                        .build();

                categoryRepository.save(newCategory);
            }
        }
    }

    // Modifier la limite d'une catégorie (une seule fois par mois, n'importe quel jour)
    @Transactional
    public void modifyMonthlyLimit(User user, Long categoryId, BigDecimal newLimit) {

        ExpenseCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        if (category.getIsModified()) {
            throw new RuntimeException("La limite a déjà été modifiée pour ce mois. Vous ne pouvez la modifier qu'une seule fois.");
        }

        if (newLimit == null || newLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("La limite doit être un nombre positif");
        }

        categoryRepository.updateMonthlyLimit(categoryId, newLimit);
    }

    // Récupérer les catégories d'un utilisateur pour le mois en cours
    public List<ExpenseCategory> getCurrentMonthCategories(User user) {
        YearMonth now = YearMonth.now();
        return categoryRepository.findByUserAndMonthAndYear(user, now.getMonthValue(), now.getYear());
    }

    // Récupérer les catégories d'un utilisateur pour un mois spécifique
    public List<ExpenseCategory> getCategoriesByMonth(User user, Integer month, Integer year) {
        return categoryRepository.findByUserAndMonthAndYear(user, month, year);
    }

    // Récupérer une catégorie spécifique pour le mois en cours
    public ExpenseCategory getCategoryByNameForCurrentMonth(User user, CategoryName categoryName) {
        YearMonth now = YearMonth.now();
        return categoryRepository
                .findByUserAndNameAndMonthAndYear(user, categoryName, now.getMonthValue(), now.getYear())
                .orElseThrow(() -> new RuntimeException("Category not found for current month"));
    }

    // Vérifier si l'utilisateur a déjà modifié une catégorie ce mois
    public boolean hasModifiedAnyCategoryThisMonth(User user) {
        YearMonth now = YearMonth.now();
        List<ExpenseCategory> categories = categoryRepository
                .findByUserAndMonthAndYear(user, now.getMonthValue(), now.getYear());

        return categories.stream().anyMatch(ExpenseCategory::getIsModified);
    }



}