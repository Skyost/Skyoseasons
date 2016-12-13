package fr.skyost.seasons;

public class Month {
	
	/**
	 * The name of the month.
	 */
	
	public final String name;
	
	/**
	 * The name of the month that comes after this one.
	 */
	
	public final String next;
	
	/**
	 * This month's number.
	 */
	
	public final int number;
	
	/**
	 * Number of days in this month.
	 */
	
	public final int days;
	
	/**
	 * Creates a new Month instance.
	 * 
	 * @param name The name.
	 * @param next The month that comes after.
	 * @param number The month's calendar number.
	 * @param days Number of days.
	 */
	
	public Month(final String name, final String next, final int number, final int days) {
		this.name = name;
		this.next = next;
		this.number = number;
		this.days = days;
	}
	
}