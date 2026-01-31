package kr.blendit.api.blending.constant;

public enum BlendingStatus {
    RECRUITING("모집중"),
    RECRUITMENT_CLOSED("마감"),
    COMPLETED("종료"),
    CANCELED("취소");

    private final String description;

    BlendingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
