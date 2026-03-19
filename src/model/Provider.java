package model;

import vo.JobPosting;

import java.util.List;
import java.util.Objects;

public class Provider {
    private Strategy strategy;

    public Provider(Strategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy must not be null");
    }

    public List<JobPosting> getJavaJobPostings(String searchString) {
        return strategy.getJobPostings(searchString);
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "strategy must not be null");
    }
}