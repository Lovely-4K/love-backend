package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryQueryRepository;
import com.lovely4k.backend.diary.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.response.DiaryListResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryListByMarkerResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryListResponse;
import com.lovely4k.backend.diary.service.response.WebDiaryMarkerResponse;
import com.lovely4k.backend.location.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiaryQueryService {

    private final DiaryQueryRepository repository;

    public WebDiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId, Long memberId) {

        DiaryDetailResponse diaryDetailResponse = repository.findDiaryDetail(diaryId, coupleId, memberId);
        if (diaryDetailResponse == null) {
            return null;
        }
        return WebDiaryDetailResponse.from(diaryDetailResponse);
    }

    public Page<WebDiaryListResponse> findDiaryList(Long coupleId, Category category, Pageable pageable) {
        Page<DiaryListResponse> pageDiary = repository.findDiaryList(coupleId, category, pageable);

        if (pageDiary.getContent().isEmpty()) {
            return Page.empty();
        }

        return pageDiary.map(WebDiaryListResponse::from);
    }

    public WebDiaryListByMarkerResponse findDiaryListByMarker(Long kakaoMapId, Long coupleId) {
        List<Diary> diaries = repository.findByMarker(kakaoMapId, coupleId);

        if (diaries.isEmpty()) {
            return null;
        } else {
            return WebDiaryListByMarkerResponse.from(
                diaries.get(0),
                diaries.stream().map(
                    WebDiaryMarkerResponse::from
                ).toList());
        }
    }
}
