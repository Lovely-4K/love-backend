package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.diary.DiaryQueryRepository;
import com.lovely4k.backend.diary.service.response.*;
import com.lovely4k.backend.location.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiaryQueryService {

    private final DiaryQueryRepository repository;

    @Cacheable(value = "diaryDetail", key = "#coupleId + '_' + #diaryId")
    public WebDiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId, Long memberId) {

        return WebDiaryDetailResponse.from(repository.findDiaryDetail(diaryId, coupleId, memberId));
    }

    @Cacheable(value = "diaryList", key = "#coupleId + '_' + #category + '_' + #pageable")
    public Page<WebDiaryListResponse> findDiaryList(Long coupleId, Category category, Pageable pageable) {

        return WebDiaryListResponse.from(repository.findDiaryList(coupleId, category, pageable));
    }

    @Cacheable(value = "diaryMarker", key = "#coupleId + '_' + #kakaoMapId")
    public WebDiaryListByMarkerResponse findDiaryListByMarker(Long kakaoMapId, Long coupleId) {

        return WebDiaryListByMarkerResponse.from(repository.findByMarker(kakaoMapId, coupleId));
    }

    @Cacheable(value = "diaryGrid", key = "#coupleId + '_' + #rLatitude + '_' + #rLongitude + '_' + #lLatitude + '_' + #lLongitude ")
    public DiaryListInGridResponse findDiaryListInGrid(BigDecimal rLatitude, BigDecimal rLongitude, BigDecimal lLatitude, BigDecimal lLongitude, Long coupleId) {

        return new DiaryListInGridResponse(repository.findDiaryListInGrid(rLatitude, rLongitude, lLatitude, lLongitude, coupleId)
            .stream().map(DiaryGridResponse::from).toList());
    }
}
