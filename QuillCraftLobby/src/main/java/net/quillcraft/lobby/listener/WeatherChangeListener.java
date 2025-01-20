package net.quillcraft.lobby.listener;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherChangeListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        final World world = event.getWorld();

        world.setWeatherDuration(0);
        world.setThundering(false);

        event.setCancelled(true);
    }

}
