package org.marketcetera.webui.data.service;

import java.util.UUID;
import org.marketcetera.webui.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByUsername(String username);
}