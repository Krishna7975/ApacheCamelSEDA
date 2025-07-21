package com.tetrus.ilink.cl.aggregator.model;

import java.util.List;
import java.util.Map;

public class Request {

    private List<Map<String, Object>> s1;
    private List<Map<String, Object>> s2;
    private List<Map<String, Object>> s3;

    public Request(List<Map<String, Object>> s1, List<Map<String, Object>> s2, List<Map<String, Object>> s3) {
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
    }

    public List<Map<String, Object>> getS1() {
        return s1;
    }

    public void setS1(List<Map<String, Object>> s1) {
        this.s1 = s1;
    }

    public List<Map<String, Object>> getS2() {
        return s2;
    }

    public void setS2(List<Map<String, Object>> s2) {
        this.s2 = s2;
    }

    public List<Map<String, Object>> getS3() {
        return s3;
    }

    public void setS3(List<Map<String, Object>> s3) {
        this.s3 = s3;
    }
}
