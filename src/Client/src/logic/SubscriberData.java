package logic;

/**
 * Represents data related to a subscriber in the library system.
 * <p>This class stores details of a subscriber, including their unique ID, name, the date when their account was frozen,
 * and the expected date when their account will be released from the frozen status.</p>
 */
public class SubscriberData {
	
	/** The unique identifier for the subscriber. */
    private String subscriberId;
    
    /** The name of the subscriber. */
    private String subscriberName;
    
    /** The date when the subscriber's account was frozen. */
    private String frozenAt;
    
    /** The expected date when the subscriber's account will be released. */
    private String expectedRelease;

    /**
     * Constructs a new SubscriberData object with the specified details.
     * 
     * @param subscriberId the unique identifier of the subscriber
     * @param subscriberName the name of the subscriber
     * @param frozenAt the date when the subscriber's account was frozen
     * @param expectedRelease the expected date when the account will be released from frozen status
     */
    public SubscriberData(String subscriberId, String subscriberName, String frozenAt, String expectedRelease) {
        this.subscriberId = subscriberId;
        this.subscriberName = subscriberName;
        this.frozenAt = frozenAt;
        this.expectedRelease = expectedRelease;
    }

    /**
     * Gets the unique identifier for the subscriber.
     * 
     * @return the subscriber's unique ID
     */
    public String getSubscriberId() {
        return subscriberId;
    }

    /**
     * Gets the name of the subscriber.
     * 
     * @return the name of the subscriber
     */
    public String getSubscriberName() {
        return subscriberName;
    }

    /**
     * Gets the date when the subscriber's account was frozen.
     * 
     * @return the date when the account was frozen
     */
    public String getFrozenAt() {
        return frozenAt;
    }

    /**
     * Gets the expected date when the subscriber's account will be released from frozen status.
     * 
     * @return the expected release date of the account
     */
    public String getExpectedRelease() {
        return expectedRelease;
    }
}
