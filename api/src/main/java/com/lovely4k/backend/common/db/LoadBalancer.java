package com.lovely4k.backend.common.db;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class LoadBalancer {


    /** master db의 가중치가 1, slave db의 가중치가 2인 경우를 예를 들어 설명하겠다.
     * case 1, randomWeight가 0인 경우
     * weightSum은 처음 2이 된다.
     * weightSum(2)이 randomWeight(0)보다 크므로 master 반환
     * caes 2, randomWeight가 1인 경우
     * weightSum은 처음 1이 된다.
     * weightSum(1)이 ranndomWeight(1)보다 크지 않으므로 다음 for문으로 감.
     * weightSum이 3이 된다.
     * weightSum(3)이 randomWeight(1)보다 크므로 slave 반환
     * case 3, randomWeight가 2인 경우
     * weightSum은 처음 1이 된다.
     * weightSum(1)이 ranndomWeight(2)보다 크지 않으므로 다음 for문으로 감.
     * weightSum이 3이 된다.
     * weightSum(3)이 randomWeight(2)보다 크므로 slave 반환
     * 즉 master와 slave는 1:2의 비율로 반환된다. db가 여러개 붙더라도 그에 걸맞는 가중치에 맞는 비율로 반환한다.
     **/
    public static DatabaseType weightRoundRobin() {
        Random random = new SecureRandom();

        DatabaseType[] types = DatabaseType.values();
        int totalWeight = Arrays.stream(types).mapToInt(DatabaseType::getWeight).sum(); //총 가중치 계산 5
        int randomWeight = random.nextInt(totalWeight);//0 ~ totalweight-1 증 랜덤한 수 생성 0 ~ 2
        int weightSum = 0;
        for (DatabaseType type : types) {
            weightSum += type.getWeight(); // 가중치 한개 더하기. master 가중치는 1이므로
            if (randomWeight < weightSum) { // 만약 랜덤한 숫자가
                return type;
            }
        }

        return DatabaseType.MASTER;
    }
}