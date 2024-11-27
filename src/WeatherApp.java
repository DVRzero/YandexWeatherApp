import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class WeatherApp {

    private static final String HEADER = "X-Yandex-Weather-Key";
    private static final String API_KEY = "ee3911c7-a794-444d-bf6e-d4bae75b3ae9";
    private static final String BASE_URL = "https://api.weather.yandex.ru/v2/forecast";;
    private static final String LAT_LON = "?lat=%s&lon=%s";
    private static final String LIMIT = "&limit=%s";
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            System.out.println(getWeather(setCoordinates(), setLimit()));
        }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }

    }

    public static String setCoordinates(){
        System.out.println("Введи координаты дней:");
        System.out.print("LAT:");
        String lat = scanner.next();
        System.out.print("LON:");
        String lon = scanner.next();
        return String.format(LAT_LON, lat, lon);
    }

    public static String setLimit(){
        System.out.print("Введи количество дней:");
        String days = scanner.next();
        return String.format(LIMIT, days);
    }

    public static String getWeather(String coordinates, String limit) {
        String responseBody1 = connect(coordinates);
        System.out.println(responseBody1);

        String temp = parseJsonBody(responseBody1, "temp");
        System.out.println("Температура сегодня в заданных координатах равна: " + temp);
        System.out.println();

        String responseBody2 = connect(coordinates + String.format(LIMIT, limit));
        String tempAvg = parseJsonBody(responseBody2, "temp_avg");
        System.out.println("temp_avg: " + tempAvg);

        return "";
    }

    private static String connect(String pathVariables) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + pathVariables))
                .header(HEADER, API_KEY)
                .headers("Content-Type", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String parseJsonBody(String body, String keyJson) {
        body = body.trim();
        if (!body.startsWith("{") || !body.endsWith("}")) return "";

        body = body.substring(1, body.length() - 1);
        String[] pairs = body.split(",");
        for (String pair : pairs) {
            String[] parts = pair.split(":");
            if (parts.length != 2) continue; //пропускаем неправильно сформированные пары

            String key = parts[0].trim().replaceAll("^\"|\"$", "");
            String value = parts[1].trim().replaceAll("^\"|\"$", "");

            if (key.equals(keyJson)) return value;
        }
        return "";
    }


}