package logic;

public class SubscriberData {
    private String subscriberId;
    private String subscriberName;
    private String frozenAt;
    private String expectedRelease;

    public SubscriberData(String subscriberId, String subscriberName, String frozenAt, String expectedRelease) {
        this.subscriberId = subscriberId;
        this.subscriberName = subscriberName;
        this.frozenAt = frozenAt;
        this.expectedRelease = expectedRelease;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public String getFrozenAt() {
        return frozenAt;
    }

    public String getExpectedRelease() {
        return expectedRelease;
    }
}
