package com.srt.tinyurl_naga.Respository;

import com.srt.tinyurl_naga.model.UrlMapping;
import com.srt.tinyurl_naga.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TinyUrlRepository extends JpaRepository<UrlMapping,Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    List<UrlMapping> findByUser(User user);
    Page<UrlMapping> findByUserId(Long userId, Pageable pageable);


    @Query("""
   SELECT u FROM UrlMapping u
   WHERE u.user.id = :userId
     AND (
       LOWER(u.shortCode) LIKE %:q%
       OR LOWER(u.originalUrl) LIKE %:q%
     )
""")
    Page<UrlMapping> search(
            @Param("userId") Long userId,
            @Param("q") String q,
            Pageable pageable
    );

    boolean existsByShortCode(String shortCode);
}
