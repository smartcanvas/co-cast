package io.cocast.connector.sc1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Search execution from smart canvas v1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Search {

    private String searchTerm;
    private Integer bucketSize;
    private List<Bucket> buckets;

    public Search() {
        buckets = new ArrayList<Bucket>();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Bucket> buckets) {
        this.buckets = buckets;
    }

    public Integer getBucketSize() {
        return bucketSize;
    }

    public void setBucketSize(Integer bucketSize) {
        this.bucketSize = bucketSize;
    }

    @Override
    public String toString() {
        return "Search{" +
                "searchTerm='" + searchTerm + '\'' +
                ", bucketSize=" + bucketSize +
                ", buckets=" + buckets +
                '}';
    }
}
