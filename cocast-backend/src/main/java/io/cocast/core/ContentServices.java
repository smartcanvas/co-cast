package io.cocast.core;

import com.google.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;

/**
 * Services for contentes
 */
@Singleton
public class ContentServices {

    private static Logger logger = LogManager.getLogger(ContentServices.class.getName());

    private static final Integer MIN_WIDTH = 800;
    private static final Integer MIN_HEIGHT = 600;

    @Inject
    private ContentRepository contentRepository;

    /**
     * Create or update a new content
     */
    public void save(Content content) throws Exception {

        //define the type
        this.defineType(content);

        //saves the content
        boolean hasChanged = contentRepository.save(content);


    }

    /**
     * Define the type
     */
    private void defineType(Content content) {

        if (!StringUtils.isEmpty(content.getType())) {
            //type is already defined
            return;
        }

        boolean hasLargeImage = hasLargeImage(content);

        if (!hasLargeImage) {
            //if the content doesn't have a great image, it will be displayed as a quote or will be
            //discarded if it has no content
            if (StringUtils.isEmpty(content.getTitle()) && (StringUtils.isEmpty(content.getSummary()))) {
                content.setType(Content.TYPE_DISCARD);
                return;
            } else {
                content.setType(Content.TYPE_QUOTE);
                return;
            }
        } else {
            if (StringUtils.isEmpty(content.getTitle()) && (StringUtils.isEmpty(content.getSummary()))) {
                content.setType(Content.TYPE_PHOTO);
                return;
            } else {
                content.setType(Content.TYPE_ANNOUNCEMENT);
                return;
            }
        }
    }


    /**
     * Defines if the content has a large image or not
     */
    private boolean hasLargeImage(Content content) {

        if (StringUtils.isEmpty(content.getImageURL())) {
            return false;
        }

        Integer width = content.getImageWidth();
        Integer height = content.getImageHeight();

        return (width >= MIN_WIDTH) && (height >= MIN_HEIGHT);

    }
}
