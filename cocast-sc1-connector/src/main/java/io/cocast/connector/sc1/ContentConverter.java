package io.cocast.connector.sc1;

import io.cocast.connector.sc1.model.Block;
import io.cocast.connector.sc1.model.Card;
import io.cocast.connector.sc1.model.Content;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import java.util.List;

/**
 * Converts a Card into a Content
 */
public class ContentConverter {

    private static final Integer MAX_WIDTH = 140;

    /**
     * Converts a Card into a Content
     */
    public static Content convert(Card card, String networkMnemonic, String tenant) throws InvallidCardException {

        if (card == null) {
            throw new InvallidCardException("Card is null");
        }

        Content content = new Content();

        //Basic info
        if (card.getMnemonic() != null) {
            content.setId("scv1_" + card.getMnemonic());
        } else {
            throw new InvallidCardException(card, "Mnemonic is null");
        }
        content.setNetworkMnemonic(networkMnemonic);
        if (card.getCategoryNames() != null) {
            content.setTags(card.getCategoryNames());
        }

        //Source
        content.setSource("Smart Canvas v1");
        content.setSourceContentURL("http://" + tenant + ".smartcanvas.com/card/" + card.getMnemonic());

        //Blocks
        Block contentBlock = getBlock(card, "content");
        Block photoBlock = getBlock(card, "photo");
        Block authorBlock = getBlock(card, "author");
        Block articleBlock = getBlock(card, "article");
        Block userActivityBlock = getBlock(card, "userActivity");

        //content
        populateContent(content, contentBlock);

        //author
        populateAuthor(content, authorBlock);

        //image
        populateImage(content, articleBlock, photoBlock);

        return content;
    }

    /**
     * Populate the content
     */
    private static void populateContent(Content content, Block contentBlock) {

        if (contentBlock == null) {
            return;
        }

        String title = contentBlock.getTitle();
        String summary = contentBlock.getSummary();
        String strContent = contentBlock.getContent();

        //remove HTML tags
        if (!StringUtils.isEmpty(title)) {
            title = Jsoup.parse(title).text();
        }
        if (!StringUtils.isEmpty(summary)) {
            title = Jsoup.parse(summary).text();
        }
        if (!StringUtils.isEmpty(strContent)) {
            title = Jsoup.parse(strContent).text();
        }

        //title
        if (!StringUtils.isEmpty(title)) {
            content.setTitle(title);
        }

        //summary
        if (!StringUtils.isEmpty(summary)) {
            content.setSummary(summary);
        } else {
            if (!StringUtils.isEmpty(strContent)) {
                content.setSummary(StringUtils.abbreviate(strContent, MAX_WIDTH));
            }
        }
    }

    /**
     * Populate the author
     */
    private static void populateAuthor(Content content, Block authorBlock) {
        if (authorBlock == null) {
            return;
        }

        content.setAuthorId(authorBlock.getAuthorId());
        content.setAuthorDisplayName(authorBlock.getAuthorDisplayName());
        content.setAuthorImageURL(authorBlock.getAuthorImageURL());
        content.setDate(authorBlock.getPublishDate());
    }

    /**
     * Populate the image
     */
    private static void populateImage(Content content, Block articleBlock, Block photoBlock) {
        if ((articleBlock != null) && (!StringUtils.isEmpty(articleBlock.getImageURL()))) {
            content.setImageURL(articleBlock.getImageURL());
            content.setImageHeight(articleBlock.getImageHeight());
            content.setImageWidth(articleBlock.getImageWidth());
            return;
        }

        if ((photoBlock != null) && (!StringUtils.isEmpty(photoBlock.getImageURL()))) {
            content.setImageURL(photoBlock.getImageURL());
            content.setImageHeight(photoBlock.getImageHeight());
            content.setImageWidth(photoBlock.getImageWidth());
            return;
        }

        return;
    }

    /**
     * Populate activities
     */
    private static void populateActivities(Content content, Block userActivitiesBlock) {
        if (userActivitiesBlock == null) {
            return;
        }

        content.setLikeCounter(userActivitiesBlock.getLikeCounter());
    }

    /**
     * Return a block of a specific type
     */
    private static Block getBlock(Card card, String type) {

        if ((card == null) || (type == null)) {
            return null;
        }

        List<Block> blocks = card.getBlocks();
        if (blocks == null) {
            return null;
        }
        for (Block block : blocks) {
            if (type.equals(block.getType())) {
                return block;
            }
        }

        return null;
    }
}
