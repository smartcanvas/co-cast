package io.cocast.core;

import com.google.inject.Singleton;
import io.cocast.util.AbstractRunnable;
import io.cocast.util.ExecutorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.util.List;

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

    @Inject
    private LiveStreamServices liveStreamServices;

    /**
     * Create or update a new content
     */
    public void save(Content content) throws Exception {

        //define the type
        this.defineType(content);
        if (Content.TYPE_DISCARD.equals(content.getType())) {
            throw new ValidationException("Card doesn't have minimum info to be displayed on TV");
        }

        //saves the content
        boolean hasChanged = contentRepository.save(content);

        //process the impact
        if (hasChanged) {
            NewContentThread newContentThread = new NewContentThread(content, liveStreamServices);
            ExecutorUtils.executeSingleThread(newContentThread);
        }
    }

    /**
     * Bulk saves a list of contents
     */
    public void bulkSave(String networkMnemonic, List<Content> contents) throws Exception {

        boolean hasChanged = false;

        for (Content content : contents) {

            content.setNetworkMnemonic(networkMnemonic);

            //define the type
            this.defineType(content);
            if (Content.TYPE_DISCARD.equals(content.getType())) {
                hasChanged = false;
            } else {
                //saves the content
                boolean resultSave = contentRepository.save(content);
                hasChanged = hasChanged || resultSave;
            }
        }

        //process the impact
        if (hasChanged) {
            NewContentThread newContentThread = new NewContentThread(networkMnemonic, contents, liveStreamServices);
            ExecutorUtils.executeSingleThread(newContentThread);
        }
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

    /**
     * Task that evaluates the impact of the new content into the system
     */
    public class NewContentThread extends AbstractRunnable {

        private Content content;
        private List<Content> contents;
        private boolean bulkMode = false;
        private LiveStreamServices liveStreamServices;

        /**
         * Constructor
         */
        public NewContentThread(Content content, LiveStreamServices liveStreamServices) {
            super(content.getNetworkMnemonic());
            this.content = content;
            this.liveStreamServices = liveStreamServices;
            bulkMode = false;
        }

        /**
         * Constructor
         */
        public NewContentThread(String networkMnemonic, List<Content> contents, LiveStreamServices liveStreamServices) {
            super(networkMnemonic);
            this.contents = contents;
            this.liveStreamServices = liveStreamServices;
            bulkMode = true;
        }

        @Override
        public void execute() throws ValidationException, Exception {
            if (bulkMode) {
                liveStreamServices.onNewContentArrivedInBulkMode(getNetworkMnemonic(), contents);
            } else {
                liveStreamServices.onNewContentArrived(content);
            }
        }

        @Override
        public String getJobName() {
            return "New Content Job";
        }
    }
}
