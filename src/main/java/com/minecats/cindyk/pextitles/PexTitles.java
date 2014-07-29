package com.minecats.cindyk.pextitles;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.logging.Level;

/**
 * Created by cindy on 7/28/14.
 */
public class PexTitles extends JavaPlugin implements Listener {

    PexTitles plugin;

    FileConfiguration fc;
    @Override
    public void onEnable() {
        super.onEnable();

        plugin = this;

        getServer().getPluginManager().registerEvents(this,this);
        // Configuration
        try {

            fc= getConfig();
            //saveConfig();
            if(fc.getString("NewPlayerPrefix",null) == null)
            {
                getLogger().info("Creating initial configuration!");
                getConfig().options().copyDefaults(true);
                saveDefaultConfig();
                reloadConfig();
            }

        } catch (Exception ex) {
            getLogger().log(Level.SEVERE,
                    "[PexTitles] Could not load configuration!", ex);
        }

    }


    @Override
    public void onDisable() {
        super.onDisable();

        getLogger().info("Disabled PexTitles");

    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void firstJoinDetection(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.getLogger().info("Player " + player.getName() + " has logged in.");
        PermissionUser user = PermissionsEx.getUser(player);
        if(user == null)
        {
            this.getLogger().warning("Pex has not Added User! This won't work!");
            return;
        }
        boolean existingPlayer = player.hasPlayedBefore();

        if(!existingPlayer)
        {
            //do Pex thingy...
            user.setPrefix(fc.getString("NewPlayerPrefix"),null);
            user.save();
            this.getLogger().info("First Join for Player " + player.getName() );
        }
        else
        {
           DebugMessage("Checking Rank for Player " + player.getName());

            ConfigurationSection cs = fc.getConfigurationSection("groups");
            DebugMessage("CS stuff " + cs.getName() + " and " + cs.getKeys(false).size());

            for(String key:cs.getKeys(false))
            {
                DebugMessage("Looking for Rank " + key + " for " + player.getName());
                String WorldName = cs.getString("world",null);
                if(user.inGroup(key))
                {
                    DebugMessage("Found Rank "+key+" for " + user.getName() + " prefix: " + cs.getString(key+".prefix") + " current prefix: " + user.getPrefix(WorldName));
                    user.setPrefix(cs.getString(key+".prefix"),WorldName);
                    user.save();
                }
            }

        }
    }


    void DebugMessage(String msg)
    {
        if(fc.getBoolean("Debug"))
        {
            this.getLogger().info(msg);
        }
    }


}
