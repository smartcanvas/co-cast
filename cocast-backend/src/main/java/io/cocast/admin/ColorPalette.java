package io.cocast.admin;

import io.cocast.auth.SecurityContext;
import io.cocast.util.DateUtils;

import java.util.Date;

/**
 * Color palettes
 */
public class ColorPalette {

    private String mnemonic;
    private String primaryColor;
    private String secondaryColor;
    private String accentColor;
    private String primaryTextColor;
    private String secondaryTextColor;
    private String accentTextColor;
    private String createdBy;
    private Date lastUpdate;

    /**
     * Constructor
     */
    public ColorPalette() {
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

    public String getPrimaryTextColor() {
        return primaryTextColor;
    }

    public void setPrimaryTextColor(String primaryTextColor) {
        this.primaryTextColor = primaryTextColor;
    }

    public String getSecondaryTextColor() {
        return secondaryTextColor;
    }

    public void setSecondaryTextColor(String secondaryTextColor) {
        this.secondaryTextColor = secondaryTextColor;
    }

    public String getAccentTextColor() {
        return accentTextColor;
    }

    public void setAccentTextColor(String accentTextColor) {
        this.accentTextColor = accentTextColor;
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
        return "ColorPalette{" +
                "mnemonic='" + mnemonic + '\'' +
                ", primaryColor='" + primaryColor + '\'' +
                ", secondaryColor='" + secondaryColor + '\'' +
                ", accentColor='" + accentColor + '\'' +
                ", primaryTextColor='" + primaryTextColor + '\'' +
                ", secondaryTextColor='" + secondaryTextColor + '\'' +
                ", accentTextColor='" + accentTextColor + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
