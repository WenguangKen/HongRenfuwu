package com.athlunakms.role.repository;

import com.athlunakms.role.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository
extends JpaRepository<Role, Long> {
    public Optional<Role> findByName(String var1);

    public boolean existsByName(String var1);
}

