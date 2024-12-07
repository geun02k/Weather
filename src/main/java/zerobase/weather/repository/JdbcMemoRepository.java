package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    // JDBC 템플릿을 이용해 JDBC 연결
    private final JdbcTemplate jdbcTemplate;

    // JdbcTemplate 생성자
    @Autowired // DataSource 정보를 알아서 가져오도록 함.
    public JdbcMemoRepository(DataSource dataSource) {
        // DataSource : application.properties에 작성한 spring.datasource 정보
        // DataSource를 이용해 JdbcTemplate 생성
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo) { // 스프링부트에 생성한 memo객체를 mysqlDB에 저장
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    public List<Memo> findAll() {
        String sql = "select * from memo";
        // ResultSet 형태의 반환값을 Memo 형태의 객체로 가져옴
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    // Optional 객체 : 데이터가 null값인 경우 Optional 객체로 랩핑해 혹시 모를 null 값을 처리하게 함.
    public Optional<Memo> findById(int id) {
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    // JDBC를 통해 mySQL의 데이터를 가져오면 가져온 데이터는 ResultSet 형식의 데이터로 받아온다.
    // ResultSet = {id = 1, text = 'this is memo'} 형태.
    // RowMapper<Memo> : ResultSet을 Memo 타입으로 맵핑.
    // Memo 클래스의 AllArgsConstructor를 이용해 객체생성.
    private RowMapper<Memo> memoRowMapper() {
        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
