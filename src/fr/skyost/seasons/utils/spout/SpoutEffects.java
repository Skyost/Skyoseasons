package fr.skyost.seasons.utils.spout;

public class SpoutEffects {
	
	public final boolean starsVisible;
	public final int starsFrequency;
	public final boolean cloudsVisible;
	public final boolean sunVisible;
	public final int sunSizePercent;
	public final boolean moonVisible;
	public final int moonSizePercent;
	
	public SpoutEffects(final boolean starsVisible, final int starsFrequency, final boolean cloudsVisible, final boolean sunVisible, final int sunSizePercent, final boolean moonVisible, final int moonSizePercent) {
		this.starsVisible = starsVisible;
		this.starsFrequency = starsFrequency;
		this.cloudsVisible = cloudsVisible;
		this.sunVisible = sunVisible;
		this.sunSizePercent = sunSizePercent;
		this.moonVisible = moonVisible;
		this.moonSizePercent = moonSizePercent;
	}
	
}