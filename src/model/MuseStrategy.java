package model;

import vo.JobPosting;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MuseStrategy implements Strategy{
    private static final String API_BASE = "https://www.themuse.com/api/public/jobs";

    @Override
    public List<JobPosting> geJobPostings(String searchString){
        List<JobPosting> allVacancies = new ArrayList<>(); 

        try{
            //Build URL
            String urlString = API_BASE + 
                "?category=Software%20Engineering" + 
                "&level=Entry%20Level, Mid%20Level, Senior%20Level" + 
                "&location=" + 
                URLEncoder.encode(searchString, "UTF-8") + "&page=1";
            
            System.out.println("Requesting URL: " + urlString);

            //HTTP GET Request
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            //Read Response
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            //Parse JSON Response
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray result = jsonResponse.getJSONArray("results");
            
            System.out.println("Number of job postings found: " + result.length());

            for(int i = 0; i < result.length(); i++){
                JSONObject job = result.getJSONObject(i);
            }






        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
}
