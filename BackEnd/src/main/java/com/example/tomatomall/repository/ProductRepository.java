package com.example.tomatomall.repository;

import com.example.tomatomall.po.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Integer> {
      Product findByTitle(String title);
      @Query("select p from Product p order by p.popularity desc")
      List<Product> getTopProducts(Pageable pageable);
      default List<Product> getTop() {
            Pageable top = PageRequest.of(0, 5);
            return getTopProducts(top);
      }
      @Query("select p from Product p where p.id=:id and p.title like %:key%")
      Optional<Product> searchTitle(@Param("id") Integer id, @Param("key") String key);

      List<Product> findByTitleLike(String key);
}
