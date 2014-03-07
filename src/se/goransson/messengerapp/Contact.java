package se.goransson.messengerapp;

public class Contact {

	private String name;
	private String phone;

	/**
	 * 
	 * @param name
	 * @param phone
	 */
	public Contact(String name, String phone) {
		super();
		this.name = name;
		this.phone = phone;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(name).append(" ").append(phone);
		
		return sb.toString();
	}
}
