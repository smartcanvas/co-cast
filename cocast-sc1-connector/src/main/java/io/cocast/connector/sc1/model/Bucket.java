package io.cocast.connector.sc1.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Smart Canvas Bucket
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bucket {

    private String id;
    private boolean isDefault;
    private List<Card> cards;
    private String token;
    private Integer size;

    public Bucket() {
        cards = new ArrayList<Card>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "id='" + id + '\'' +
                ", isDefault=" + isDefault +
                ", cards=" + cards +
                ", token='" + token + '\'' +
                ", size=" + size +
                '}';
    }
}
