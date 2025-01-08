package logic;

/**
 * Enum representing the different types of requests a subscriber can make.
 * This enum is used to define actions related to a subscriber's interaction with the library system.
 */
public enum SubscriberRequestType {
    /**
     * Represents a request to register a new subscriber.
     */
    REGISTER,

    /**
     * Represents a request to return a borrowed item.
     */
    RETURN,

    /**
     * Represents a request to borrow an item.
     */
    BORROW,

    /**
     * Represents a request to extend the borrowing period for an item.
     */
    EXTEND;
}
