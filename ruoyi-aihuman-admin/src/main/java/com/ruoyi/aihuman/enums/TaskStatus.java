package com.ruoyi.aihuman.enums;

/**
 * 任务状态枚举
 */
public enum TaskStatus {
    PENDING("0", "待处理"),
    PROCESSING("1", "处理中"),
    COMPLETED("2", "已完成"),
    FAILED("3", "失败");

    private final String value;
    private final String description;

    TaskStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

}