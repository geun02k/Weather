package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    // 해당 일자에 대한 모든 일기 조회
    List<Diary> findAllByDate(LocalDate date);

    // 해당 일자 구간에 대한 모든 일기 조회
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    // 해당 일자에 대한 가장 첫번쨰 일기 조회
    Diary getFirstByDate(LocalDate date);
}
