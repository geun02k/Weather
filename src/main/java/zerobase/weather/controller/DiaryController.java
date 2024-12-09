package zerobase.weather.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }


    @ApiOperation(value="일기 텍스트와 날씨를 이용해 DB에 일기를 저장.", notes="상세내용을 작성합니다.")
    @PostMapping("/create/diary")
    void createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text) {
        // date 포맷 = 2024-05-22
        diaryService.createDiary(date, text);
    }

    @ApiOperation(value="선택한 날짜의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diary")
    List<Diary> readDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return diaryService.readDiary(date);
    }

    @ApiOperation(value="선택한 기간 중의 모든 일기 데이터를 가져옵니다.")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value="조회할 기간의 첫번째 날 입니다.\n날짜형식 : yyyy-MM-dd", example="2020-02-02")
                LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value="조회할 기간의 마지막 날 입니다.\n날짜형식 : yyyy-MM-dd", example="2020-02-02")
                LocalDate endDate) {
        return diaryService.readDiaries(startDate, endDate);
    }

    @ApiOperation(value="일기 신규 텍스트와 날짜를 입력받아 DB의 일기를 수정.")
    @PutMapping("/update/diary")
    void updateDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text) {
        diaryService.updateDiary(date, text);
    }

    @ApiOperation(value="날짜를 입력받아 해당 날짜에 대한 DB의 일기를 삭제.")
    @DeleteMapping("/delete/diary")
    void deleteDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        diaryService.deleteDiary(date);
    }
}
