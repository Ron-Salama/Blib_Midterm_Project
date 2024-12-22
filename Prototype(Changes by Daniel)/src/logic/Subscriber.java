package logic;


public class Subscriber {

	private int Subscriber_id;
	private int detailed_subscription_history;
	private String subscriber_name;
	private String subscriber_phone_number;
	private String subscriber_email;
	
	
	
	public Subscriber(int subscriber_id, int detailed_subscription_history, String subscriber_name,String subscriber_phone_number, String subscriber_email) {
		Subscriber_id = subscriber_id;
		this.detailed_subscription_history = detailed_subscription_history;
		this.subscriber_name = subscriber_name;
		this.subscriber_phone_number = subscriber_phone_number;
		this.subscriber_email = subscriber_email;
	}

	public int getSubscriber_id() {
		return Subscriber_id;
	}
	public void setSubscriber_id(int subscriber_id) {
		Subscriber_id = subscriber_id;
	}
	public int getDetailed_subscription_history() {
		return detailed_subscription_history;
	}
	public void setDetailed_subscription_history(int detailed_subscription_history) {
		this.detailed_subscription_history = detailed_subscription_history;
	}
	public String getSubscriber_name() {
		return subscriber_name;
	}
	public void setSubscriber_name(String subscriber_name) {
		this.subscriber_name = subscriber_name;
	}
	public String getSubscriber_phone_number() {
		return subscriber_phone_number;
	}
	public void setSubscriber_phone_number(String subscriber_phone_number) {
		this.subscriber_phone_number = subscriber_phone_number;
	}
	public String getSubscriber_email() {
		return subscriber_email;
	}
	public void setSubscriber_email(String subscriber_email) {
		this.subscriber_email = subscriber_email;
	}

	@Override
	public String toString() {
		return "Subscriber [Subscriber_id=" + Subscriber_id + ", detailed_subscription_history="
				+ detailed_subscription_history + ", subscriber_name=" + subscriber_name + ", subscriber_phone_number="
				+ subscriber_phone_number + ", subscriber_email=" + subscriber_email + "]";
	}
	
	
	
	
}
