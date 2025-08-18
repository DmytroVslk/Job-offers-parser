package view;

import main.Controller;
import vo.JobPosting;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.nio.file.*;

public class HtmlView implements View {

    private final String filePath = Paths.get(
            System.getProperty("user.dir"),
            "src", "view", "jobPostings.html"
    ).toString();

    private Controller controller;

    @Override
    public void update(List<JobPosting> vacancies) {
        System.out.println("HtmlView.update() called with " + vacancies.size() + " vacancies");

        try {
            // Перевіряємо, чи існує файл
            File file = new File(filePath);
            System.out.println("File path: " + filePath);
            System.out.println("File exists: " + file.exists());

            if (!file.exists()) {
                System.out.println("File does not exist, creating from backup...");
                createFileFromBackup();
            }

            String newContent = getUpdatedFileContents(vacancies);
            System.out.println("Generated content length: " + newContent.length());

            updateFile(newContent);
            System.out.println("File updated successfully");

        } catch (Exception e) {
            System.out.println("Error in HtmlView.update(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setController(Controller controller) {
        this.controller = controller;
        System.out.println("Controller set in HtmlView");
    }

    public void emulateCitySelection() {
        System.out.println("Emulating city selection: Dallas-Fort Worth Metroplex");
        controller.onCitySelected("Dallas-Fort Worth Metroplex");
    }

    private void createFileFromBackup() {
        try {
            String backupPath = Paths.get(
                    System.getProperty("user.dir"),
                    "src", "view", "backup.html"
            ).toString();

            File backupFile = new File(backupPath);
            if (backupFile.exists()) {
                System.out.println("Using backup file: " + backupPath);
                String backupContent = new String(Files.readAllBytes(backupFile.toPath()));
                updateFile(backupContent);
            } else {
                System.out.println("Backup file not found, creating default HTML");
                createDefaultHtml();
            }
        } catch (IOException e) {
            System.out.println("Error creating file from backup: " + e.getMessage());
            createDefaultHtml();
        }
    }

    private void createDefaultHtml() {
        String defaultContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <title>Job Postings</title>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
                "</head>\n" +
                "<body>\n" +
                "<table>\n" +
                "    <tr>\n" +
                "        <th>Title</th>\n" +
                "        <th>City</th>\n" +
                "        <th>Company Name</th>\n" +
                "    </tr>\n" +
                "    <tr class=\"vacancy template\" style=\"display: none\">\n" +
                "        <td class=\"title\"><a href=\"url\"></a></td>\n" +
                "        <td class=\"city\"></td>\n" +
                "        <td class=\"companyName\"></td>\n" +
                "    </tr>\n" +
                "</table>\n" +
                "</body>\n" +
                "</html>";

        updateFile(defaultContent);
        System.out.println("Default HTML created");
    }

    private String getUpdatedFileContents(List<JobPosting> vacancies) {
        try {
            Document doc = getDocument();
            System.out.println("Document parsed, title: " + doc.title());

            Elements templateHidden = doc.getElementsByClass("template");
            System.out.println("Template elements found: " + templateHidden.size());

            if (templateHidden.isEmpty()) {
                System.out.println("No template found! Creating default template");
                // Додаємо шаблон, якщо його немає
                Element table = doc.select("table").first();
                if (table != null) {
                    Element templateRow = doc.createElement("tr");
                    templateRow.addClass("vacancy").addClass("template");
                    templateRow.attr("style", "display: none");

                    templateRow.html("<td class=\"title\"><a href=\"url\"></a></td>" +
                            "<td class=\"city\"></td>" +
                            "<td class=\"companyName\"></td>");
                    table.appendChild(templateRow);
                    templateHidden = doc.getElementsByClass("template");
                }
            }

            Element template = templateHidden.clone().removeAttr("style").removeClass("template").get(0);

            // Видаляємо старі вакансії (крім шаблону)
            Elements prevVacancies = doc.getElementsByClass("vacancy");
            System.out.println("Previous vacancies found: " + prevVacancies.size());

            for (Element redundant : prevVacancies) {
                if (!redundant.hasClass("template")) {
                    redundant.remove();
                }
            }

            // Додаємо нові вакансії
            System.out.println("Adding " + vacancies.size() + " new vacancies");
            for (JobPosting vacancy : vacancies) {
                Element vacancyElement = template.clone();

                // Заголовок та URL
                Element vacancyLink = vacancyElement.select("a").first();
                if (vacancyLink != null) {
                    vacancyLink.text(vacancy.getTitle());
                    vacancyLink.attr("href", vacancy.getUrl());
                }

                // Місто
                Element city = vacancyElement.getElementsByClass("city").first();
                if (city != null) {
                    city.text(vacancy.getCity());
                }

                // Назва компанії
                Element companyName = vacancyElement.getElementsByClass("companyName").first();
                if (companyName != null) {
                    companyName.text(vacancy.getCompanyName());
                }

                templateHidden.first().before(vacancyElement.outerHtml());
                System.out.println("Added vacancy: " + vacancy.getTitle());
            }

            return doc.html();
        } catch (IOException e) {
            System.out.println("Error in getUpdatedFileContents: " + e.getMessage());
            e.printStackTrace();
        }
        return "Some exception occurred";
    }

    protected Document getDocument() throws IOException {
        File file = new File(filePath);
        System.out.println("Parsing file: " + filePath + " (exists: " + file.exists() + ")");
        return Jsoup.parse(file, "UTF-8");
    }

    private void updateFile(String content) {
        File file = new File(filePath);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content.getBytes("UTF-8"));
            System.out.println("File written successfully, size: " + content.length() + " characters");
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}