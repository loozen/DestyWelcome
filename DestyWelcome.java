package ru.loozen.destywelcome;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class DestyWelcome extends JavaPlugin implements Listener {

    private WelcomeMessage welcomeMessage;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        createConfigFile();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Добавьте код для завершения работы, если необходимо
    }

    private void createConfigFile() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs(); // Создаем папку, если не существует
                configFile.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8))) {
                    writer.write("welcome-message:\n");
                    writer.write("  title: \"§6Добро пожаловать на сервер!\"\n");
                    writer.write("  message: \"§aСледите за нами в социальных сетях:\n" +
                            "  \n§bTelegram: t.me/WertyGriefLoozenYT\n" +
                            "  \n§bVK: t.me/WertyGriefLoozenYT\n" +
                            "  \n§bDiscord: t.me/WertyGriefLoozenYT\"\n");
                    writer.write("sound: \"ENTITY_PLAYER_LEVELUP\" # Звук по умолчанию\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig() {
        config = getConfig();
        ConfigurationSection configSection = config.getConfigurationSection("welcome-message");
        if (configSection != null) {
            String title = configSection.getString("title");
            String text = configSection.getString("message");
            welcomeMessage = new WelcomeMessage(title, text);
        } else {
            getLogger().severe("Раздел 'welcome-message' не найден в файле конфигурации!");
            // Создание раздела "welcome-message" по умолчанию
            config.createSection("welcome-message");
            saveConfig();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Воспроизведение звука
        String soundName = config.getString("sound", "ENTITY_PLAYER_LEVELUP"); // Звук по умолчанию
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, 1.0f, 1.0f);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Звук " + soundName + " не найден! Проверьте конфигурацию.");
        }

        // Отправка приветственного сообщения
        if (welcomeMessage != null) {
            player.sendTitle(welcomeMessage.getTitle(), "", 10, 70, 20);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', welcomeMessage.getText()));
        }
    }

    private class WelcomeMessage {
        private String title;
        private String text;

        public WelcomeMessage(String title, String text) {
            this.title = title;
            this.text = text;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }
    }
}
