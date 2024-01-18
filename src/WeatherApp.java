import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//retrive weather data from API
public class WeatherApp {
    //fetch weather data from different locations
    public static JSONObject getWeatherData(String locationName) {
        //fetch data from give location
        JSONArray locationData = getLocationData(locationName);

        //extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + 33.767 + "&longitude=" + longitude + -118.1892 +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Europe%2FLondon";

        try {
            //call API and  get response
            HttpURLConnection conn = fetchApiResponse(urlString);
            //check for response status (200)
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Couldn't connect to API");
            }

            //Store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                //read and store into the string builder
                resultJson.append(scanner.nextLine());
            }
            scanner.close();

            conn.disconnect();

            //parse through the data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //in order to get the current hour's data we need the index
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //retrieve geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        //replace any whitespace in a location name to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        //build API url location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            // call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for a successful connnection (200)
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Couldn't connect to API");
                return null;
            } else {
                //store ateh API result
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner((conn.getInputStream()));
                //read and store the resulting json data into the string builder
                while ((scanner.hasNext())) {
                    resultJson.append(scanner.nextLine());
                }
                //close scanner
                scanner.close();
                //close url connection
                conn.disconnect();
                //parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                //get the list of location data the API integrated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            //attempt to create a connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get
            conn.setRequestMethod("GET");

            //connect to my API
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //couldn't make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches the corrent time
        for (int i=0; i<timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return index
                return i;
            }
        }
        return 0;
    }
    public static String getCurrentTime(){
        //get current data and time
        LocalDateTime currentDataTime = LocalDateTime.now();

        //format dataTime : 2024-09-02T00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        //format and print the current data and time
        String formattedDateTime = currentDataTime.format(formatter);
        return formattedDateTime;
    }
}
