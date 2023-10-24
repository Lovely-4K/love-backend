package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.service.request.DiaryCreateRequest;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public Long createDiary(DiaryCreateRequest diaryCreateRequest, Long memberId) {
        Member member = validateMemberId(memberId);
        Diary diary = diaryCreateRequest.toEntity(member);
        Diary savedDiary = diaryRepository.save(diary);

        return savedDiary.getId();
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("invalid member id")
        );
    }
}
