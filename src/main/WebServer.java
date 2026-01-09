package main;

import com.sun.net.httpserver.HttpServer;
import com.sun.tools.javac.parser.ReferenceParser.Model;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Model;
import model.MuseStrategy;
import model.Provider;
import vo.JobPosting;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebServer {
    private static Model model;

    public static void main(String[] args) throws IOException {
        
    }

    static class StaticFileHandler implements HttpHandler{
        @Override
        public static void handle(HttpExchange exchange) throws IOExceptionIO{
            String path = exchange.getRequestURI().getPath();

            if(path.equals("/")){
                path = "/index.html";
            }

            String filePath = "src/view" + path;
            File file = new File(filePath);

            if(file.exists()){
                String contentType = getContentType(filePath);
                byte[] content = Files.readAllBytes(file.toPath());

                exchange.getResponseHeaders().set("Content-Type", contentType);
                exchange.sendResponseHeaders(200, content.length);
                OutputStream os = exchange.getResponseBody();
                os.write(content);
                os.close();
            } else {
                String response = "404 (Not Found)/n";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            return "text/plain";
        }
    }

    static class SearchHandler implements HttpHandler{
        public void handle(HttpExchange exchange) throws IOException{
            Map<String, String> params = parseQuery(exchange.getRequestURI().getQuery());

            String location = params.getOrDefault("location", "Dallas, TX");
            String position = params.getOrDefault("position", "");

            System.out.println("Search request: location=" + location + ", position=" + position);

            List<JobPosting> jobs = model.getJobPostings(location);
            String jsonResponse - convertToJson(jobs);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();
        }
    }
}
