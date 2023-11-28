package com.lovely4k.backend.member.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomUtils {

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
        COLOR1("#378F09"),
        COLOR2("#F32BC9"),
        COLOR3("#EDB029"),
        COLOR4("#D47E2A"),
        COLOR5("#1498CA"),
        COLOR6("#BE15BD"),
        COLOR7("#78F0BE"),
        COLOR8("#360926"),
        COLOR9("#8C3BC0"),
        COLOR10("#B5A762");

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
