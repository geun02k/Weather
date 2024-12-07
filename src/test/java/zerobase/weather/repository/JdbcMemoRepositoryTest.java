package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
// @Transactional
// : 테스트코드로 인해 데이터베이스의 정보가 변경되지 않도록 하기 위한 어노테이션.
//   테스트코드를 실행 후 DB를 원상태로 복구한다.
@SpringBootTest
@Transactional
class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest() {
        // given
        Memo newMemo = new Memo(1, "insertMemoTest");

        // when
        Memo savedMemo = jdbcMemoRepository.save(newMemo);

        // then
        assertEquals(1, savedMemo.getId());
        assertEquals("insertMemoTest", savedMemo.getText());
    }

    @Test
    void findAllMemoTest() {
        // given
        Memo newMemo1 = new Memo(1, "findAllMemoTest memo1");
        Memo newMemo2 = new Memo(2, "findAllMemoTest memo2");
        // 이거 jdbcMemoRepository.save(newMemo) 에 의존하게 되는데 괜찮나?
        jdbcMemoRepository.save(newMemo1);
        jdbcMemoRepository.save(newMemo2);

        // when
        List<Memo> memoList = jdbcMemoRepository.findAll();

        // then
        System.out.println(memoList.get(0));
        System.out.println(memoList.get(1));
        assertTrue(memoList.size() == 2);
    }
}