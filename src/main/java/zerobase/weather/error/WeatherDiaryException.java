package zerobase.weather.error;


import lombok.*;
import zerobase.weather.type.ErrorCode;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherDiaryException extends RuntimeException {
    private ErrorCode errorCode;
    private String errorMessage;

    public WeatherDiaryException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
