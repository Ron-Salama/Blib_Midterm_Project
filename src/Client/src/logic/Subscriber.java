package logic;

/**
 * Represents a subscriber in the library system. A Subscriber object holds details about a library user, including their subscription history, personal details, and contact information.
 * <p>This class is used to manage information about subscribers in the system.</p>
 */
public class Subscriber {

    /** The unique identifier for the subscriber. */
    private int Subscriber_id;

    /** The detailed subscription history of the subscriber. */
    private int detailed_subscription_history;

    /** The name of the subscriber. */
    private String subscriber_name;

    /** The phone number of the subscriber. */
    private String subscriber_phone_number;

    /** The email address of the subscriber. */
    private String subscriber_email;
    
    private String status;

    /**
     * Constructs a new Subscriber with the specified details.
     * 
     * @param subscriber_id the unique identifier of the subscriber
     * @param detailed_subscription_history the subscription history of the subscriber
     * @param subscriber_name the name of the subscriber
     * @param subscriber_phone_number the phone number of the subscriber
     * @param subscriber_email the email address of the subscriber
     */
    public Subscriber(int subscriber_id, int detailed_subscription_history, String subscriber_name, String subscriber_phone_number, String subscriber_email, String status) {
        Subscriber_id = subscriber_id;
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
     * @param subscriber_id the unique identifier to be set for the subscriber
     */
    public void setSubscriber_id(int subscriber_id) {
        Subscriber_id = subscriber_id;
    }

    /**
     * Gets the detailed subscription history of the subscriber.
     * 
     * @return the detailed subscription history of the subscriber
     */
    public int getDetailed_subscription_history() {
        return detailed_subscription_history;
    }

    /**
     * Sets the detailed subscription history of the subscriber.
     * 
     * @param detailed_subscription_history the subscription history to be set for the subscriber
     */
    public void setDetailed_subscription_history(int detailed_subscription_history) {
        this.detailed_subscription_history = detailed_subscription_history;
    }

    /**
     * Gets the name of the subscriber.
     * 
     * @return the name of the subscriber
     */
    public String getSubscriber_name() {
        return subscriber_name;
    }

    /**
     * Sets the name of the subscriber.
     * 
     * @param subscriber_name the name to be set for the subscriber
     */
    public void setSubscriber_name(String subscriber_name) {
        this.subscriber_name = subscriber_name;
    }

    /**
     * Gets the phone number of the subscriber.
     * 
     * @return the phone number of the subscriber
     */
    public String getSubscriber_phone_number() {
        return subscriber_phone_number;
    }

    /**
     * Sets the phone number of the subscriber.
     * 
     * @param subscriber_phone_number the phone number to be set for the subscriber
     */
    public void setSubscriber_phone_number(String subscriber_phone_number) {
        this.subscriber_phone_number = subscriber_phone_number;
    }

    /**
     * Gets the email address of the subscriber.
     * 
     * @return the email address of the subscriber
     */
    public String getSubscriber_email() {
        return subscriber_email;
    }

    /**
     * Sets the email address of the subscriber.
     * 
     * @param subscriber_email the email address to be set for the subscriber
     */
    public void setSubscriber_email(String subscriber_email) {
        this.subscriber_email = subscriber_email;
    }

    /**
     * Returns a string representation of the subscriber, which includes the subscriber's ID, subscription history, name, phone number, and email.
     * 
     * @return a string containing the subscriber's details
     */
    @Override
    public String toString() {
        return "Subscriber [Subscriber_id=" + Subscriber_id + ", detailed_subscription_history=" + detailed_subscription_history + 
               ", subscriber_name=" + subscriber_name + ", subscriber_phone_number=" + subscriber_phone_number + 
               ", subscriber_email=" + subscriber_email + "]";
    }
    
    public String getStatus() {
    	return status;
    }
    
    public void setStatus(String status) {
    	this.status = status;
    }
}
