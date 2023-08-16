package minealex.tmotd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.api.event.ProxyPingEvent;

public class MOTDListener implements Listener {

    private final TMOTD plugin;

    public MOTDListener(TMOTD plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProxyPing(ProxyPingEvent event) {
        // Obtener el MOTD personalizado y configurarlo
        String motd = ChatColor.translateAlternateColorCodes('&', String.join("\n", plugin.getCurrentMotd()));
        event.getResponse().setDescription(motd);
    }
}
