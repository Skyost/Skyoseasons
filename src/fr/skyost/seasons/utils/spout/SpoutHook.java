package fr.skyost.seasons.utils.spout;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.player.SkyManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import fr.skyost.seasons.SeasonWorld;
import fr.skyost.seasons.Skyoseasons;

public class SpoutHook implements Listener {
	
	private final SkyManager sky;
	
	public SpoutHook() {
		Bukkit.getPluginManager().registerEvents(this, Skyoseasons.instance);
		sky = SpoutManager.getSkyManager();
	}
	
	@EventHandler
	private final void onSpoutCraftEnable(final SpoutCraftEnableEvent event) {
		final SpoutPlayer player = event.getPlayer();
		final SeasonWorld world = Skyoseasons.worlds.get(player.getWorld().getName());
		if(world != null) {
			sendEffects(player, world.season.effects);
		}
	}
	
	public final boolean isSpoutPlayer(final Player player) {
		return player instanceof SpoutPlayer;
	}
	
	public final void sendEffects(final Player player, final SpoutEffects effects) {
		sendEffects(SpoutManager.getPlayer(player), effects);
	}
	
	public final void sendEffects(final SpoutPlayer player, final SpoutEffects effects) {
		sky.setCloudsVisible(player, effects.cloudsVisible);
		sky.setMoonSizePercent(player, effects.moonSizePercent);
		sky.setMoonVisible(player, effects.moonVisible);
		sky.setStarFrequency(player, effects.starsFrequency);
		sky.setSunSizePercent(player, effects.sunSizePercent);
		sky.setSunVisible(player, effects.sunVisible);
	}
	
}
