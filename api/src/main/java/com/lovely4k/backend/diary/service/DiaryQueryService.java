package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.common.cache.CacheConstants;
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

    @Cacheable(value = CacheConstants.DIARY_DETAILS, key = "#coupleId + '_' + #diaryId")
    public WebDiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId, Long memberId) {

        return WebDiaryDetailResponse.from(repository.findDiaryDetail(diaryId, coupleId, memberId));
    }

    @Cacheable(value = CacheConstants.DIARY_LIST, key = "#coupleId + '_' + #category + '_' + #pageable")
    public Page<WebDiaryListResponse> findDiaryList(Long coupleId, Category category, Pageable pageable) {

        return WebDiaryListResponse.from(repository.findDiaryList(coupleId, category, pageable));
    }

    @Cacheable(value = CacheConstants.DIARY_MARKER, key = "#coupleId + '_' + #kakaoMapId")
    public WebDiaryListByMarkerResponse findDiaryListByMarker(Long kakaoMapId, Long coupleId) {

        return WebDiaryListByMarkerResponse.from(repository.findByMarker(kakaoMapId, coupleId));
    }

    @Cacheable(value = CacheConstants.DIARY_GRID, key = "#coupleId + '_' + #rLatitude + '_' + #rLongitude + '_' + #lLatitude + '_' + #lLongitude ")
    public DiaryListInGridResponse findDiaryListInGrid(BigDecimal rLatitude, BigDecimal rLongitude, BigDecimal lLatitude, BigDecimal lLongitude, Long coupleId) {

        return new DiaryListInGridResponse(repository.findDiaryListInGrid(rLatitude, rLongitude, lLatitude, lLongitude, coupleId)
            .stream().map(DiaryGridResponse::from).toList());
    }
}