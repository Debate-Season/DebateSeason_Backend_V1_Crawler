package com.debate.croll.test.testService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.debate.croll.domain.entity.MediaKeyWord;

@Repository
public interface MediaKeyWordRepository extends JpaRepository<MediaKeyWord,Long> {
}
