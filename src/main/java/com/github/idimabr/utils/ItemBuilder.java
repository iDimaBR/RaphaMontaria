package com.github.idimabr.utils;

import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemBuilder {

	private ItemStack is;
	private final Map<String, ItemMeta> cacheSkull = Maps.newHashMap();

	public ItemBuilder(Material m) {
		this(m, 1);
	}

	public ItemBuilder(String material){
		final boolean skull = material.startsWith("eyJ0");
		is = new ItemStack(skull ? Material.SKULL : Material.valueOf(material), 1);
	}

	public ItemBuilder(ItemStack is) {
		this.is = is;
	}

	public ItemBuilder setMaterial(Material material){
		is.setType(material);
		return this;
	}

	public ItemBuilder(Material m, int quantia) {
		is = new ItemStack(m, quantia);
	}

	public ItemBuilder(Material m, int quantia, byte durabilidade) {
		is = new ItemStack(m, quantia, durabilidade);
	}

	public ItemBuilder setDurability(short durabilidade) {
		is.setDurability(durabilidade);
		return this;
	}

	public ItemBuilder setQuantity(int number){
		is.setAmount(number);
		return this;
	}


	public ItemBuilder setPotion(PotionEffectType type, int duration, int amplifier) {
		PotionMeta im = (PotionMeta) is.getItemMeta();
		im.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setName(String nome) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(nome);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {
		is.addUnsafeEnchantment(ench, level);
		return this;
	}

	public ItemBuilder removeEnchantment(Enchantment ench) {
		is.removeEnchantment(ench);
		return this;
	}

	public ItemBuilder setSkullOwner(String dono) {
		try {
			SkullMeta im = (SkullMeta) is.getItemMeta();
			im.setOwner(dono);
			is.setItemMeta(im);
			is.setDurability((short) 3);
		} catch (ClassCastException expected) {}
		return this;
	}

	public ItemBuilder addEnchant(Enchantment ench, int level) {
		ItemMeta im = is.getItemMeta();
		im.addEnchant(ench, level, true);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setAmount(int amount){
		is.setAmount(amount);
		return this;
	}

	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
		is.addEnchantments(enchantments);
		return this;
	}

	public ItemBuilder setInfinityDurability() {
		is.setDurability(Short.MAX_VALUE);
		return this;
	}

	public ItemBuilder addItemFlag(ItemFlag flag) {
		final ItemMeta itemMeta = is.getItemMeta();
		itemMeta.addItemFlags(flag);
		is.setItemMeta(itemMeta);
		return this;
	}

	public ItemBuilder setLore(String... lore) {
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder replaceLore(String key, String value){
		ItemMeta im = is.getItemMeta();
		final List<String> lore = im.getLore()
				.stream()
				.map($ -> $.replace(key, value))
				.collect(Collectors.toList());
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setLore(List<String> lore) {
		ItemMeta im = is.getItemMeta();
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setLoreIf(boolean condition, String... lore) {
		if(!condition) return this;
		ItemMeta im = is.getItemMeta();
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		return this;

	}

	public ItemBuilder setLoreIf(boolean condition, List<String> lore) {
		if(!condition) return this;
		ItemMeta im = is.getItemMeta();
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder removeLoreLine(String linha) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if (!lore.contains(linha)) return this;
		lore.remove(linha);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder removeLoreLine(int index) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		if (index < 0 || index > lore.size())
			return this;
		lore.remove(index);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;

	}

	public ItemBuilder addLoreIf(boolean condition, String string) {
		if(!condition) return this;
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		if (im.hasLore())
			lore = new ArrayList<>(im.getLore());
		lore.add(string);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addLoreLine(String string) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		if (im.hasLore())
			lore = new ArrayList<>(im.getLore());
		lore.add(string);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addLoreLine(int pos, String string) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		lore.set(pos, string);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	@SuppressWarnings("deprecation")

	public ItemBuilder setDyeColor(DyeColor cor) {
		this.is.setDurability(cor.getDyeData());
		return this;
	}

	@Deprecated

	public ItemBuilder setWoolColor(DyeColor cor) {
		if (!is.getType().toString().contains("WOOL")) return this;

		this.is.setDurability(cor.getWoolData());
		return this;
	}

	public ItemBuilder setLeatherArmorColor(Color cor) {
		try {
			LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
			im.setColor(cor);
			is.setItemMeta(im);
		} catch (ClassCastException expected) {
		}
		return this;
	}

	public ItemStack build() {
		return is;
	}

    public ItemBuilder removeLastLoreLine() {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		lore.remove(lore.size()-1);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
    }
}