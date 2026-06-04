package com.athlunakms.user.service;

import com.athlunakms.user.entity.UserLog;
import com.athlunakms.user.repository.UserLogRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLogService {
    private final UserLogRepository userLogRepository;

    @Transactional(readOnly=true)
    public Page<UserLog> getUserLogs(Long userId, String actionType, String keyword, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return this.userLogRepository.findAll((Specification<UserLog>)(root, query, cb) -> {
            ArrayList<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(root.get("userId"), userId));
            if (actionType != null && !actionType.isEmpty()) {
                predicates.add(cb.equal(root.get("actionType"), actionType));
            }
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword + "%";
                predicates.add(cb.or(cb.like(root.get("actionDesc"), pattern), cb.like(root.get("actionType"), pattern)));
            }
            if (startTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startTime));
            }
            if (endTime != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endTime));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional(readOnly=true)
    public Page<UserLog> getLogsByActionType(String actionType, Pageable pageable) {
        return this.userLogRepository.findByActionTypeOrderByCreatedAtDesc(actionType, pageable);
    }

    @Transactional(readOnly=true)
    public Page<UserLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        return this.userLogRepository.findByTimeRange(startTime, endTime, pageable);
    }

    public UserLogService(UserLogRepository userLogRepository) {
        this.userLogRepository = userLogRepository;
    }
}

