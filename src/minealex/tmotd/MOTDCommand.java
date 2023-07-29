package minealex.tmotd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MOTDCommand extends Command {

    private final TMOTD plugin;

    public MOTDCommand(TMOTD plugin) {
        super("motd");
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("set") && args.length >= 3) {
                // Verificar el permiso para ejecutar /motd set
                if (!sender.hasPermission("tmotd.set")) {
                    sender.sendMessage(ChatColor.RED + plugin.getNoPermissionMsg());
                    return;
                }
                // Código para actualizar el MOTD, igual que antes
                // ...
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMotdSetSuccessMsg())); // Enviar mensaje personalizado
            } else if (args[0].equalsIgnoreCase("reload")) {
                // Verificar el permiso para ejecutar /motd reload
                if (!sender.hasPermission("tmotd.reload")) {
                    sender.sendMessage(ChatColor.RED + plugin.getNoPermissionMsg());
                    return;
                }
                plugin.loadConfig(); // Vuelve a cargar la configuración en el plugin
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMotdReloadMsg())); // Enviar mensaje personalizado
            } else {
                // Mostrar el uso correcto del comando
                sender.sendMessage(ChatColor.RED + "Uso:");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMotdSetUsageMsg())); // Enviar mensaje personalizado
            }
        } else {
            // Mostrar el uso correcto del comando
            sender.sendMessage(ChatColor.RED + "Uso:");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMotdSetUsageMsg())); // Enviar mensaje personalizado
        }
    }
}
