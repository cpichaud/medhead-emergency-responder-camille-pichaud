package com.medhead.emergency_responder.dto;

import java.util.List;

public class DistanceMatrixResponse {
    public String status;
    public List<Row> rows;

    public static class Row {
        public List<Element> elements;
    }

    public static class Element {
        public Duration duration;
        public String status;
    }

    public static class Duration {
        public double value;
    }
}