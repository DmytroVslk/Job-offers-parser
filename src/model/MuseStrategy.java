package model;

import vo.JobPosting;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MuseStrategy implements Strategy{
    private static final String API_BASE = "https://www.themuse.com/api/public/jobs";

    @Override
    public List<JobPosting> getJobPostings(String searchString) {
        List<JobPosting> allVacancies = new ArrayList<>(); 

        try{
            // Запит декількох сторінок
            for(int page = 1; page <= 3; page++) {
                //Build URL
                String urlString = API_BASE + 
                    "?category=Software%20Engineering" + 
                    "&level=Junior%20Level" + 
                    "&location=" + 
                    URLEncoder.encode(searchString, "UTF-8") + 
                    "&page=" + page + "&page_size=50";
                
                System.out.println("Requesting page " + page + ": " + urlString);

                //HTTP GET Request
                URL url = URI.create(urlString).toURL();
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
                
                System.out.println("Number of job postings found on page " + page + ": " + result.length());

                // Якщо результатів немає, припиняємо запити
                if(result.length() == 0) {
                    break;
                }

                for(int i = 0; i < result.length(); i++){
                    JSONObject job = result.getJSONObject(i);
                    JobPosting vacancy = extractJobPosting(job);
                    allVacancies.add(vacancy);
                }
            }
        } catch (Exception e){
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return allVacancies;
    }

    // Extract and set job details
    private JobPosting extractJobPosting(JSONObject job){
        JobPosting vacancy = new JobPosting();

        //Title
        vacancy.setTitle(job.optString("name", "N/A"));

        //URL - Use landing_page from refs object
        String jobUrl = "";
        JSONObject refs = job.optJSONObject("refs");
        if(refs != null){
            jobUrl = refs.optString("landing_page", "");
        }
        if(jobUrl.isEmpty()){
            jobUrl = "https://www.themuse.com/jobs/" + job.optString("id", "");
        }
        vacancy.setUrl(jobUrl);

        //Company
        JSONObject company = job.optJSONObject("company");
        if(company != null){
            vacancy.setCompanyName(company.optString("name", "Unknown"));
        } else {
            vacancy.setCompanyName("Unknown");   
        }

        //Location
        JSONArray locations = job.optJSONArray("locations");
        if(locations != null && locations.length() > 0){
            JSONObject location = locations.getJSONObject(0);
            vacancy.setCity(location.optString("name", "Remote"));
        } else {
            vacancy.setCity("Remote");

        }

        vacancy.setWebsiteName("themuse.com");

        return vacancy;
    }
}

