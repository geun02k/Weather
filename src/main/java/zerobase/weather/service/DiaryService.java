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
import java.util.Map;

@Service
public class DiaryService {
    @Value("${openweathermap.key}")
    private String apiKey;

    public void createDiary(LocalDate date, String text) {
        // 1. OpenWeahterMap에서 SpringBoot로 데이터 받아오기
        String weatherData = getWeatherString();
        System.out.println(weatherData);

        // 2. 받아온 json 데이터 사용 가능하게 파싱하기

        // 3. SpringBoot에서 DB로 데이터 저장하기
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
}
