package view;

import main.Controller;
import vo.JobPosting;

import java.util.List;

public interface View {
    void update(List<JobPosting> vacancies);

    void setController(Controller controller);
}
