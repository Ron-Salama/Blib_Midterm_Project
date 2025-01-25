package logic;

/**
 * Represents a subscriber in the library system. 
 * A {@code Subscriber} object holds details about a library user, including:
 * <ul>
 *     <li>A unique subscriber ID</li>
 *     <li>A detailed subscription history reference</li>
 *     <li>Personal details (name, phone, email)</li>
 *     <li>Subscriber status (e.g., frozen, active)</li>
 * </ul>
 * <p>
 * This class is used throughout the system to manage information about subscribers.
 * </p>
 * 
 * <p>
 * Usage example:
 * <pre>{@code
 * Subscriber newSub = new Subscriber(123, 1, "Alice", "555-1234", "alice@example.com", "Not Frozen");
 * }</pre>
 * </p>
 * 
 * @author  
 * @version 1.0
 * @since 2025-01-01
 */
public class Subscriber {

    /** The unique identifier for the subscriber. */
    private int Subscriber_id;

    /** The detailed subscription history ID or reference for the subscriber. */
    private int detailed_subscription_history;

    /** The name of the subscriber. */
    private String subscriber_name;

    /** The phone number of the subscriber. */
    private String subscriber_phone_number;

    /** The email address of the subscriber. */
    private String subscriber_email;

    /**
     * The status of the subscriber (e.g., "Not Frozen", "Frozen at:<date/time>").
     * This indicates whether the subscriber can borrow or has restrictions.
     */
    private String status;

    /**
     * Constructs a new {@code Subscriber} with the specified details.
     *
     * @param subscriber_id                 the unique identifier of the subscriber
     * @param detailed_subscription_history the subscription history reference for the subscriber
     * @param subscriber_name               the name of the subscriber
     * @param subscriber_phone_number       the phone number of the subscriber
     * @param subscriber_email              the email address of the subscriber
     * @param status                        the current status of the subscriber (e.g., "Not Frozen" or "Frozen at:<time>")
     */
    public Subscriber(int subscriber_id, 
                      int detailed_subscription_history,
                      String subscriber_name,
                      String subscriber_phone_number,
                      String subscriber_email,
                      String status) {
        this.Subscriber_id = subscriber_id;
        this.detailed_subscription_history = detailed_subscription_history;
        this.subscriber_name = subscriber_name;
        this.subscriber_phone_number = subscriber_phone_number;
        this.subscriber_email = subscriber_email;
        this.status = status;
    }

    /**
     * Gets the unique identifier of the subscriber.
     *
     * @return the unique identifier of the subscriber
     */
    public int getSubscriber_id() {
        return Subscriber_id;
    }

    /**
     * Sets the unique identifier of the subscriber.
     *
     * @param subscriber_id the new unique identifier for the subscriber
     */
    public void setSubscriber_id(int subscriber_id) {
        Subscriber_id = subscriber_id;
    }

    /**
     * Gets the detailed subscription history reference for the subscriber.
     *
     * @return the detailed subscription history of the subscriber
     */
    public int getDetailed_subscription_history() {
        return detailed_subscription_history;
    }

    /**
     * Sets the detailed subscription history reference for the subscriber.
     *
     * @param detailed_subscription_history the new subscription history reference for the subscriber
     */
    public void setDetailed_subscription_history(int detailed_subscription_history) {
        this.detailed_subscription_history = detailed_subscription_history;
    }

    /**
     * Gets the name of the subscriber.
     *
     * @return the subscriber's name
     */
    public String getSubscriber_name() {
        return subscriber_name;
    }

    /**
     * Sets the name of the subscriber.
     *
     * @param subscriber_name the new name for the subscriber
     */
    public void setSubscriber_name(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    /**
     * Gets the phone number of the subscriber.
     *
     * @return the subscriber's phone number
     */
    public String getSubscriber_phone_number() {
        return subscriber_phone_number;
    }

    /**
     * Sets the phone number of the subscriber.
     *
     * @param subscriber_phone_number the new phone number for the subscriber
     */
    public void setSubscriber_phone_number(String subscriber_phone_number) {
        this.subscriber_phone_number = subscriber_phone_number;
    }

    /**
     * Gets the email address of the subscriber.
     *
     * @return the subscriber's email address
     */
    public String getSubscriber_email() {
        return subscriber_email;
    }

    /**
     * Sets the email address of the subscriber.
     *
     * @param subscriber_email the new email address for the subscriber
     */
    public void setSubscriber_email(String subscriber_email) {
        this.subscriber_email = subscriber_email;
    }

    /**
     * Gets the current status of the subscriber (e.g., "Not Frozen" or "Frozen at:<time>").
     *
     * @return the subscriber's current status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the subscriber (e.g., to "Not Frozen" or "Frozen at:<time>").
     *
     * @param status the new status for the subscriber
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a string representation of the subscriber, which includes:
     * <ul>
     *     <li>The subscriber's ID</li>
     *     <li>Subscription history reference</li>
     *     <li>Name</li>
     *     <li>Phone number</li>
     *     <li>Email</li>
     * </ul>
     *
     * @return a string containing the subscriber's details
     */
    @Override
    public String toString() {
        return "Subscriber [Subscriber_id=" + Subscriber_id 
                + ", detailed_subscription_history=" + detailed_subscription_history 
                + ", subscriber_name=" + subscriber_name 
                + ", subscriber_phone_number=" + subscriber_phone_number 
                + ", subscriber_email=" + subscriber_email 
                + ", status=" + status + "]";
    }
}
