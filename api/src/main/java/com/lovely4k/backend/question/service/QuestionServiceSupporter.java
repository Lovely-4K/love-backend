package com.lovely4k.backend.question.service;

import com.lovely4k.backend.common.cache.CacheConstants;
import com.lovely4k.backend.common.utils.DateConverter;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.lovely4k.backend.common.error.ExceptionMessage.notFoundEntityMessage;

/**
 * Question을 생성할 때 Couple 테이블을 통해 얻어와야 하는 정보, 검증해야 하는 로직들이 다수 있습니다. 이에 따라 별도의 클래스로 분리했고 db에서 값을 읽어와 couple 테이블의 필요한 데이터 정보를 반환하는 클래스라고 생각하면 될 것 같습니다. 초기에는 Couple 외에 다른 도메인의 정보가 필요할 수도 있다고 생각해서 Supporter라는 네이밍을 했는데 구현을 완료하니 Couple 테이블의 정보만 필요했습니다.
 * */
@RequiredArgsConstructor
@Component
public class QuestionServiceSupporter {

    private final CoupleRepository coupleRepository;

    @Transactional(readOnly = true)
    public long getQuestionDay(Long coupleId) {
        Couple couple = coupleRepository.findById(coupleId)
                .orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("couple", coupleId)));
        return DateConverter.getDurationOfAppUsage(couple.getCreatedDate());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstants.BOY_ID, key = "#coupleId")
    public long getBoyId(Long coupleId) {
        Couple couple = coupleRepository.findById(coupleId)
            .orElseThrow(() -> new EntityNotFoundException(notFoundEntityMessage("couple", coupleId)));
        return couple.getBoyId();
    }
}