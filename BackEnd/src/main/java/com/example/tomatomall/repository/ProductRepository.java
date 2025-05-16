package com.example.tomatomall.repository;

import com.example.tomatomall.po.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {
      Product findByTitle(String title);
      List<Product> findByIdIn(List<Integer> productIds);
      @Query("select p from Product p order by p.popularity desc")
      List<Product> getTopProducts(Pageable pageable);
      default List<Product> getTop() {
            Pageable top = PageRequest.of(0, 5);
            return getTopProducts(top);
      }
      //将多表查询拆成两次查询，效率可能更好？毕竟不用笛卡尔积了
      //@Query("select p from Product p join fetch ProductTypes pt where pt.typeId = :id and p.id = pt.productId and (p.title like %:key% or p.description like %:key% or p.detail like %:key%) order by case when p.title like %:key% then 0 else 1 end, case when p.description like %:key% then 0 else 1 end, case when p.detail like %:key% then 0 else 1 end, p.popularity desc")
      //@Query(value = "select p.id,title,price,rate,description,cover,detail,popularity from Product p join Product_Types pt where pt.type_Id = :id and p.id = pt.product_Id and (p.title like :key or p.description like :key or p.detail like :key) order by p.title like :key, p.description like :key, p.detail like :key, p.popularity desc",nativeQuery = true)
      @Query("select p from Product p where p.id in :id and (p.title like %:key% or p.description like %:key% or p.detail like %:key%) order by case when p.title like %:key% then 0 else 1 end, case when p.description like %:key% then 0 else 1 end, case when p.detail like %:key% then 0 else 1 end, p.popularity desc")
      List<Product> searchWithType(@Param("id") List<Integer> productIds, @Param("key") String key);

      @Query("select p from Product p where p.title like %:key% or p.description like %:key% or p.detail like %:key% order by case when p.title like %:key% then 0 else 1 end, case when p.description like %:key% then 0 else 1 end, case when p.detail like %:key% then 0 else 1 end, p.popularity desc")
      List<Product> searchWithoutType(@Param("key") String key);
}
