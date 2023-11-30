package com.lovely4k.backend.member.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MemberRandomUtils {

    private static final SecureRandom random;

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SecureRandom 객체 생성 실패", e);
        }
    }

    private enum MBTIType {
        INFP, ENFP, INFJ, ENFJ, INTJ, ENTJ, INTP, ENTP,
        ISFP, ESFP, ISTP, ESTP, ISFJ, ESFJ, ISTJ, ESTJ
    }

    private enum ColorCode {
        BLUE("#3AB2C2"),
        ORANGE("#FF9029"),
        GREEN("#7EB934"),
        PINK("#EB366C"),
        PURPLE("#7E50DF"),
        INDIGO("#265073");

        private final String hexCode;

        ColorCode(String hexCode) {
            this.hexCode = hexCode;
        }

        public String getHexCode() {
            return hexCode;
        }
    }

    public static String getRandomMBTI() {
        return MBTIType.values()[random.nextInt(MBTIType.values().length)].toString();
    }

    public static String getRandomColor() {
        return ColorCode.values()[random.nextInt(ColorCode.values().length)].getHexCode();
    }
}
