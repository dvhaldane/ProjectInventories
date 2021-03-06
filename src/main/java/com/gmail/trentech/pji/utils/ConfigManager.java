package com.gmail.trentech.pji.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import com.gmail.trentech.pji.Main;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigManager {

	private Path path;
	private CommentedConfigurationNode config;
	private ConfigurationLoader<CommentedConfigurationNode> loader;
	
	private static ConcurrentHashMap<String, ConfigManager> configManagers = new ConcurrentHashMap<>();

	private ConfigManager(String configName) {
		try {
			path = Main.instance().getPath().resolve(configName + ".conf");
			
			if (!Files.exists(path)) {		
				Files.createFile(path);
				Main.instance().getLog().info("Creating new " + path.getFileName() + " file...");
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}

		load();
	}
	
	public static ConfigManager get(String configName) {
		return configManagers.get(configName);
	}
	
	public static ConfigManager get() {
		return configManagers.get("config");
	}

	public static ConfigManager init() {
		return init("config");
	}
	
	public static ConfigManager init(String configName) {
		ConfigManager configManager = new ConfigManager(configName);
		CommentedConfigurationNode config = configManager.getConfig();
		
		if (configName.equalsIgnoreCase("config")) {
			if (config.getNode("options", "health").isVirtual()) {
				config.getNode("options", "health").setValue(true).setComment("Enable inventory specific health");
			}
			if (config.getNode("options", "hunger").isVirtual()) {
				config.getNode("options", "hunger").setValue(true).setComment("Enable inventory specific hunger");
			}
			if (config.getNode("options", "experience").isVirtual()) {
				config.getNode("options", "experience").setValue(true).setComment("Enable inventory specific experience");
			}
			if (config.getNode("settings", "sql").isVirtual()) {
				config.getNode("settings", "sql", "enable").setValue(false);
				config.getNode("settings", "sql", "prefix").setValue("NONE");
				config.getNode("settings", "sql", "url").setValue("localhost:3306/database");
				config.getNode("settings", "sql", "username").setValue("root");
				config.getNode("settings", "sql", "password").setValue("password");
			}
		}
		
		configManager.save();
		
		configManagers.put(configName, configManager);
		
		return configManager;
	}
	
	public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
		return loader;
	}

	public CommentedConfigurationNode getConfig() {
		return config;
	}

	public void save() {
		try {
			loader.save(config);
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to save config");
			e.printStackTrace();
		}
	}

	private void load() {
		loader = HoconConfigurationLoader.builder().setPath(path).build();
		try {
			config = loader.load();
		} catch (IOException e) {
			Main.instance().getLog().error("Failed to load config");
			e.printStackTrace();
		}
	}
}
