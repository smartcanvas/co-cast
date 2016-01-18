package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.auth.SecurityContext;
import io.cocast.core.SettingsServices;
import io.cocast.ext.people.Person;
import io.cocast.ext.people.PersonServices;
import io.cocast.util.AbstractRunnable;
import io.cocast.util.ExecutorUtils;
import io.cocast.util.GCMUtils;
import io.cocast.util.ParseUtils;
import io.cocast.util.log.LogUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Services for matches
 */
@Singleton
public class MatchServices {

    private static Logger logger = LogManager.getLogger(MatchServices.class.getName());

    @Inject
    private MatchActionRepository matchActionRepository;

    @Inject
    private MatchRepository matchRepository;

    @Inject
    private PersonServices personServices;

    @Inject
    private GCMUtils gcmUtils;

    @Inject
    private ParseUtils parseUtils;

    @Inject
    private SettingsServices settingsServices;

    /**
     * Evaluates if a match has happened or not
     *
     * @param networkMnemonic Network
     * @param personId        ID of the person executing the action
     * @param email           Email of the other persons who received the like
     * @return If a match has happened or not
     */
    public boolean evaluatesMatch(String networkMnemonic, String personId, String email) throws Exception {
        List<MatchAction> likes = matchActionRepository.listActions(networkMnemonic, email, MatchAction.LIKE);
        for (MatchAction likeAction : likes) {
            if (likeAction.getPersonId().equals(personId)) {
                String myEmail = SecurityContext.get().email();
                matchRepository.save(networkMnemonic, myEmail, email);

                Person person1 = personServices.get(networkMnemonic, myEmail);
                Person person2 = personServices.get(networkMnemonic, email);

                //send a notification
                NotifyMatchesThread thread1 = new NotifyMatchesThread(networkMnemonic, person1, person2);
                ExecutorUtils.execute(thread1);

                NotifyMatchesThread thread2 = new NotifyMatchesThread(networkMnemonic, person2, person1);
                ExecutorUtils.execute(thread2);

                return true;
            }
        }

        return false;
    }

    /**
     * Thread to notify matches in background
     */
    private class NotifyMatchesThread extends AbstractRunnable {

        private Person person1;
        private Person person2;

        public NotifyMatchesThread(String networkMnemonic, Person person1, Person person2) {
            super(networkMnemonic);
            this.person1 = person1;
            this.person2 = person2;
        }

        @Override
        public void execute() throws Exception {

            try {
                List<String> deviceList = person1.getDeviceTypeList();
                List<String> deviceIdList = person1.getDeviceIdentifierList();
                String deviceId;
                if ((deviceList != null) && (deviceIdList != null)) {
                    int count = 0;
                    for (String deviceType : deviceList) {

                        //Gets the device ID
                        if (deviceIdList.size() >= count) {
                            deviceId = deviceIdList.get(count);
                        } else {
                            LogUtils.fatal(logger, "Inconsistency on device ID list (too short) for person "
                                    + person1, null);
                            return;
                        }

                        //Sends the message using the proper service
                        if ("android".equals(deviceType)) {
                            sendGCMMessage(deviceId, person2);
                        } else if ("ios".equals(deviceType)) {
                            sendParseMessage(getNetworkMnemonic(), deviceId, person2);
                        } else {
                            LogUtils.fatal(logger, "Unsupported device type: " + deviceType + ". No notification for: "
                                    + person1, null);
                            return;
                        }

                        count++;
                    }
                }
            } catch (Exception exc) {
                throw new Exception("Error notifying match in background between " + person1.getEmail()
                        + " and " + person2.getEmail(), exc);
            }
        }

        @Override
        public String getJobName() {
            return "Notify Matches Job";
        }
    }

    /**
     * Send a GCM message
     */
    private void sendGCMMessage(String deviceId, Person person) throws IOException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", "match");
        data.put("person", person);
        GCMUtils.GCMMessage message = new GCMUtils.GCMMessage(deviceId, data);
        gcmUtils.send(message);
    }

    /**
     * Send a parse message
     */
    private void sendParseMessage(String networkMnemonic, String deviceId, Person person) throws Exception {

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", "match");
        data.put("person", person);

        String title = settingsServices.getString(networkMnemonic, "match-notification-title");
        String body = settingsServices.getString(networkMnemonic, "match-notification-body");
        title = title.replace(":displayName", person.getDisplayName());
        body = body.replace(":displayName", person.getDisplayName());

        ParseUtils.ParseMessage message = new ParseUtils.ParseMessage("ios", deviceId, title, body, data);
        parseUtils.send(message);
    }
}
