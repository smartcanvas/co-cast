package io.cocast.connector.sc1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

    private Long id;
    private String authorId;
    private String providerId;
    private String providerContentId;
    private String mnemonic;
    private Date createDate;
    private boolean feature;
    private Date updateDate;
    private String canvasPreference;
    private List<String> categoryNames;
    private String jsonExtendedData;
    private List<Block> blocks;
    private Long recommendationRequestId;

    public Card() {
        categoryNames = new ArrayList<String>();
        blocks = new ArrayList<Block>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderContentId() {
        return providerContentId;
    }

    public void setProviderContentId(String providerContentId) {
        this.providerContentId = providerContentId;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public boolean isFeature() {
        return feature;
    }

    public void setFeature(boolean feature) {
        this.feature = feature;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCanvasPreference() {
        return canvasPreference;
    }

    public void setCanvasPreference(String canvasPreference) {
        this.canvasPreference = canvasPreference;
    }

    public List<String> getCategoryNames() {
        return categoryNames;
    }

    public void setCategoryNames(List<String> categoryNames) {
        this.categoryNames = categoryNames;
    }

    public String getJsonExtendedData() {
        return jsonExtendedData;
    }

    public void setJsonExtendedData(String jsonExtendedData) {
        this.jsonExtendedData = jsonExtendedData;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public Long getRecommendationRequestId() {
        return recommendationRequestId;
    }

    public void setRecommendationRequestId(Long recommendationRequestId) {
        this.recommendationRequestId = recommendationRequestId;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", authorId='" + authorId + '\'' +
                ", providerId='" + providerId + '\'' +
                ", providerContentId='" + providerContentId + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", createDate=" + createDate +
                ", feature=" + feature +
                ", updateDate=" + updateDate +
                ", canvasPreference='" + canvasPreference + '\'' +
                ", categoryNames=" + categoryNames +
                ", jsonExtendedData='" + jsonExtendedData + '\'' +
                ", blocks=" + blocks +
                ", recommendationRequestId=" + recommendationRequestId +
                '}';
    }
}
