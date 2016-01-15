package io.cocast.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Themes
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Theme implements Serializable {

    private String mnemonic;
    private String primaryColor;
    private String secondaryColor;
    private String accentColor;
    private String primaryFont;
    private String secondaryFont;
    private String primaryFontColor;
    private String secondaryFontColor;
    private String accentFontColor;
    private String createdBy;
    private Date lastUpdate;

    /**
     * Constructor
     */
    public Theme() {
        lastUpdate = DateUtils.now();
        createdBy = SecurityContext.get().userIdentification();
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(String accentColor) {
        this.accentColor = accentColor;
    }

    public String getPrimaryFont() {
        return primaryFont;
    }

    public void setPrimaryFont(String primaryFont) {
        this.primaryFont = primaryFont;
    }

    public String getSecondaryFont() {
        return secondaryFont;
    }

    public void setSecondaryFont(String secondaryFont) {
        this.secondaryFont = secondaryFont;
    }

    public String getPrimaryFontColor() {
        return primaryFontColor;
    }

    public void setPrimaryFontColor(String primaryFontColor) {
        this.primaryFontColor = primaryFontColor;
    }

    public String getSecondaryFontColor() {
        return secondaryFontColor;
    }

    public void setSecondaryFontColor(String secondaryFontColor) {
        this.secondaryFontColor = secondaryFontColor;
    }

    public String getAccentFontColor() {
        return accentFontColor;
    }

    public void setAccentFontColor(String accentFontColor) {
        this.accentFontColor = accentFontColor;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Theme{" +
                "mnemonic='" + mnemonic + '\'' +
                ", primaryColor='" + primaryColor + '\'' +
                ", secondaryColor='" + secondaryColor + '\'' +
                ", accentColor='" + accentColor + '\'' +
                ", primaryFont='" + primaryFont + '\'' +
                ", secondaryFont='" + secondaryFont + '\'' +
                ", primaryFontColor='" + primaryFontColor + '\'' +
                ", secondaryFontColor='" + secondaryFontColor + '\'' +
                ", accentFontColor='" + accentFontColor + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
