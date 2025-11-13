package com.clara.ops.challenge.document_management_service_challenge.repository;

import com.clara.ops.challenge.document_management_service_challenge.domain.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {

    @Query("SELECT DISTINCT d FROM Document d JOIN d.user u JOIN d.tags t WHERE " +
            "u.name LIKE %:username% AND d.name LIKE %:name% AND t.tag IN :tags " +
            "GROUP BY d.id HAVING COUNT(DISTINCT t.tag) = 1")
    Page<Document> findAllByUsernameAndNameAndTagName(
            String username, String name, List<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Document d JOIN d.user u JOIN d.tags t WHERE " +
            "u.name LIKE %:username% AND d.name LIKE %:name%")
    Page<Document> findAllByUsernameAndName(String username, String name, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Document d JOIN d.user u JOIN d.tags t WHERE " +
            "u.name LIKE %:username% AND t.tag IN :tags " +
            "GROUP BY d.id HAVING COUNT(DISTINCT t.tag) = 1")
    Page<Document> findAllByUsernameAndTags(String username, List<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Document d JOIN d.user u JOIN d.tags t WHERE " +
            "d.name LIKE %:name% AND t.tag IN :tags " +
            "GROUP BY d.id HAVING COUNT(DISTINCT t.tag) = 1")
    Page<Document> findAllByNameAndTags(String name, List<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Document d JOIN d.user u JOIN d.tags t WHERE " +
            "u.username LIKE %:username%")
    Page<Document> findAllByUsername(String username, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Document d JOIN d.user u JOIN d.tags t WHERE " +
            "d.name LIKE %:name%")
    Page<Document> findAllByName(String filename, Pageable pageable);

    @Query("SELECT DISTINCT d FROM Document d JOIN d.user u JOIN d.tags t WHERE " +
            "t.tag IN :tags" +
            "GROUP BY d.id HAVING COUNT(DISTINCT t.tag) = 1")
    Page<Document> findAllByTags(List<String> tags, Pageable pageable);
}
