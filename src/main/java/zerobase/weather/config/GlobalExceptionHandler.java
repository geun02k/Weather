package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.weather.dto.ErrorResponse;
import zerobase.weather.error.WeatherDiaryException;

import static zerobase.weather.type.ErrorCode.INTERNAL_SERVER_ERROR;

@RestControllerAdvice()
public class GlobalExceptionHandler {
    // @ResponseStatus
    // 예외가 발생한 시점에 클라이언트에서 서버로 요청했을 때 발생할 수 있다.
    // 따라서 해당 예외 발생 시 어떤 응답 상태코드를 전달할지 지정할 수 있다.
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    @ExceptionHandler(WeatherDiaryException.class)
    public ErrorResponse handleWeatherDiaryException(WeatherDiaryException e) {
        System.out.println("errorMessage => " + e.getErrorMessage());
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAllException(Exception e) {
        System.out.println(e);
        return new ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getDescription());
    }
}
