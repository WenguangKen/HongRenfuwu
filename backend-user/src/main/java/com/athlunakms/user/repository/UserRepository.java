package com.athlunakms.user.repository;

import com.athlunakms.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository
extends JpaRepository<User, Long>,
JpaSpecificationExecutor<User> {
    public Optional<User> findByEmailHash(String var1);

    public Optional<User> findByUsername(String var1);

    public boolean existsByEmailHash(String var1);
}

