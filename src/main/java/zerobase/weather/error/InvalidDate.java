package zerobase.weather.error;

public class InvalidDate extends RuntimeException {
    private static final String MESSAGE = "너무 과거 혹은 미래의 날짜입니다.";

    public InvalidDate() {
        // 아래의 RuntimeException(부모) 생성자 호출
        // public RuntimeException(String message) {
        //        super(message);
        //    }
        super(MESSAGE);
    }
}
