package com.srt.tinyurl_naga.Respository;

import com.srt.tinyurl_naga.model.UrlMapping;
import com.srt.tinyurl_naga.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TinyUrlRepository extends JpaRepository<UrlMapping,Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    List<UrlMapping> findByUser(User user);
}
