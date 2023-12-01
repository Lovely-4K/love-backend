package com.lovely4k.backend.diary;

import com.lovely4k.backend.diary.response.DiaryDetailResponse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    @EntityGraph(attributePaths = {"location"})
    @Query("select d from Diary d where d.location.kakaoMapId = :kakaoMapId and d.coupleId = :coupleId")
    List<Diary> findByMarker(Long kakaoMapId, Long coupleId);

    @EntityGraph(attributePaths = {"location"})
    @Query("""
    select d from Diary d where
     d.coupleId = :coupleId 
     and d.location.latitude between :lLatitude and :rLatitude
     and d.location.longitude between :lLongitude and :rLongitude
    """)
    List<Diary> findInGrid(BigDecimal rLatitude, BigDecimal rLongitude, BigDecimal lLatitude, BigDecimal lLongitude, Long coupleId);

    @Query("""
        SELECT  new com.lovely4k.backend.diary.response.DiaryDetailResponse(
        l.kakaoMapId, d.datingDay, d.score, l.category,
        CASE WHEN c.boyId = :memberId THEN d.boyText ELSE d.girlText END,
        CASE WHEN c.boyId = :memberId THEN d.girlText ELSE d.boyText END,
        d.photos.firstImage, d.photos.secondImage, d.photos.thirdImage, d.photos.fourthImage, d.photos.fifthImage,
        l.placeName, l.latitude, l.longitude)
        FROM Diary d
        JOIN d.location l
        JOIN Couple c ON d.coupleId = c.id
        WHERE d.id = :diaryId AND d.coupleId = :coupleId
         """)
    DiaryDetailResponse findDiaryDetail(@Param("diaryId") Long diaryId,
                                        @Param("coupleId") Long coupleId,
                                        @Param("memberId") Long memberId);


}
