package io.cocast.ext.match;

import com.google.inject.Singleton;
import io.cocast.ext.people.Person;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Shuffles using teamwork Google event logic
 */
@Singleton
public class TeamworkShuffler extends Shuffler {

    private Logger logger = LogManager.getLogger(TeamworkShuffler.class.getName());

    @Override
    public List<Person> shuffle(String networkMnemonic, List<Person> personList, Person requester) throws Exception {
        long timestamp = System.currentTimeMillis();

        if (personList == null) {
            logger.debug("Trying to shuffle null list, returning");
            return personList;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Shuffling list with " + personList.size() + " persons");
        }

        //preferred track: Ex = cloud, devices, apps, maps or search
        String preferredTrack = this.getPreferredTrack(requester);
        logger.debug("Preferred track = " + preferredTrack);

        //is the requester is a technology or services partner
        boolean isTechnologyPartner = this.isTechnologyPartner(requester);
        boolean isServicePartner = this.isServicePartner(requester);
        if (logger.isDebugEnabled()) {
            logger.debug("isTechnologyPartner = " + isTechnologyPartner + ", isServicePartner = " + isServicePartner);
        }

        List<Person> result = this.createShuffledList(personList, preferredTrack, isTechnologyPartner, isServicePartner);

        long timestampFinal = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("Raffle executed in " + (timestampFinal - timestamp) + " milliseconds");
        }

        return result;

    }

    /**
     * Creates the shuffled list based on business parameters
     */
    private List<Person> createShuffledList(List<Person> personList, String preferredTrack,
                                            boolean isTechnologyPartner, boolean isServicePartner) {

        PersonRaffle personRaffle = new PersonRaffle();
        for (Person person : personList) {

            //services partners want to meet technology partners
            if ((isServicePartner) && (this.isTechnologyPartner(person))) {
                if (preferredTrack.equals(getPreferredTrack(person))) {
                    logger.debug("Same track, services meeting technology -> HIGH ODDS");
                    personRaffle.add(person, PersonRaffle.HIGH_ODDS);
                } else {
                    logger.debug("Different tracks, services meeting technology -> MID ODDS");
                    personRaffle.add(person, PersonRaffle.MID_ODDS);
                }
            } else if (preferredTrack.equalsIgnoreCase("devices") &&
                    "apps".equalsIgnoreCase(this.getPreferredTrack(person))) {

                logger.debug("Devices meeting apps -> MID ODDS");

                //people from devices have bigger odds to find someone from apps
                personRaffle.add(person, PersonRaffle.MID_ODDS);
            } else {
                personRaffle.add(person, PersonRaffle.LOW_ODDS);
            }

            //if the person has already logged in, increases the odds
            if (person.getImageURL() != null) {
                personRaffle.add(person, PersonRaffle.MID_ODDS);
            }
        }

        return personRaffle.raffle();
    }

    private String getPreferredTrack(Person person) {
        List<String> tags = person.getTags();
        if ((tags == null) || (tags.size() == 0)) {
            return "NONE";
        }

        return tags.get(0);
    }

    private boolean isServicePartner(Person person) {
        List<String> tags = person.getTags();
        if (tags == null) {
            return false;
        }

        return tags.contains("Sales & Services Partner");
    }

    private boolean isTechnologyPartner(Person person) {
        List<String> tags = person.getTags();
        if (tags == null) {
            return false;
        }

        return tags.contains("Technology Partner");
    }
}
