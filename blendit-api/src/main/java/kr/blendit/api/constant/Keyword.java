package kr.blendit.api.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Keyword {
    PRACTICAL_APPLICATION("실무"),
    MENTORING("멘토링"),
    STUDY("스터디"),
    SIDE_PROJECT("사이드프로젝트"),
    TREND("트렌드"),
    PORTFOLIO("포트폴리오"),
    RESUME("자소서"),
    INTERVIEW("면접"),
    CAREER("커리어"),
    JOB_CHANGE("이직"),
    COLLABORATION("협업");

    private final String description;
}
