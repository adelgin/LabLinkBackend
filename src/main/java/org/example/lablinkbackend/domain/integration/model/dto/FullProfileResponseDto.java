package org.example.lablinkbackend.domain.integration.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class FullProfileResponseDto {
    private String firstName;
    private String lastName;
    private String orcid;
    private List<InstitutionDto> institutions;
    private MetricsDto metrics;
    private List<TopicDto> topics;
    private List<WorkDto> works;

    @Data
    public static class InstitutionDto {
        private String countryCode;
        private String name;
    }

    @Data
    public static class MetricsDto {
        private int hIndex;
        private int i10Index;
        private int totalCitedCount;
        private int totalWorksCount;
    }

    @Data
    public static class TopicDto {
        private String field;
        private String name;
    }

    @Data
    public static class WorkDto {
        private String doi;
        private String journalTitle;
        private String source;
        private String title;
        private String url;
        private int year;
    }
}
