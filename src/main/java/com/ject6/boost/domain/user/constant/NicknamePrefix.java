package com.ject6.boost.domain.user.constant;

import java.security.SecureRandom;

public enum NicknamePrefix {
    GOD_LIFE("갓생"),
    TWO_JOB("투잡"),
    N_JOB("N잡"),
    SIDE_CHARACTER("부캐"),
    HARD_WORKING("열일"),
    EXTRA_SALARY("월급외"),
    GROWTH("성장"),
    STEADY("꾸준"),
    SINCERE("성실"),
    CHALLENGE("도전"),
    LUCKY("행운"),
    WISE("슬기로운"),
    FLEXIBLE("유연한"),
    SENSIBLE("센스있는"),
    SOLID("야무진"),
    RELIABLE("든든한"),
    SHINING("빛나는"),
    SPARKLING("반짝이는"),
    TALENTED("능력자"),
    PRO("프로");

    private final String value;

    NicknamePrefix(String value) {
        this.value = value;
    }

    /**
     * 랜덤 닉네임 prefix 표시값을 반환하는 함수.
     */
    public static String randomValue(SecureRandom random) {
        NicknamePrefix[] values = values();
        return values[random.nextInt(values.length)].value;
    }
}
