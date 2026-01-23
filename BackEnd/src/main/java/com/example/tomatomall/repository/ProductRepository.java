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

      //获取热度排名前5
      @Query("select p from Product p order by p.popularity desc")
      List<Product> getTopProducts(Pageable pageable);
      default List<Product> getTop() {
            return getTopProducts(PageRequest.of(0, 5));
      }

      //获取所有
      @Query("select p from Product p")
      List<Product> searchAllDB(Pageable pageable);
      default List<Product> searchAll(int page,int size) {
            return searchAllDB(PageRequest.of(page, size));
      }

      //按商品id列表获取多个商品
      @Query("select p from Product p where p.id in :id")
      List<Product> searchTypeDB(Pageable pageable,@Param("id") List<Integer> productIds);
      default List<Product> searchType(List<Integer> productIds,int page,int size) {
            return searchTypeDB(PageRequest.of(page, size),productIds);
      }

      //同时有类型过滤和搜索
      @Query(value = "select * from Product p where p.id in :id and (p.title rlike :key or p.description rlike :key or p.detail rlike :key) order by p.title rlike :key, p.description rlike :key, p.detail rlike :key, p.popularity desc",nativeQuery = true)
      List<Product> searchWithTypeDB(Pageable pageable,@Param("id") List<Integer> productIds, @Param("key") String key);
      default List<Product> searchWithType(List<Integer> productIds,String key,int page,int size) {
            return searchWithTypeDB(PageRequest.of(page, size),productIds,key);
      }
      @Query(value = "select count(*) from Product p where p.id in :id and (p.title rlike :key or p.description rlike :key or p.detail rlike :key)",nativeQuery = true)
      int countWithType(@Param("id") List<Integer> productIds, @Param("key") String key);

      //仅有搜索
      @Query(value = "select * from Product p where (p.title rlike :key or p.description rlike :key or p.detail rlike :key)",nativeQuery = true)
      List<Product> searchWithoutTypeDB(Pageable pageable,@Param("key") String key);
      default List<Product> searchWithoutType(String key,int page,int size) {
            return searchWithoutTypeDB(PageRequest.of(page, size),key);
      }
      @Query(value = "select count(*) from Product p where (p.title rlike :key or p.description rlike :key or p.detail rlike :key)",nativeQuery = true)
      int countWithoutType(@Param("key") String key);
}
