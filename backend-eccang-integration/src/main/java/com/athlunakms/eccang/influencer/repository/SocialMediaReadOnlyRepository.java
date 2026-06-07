package com.athlunakms.eccang.influencer.repository;

import com.athlunakms.eccang.influencer.entity.SocialMediaReadOnly;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialMediaReadOnlyRepository extends JpaRepository<SocialMediaReadOnly, Long> {
    @Query("SELECT s FROM SocialMediaReadOnly s WHERE LOWER(s.handle) = LOWER(:handle)")
    List<SocialMediaReadOnly> findByHandleIgnoreCase(@Param("handle") String handle);
}
