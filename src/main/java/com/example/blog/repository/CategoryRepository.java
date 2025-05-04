package com.example.blog.repository;

import com.example.blog.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
    @EntityGraph(attributePaths = {"posts"})
    Optional<Category> findById(String id);

}
