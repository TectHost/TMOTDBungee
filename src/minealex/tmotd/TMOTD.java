package minealex.tmotd;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TMOTD extends Plugin {

    private List<List<String>> motdList;
    private int currentMotd;
    private int changeInterval;
    private String motdSetSuccessMsg;
    private String motdSetUsageMsg;
    private String motdReloadMsg;
    private String noPermissionMsg;
    private String pluginVersionMsg;
    private boolean showPlayerCount;
    private int playerCount;
    private boolean showExtraPlayerCount;
    private int extraPlayerCount;

    @Override
    public void onEnable() {
        loadConfig(); // Carga la configuración del archivo.
        getProxy().getPluginManager().registerListener(this, new MOTDListener(this));
        getProxy().getPluginManager().registerCommand(this, new MOTDCommand(this));

        // Iniciar el ciclo de cambio de MOTD cada 'changeInterval' segundos
        int ticks = 20 * changeInterval; // Convertir segundos a ticks (20 ticks = 1 segundo)
        getProxy().getScheduler().schedule(this, this::changeMotd, 0, ticks, TimeUnit.SECONDS);
    }

    public void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            motdList = new ArrayList<>();
            if (configuration.contains("motds.motd_list")) {
                List<String> motdKeys = configuration.getStringList("motds.motd_list");
                for (String key : motdKeys) {
                    List<String> motdLines = configuration.getStringList("motds." + key + ".lines");
                    motdList.add(motdLines);
                }
            } else {
                // Crea una lista de MOTDs por defecto si no se encuentra en el archivo config.yml
                List<String> motdLines = new ArrayList<>();
                motdLines.add("&aWelcome to the server!");
                motdLines.add("&6Have fun playing!");
                motdList.add(motdLines);
                configuration.set("motds.motd_list", new ArrayList<>());
                saveConfig(configuration, configFile);
            }

            currentMotd = configuration.getInt("motds.current_motd", 0);
            changeInterval = configuration.getInt("motds.change_interval", 5);

            // Asegurarnos de que el índice del MOTD actual esté dentro de los límites de la lista
            currentMotd = Math.max(0, Math.min(currentMotd, motdList.size() - 1));

            formatColors(); // Formatea los colores en las líneas del MOTD actual

            // Cargar mensajes customizables
            motdSetSuccessMsg = ChatColor.translateAlternateColorCodes('&', configuration.getString("messages.motd_set_success"));
            motdSetUsageMsg = ChatColor.translateAlternateColorCodes('&', configuration.getString("messages.motd_set_usage"));
            motdReloadMsg = ChatColor.translateAlternateColorCodes('&', configuration.getString("messages.motd_reload"));
            noPermissionMsg = ChatColor.translateAlternateColorCodes('&', configuration.getString("messages.no_permission_msg"));
            pluginVersionMsg = ChatColor.translateAlternateColorCodes('&', configuration.getString("messages.plugin_version_msg"));

            // Opciones para personalizar el número de jugadores en la lista de servidores
            showPlayerCount = configuration.getBoolean("options.show_player_count", true);
            playerCount = configuration.getInt("options.player_count", 100);
            showExtraPlayerCount = configuration.getBoolean("options.show_extra_player_count", true);
            extraPlayerCount = configuration.getInt("options.extra_player_count", 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(Configuration configuration, File configFile) {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void formatColors() {
        List<String> motdLines = motdList.get(currentMotd);
        for (int i = 0; i < motdLines.size(); i++) {
            motdLines.set(i, ChatColor.translateAlternateColorCodes('&', motdLines.get(i)));
            motdLines.set(i, convertHexColors(motdLines.get(i)));
        }
    }

    private String convertHexColors(String message) {
        Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while (matcher.find()) {
            String hexColor = matcher.group().substring(2); // Elimina los caracteres "&#" del color hexadecimal
            ChatColor color = ChatColor.getByChar(hexColor.charAt(0)); // Obtiene el color correspondiente
            matcher.appendReplacement(buffer, color.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private void changeMotd() {
        currentMotd = new Random().nextInt(motdList.size());
        formatColors(); // Formatea los colores en las líneas del nuevo MOTD
    }

    public void updateMotd(String line1, String line2) {
        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

            // Actualizar las líneas del MOTD en la configuración
            List<String> motdLines = new ArrayList<>();
            motdLines.add(line1);
            motdLines.add(line2);

            configuration.set("motds." + "motd" + (currentMotd + 1) + ".lines", motdLines);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getCurrentMotd() {
        return motdList.get(currentMotd);
    }

    public int getChangeInterval() {
        return changeInterval;
    }

    public String getMotdSetSuccessMsg() {
        return motdSetSuccessMsg;
    }

    public String getMotdSetUsageMsg() {
        return motdSetUsageMsg;
    }

    public String getMotdReloadMsg() {
        return motdReloadMsg;
    }

    public String getNoPermissionMsg() {
        return noPermissionMsg;
    }

    public String getPluginVersionMsg() {
        return pluginVersionMsg;
    }

    // Opciones para personalizar el número de jugadores en la lista de servidores
    public boolean isShowPlayerCount() {
        return showPlayerCount;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public boolean isShowExtraPlayerCount() {
        return showExtraPlayerCount;
    }

    public int getExtraPlayerCount() {
        return extraPlayerCount;
    }
}
