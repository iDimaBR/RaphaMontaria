package com.github.idimabr;

import com.github.idimabr.commands.AdminCommand;
import com.github.idimabr.commands.UserCommand;
import com.github.idimabr.controllers.PlayerController;
import com.github.idimabr.listeners.LoadListener;
import com.github.idimabr.menus.HorseMenu;
import com.github.idimabr.storage.DatabaseFactory;
import com.github.idimabr.storage.dao.PlayerRepository;
import com.github.idimabr.tasks.CheckHorse;
import com.github.idimabr.utils.ConfigUtil;
import com.google.gson.Gson;
import com.henryfabio.sqlprovider.connector.SQLConnector;
import lombok.Getter;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.stream.Collectors;

@Getter
public final class RaphaMontaria extends JavaPlugin {

    @Getter
    private ConfigUtil config;
    private ViewFrame view;
    private SQLConnector connection;
    private PlayerRepository repository;
    private PlayerController controller;
    @Getter
    public static RaphaMontaria plugin;
    public static Gson GSON;

    @Override
    public void onLoad() {
        this.config = new ConfigUtil(this, "config");
    }

    @Override
    public void onEnable() {
        this.plugin = this;
        this.GSON = new Gson();

        loadStorage();
        loadController();
        loadMenu();
        loadCommands();
        loadListeners();

        new CheckHorse(controller).runTaskTimer(this, 20L * config.getInt("schedule-check", 60), 20L * config.getInt("schedule-check", 60));
    }

    private void loadController() {
        this.controller = new PlayerController(config);
        this.controller.load();
    }

    @Override
    public void onDisable() {
        int entities = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity horse : world.getEntities().stream().filter($ -> $.hasMetadata("customHorse")).collect(Collectors.toList())) {
                horse.remove();
                entities++;
            }
        }

        getLogger().warning("Foram removidos " + entities + " cavalos do mundo.");
    }

    private void loadCommands(){
        getCommand("horsehash").setExecutor(new AdminCommand(controller, repository));
        getCommand("cavalo").setExecutor(new UserCommand(view));
    }

    private void loadListeners(){
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new LoadListener(controller, repository), this);
    }

    private void loadMenu(){
        view = ViewFrame.of(this,
                new HorseMenu(this,
                        config.getInt("menu.row", 5),
                        config.getString("menu.title", "Seus cavalos")
                )
        );
        view.register();
    }

    private void loadStorage() {
        connection = DatabaseFactory.createConnector(config.getConfigurationSection("Database"));
        repository = new PlayerRepository(connection);
        repository.createTable();
    }
}
