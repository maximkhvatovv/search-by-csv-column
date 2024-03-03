package ru.khvatov.testtasks.searchbycsvcolumn.search;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class SearchingResult {
    private long initTime;
    @Builder.Default
    private List<SubResult> result = new ArrayList<>();

    @Data
    @Builder
    public static class SubResult {
        private String search;
        @Builder.Default
        private List<Integer> result = new ArrayList<>();
        private long time;
    }
}
