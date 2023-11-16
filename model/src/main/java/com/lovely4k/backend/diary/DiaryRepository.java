package com.lovely4k.backend.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @Query("select d from Diary d where d.location.kakaoMapId = :kakaoMapId and d.coupleId = :coupleId")
    List<Diary> findByMarker(@Param("kakaoMapId") Long kakaoMapId, @Param("coupleId") Long coupleId);

}
