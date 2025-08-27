package com.cjh.watching.watchback.enums;

/**
 * - @author Cjh。
 * - @date 2025/8/27 16:07。
 **/
public enum MovieStatusEnum {
    HASCOLLECTED("已收藏", 1),
    HASWATCHED("已观看", 2),
    WANTWATCH("想看", 3),
            ;

    private final String code;
    private final Integer value;

    MovieStatusEnum(String code, Integer value) {
        this.code = code;
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public static MovieStatusEnum fromCode(String code) {
        for (MovieStatusEnum type : MovieStatusEnum.values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ProcurePeriodType code: " + code);
    }

    public static MovieStatusEnum fromValue(String value) {
        for (MovieStatusEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid ProcurePeriodType value: " + value);
    }

    public static String getCodeByValue(String value) {
        return fromValue(value).getCode();
    }

    public static Integer getValueByCode(String code) {
        return fromCode(code).getValue();
    }
}
