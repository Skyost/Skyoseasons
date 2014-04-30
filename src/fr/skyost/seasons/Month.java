package fr.skyost.seasons;

public class Month {
	
	public final String name;
	public final String next;
	public final int number;
	public final int days;
	
	public Month(final String name, final String next, final int number, final int days) {
		this.name = name;
		this.next = next;
		this.number = number;
		this.days = days;
	}
	
}
