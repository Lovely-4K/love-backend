package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.common.cache.CacheEvictedEvent;
import com.lovely4k.backend.common.event.Events;
import com.lovely4k.backend.common.imageuploader.ImageUploader;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.IncreaseTemperatureEvent;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.Photos;
import com.lovely4k.backend.diary.controller.request.DiaryDeleteRequest;
import com.lovely4k.backend.diary.service.request.DiaryCreateRequest;
import com.lovely4k.backend.diary.service.request.DiaryEditRequest;
import com.lovely4k.backend.member.Sex;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final CoupleRepository coupleRepository;
    private final ImageUploader imageUploader;

    @Transactional
    public Long createDiary(List<MultipartFile> multipartFileList, DiaryCreateRequest diaryCreateRequest, Long memberId, Long coupleId) {
        Couple couple = getCouple(coupleId);
        Sex sex = couple.getCoupleRole(memberId);
        checkCountOfImage(multipartFileList);
        List<String> uploadedImageUrls = uploadImages(multipartFileList);
        Diary diary = diaryCreateRequest.toEntity(coupleId, sex);
        diary.addPhoto(Photos.create(uploadedImageUrls));
        Diary savedDiary = diaryRepository.save(diary);

        Events.raise(new IncreaseTemperatureEvent(coupleId));
        Events.raise(new CacheEvictedEvent(coupleId.toString(), List.of(CacheConstants.DIARY_LIST, CacheConstants.DIARY_MARKER, CacheConstants.DIARY_GRID)));
        return savedDiary.getId();
    }

    private void checkCountOfImage(List<MultipartFile> multipartFileList) {
        if (multipartFileList == null) {
            return;
        }
        if (multipartFileList.size() > 5) {
            throw new IllegalArgumentException("Image file can be uploaded maximum 5");
        }
    }

    @Transactional
    public void editDiary(Long diaryId, List<MultipartFile> multipartFileList, DiaryEditRequest request, Long coupleId, Long memberId) {
        Diary diary = getDiary(diaryId);
        diary.checkAuthority(coupleId);
        List<String> editedImageUrls = new ArrayList<>();

        addRemainImages(request, editedImageUrls);
        checkNumberOfImages(editedImageUrls, multipartFileList);
        addUploadedImages(multipartFileList, editedImageUrls);
        deleteImageFromS3(request, diary);
        Sex sex = getCouple(coupleId).getCoupleRole(memberId);

        diary.update(sex, request.score(), request.datingDay(), request.category(), request.text(), editedImageUrls);
        Events.raise(new CacheEvictedEvent(coupleId.toString(), List.of(CacheConstants.DIARY_DETAILS, CacheConstants.DIARY_LIST, CacheConstants.DIARY_MARKER, CacheConstants.DIARY_GRID)));
    }

    private Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
            () -> new EntityNotFoundException("invalid diary id")
        );
    }

    private void addRemainImages(DiaryEditRequest request, List<String> editedImageUrls) {
        if (request.images() != null) {
            editedImageUrls.addAll(request.images());
        }
    }

    private void addUploadedImages(List<MultipartFile> multipartFileList, List<String> editedImageUrls) {
        editedImageUrls.addAll(uploadImages(multipartFileList));
    }

    private void checkNumberOfImages(List<String> editedImageUrls, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            return;
        }
      
        if (editedImageUrls.size() + multipartFiles.size() > 5) {
            throw new IllegalArgumentException("이미지는 최대 5개를 넘길 수 없습니다.");
        }
    }

    private void deleteImageFromS3(DiaryEditRequest request, Diary diary) {
        if (request.images() != null) {
            if (diary.getPhotos() == null) {
                throw new IllegalArgumentException("잘못된 요청입니다. 당사의 API docs를 다시 읽어주세요.");
            }
            deleteImages(diary.getPhotos().getPhotoList()
                .stream().filter(
                    imageUrl -> !request.images().contains(imageUrl)
                ).toList());
        } else {
            if (diary.getPhotos() != null) {
                deleteImages(diary.getPhotos().getPhotoList());
            }
        }
    }


    private Couple getCouple(Long coupleId) {
        return coupleRepository.findById(coupleId).orElseThrow(
            () -> new EntityNotFoundException("invalid couple id")
        );
    }

    @Transactional
    public void deleteDiary(Long diaryId, Long coupleId) {
        Diary diary = getDiary(diaryId);
        diary.checkAuthority(coupleId);
        diaryRepository.delete(diary);

        Events.raise(new CacheEvictedEvent(coupleId.toString(), List.of(CacheConstants.DIARY_DETAILS, CacheConstants.DIARY_LIST, CacheConstants.DIARY_MARKER, CacheConstants.DIARY_GRID)));
    }

    @Transactional
    public void deleteDiaries(DiaryDeleteRequest request, Long coupleId) {
        List<Diary> diaries = request.diaryList().stream().map(
            diaryId -> {
                Diary diary = getDiary(diaryId);
                diary.checkAuthority(coupleId);
                return diary;
            }
        ).toList();

        diaryRepository.deleteAll(diaries);

        Events.raise(new CacheEvictedEvent(coupleId.toString(), List.of(CacheConstants.DIARY_DETAILS, CacheConstants.DIARY_LIST, CacheConstants.DIARY_MARKER, CacheConstants.DIARY_GRID)));
    }

    private List<String> uploadImages(List<MultipartFile> multipartFileList) {
        return imageUploader.upload("diary/", multipartFileList);   // NOSONAR
    }

    private void deleteImages(List<String> imageUrls) {
        if (imageUrls == null) {
            return;
        }
        imageUploader.delete("diary/", imageUrls);
    }
}