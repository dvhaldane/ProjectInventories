package com.gmail.trentech.pji.data.inventory.extra;

import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.type.GridInventory;

import com.gmail.trentech.pji.data.inventory.Inventory;
import com.gmail.trentech.pji.sql.SQLInventory;
import com.gmail.trentech.pji.utils.ConfigManager;

import ninja.leaping.configurate.ConfigurationNode;

public class InventoryHelper {

	public static void setInventory(Player player, String name) {
		player.getInventory().clear();
		
		Inventory inventory;
		Optional<Inventory> optionalInventory = SQLInventory.get(player, name);

		if(optionalInventory.isPresent()) {
			inventory = optionalInventory.get();
		}else{
			inventory = new Inventory();
		}
		
		Map<Integer, ItemStack> hotbar = inventory.getHotbar();
		
		if(!hotbar.isEmpty()) {
			int i = 0;			
			for(org.spongepowered.api.item.inventory.Inventory slot : player.getInventory().query(Hotbar.class).slots()) {
				if(hotbar.containsKey(i)) {
					slot.set(hotbar.get(i));
				}				
				i++;
			}
		}

		Map<Integer, ItemStack> grid = inventory.getGrid();

		if(!grid.isEmpty()) {
			int i = 0;			
			for(org.spongepowered.api.item.inventory.Inventory slot : player.getInventory().query(GridInventory.class).slots()) {
				if(grid.containsKey(i)) {
					slot.set(grid.get(i));
				}				
				i++;
			}
		}

		Map<Integer, ItemStack> armorMap = inventory.getArmor();

		if(!armorMap.isEmpty()) {
			if(armorMap.containsKey(1)) {
				player.setHelmet(armorMap.get(1));
			}
		
			if(armorMap.containsKey(2)) {
				player.setChestplate(armorMap.get(2));
			}
		
			if(armorMap.containsKey(3)) {
				player.setLeggings(armorMap.get(3));
			}

			if(armorMap.containsKey(4)) {
				player.setBoots(armorMap.get(4));
			}
		}

		ConfigurationNode config = new ConfigManager().getConfig();

		if(config.getNode("options", "health").getBoolean()) {
			player.offer(Keys.HEALTH, inventory.getHealth());
		}

		if(config.getNode("options", "health").getBoolean()) {
			player.offer(Keys.FOOD_LEVEL, inventory.getFood());
			player.offer(Keys.SATURATION, inventory.getSaturation());
		}
		
		if(config.getNode("options", "experience").getBoolean()) {
			player.offer(Keys.EXPERIENCE_LEVEL, inventory.getExpLevel());
			player.offer(Keys.TOTAL_EXPERIENCE, inventory.getExperience());
		}
	}
	
	public static void saveInventory(Player player, String name) {
		Inventory inventory = new Inventory();

		int i = 0;		
		for(org.spongepowered.api.item.inventory.Inventory item : player.getInventory().query(Hotbar.class)) {
			Slot slot = (Slot) item;

			Optional<ItemStack> peek = slot.peek();

			if(peek.isPresent()) {
				inventory.addHotbar(i, peek.get());
			}
			i++;
		}

		i = 0;		
		for(org.spongepowered.api.item.inventory.Inventory inv : player.getInventory().query(GridInventory.class).slots()) {
			Slot slot = (Slot) inv;
			
			Optional<ItemStack> peek = slot.peek();
			
			if(peek.isPresent()) {
				inventory.addGrid(i, peek.get());
			}
			i++;
		}

		Optional<ItemStack> optionalItemStack = player.getHelmet();
		if(optionalItemStack.isPresent()) {
			inventory.addArmor(i, optionalItemStack.get());
		}
		
		optionalItemStack = player.getChestplate();
		if(optionalItemStack.isPresent()) {
			inventory.addArmor(i, optionalItemStack.get());
		}
		
		optionalItemStack = player.getLeggings();
		if(optionalItemStack.isPresent()) {
			inventory.addArmor(i, optionalItemStack.get());
		}
		
		optionalItemStack = player.getBoots();
		if(optionalItemStack.isPresent()) {
			inventory.addArmor(i, optionalItemStack.get());
		}

		inventory.setHealth(player.get(Keys.HEALTH).get());
		inventory.setFood(player.get(Keys.FOOD_LEVEL).get());
		inventory.setSaturation(player.get(Keys.SATURATION).get());
		inventory.setExpLevel(player.get(Keys.EXPERIENCE_LEVEL).get());
		inventory.setExperience(player.get(Keys.TOTAL_EXPERIENCE).get());

		if(SQLInventory.exists(player, name)) {
			SQLInventory.update(player, name, inventory);
		}else{
			SQLInventory.create(player, name, inventory);
		}
	}
}