package com.example.cloud.respository;

import com.example.cloud.entity.CloudAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CloudAccountRepository
        extends JpaRepository<CloudAccount, UUID> {

    Optional<CloudAccount> findByIdAndUserEmail(
                    UUID id,
                    String email
            );

    List<CloudAccount> findByUserEmail(
            String email
    );
}