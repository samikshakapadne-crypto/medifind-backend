package com.medifind.backend.repository;

import com.medifind.backend.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MedicineRepository
        extends JpaRepository<Medicine, Long> {

    // Existing methods
    List<Medicine> findByActiveTrue();

    List<Medicine> findByMedicineNameContainingIgnoreCaseAndActiveTrue(
            String medicineName
    );

    List<Medicine> findByGenericNameContainingIgnoreCaseAndActiveTrue(
            String genericName
    );


    // ==========================================
    // PAGINATION
    // ==========================================

    Page<Medicine> findByActiveTrue(
            Pageable pageable
    );


    // ==========================================
    // CATEGORY FILTER + PAGINATION
    // ==========================================

    Page<Medicine> findByCategoryIgnoreCaseAndActiveTrue(
            String category,
            Pageable pageable
    );


    // ==========================================
    // GET ALL DISTINCT MAIN CATEGORIES
    // ==========================================

    @Query("""
            SELECT DISTINCT m.category
            FROM Medicine m
            WHERE m.active = true
              AND m.category IS NOT NULL
              AND m.category <> ''
            ORDER BY m.category
            """)
    List<String> findDistinctActiveCategories();


    // ==========================================
    // GET SUBCATEGORIES BY MAIN CATEGORY
    // ==========================================

    @Query("""
            SELECT DISTINCT m.subcategory
            FROM Medicine m
            WHERE m.active = true
              AND LOWER(m.category) = LOWER(:category)
              AND m.subcategory IS NOT NULL
              AND m.subcategory <> ''
            ORDER BY m.subcategory
            """)
    List<String> findDistinctSubcategoriesByCategory(
            @Param("category") String category
    );


    // ==========================================
    // SEARCH + PAGINATION
    // ==========================================

    @Query("""
            SELECT m
            FROM Medicine m
            WHERE m.active = true
              AND (
                    LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(m.brandName) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<Medicine> searchActiveMedicines(
            @Param("search") String search,
            Pageable pageable
    );


    // ==========================================
    // SEARCH + CATEGORY + PAGINATION
    // ==========================================

    @Query("""
            SELECT m
            FROM Medicine m
            WHERE m.active = true
              AND LOWER(m.category) = LOWER(:category)
              AND (
                    LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(m.genericName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(m.brandName) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<Medicine> searchActiveMedicinesByCategory(
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable
    );
}