package com.athlunakms.influencer.repository;

import com.athlunakms.influencer.entity.Influencer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InfluencerRepository
                extends JpaRepository<Influencer, Long>,
                JpaSpecificationExecutor<Influencer> {
        public List<Influencer> findByEmailContaining(String email);

        public boolean existsByNickNameAndDefaultSocialId(String nickName, Long socialId);

        public boolean existsByEmail(String email);

        public List<Influencer> findByEmailIgnoreCase(String email);

        public Optional<Influencer> findByEmailAndDefaultPlatformAndDefaultHandle(String email, String platform,
                        String handle);

        public Optional<Influencer> findFirstByDefaultPlatformAndDefaultHandle(String platform, String handle);

        @Query(value = "SELECT i.id FROM Influencer i WHERE i.status = :status")
        public List<Long> findIdsByStatus(@Param("status") Influencer.Status status);

        public long countByStatus(Influencer.Status status);

        public long countByTagsContaining(String var1);

        public List<Influencer> findByTagsContaining(String var1);

        public List<Influencer> findByDefaultHandleContainingIgnoreCase(String var1);

        public List<Influencer> findByContactPersonIdIn(List<Long> var1);

        public List<Influencer> findByOwnerIdIn(List<Long> ownerIds);

        public List<Influencer> findByNickNameContainingIgnoreCase(String var1);
        
        public List<Influencer> findByNickNameContainingIgnoreCaseOrRealNameContainingIgnoreCase(String nickName, String realName);

        List<Influencer> findByBackupEmailIgnoreCase(String backupEmail);
}
