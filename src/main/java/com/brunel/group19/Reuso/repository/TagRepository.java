package com.brunel.group19.Reuso.repository;

import com.brunel.group19.Reuso.model.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{
    Tag findByName(String tagName);
    Boolean existsByName(String tagName);
}
