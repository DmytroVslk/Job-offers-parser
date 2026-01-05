package model;

import vo.JobPosting;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LinkedinStrategy implements Strategy {

    private static final String URL_FORMAT = "https://www.linkedin.com/jobs/search?keywords=java+%s&start=%d";

    @Override
    public List<JobPosting> getJobPostings(String searchString) {
        List<JobPosting> allVacancies = new ArrayList<>();

        System.out.println("Starting job search for: " + searchString);

        int start = 0;
        try {
            Document doc = getDocument(searchString, start);

            System.out.println("Document title: " + doc.title());
            System.out.println("Document length: " + doc.html().length());

            // Знаходимо елементи вакансій
            Elements vacanciesHtmlList = doc.select("a[href*='/jobs/view/']");
            System.out.println("Found jobPosting elements: " + vacanciesHtmlList.size());

            // Виводимо структуру перших кількох елементів для аналізу
            for (int i = 0; i < Math.min(2, vacanciesHtmlList.size()); i++) {
                Element element = vacanciesHtmlList.get(i);
                System.out.println("=== Element " + (i + 1) + " structure ===");
                System.out.println(element.html());
                System.out.println("=== End structure ===");
            }

            for (Element element : vacanciesHtmlList) {
                try {
                    JobPosting vacancy = extractJobPosting(element);
                    if (vacancy != null) {
                        allVacancies.add(vacancy);
                        System.out.println("Added job: " + vacancy.getTitle() + " at " + vacancy.getCompanyName());
                    }
                } catch (Exception e) {
                    System.out.println("Error processing job element: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Total jobs found: " + allVacancies.size());
        return allVacancies;
    }

    private JobPosting extractJobPosting(Element element) {
        JobPosting vacancy = new JobPosting();
        vacancy.setWebsiteName("linkedin.com");

        // Розширений пошук заголовка
        String title = findText(element,
                "h3 a span[title]", "title",
                "h3 a span", "text",
                "h3 a", "text",
                "[data-test-id='job-title'] a", "text",
                ".job-card-list__title", "text",
                "a[data-tracking-control-name='public_jobs_jserp-result_search-card'] span[title]", "title",
                "a[data-tracking-control-name='public_jobs_jserp-result_search-card']", "text"
        );

        vacancy.setTitle(title.isEmpty() ? "Title not found" : title);

        // Розширений пошук URL
        Elements linkElements = element.select("h3 a, a[data-tracking-control-name='public_jobs_jserp-result_search-card']");
        String jobUrl = "https://linkedin.com";
        if (!linkElements.isEmpty()) {
            String href = linkElements.first().attr("href");
            if (!href.isEmpty()) {
                if (href.startsWith("http")) {
                    jobUrl = href;
                } else {
                    jobUrl = "https://linkedin.com" + href;
                }
            }
        }
        vacancy.setUrl(jobUrl);

        // Розширений пошук компанії
        String company = findText(element,
                "h4 a", "text",
                "[data-test-id='job-company']", "text",
                ".job-card-container__company-name", "text",
                "a[data-tracking-control-name='public_jobs_jserp-result_job-search-card-subtitle']", "text",
                "span.job-card-container__company-name", "text"
        );

        vacancy.setCompanyName(company.isEmpty() ? "Company not found" : company);

        // Розширений пошук локації
        String location = findText(element,
                "[data-test-id='job-location']", "text",
                ".job-card-container__metadata-item", "text",
                "span.job-card-container__metadata-item", "text",
                ".job-search-card__location", "text"
        );

        vacancy.setCity(location.isEmpty() ? "Location not found" : location);

        // Якщо нічого не знайшли, виведемо додаткову інформацію
        if (title.isEmpty() && company.isEmpty() && location.isEmpty()) {
            System.out.println("No data extracted, element classes: " + element.className());
            System.out.println("Element text: " + element.text().substring(0, Math.min(100, element.text().length())));
        }

        return vacancy;
    }

    // Допоміжний метод для пошуку тексту з різними селекторами
    private String findText(Element element, String... selectorTypePairs) {
        for (int i = 0; i < selectorTypePairs.length; i += 2) {
            String selector = selectorTypePairs[i];
            String type = selectorTypePairs[i + 1];

            Elements elements = element.select(selector);
            if (!elements.isEmpty()) {
                Element found = elements.first();
                String result = "";

                if ("text".equals(type)) {
                    result = found.text().trim();
                } else if ("title".equals(type)) {
                    result = found.attr("title").trim();
                }

                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        return "";
    }

    protected Document getDocument(String searchString, int start) throws IOException {
        String url = String.format(URL_FORMAT, searchString, start);
        System.out.println("Requesting URL: " + url);

        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Connection", "keep-alive")
                .timeout(10000)
                .followRedirects(true)
                .get();
    }
}