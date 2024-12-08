package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zerobase.weather.domain.Diary;
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
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey;

    private final DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    /**
     * 날씨일기저장
     * 1. OpenWeahterMap에서 SpringBoot로 데이터 받아오기
     * 2. 받아온 json 데이터 사용 가능하게 파싱하기
     * 3. SpringBoot에서 DB로 데이터 저장하기
     * @param date 일자
     * @param text 일기내용
     */
    public void createDiary(LocalDate date, String text) {
        // 1. OpenWeahterMap에서 SpringBoot로 데이터 받아오기
        String weatherData = getWeatherString();
        System.out.println(weatherData);

        // 2. 받아온 json 데이터 사용 가능하게 파싱하기
        // url을 통해 받아온 json 데이터는 20개가 넘는 다량의 데이터를 가지고 있다.
        // 이 20개의 정보들을 명시한 class를 만들어 데이터를 담을수도 있지만
        // 이는 사용하지 않을 데이터들을 담기 떄문에 비효율적이다.
        // 따라서 모든걸 명시한 클래스를 만들어 url 요청으로 받은 json 데이터를
        // 해당 클래스타입으로 변환할 수도 있지만
        // 이럴 때는 큰 class를 만드는 것이 아닌
        // json 데이터를 파싱해와 class에 담는 방식이 더 좋아보인다.
        Map<String, Object> parsedWeather = parseWeather(weatherData);

        // 3. SpringBoot에서 DB로 데이터 저장하기
        Diary newDiary = new Diary();
        newDiary.setWeather(parsedWeather.get("main").toString());
        newDiary.setIcon(parsedWeather.get("icon").toString());
        newDiary.setTemperature((Double) parsedWeather.get("temp"));
        newDiary.setText(text);
        newDiary.setDate(date);
        diaryRepository.save(newDiary);
    }

    /**
     * 날짜에 대한 날씨일기 목록조회
     * @param date 일자
     * @return Diary 객체 목록
     */
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    /**
     * 날짜 구간에 대한 날씨일기 목록조회
     * @param startDate 시작일자
     * @param endDate 종료일자
     * @return Diary 객체 목록
     */
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
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
