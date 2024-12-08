package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DateWeatherRepository dateWeatherRepository;
    private final DiaryRepository diaryRepository;

    // Logger 생성
    // LoggerFactory.getLogger() 떤 클래스에서 Logger 객체를 가져올지 지정.
    // 우리는 Logger를 프로젝트 전체에 하나만 만들어서 사용
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    public DiaryService(DateWeatherRepository dateWeatherRepository, DiaryRepository diaryRepository) {
        this.dateWeatherRepository = dateWeatherRepository;
        this.diaryRepository = diaryRepository;
    }

    /**
     * 매일 새벽 1시 OpenWeatherMap api 호출해 현재일자에 대한 날씨정보 DB에 저장
     * (스케줄링을 통한 서버에서 캐싱)
     */
    @Transactional(readOnly = false)
    @Scheduled(cron="0 0 1 * * *") // 매일 새벽 1시
    public void saveWeatherDate() {
        DateWeather savedWeather = dateWeatherRepository.save(getWeatherFromApi());
        logger.info(savedWeather.getDate().toString() + " 날씨데이터 잘 가져옴.");
    }

    /**
     * 날씨일기저장
     * 1. 날씨 데이터 가져오기
     * 2. DBd에 일기 데이터 저장하기
     * @param date 일자
     * @param text 일기내용
     */
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary.");
        // 1. 날씨 데이터 가져오기
        DateWeather dateWeather = getDateWeather(date);

        // 2. DBd에 일기 데이터 저장하기
        Diary newDiary = new Diary();
        newDiary.setDateWeather(dateWeather);
        newDiary.setText(text);
        newDiary.setDate(date);
        diaryRepository.save(newDiary);
        logger.info("end to create diary.");
        // createDiary() 메서드 호출 시 logs 폴더의 log_file.log 파일에 아래의 두 로그 출력됨.
//        [2024-12-09 04:48:44:28242][http-nio-8080-exec-1] INFO  z.weather.WeatherApplication - started to create diary.
//        [2024-12-09 04:48:45:29280][http-nio-8080-exec-1] INFO  z.weather.WeatherApplication - end to create diary.
    }

    /**
     * 날짜에 대한 날씨일기 목록조회
     * @param date 일자
     * @return Diary 객체 목록
     */
    public List<Diary> readDiary(LocalDate date) {
        logger.debug("read diary.");
        // 3050년이 넘는 일자를 넣었을 때
        if(date.isAfter(LocalDate.ofYearDay(3050, 1))) {
            throw new InvalidDate();
        }
        return diaryRepository.findAllByDate(date);
    }

    /**
     * 날짜 구간에 대한 날씨일기 목록조회
     * @param startDate 시작일자
     * @param endDate 종료일자
     * @return Diary 객체 목록
     */
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        logger.debug("read diaries.");
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    /**
     * 해당 날짜에 대해 가장 첫번째 일기 수정
     * @param date 일자
     * @param text 신규 일기내용
     */
    @Transactional(readOnly = false)
    public void updateDiary(LocalDate date, String text) {
        logger.debug("started to update diary.");
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);

        diaryRepository.save(nowDiary);
        logger.debug("end to update diary.");
    }

    /**
     * 해당 날짜에 대한 모든 일기 삭제
     * @param date 일자
     */
    @Transactional(readOnly = false)
    public void deleteDiary(LocalDate date) {
        logger.debug("started to delete diary.");
        diaryRepository.deleteAllByDate(date);
        logger.debug("end to delete diary.");
    }

    private DateWeather getDateWeather(LocalDate date) {
        // 해당 날씨 데이터 DB에 존재여부 확인
        List<DateWeather> dateWeatherListFromDB =
                dateWeatherRepository.findAllByDate(date);
        if(dateWeatherListFromDB.size() == 0) {
            // 새로 api 호출해 날씨 정보 가져오기
            return getWeatherFromApi();
        } else {
            return dateWeatherListFromDB.get(0);
        }
    }

    private DateWeather getWeatherFromApi() {
        // 1. 외부 api에서 전일 날씨 데이터 얻어오기
        String weatherData = getWeatherString();

        // 2. 받아온 json 데이터 사용 가능하게 파싱하기
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        // 3. DateWeather 타입의 객체에 데이터 저장
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        return dateWeather;
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?"
                            +"q=seoul&appid=" + apiKey;
        System.out.println(apiUrl);

        try {
            // 1. 문자열 url 정보를 담은 java의 URL클래스 객체 생성
            //    -> 단순 문자열을 url로 사용가능
            URL url = new URL(apiUrl);
            // 2. url 객체를 HTTP 형식으로 연결
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader br; // BufferedReader : 데이터가 길 때 사용 시 속도 및 성능 향상
            int responseCode = connection.getResponseCode(); // HTTP 요청에 대한 응답코드 받기.
            if(responseCode == 200) {
                // 응답데이터를 받아 br에 저장
                br =  new BufferedReader(new InputStreamReader(connection.getInputStream()));
            } else {
                // 응답에러를 받아 br에 저장
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            // br(BufferedReader)을 읽어들여 response(StringBuilder)에 결과값들 저장
            while((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            return response.toString();

        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            // 단순 예외만 발생(예외를 처리해서 없애는 것이 아님!!)
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();

        // 중괄호가 아닌 대괄호로 시작하는 json 데이터는 JSONObject가 아닌 JSONArray이다.
        // 따라서 파싱방법이 다르다.
        // JSONArray 형태이지만 row는 단 하나이다. 따라서 0번째 객체를 가져오면 된다.
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        return resultMap;
    }
}
