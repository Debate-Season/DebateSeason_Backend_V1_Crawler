package com.debate.croll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.debate.croll.domain.entity.Media;


@Repository
public interface MediaRepository extends JpaRepository<Media,Long> {

}
