package zerobase.weather.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR("서버 오류입니다."),
    TOO_LONG_OR_TOO_SHORT_DATE("너무 과거 혹은 미래의 날짜입니다."),
    API_KEY_NOT_EXIST("api-key가 존재하지 않습니다.\n" +
            "해당 프로젝트는 공개프로젝트로 OpenWeatherMap api-key를 노출하지 않기 위해 제거된 상태입니다.\n" +
            "테스트를 수행하기 위해 application.properties의 openweathermap.key값을 입력해주세요.")
    ;

    private final String description;
}
