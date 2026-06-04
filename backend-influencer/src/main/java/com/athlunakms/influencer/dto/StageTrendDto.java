package com.athlunakms.influencer.dto;

import java.util.List;

public class StageTrendDto {
    private List<String> dates;
    private List<StageTrendSeries> series;

    public List<String> getDates() {
        return this.dates;
    }

    public List<StageTrendSeries> getSeries() {
        return this.series;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public void setSeries(List<StageTrendSeries> series) {
        this.series = series;
    }

    public static class StageTrendSeries {
        private String label;
        private String color;
        private List<Long> data;

        public StageTrendSeries() {}

        public StageTrendSeries(String label, String color, List<Long> data) {
            this.label = label;
            this.color = color;
            this.data = data;
        }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getColor() { return color; }
        public void setColor(String color) { this.color = color; }
        public List<Long> getData() { return data; }
        public void setData(List<Long> data) { this.data = data; }
    }
}
