package fr.skyost.seasons.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.events.time.DayEvent;
import fr.skyost.seasons.events.time.NightEvent;

public class TimeControl implements Runnable {
	
	private final SeasonWorld seasonWorld;
	private final World world;
	private final float dayLenght;
	private final float nightLenght;
	
	private float oldRealTime;
	private final int refreshRate;
	private float timeBalance;
	private float newRealTime;
	private long estimatedRealTime;
	
	public TimeControl(final SeasonWorld seasonWorld, float dayLenght, float nightLenght, int refreshRate) {
		this.seasonWorld = seasonWorld;
		this.world = seasonWorld.world;
		this.oldRealTime = this.world.getTime();
		this.dayLenght = dayLenght;
		this.nightLenght = nightLenght;
		this.refreshRate = refreshRate;
	}
	
	@Override
	public void run() {
		if(world.getTime() != estimatedRealTime) {
			newRealTime = world.getTime();
		}
		if(newRealTime > 12000) {
			timeBalance = refreshRate * (12000 / (nightLenght * 20));
		}
		else {
			timeBalance = refreshRate * (12000 / (dayLenght * 20));
		}
		newRealTime += timeBalance;
		if(newRealTime > oldRealTime && oldRealTime <= 12000 && newRealTime > 12000 && seasonWorld.season.nightMessageEnabled) {
			Bukkit.getPluginManager().callEvent(new NightEvent(seasonWorld, seasonWorld.season.nightMessage));
		}
		else if(oldRealTime > newRealTime && oldRealTime > 12000 && newRealTime <= 12000 && seasonWorld.season.dayMessageEnabled) {
			Bukkit.getPluginManager().callEvent(new DayEvent(seasonWorld, seasonWorld.season.dayMessage));
		}
		world.setTime((long)newRealTime);
		oldRealTime = newRealTime;
		estimatedRealTime = (long)newRealTime + refreshRate;
	}
	
}
