package zerobase.weather.domain;

import lombok.*;

// mysql Memo테이블과 매칭되는 Memo클래스
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Memo {
    private int id;
    private String text;
}
