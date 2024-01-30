package com.hgc.shortenerbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hgc.shortenerbackend.entity.Link;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByLinkAcortado(String linkAcortado);    // Getters y setters
    
}
