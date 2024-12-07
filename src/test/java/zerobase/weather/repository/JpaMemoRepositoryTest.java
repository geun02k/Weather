package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JpaMemoRepositoryTest {
    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void inertMemoTest() {
        // given
        // 강의와 같이 Memo newMemo = new Memo(1, "testText"); 로 객체를 생성하면 오류발생
//        Memo newMemo = new Memo(1, "testText");
        Memo newMemo = new Memo();
        newMemo.setText("testText");

        // when
        // jpaMemoRepository에 save() 메서드를 직접 생성하지 않아도 호출해 사용가능.
        jpaMemoRepository.save(newMemo);

        // then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0);
    }

    @Test
    void findByIdTest() {
        // given
        Memo memo = new Memo();
        memo.setText("findByIdTest text");
        Memo savedMemo = jpaMemoRepository.save(memo);

        // when
        Optional<Memo> resultMemo = jpaMemoRepository.findById(savedMemo.getId());

        // then
        assertEquals(resultMemo.get().getText(), "findByIdTest text");
    }
}