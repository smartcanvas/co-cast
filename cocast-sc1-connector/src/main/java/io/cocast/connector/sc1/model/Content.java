package io.cocast.connector.sc1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity that represents an object that will be shown in a channel
 *
 * @author Daniel Viveiros
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content implements Serializable {

    /* Basic Info */
    private String id;
    private String networkMnemonic;
    private List<String> tags;
    private String type;

    /* Content */
    private String title;
    private String summary;

    /* Author */
    private String authorId;
    private String authorDisplayName;
    private String authorImageURL;
    private Date date;

    /* Image */
    private String imageURL;
    private Integer imageWidth;
    private Integer imageHeight;

    /* Source Info */
    private String source;
    private String sourceContentURL;

    /* Counters */
    private Integer likeCounter = 0;

    /* Json Extended Data */
    private String jsonExtendedData;

    /**
     * Constructor
     */
    public Content() {
        tags = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNetworkMnemonic() {
        return networkMnemonic;
    }

    public void setNetworkMnemonic(String networkMnemonic) {
        this.networkMnemonic = networkMnemonic;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public String getAuthorImageURL() {
        return authorImageURL;
    }

    public void setAuthorImageURL(String authorImageURL) {
        this.authorImageURL = authorImageURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceContentURL() {
        return sourceContentURL;
    }

    public void setSourceContentURL(String sourceContentURL) {
        this.sourceContentURL = sourceContentURL;
    }

    public Integer getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(Integer likeCounter) {
        this.likeCounter = likeCounter;
    }

    @JsonRawValue
    public String getJsonExtendedData() {
        return jsonExtendedData;
    }

    public void setJsonExtendedData(final JsonNode jsonExtendedDataNode) {
        jsonExtendedData = jsonExtendedDataNode == null ? null : jsonExtendedDataNode.toString();
    }

    public void setJsonExtendedDataRaw(final String jsonExtendedData) {
        this.jsonExtendedData = jsonExtendedData;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", tags=" + tags +
                ", type='" + type + '\'' +
                ", authorId='" + authorId + '\'' +
                ", authorDisplayName='" + authorDisplayName + '\'' +
                ", authorImageURL='" + authorImageURL + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", imageWidth=" + imageWidth +
                ", imageHeight=" + imageHeight +
                ", source='" + source + '\'' +
                ", sourceContentURL='" + sourceContentURL + '\'' +
                ", likeCounter=" + likeCounter +
                '}';
    }
}

