package com.ruoyi.aihuman.enums;

/**
 * 任务状态枚举
 */
public enum ClusterStatus {
    AbNormal("0", "异常"),
    Normal("1", "正常");

    private final String value;
    private final String description;

    ClusterStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ClusterStatus fromValue(String value) {
        for (ClusterStatus status : ClusterStatus.values()) {
            if (status.getValue().equals(value)){
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ClusterStatus value: " + value);
    }

    public static String setStatus(ClusterStatus status) {
        return status.getValue();
    }
}