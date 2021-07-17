package com.itech4kids.mc.headhunt;

import com.itech4kids.mc.headhunt.Objects.*;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.bukkit.Material.*;

public class EventListener implements Listener {
    private HeadHunt headHunt;
    Inventory kits;
    Inventory cmdPanel;
    Inventory perks;
    Inventory panel;

    public EventListener(HeadHunt headHunt) {
        HeadHunt.log.info("A event listener registered");
        this.headHunt = headHunt;
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) throws IOException {
        headHunt.updateScoreBoard();
        Player player = event.getPlayer();
        Config.createPlayer(player.getName());
        //headHunt.updateStar(player);
        if (!headHunt.getPlayers().containsKey(event.getPlayer().getName())) {
            ItemStack shop = new ItemStack(EMERALD);
            ItemMeta shopMeta = shop.getItemMeta();
            shopMeta.setDisplayName(ChatColor.GREEN + "Shop" + ChatColor.GRAY + " (Right Click)");
            shop.setItemMeta(shopMeta);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.getInventory().setItem(4, shop);
        }
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) throws IOException {
        Player player = e.getEntity().getPlayer();
        Location location = player.getLocation();
        /* Don't drop items */
        e.setKeepInventory(true);
        e.getDrops().clear();
        /* Drop player's own head */
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setDisplayName(ChatColor.RED + player.getName() + "'s Head");
        meta.setOwner(player.getName() + "");
        skull.setItemMeta(meta);
        location.setX(location.getX() + 1);
        if (Config.hasUnlockedPerk(player.getKiller(), PerksType.TELEKINESIS)){
            player.getKiller().getInventory().addItem(skull);
            for (ItemStack i : player.getInventory().getContents()) {
                if ((i != null) && (i.getType() == Material.SKULL_ITEM)) {
                    player.getInventory().remove(i);
                    player.getKiller().getInventory().addItem(i);
                }
            }
        }else{
            player.getWorld().dropItem(location, skull);
            for (ItemStack i : player.getInventory().getContents()) {
                if ((i != null) && (i.getType() == Material.SKULL_ITEM)) {
                    location.setX(location.getX() + 1);
                    player.getWorld().dropItem(location, i);
                    player.getInventory().remove(i);
                }
            }
        }
        /* Give effects to the killer */
        Player killer = player.getKiller();
        ActivePlayer activeKiller = headHunt.findPlayer(player.getKiller().getName());
        Config.addKills(killer.getName(), 1);
        Config.addCoins(killer.getName(), 100);
        Config.addXp(killer.getName(), 5, "Kill");
        Config.addDeath(player.getName(), 1);
        activeKiller.addCoins(25);
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§6+50 Coins! (Kill)" + "\"}"), (byte) 2);
        ((CraftPlayer) killer).getHandle().playerConnection.sendPacket(packet);
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
        headHunt.updateScoreBoard();
        Random rand = new Random();
        ItemStack itemStack = killer.getItemInHand();
        int v = rand.nextInt(15);
        int c = rand.nextInt(15);
        int b = rand.nextInt(6);
        int g = rand.nextInt(20);

        if (c == 1 && Config.hasUnlockedPerk(killer, PerksType.BARBARIAN)) {
            if (itemStack.getType() == Material.DIAMOND_SWORD || itemStack.getType() == IRON_SWORD || itemStack.getType() == GOLD_SWORD || itemStack.getType() == Material.STONE_SWORD || itemStack.getType() == WOOD_SWORD) {
                killer.sendMessage(ChatColor.GREEN + "Your Barbarian Perk gave your item an extra sharpness level!");
                if (killer.getItemInHand().containsEnchantment(Enchantment.DAMAGE_ALL)) {
                    int level = killer.getItemInHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                    killer.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level + 1);
                } else {
                    killer.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                }
            }
        }
        if (Config.hasUnlockedPerk(killer, PerksType.BULLDOZER)) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
        }
        if (Config.hasUnlockedPerk(killer, PerksType.JUGGERNAUT)) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
        }
        if (Config.hasUnlockedPerk(killer, PerksType.SAVIOR)) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 400, 0));
        }
        if (Config.hasUnlockedPerk(killer, PerksType.KNOWLEDGE)) {
            killer.setLevel(killer.getLevel() + 3);
        }
        if (b == 1 && Config.hasUnlockedPerk(killer, PerksType.BLACK_MAGIC)) {
            killer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
            killer.sendMessage(ChatColor.DARK_PURPLE + "Your Black Magic Perk gave you an Ender Pearl!");
        }
        if (b == 2 && Config.hasUnlockedPerk(killer, PerksType.LUCKY_CHARM)) {
            killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
            killer.sendMessage(ChatColor.GOLD + "Your Lucky Charm Perk gave you a Golden Apple!");
        }
        if (g == 1 && Config.hasUnlockedPerk(killer, PerksType.DIAMOND)){
            killer.getInventory().addItem(new ItemStack(Material.DIAMOND, 4));
            killer.sendMessage(ChatColor.DARK_AQUA + "Your Diamond perk gave you 4 diamonds!");
        }else {
            killer.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 2));
        }
        if (v == 1) {
            if (itemStack.getType() == BOW && Config.hasUnlockedPerk(killer, PerksType.MARKSMANSHIP)) {
                killer.sendMessage(ChatColor.GREEN + "Your Marksmanship Perk gave your item an extra power level!");
                int level = killer.getItemInHand().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
                killer.getItemInHand().addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, level + 1);
            } else {
                killer.getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            }
        }
    }

    @EventHandler
    public void PlayerReSpawn(PlayerRespawnEvent e) throws IOException {
        Player player = e.getPlayer();
        if (headHunt.gameState == GameState.BATTLE) {
            Location location = headHunt.battleArena.getASpawnLocation();
            e.setRespawnLocation(location);
            player.teleport(location);
            player.setNoDamageTicks(0);
        } else if (headHunt.gameState == GameState.DEATH_MATCH) {
            Location location = headHunt.deathMatchArena.getASpawnLocation();
            e.setRespawnLocation(location);
            player.sendMessage(ChatColor.RED + "You died!");
            // remove this player from active player
            headHunt.removePlayer(player.getName());
            player.setGameMode(GameMode.SPECTATOR);
            e.setRespawnLocation((Location) headHunt.getConfig().get("lobby-spawn"));
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) throws IOException {
        Player player = (Player) e.getWhoClicked();
        ActivePlayer activePlayer = headHunt.findPlayer(player.getName());
        ItemStack item = e.getCurrentItem();
        Inventory inv = e.getClickedInventory();
        InventoryView view = e.getView();
        ItemStack space1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData());
        ItemMeta spaceMeta = space1.getItemMeta();
        spaceMeta.setDisplayName(" ");
        space1.setItemMeta(spaceMeta);

        if (view.getTitle().equals("HeadHunt Shop")) {
            if (e.getCurrentItem() != null) {
                e.setCancelled(true);
                switch (e.getCurrentItem().getType()) {
                    case DIAMOND_SWORD:
                        kits = Bukkit.createInventory(null, 36, "Buy Kits");
                        kits.clear();

                        ItemStack enderpearl = new ItemStack(Material.ENDER_PEARL);
                        ItemStack bow = new ItemStack(Material.BOW);
                        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                        ItemStack pigman = new ItemStack(Material.GRILLED_PORK);
                        ItemStack tank = new ItemStack(Material.DIAMOND_CHESTPLATE);
                        ItemStack pyromancer = new ItemStack(Material.FLINT_AND_STEEL);
                        ItemStack fisherman = new ItemStack(Material.FISHING_ROD);
                        ItemStack herobrine = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
                        ItemStack scout = new ItemStack(Material.DIAMOND_BOOTS);
                        ItemStack farmer = new ItemStack(Material.EGG);
                        ItemStack sloth = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
                        ItemStack pigrider = new ItemStack(Material.CARROT_STICK);
                        ItemStack troll = new ItemStack(Material.WEB);
                        ItemStack onepoundfish = new ItemStack(Material.RAW_FISH);
                        ItemStack zombie = new ItemStack(Material.ROTTEN_FLESH);
                        ItemStack dreadlord = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
                        ItemStack bomber = new ItemStack(Material.TNT);
                        ItemStack meat = new ItemStack(Material.COOKED_BEEF);
                        ItemStack snow = new ItemStack(Material.SNOW_BALL);
                        ItemStack speleo = new ItemStack(Material.DIAMOND_PICKAXE);
                        ItemStack spider = new ItemStack(Material.SPIDER_EYE);
                        ItemStack eco = new ItemStack(Material.DIAMOND_AXE);

                        ItemMeta itemMeta = enderpearl.getItemMeta();
                        ItemMeta itemMeta1 = bow.getItemMeta();
                        ItemMeta itemMeta2 = sword.getItemMeta();
                        ItemMeta itemMeta4 = pigman.getItemMeta();
                        ItemMeta itemMeta9 = scout.getItemMeta();
                        ItemMeta itemMeta10 = farmer.getItemMeta();
                        SkullMeta itemMeta5 = (SkullMeta) herobrine.getItemMeta();
                        ItemMeta itemMeta6 = tank.getItemMeta();
                        ItemMeta itemMeta7 = pyromancer.getItemMeta();
                        ItemMeta itemMeta8 = fisherman.getItemMeta();
                        SkullMeta itemMeta11 = (SkullMeta) sloth.getItemMeta();
                        ItemMeta itemMeta12 = pigrider.getItemMeta();
                        ItemMeta itemMeta13 = troll.getItemMeta();
                        ItemMeta itemMeta14 = onepoundfish.getItemMeta();
                        ItemMeta itemMeta15 = zombie.getItemMeta();
                        SkullMeta itemMeta16 = (SkullMeta) dreadlord.getItemMeta();
                        ItemMeta itemMeta17 = bomber.getItemMeta();
                        ItemMeta itemMeta18 = meat.getItemMeta();
                        ItemMeta itemMeta19 = snow.getItemMeta();
                        ItemMeta itemMeta20 = speleo.getItemMeta();
                        ItemMeta itemMeta21 = spider.getItemMeta();
                        ItemMeta itemMeta22 = eco.getItemMeta();

                        itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Enderman Kit" + ChatColor.GOLD + " Cost: 15,000");
                        itemMeta22.setDisplayName(ChatColor.GREEN + "Ecologist Kit" + ChatColor.GOLD + " Cost: 15,000");
                        itemMeta18.setDisplayName(ChatColor.YELLOW + "Meatmaster Kit" + ChatColor.GOLD + " Cost: 15,000");
                        itemMeta11.setDisplayName(ChatColor.GRAY + "Sloth Kit" + ChatColor.GOLD + " Cost: 15,000");
                        itemMeta19.setDisplayName(ChatColor.WHITE + "Snowman Kit" + ChatColor.GOLD + " Cost: 15,000");
                        itemMeta20.setDisplayName(ChatColor.GRAY + "Speleologist Kit" + ChatColor.GOLD + " Cost: 15,000");
                        itemMeta12.setDisplayName(ChatColor.GOLD + "Pig Rider Kit" + ChatColor.GOLD + " Cost: 15,000");

                        itemMeta8.setDisplayName(ChatColor.AQUA + "Fisherman Kit" + ChatColor.GOLD + " Cost: 20,000");

                        itemMeta17.setDisplayName(ChatColor.DARK_RED + "Bomber Kit" + ChatColor.GOLD + " Cost: 25,000");
                        itemMeta4.setDisplayName(ChatColor.GOLD + "Pigman Kit" + ChatColor.GOLD + " Cost: 25,000");
                        itemMeta10.setDisplayName(ChatColor.GREEN + "Farmer Kit" + ChatColor.GOLD + " Cost: 25,000");
                        itemMeta14.setDisplayName(ChatColor.AQUA + "One-Pound-Fish Kit" + ChatColor.GOLD + " Cost: 25,000");

                        itemMeta13.setDisplayName(ChatColor.LIGHT_PURPLE + "Troll Kit" + ChatColor.GOLD + " Cost: 30,000");
                        itemMeta15.setDisplayName(ChatColor.DARK_GREEN + "Zombie Kit" + ChatColor.GOLD + " Cost: 30,000");
                        itemMeta21.setDisplayName(ChatColor.GRAY + "Spider Kit" + ChatColor.GOLD + " Cost: 30,000");

                        itemMeta16.setDisplayName(ChatColor.DARK_GRAY + "Dreadlord Kit" + ChatColor.GOLD + " Cost: 35,000");
                        itemMeta7.setDisplayName(ChatColor.RED + "Pyromancer Kit" + ChatColor.GOLD + " Cost: 35,000");
                        itemMeta5.setDisplayName(ChatColor.DARK_PURPLE + "Herobrine Kit" + ChatColor.GOLD + " Cost: 35,000");

                        itemMeta1.setDisplayName(ChatColor.YELLOW + "Archer Kit" + ChatColor.GOLD + " Cost: 40,000");
                        itemMeta6.setDisplayName(ChatColor.DARK_GRAY + "Tank Kit" + ChatColor.GOLD + " Cost: 40,000");

                        itemMeta9.setDisplayName(ChatColor.BLUE + "Scout Kit" + ChatColor.GOLD + " Cost: 45,000");
                        itemMeta2.setDisplayName(ChatColor.RED + "Berserker Kit" + ChatColor.GOLD + " Cost: 45,000");
                        itemMeta5.setOwner("01Herobrine10");
                        itemMeta11.setOwner("Sloth");
                        itemMeta16.setOwner("WitherSkeleton");

                        tank.setItemMeta(itemMeta6);
                        pyromancer.setItemMeta(itemMeta7);
                        fisherman.setItemMeta(itemMeta8);
                        enderpearl.setItemMeta(itemMeta);
                        bow.setItemMeta(itemMeta1);
                        sword.setItemMeta(itemMeta2);
                        pigman.setItemMeta(itemMeta4);
                        herobrine.setItemMeta(itemMeta5);
                        farmer.setItemMeta(itemMeta10);
                        scout.setItemMeta(itemMeta9);
                        sloth.setItemMeta(itemMeta11);
                        pigrider.setItemMeta(itemMeta12);
                        troll.setItemMeta(itemMeta13);
                        onepoundfish.setItemMeta(itemMeta14);
                        zombie.setItemMeta(itemMeta15);
                        dreadlord.setItemMeta(itemMeta16);
                        bomber.setItemMeta(itemMeta17);
                        meat.setItemMeta(itemMeta18);
                        snow.setItemMeta(itemMeta19);
                        speleo.setItemMeta(itemMeta20);
                        spider.setItemMeta(itemMeta21);
                        eco.setItemMeta(itemMeta22);

                        kits.setItem(35, space1);
                        kits.setItem(34, space1);
                        kits.setItem(33, space1);
                        kits.setItem(32, space1);
                        kits.setItem(31, space1);
                        kits.setItem(30, space1);
                        kits.setItem(29, space1);
                        kits.setItem(28, space1);
                        kits.setItem(27, space1);
                        kits.setItem(0, enderpearl);
                        kits.setItem(1, eco);
                        kits.setItem(2, meat);
                        kits.setItem(3, sloth);
                        kits.setItem(4, snow);
                        kits.setItem(5, speleo);
                        kits.setItem(6, pigrider);
                        kits.setItem(7, fisherman);
                        kits.setItem(8, bomber);
                        kits.setItem(9, pigman);
                        kits.setItem(10, farmer);
                        kits.setItem(11, onepoundfish);
                        kits.setItem(12, troll);
                        kits.setItem(13, zombie);
                        kits.setItem(14, spider);
                        kits.setItem(15, dreadlord);
                        kits.setItem(16, pyromancer);
                        kits.setItem(17, herobrine);
                        kits.setItem(18, bow);
                        kits.setItem(19, tank);
                        kits.setItem(20, scout);
                        kits.setItem(21, sword);
                        player.openInventory(kits);
                        break;
                    case CAULDRON_ITEM:
                        perks = Bukkit.createInventory(null, 27, "Buy Perks");
                        perks.clear();

                        ItemStack arrowRecovery = new ItemStack(Material.ARROW);
                        ItemStack barbarian = new ItemStack(Material.DIAMOND_SWORD);
                        ItemStack blackMagic = new ItemStack(ENDER_PEARL);
                        ItemStack blazingArrows = new ItemStack(BLAZE_POWDER);
                        ItemStack bulldozer = new ItemStack(Material.ANVIL);
                        ItemStack juggernaut = new ItemStack(Material.GOLDEN_CARROT);
                        ItemStack knowledge = new ItemStack(EXP_BOTTLE);
                        ItemStack luckyCharm = new ItemStack(Material.GOLDEN_APPLE);
                        ItemStack marksmanship = new ItemStack(Material.BOW);
                        ItemStack resistanceBoost = new ItemStack(DIAMOND_CHESTPLATE);
                        ItemStack savior = new ItemStack(GOLDEN_CARROT);
                        ItemStack telekenisis = new ItemStack(ENCHANTED_BOOK);
                        ItemStack fireAspect = new ItemStack(FLINT_AND_STEEL);
                        ItemStack gauntlet = new ItemStack(FIREBALL);
                        ItemStack weakness = new ItemStack(WOOD_SWORD);
                        ItemStack diamond = new ItemStack(Material.DIAMOND);
                        ItemStack silverfish = new ItemStack(MOB_SPAWNER);

                        ItemMeta arrowRecoveryItemMeta = arrowRecovery.getItemMeta();
                        ItemMeta barbarianItemMeta = barbarian.getItemMeta();
                        ItemMeta blackMagicItemMeta = blackMagic.getItemMeta();
                        ItemMeta blazingArrowsItemMeta = blazingArrows.getItemMeta();
                        ItemMeta bulldozerItemMeta = bulldozer.getItemMeta();
                        ItemMeta juggernautItemMeta = juggernaut.getItemMeta();
                        ItemMeta knowledgeItemMeta = knowledge.getItemMeta();
                        ItemMeta luckyCharmItemMeta = luckyCharm.getItemMeta();
                        ItemMeta marksmanshipItemMeta = marksmanship.getItemMeta();
                        ItemMeta resistanceBoostItemMeta = resistanceBoost.getItemMeta();
                        ItemMeta saviorItemMeta = savior.getItemMeta();
                        ItemMeta telekenisisItemMeta = telekenisis.getItemMeta();
                        ItemMeta fireAspectItemMeta = fireAspect.getItemMeta();
                        ItemMeta gauntletItemMeta = gauntlet.getItemMeta();
                        ItemMeta weaknessItemMeta = weakness.getItemMeta();
                        ItemMeta diamondItemMeta = diamond.getItemMeta();
                        ItemMeta silverfishItemMeta = silverfish.getItemMeta();

                        saviorItemMeta.setDisplayName(ChatColor.YELLOW + "Savior Perk" + ChatColor.GOLD + " Cost: 2,500 Coins");

                        arrowRecoveryItemMeta.setDisplayName(ChatColor.WHITE + "Arrow Recovery Perk" + ChatColor.GOLD + " Cost: 5,000 Coins");
                        weaknessItemMeta.setDisplayName(ChatColor.GRAY + "Weakness Perk" + ChatColor.GOLD + " Cost: 5,000 Coins");
                        resistanceBoostItemMeta.setDisplayName(ChatColor.WHITE + "Resistance Boost Perk" + ChatColor.GOLD + " Cost: 5,000 Coins");

                        blazingArrowsItemMeta.setDisplayName(ChatColor.GOLD + "Blazing Arrows Perk" + ChatColor.GOLD + " Cost: 7,500 Coins");
                        fireAspectItemMeta.setDisplayName(ChatColor.RED + "Fire Aspect Perk" + ChatColor.GOLD + " Cost: 7,500 Coins");

                        juggernautItemMeta.setDisplayName(ChatColor.YELLOW + "Juggernaut Perk" + ChatColor.GOLD + " Cost: 10,000 Coins");
                        knowledgeItemMeta.setDisplayName(ChatColor.AQUA + "Knowledge Perk" + ChatColor.GOLD + " Cost: 10,000 Coins");

                        luckyCharmItemMeta.setDisplayName(ChatColor.GOLD + "Lucky Charm Perk" + ChatColor.GOLD + " Cost: 15,000 Coins");
                        gauntletItemMeta.setDisplayName(ChatColor.GOLD + "Gauntlet Perk" + ChatColor.GOLD + " Cost: 15,000 Coins");
                        silverfishItemMeta.setDisplayName(ChatColor.GRAY + "Arrow Silverfish Perk" + ChatColor.GOLD + " Cost: 15,000 Coins");

                        barbarianItemMeta.setDisplayName(ChatColor.GREEN + "Barbarian Perk" + ChatColor.GOLD + " Cost: 20,000 Coins");
                        marksmanshipItemMeta.setDisplayName(ChatColor.GREEN + "Marksmanship Perk" + ChatColor.GOLD + " Cost: 20,000 Coins");
                        blackMagicItemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Black Magic Perk" + ChatColor.GOLD + " Cost: 20,000 Coins");

                        bulldozerItemMeta.setDisplayName(ChatColor.RED + "Bulldozer Perk" + ChatColor.GOLD + " Cost: 25,000 Coins");
                        telekenisisItemMeta.setDisplayName(ChatColor.BLUE + "Telekenisis Perk" + ChatColor.GOLD + " Cost: 25,000 Coins");

                        diamondItemMeta.setDisplayName(ChatColor.DARK_AQUA + "Diamond Perk" + ChatColor.GOLD + " Cost: 30,000 Coins");

                        arrowRecovery.setItemMeta(arrowRecoveryItemMeta);
                        barbarian.setItemMeta(barbarianItemMeta);
                        blackMagic.setItemMeta(blackMagicItemMeta);
                        blazingArrows.setItemMeta(blazingArrowsItemMeta);
                        bulldozer.setItemMeta(bulldozerItemMeta);
                        juggernaut.setItemMeta(juggernautItemMeta);
                        knowledge.setItemMeta(knowledgeItemMeta);
                        luckyCharm.setItemMeta(luckyCharmItemMeta);
                        marksmanship.setItemMeta(marksmanshipItemMeta);
                        resistanceBoost.setItemMeta(resistanceBoostItemMeta);
                        savior.setItemMeta(saviorItemMeta);
                        telekenisis.setItemMeta(telekenisisItemMeta);
                        fireAspect.setItemMeta(fireAspectItemMeta);
                        silverfish.setItemMeta(silverfishItemMeta);
                        gauntlet.setItemMeta(gauntletItemMeta);
                        weakness.setItemMeta(weaknessItemMeta);
                        diamond.setItemMeta(diamondItemMeta);

                        perks.setItem(26, space1);
                        perks.setItem(25, space1);
                        perks.setItem(24, space1);
                        perks.setItem(23, space1);
                        perks.setItem(22, space1);
                        perks.setItem(21, space1);
                        perks.setItem(20, space1);
                        perks.setItem(19, space1);
                        perks.setItem(18, space1);
                        perks.setItem(0, savior);
                        perks.setItem(1, arrowRecovery);
                        perks.setItem(2, weakness);
                        perks.setItem(3, resistanceBoost);
                        perks.setItem(4, blazingArrows);
                        perks.setItem(5, fireAspect);
                        perks.setItem(6, juggernaut);
                        perks.setItem(7, knowledge);
                        perks.setItem(8, luckyCharm);
                        perks.setItem(9, gauntlet);
                        perks.setItem(10, silverfish);
                        perks.setItem(11, barbarian);
                        perks.setItem(12, marksmanship);
                        perks.setItem(13, blackMagic);
                        perks.setItem(14, bulldozer);
                        perks.setItem(15, telekenisis);
                        perks.setItem(16, diamond);
                        player.openInventory(perks);
                        break;
                    case FIREBALL:
                        panel = Bukkit.createInventory(null, 2*9, "Buy Panel Abilities");
                        panel.clear();

                        ItemStack clearHeads = new ItemStack(FIREBALL);
                        ItemMeta clearHeadsMeta = clearHeads.getItemMeta();
                        clearHeadsMeta.setDisplayName(ChatColor.RED + "Clear all Heads " + ChatColor.GOLD + "25,000 Coins");
                        clearHeadsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                        clearHeadsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        clearHeads.setItemMeta(clearHeadsMeta);

                        ItemStack nuke = new ItemStack(TNT);
                        ItemMeta nukeMeta = nuke.getItemMeta();
                        nukeMeta.setDisplayName(ChatColor.DARK_RED + "Nuke all Players " + ChatColor.GOLD + "15,000 Coins");
                        nuke.setItemMeta(nukeMeta);

                        ItemStack randomTp = new ItemStack(ENDER_PEARL);
                        ItemMeta randomTpMeta = randomTp.getItemMeta();
                        randomTpMeta.setDisplayName(ChatColor.BLUE + "Teleport all Players " + ChatColor.GOLD + "5,000 Coins");
                        randomTp.setItemMeta(randomTpMeta);

                        ItemStack speed = new ItemStack(FEATHER);
                        ItemMeta speedMeta = speed.getItemMeta();
                        speedMeta.setDisplayName(ChatColor.WHITE + "Give players the Speed effect " + ChatColor.GOLD + "5,000");
                        speed.setItemMeta(speedMeta);

                        ItemStack sharp = new ItemStack(DIAMOND_SWORD);
                        ItemMeta sharpMeta = sharp.getItemMeta();
                        sharpMeta.setDisplayName(ChatColor.AQUA + "Enchant all swords Sharpness " + ChatColor.GOLD + "15,000");
                        sharp.setItemMeta(sharpMeta);

                        ItemStack prot = new ItemStack(DIAMOND_CHESTPLATE);
                        ItemMeta protMeta = prot.getItemMeta();
                        protMeta.setDisplayName(ChatColor.AQUA + "Enchant all armor items Protection " + ChatColor.GOLD + "15,000");
                        prot.setItemMeta(protMeta);

                        ItemStack xp = new ItemStack(FISHING_ROD);
                        ItemMeta xpMeta = xp.getItemMeta();
                        xpMeta.setDisplayName(ChatColor.AQUA + "Give all players a fishing rod " + ChatColor.GOLD + "15,000");
                        xp.setItemMeta(xpMeta);

                        ItemStack absorption = new ItemStack(GOLDEN_APPLE);
                        ItemMeta absorptionMeta = absorption.getItemMeta();
                        absorptionMeta.setDisplayName(ChatColor.YELLOW + "Give all players the Absorption effect " + ChatColor.GOLD + "10,000");
                        absorption.setItemMeta(absorptionMeta);

                        ItemStack poison = new ItemStack(SPIDER_EYE);
                        ItemMeta poisonMeta = poison.getItemMeta();
                        poisonMeta.setDisplayName(ChatColor.DARK_GREEN + "Give all players the Poison effect " + ChatColor.GOLD + "10,000");
                        poison.setItemMeta(poisonMeta);

                        ItemStack space3 = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData());
                        space1.getItemMeta().setDisplayName(" ");


                        panel.setItem(17, space1);
                        panel.setItem(16, space1);
                        panel.setItem(15, space1);
                        panel.setItem(14, space1);
                        panel.setItem(13, space1);
                        panel.setItem(12, space1);
                        panel.setItem(11, space1);
                        panel.setItem(10, space1);
                        panel.setItem(9, space1);

                        panel.setItem(0, speed);
                        panel.setItem(1, randomTp);
                        panel.setItem(2, absorption);
                        panel.setItem(3, poison );
                        panel.setItem(4, xp);
                        panel.setItem(5, prot);
                        panel.setItem(6, sharp);
                        panel.setItem(7, nuke);
                        panel.setItem(8, clearHeads);

                        player.openInventory(panel);
                }
            }
        }

        if (view.getTitle().equals("Control Panel")) {
            if (e.getCurrentItem() != null) {
                e.setCancelled(true);
                player.closeInventory();
                switch (e.getCurrentItem().getType()) {
                    case ENDER_PEARL:
                        if (Config.hasUnlockedPanel(player, PanelType.TELEPORT)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.TelePortTo(headHunt.battleArena.getASpawnLocation());
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.BLUE + "Special Ability!", ChatColor.GRAY + player.getName() + ": Teleport");
                            }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case FEATHER:
                        if (Config.hasUnlockedPanel(player, PanelType.SPEED)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1600, 1));
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.DRINK, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.WHITE + "Special Ability!", ChatColor.GRAY + player.getName() + ": Speed");
                            }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case DIAMOND_SWORD:
                        if (Config.hasUnlockedPanel(player, PanelType.SHARPNESS)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.ANVIL_USE, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.AQUA + "Special Ability!", ChatColor.GRAY + player.getName() + ": Sharpness");
                                for (ItemStack itemStack : player.getInventory().getContents()) {
                                    if ((itemStack != null)) {
                                        if (itemStack.getType() == Material.DIAMOND_SWORD || itemStack.getType() == IRON_SWORD || itemStack.getType() == GOLD_SWORD || itemStack.getType() == STONE_SWORD || itemStack.getType() == WOOD_SWORD) {
                                            if (itemStack.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                                                int level = itemStack.getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                                                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level + 1);
                                            } else {
                                                itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                                            }
                                            aplayers.getBukkitPlayer().updateInventory();
                                        }
                                    }                                }
                                }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case DIAMOND_CHESTPLATE:
                        if (Config.hasUnlockedPanel(player, PanelType.PROTECTION)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.ANVIL_USE, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.AQUA + "Special Ability!", ChatColor.GRAY + player.getName() + ": Protection");
                                    for (ItemStack itemStack : aplayers.getBukkitPlayer().getInventory().getArmorContents()){
                                        if ((itemStack != null)) {
                                            if (itemStack.containsEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL)) {
                                                int level = itemStack.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
                                                itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, level + 1);
                                                aplayers.getBukkitPlayer().updateInventory();
                                            } else {
                                                itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                                aplayers.getBukkitPlayer().updateInventory();
                                            }
                                        }
                                    }
                                }
                                player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case FISHING_ROD:
                        if (Config.hasUnlockedPanel(player, PanelType.ROD)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.ITEM_PICKUP, 10, 10);
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.SPLASH, 10, 10);
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.SPLASH2, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.AQUA + "Special Ability!", ChatColor.GRAY + player.getName() + ": Fishing Rod");
                                ItemStack rod = new ItemStack(FISHING_ROD);
                                ItemMeta itemMeta = rod.getItemMeta();
                                itemMeta.spigot().setUnbreakable(true);
                                rod.setItemMeta(itemMeta);
                                aplayers.getBukkitPlayer().getInventory().addItem(rod);
                                aplayers.getBukkitPlayer().updateInventory();
                            }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case TNT:
                        if (Config.hasUnlockedPanel(player, PanelType.NUKE)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.getBukkitPlayer().setHealth(aplayers.getBukkitPlayer().getHealth() - 7);
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.BLAZE_BREATH, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.DARK_RED + "Special Ability!", ChatColor.GRAY + player.getName() + ": Nuke");
                                for (Player p : Bukkit.getOnlinePlayers()){
                                    p.playEffect(aplayers.getBukkitPlayer().getLocation(), Effect.EXPLOSION_LARGE, 10);
                                    p.playSound(aplayers.getBukkitPlayer().getLocation(), Sound.EXPLODE, 10, 10);
                                }
                            }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case FIREBALL:
                        if (Config.hasUnlockedPanel(player, PanelType.CLEAR_HEADS)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                Player player1 = aplayers.getBukkitPlayer();
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.FIRE, 10, 10);
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.BLAZE_BREATH, 10, 10);
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.EXPLODE, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.RED + "Special Ability!", ChatColor.GRAY + player.getName() + ": Clear Heads");
                                for (ItemStack i : player1.getInventory().getContents()) {
                                    if ((i != null) && (i.getType() == Material.SKULL_ITEM)) {
                                        player1.getInventory().remove(i);
                                    }
                                }
                            }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case SPIDER_EYE:
                        if (Config.hasUnlockedPanel(player, PanelType.POISON)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.VILLAGER_NO, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.DARK_GREEN + "Special Ability!", ChatColor.GRAY + player.getName() + ": Poison");
                                aplayers.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 400, 1));
                                aplayers.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1));
                                aplayers.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 400, 1));
                            }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        break;
                    case GOLDEN_APPLE:
                        if (Config.hasUnlockedPanel(player, PanelType.ABSORPTION)) {
                            for (Map.Entry<String, ActivePlayer> entry : headHunt.players.entrySet()) {
                                ActivePlayer aplayers = entry.getValue();
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.BURP, 10, 10);
                                aplayers.getBukkitPlayer().playSound(aplayers.getBukkitPlayer().getLocation(), Sound.EAT, 10, 10);
                                aplayers.getBukkitPlayer().sendTitle(ChatColor.YELLOW + "Special Ability!", ChatColor.GRAY + player.getName() + ": Absorption");
                                aplayers.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1600, 3));
                            }
                            player.getInventory().setItemInHand(null);
                        }else{
                            player.sendMessage(ChatColor.RED + "You haven't unlocked this!");
                        }
                        break;
                    }
                }
            }

        if (view.getTitle().equals("HeadHunt Kits")) {
                if (e.getCurrentItem() != null) {
                    e.setCancelled(true);
                    player.closeInventory();
                    switch (e.getCurrentItem().getType()) {
                        case ENDER_PEARL:
                            if (Config.hasUnlockedKit(player, KitsType.ENDERMAN)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Enderman Kit!");
                                activePlayer.selectKit(KitsType.ENDERMAN);
                                PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Enderman" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case BOW:
                            if (Config.hasUnlockedKit(player, KitsType.ARCHER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Archer Kit!");
                                activePlayer.selectKit(KitsType.ARCHER);
                                PacketPlayOutChat packet1 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Archer" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet1);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case DIAMOND_SWORD:
                            if (Config.hasUnlockedKit(player, KitsType.BERSERKER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Berserker Kit!");
                                PacketPlayOutChat packet2 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Berserker" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet2);
                                activePlayer.selectKit(KitsType.BERSERKER);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case IRON_CHESTPLATE:
                            player.sendMessage(ChatColor.GREEN + "You have selected Default Kit!");
                            PacketPlayOutChat packet3 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Default" + "\"}"), (byte) 2);
                            ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet3);
                            activePlayer.selectKit(KitsType.DEFAULT);
                            player.closeInventory();
                            break;
                        case GRILLED_PORK:
                            if (Config.hasUnlockedKit(player, KitsType.PIGMAN)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Pigman Kit!");
                                PacketPlayOutChat packet4 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Pigman" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet4);
                                activePlayer.selectKit(KitsType.PIGMAN);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case SKULL_ITEM:
                            SkullMeta skullMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
                            if (skullMeta.getOwner().equals("Sloth")) {
                                if (Config.hasUnlockedKit(player, KitsType.SLOTH)) {
                                    player.sendMessage(ChatColor.GREEN + "You have selected Sloth Kit!");
                                    PacketPlayOutChat packet11 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Sloth" + "\"}"), (byte) 2);
                                    ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet11);
                                    activePlayer.selectKit(KitsType.SLOTH);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                                }
                                player.closeInventory();
                            } else if (skullMeta.getOwner().equals("WitherSkeleton")) {
                                if (Config.hasUnlockedKit(player, KitsType.DREADLORD)) {
                                    player.sendMessage(ChatColor.GREEN + "You have selected Dreadlord Kit!");
                                    PacketPlayOutChat packet16 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Dreadlord" + "\"}"), (byte) 2);
                                    ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet16);
                                    activePlayer.selectKit(KitsType.DREADLORD);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                                }
                                player.closeInventory();
                            } else {
                                if (Config.hasUnlockedKit(player, KitsType.HEROBRINE)) {
                                    player.sendMessage(ChatColor.GREEN + "You have selected Herobrine Kit!");
                                    PacketPlayOutChat packet5 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Herobrine" + "\"}"), (byte) 2);
                                    ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet5);
                                    activePlayer.selectKit(KitsType.HEROBRINE);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                                }
                                player.closeInventory();
                            }
                            break;

                        case DIAMOND_CHESTPLATE:
                            if (Config.hasUnlockedKit(player, KitsType.TANK)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Tank Kit!");
                                PacketPlayOutChat packet6 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Tank" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet6);
                                activePlayer.selectKit(KitsType.TANK);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case FLINT_AND_STEEL:
                            if (Config.hasUnlockedKit(player, KitsType.PYROMANCER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Pyromancer Kit!");
                                PacketPlayOutChat packet7 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Pyromancer" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet7);
                                activePlayer.selectKit(KitsType.PYROMANCER);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case FISHING_ROD:
                            if (Config.hasUnlockedKit(player, KitsType.FISHERMAN)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Fisherman Kit!");
                                PacketPlayOutChat packet8 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Fisherman" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet8);
                                activePlayer.selectKit(KitsType.FISHERMAN);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            break;
                        case DIAMOND_BOOTS:
                            if (Config.hasUnlockedKit(player, KitsType.SCOUT)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Scout Kit!");
                                PacketPlayOutChat packet9 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Scout" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet9);
                                activePlayer.selectKit(KitsType.SCOUT);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case EGG:
                            if (Config.hasUnlockedKit(player, KitsType.FARMER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Farmer Kit!");
                                PacketPlayOutChat packet10 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Farmer" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet10);
                                activePlayer.selectKit(KitsType.FARMER);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case CARROT_STICK:
                            if (Config.hasUnlockedKit(player, KitsType.PIGRIDER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Pig Rider Kit!");
                                PacketPlayOutChat packet12 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Pig Rider" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet12);
                                activePlayer.selectKit(KitsType.PIGRIDER);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case WEB:
                            if (Config.hasUnlockedKit(player, KitsType.TROLL)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Troll Kit!");
                                PacketPlayOutChat packet13 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Troll" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet13);
                                activePlayer.selectKit(KitsType.TROLL);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case RAW_FISH:
                            if (Config.hasUnlockedKit(player, KitsType.ONEPOUNDFISH)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected One-Pound-Fish Kit!");
                                PacketPlayOutChat packet14 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a One-Pound-Fish" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet14);
                                activePlayer.selectKit(KitsType.ONEPOUNDFISH);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case ROTTEN_FLESH:
                            if (Config.hasUnlockedKit(player, KitsType.ZOMBIE)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Zombie Kit!");
                                PacketPlayOutChat packet15 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Zombie" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet15);
                                activePlayer.selectKit(KitsType.ZOMBIE);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case TNT:
                            if (Config.hasUnlockedKit(player, KitsType.BOMBER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Bomber Kit!");
                                PacketPlayOutChat packet16 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Bomber" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet16);
                                activePlayer.selectKit(KitsType.BOMBER);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case COOKED_BEEF:
                            if (Config.hasUnlockedKit(player, KitsType.MEATMASTER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Meatmaster Kit!");
                                PacketPlayOutChat packet17 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Meatmaster" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet17);
                                activePlayer.selectKit(KitsType.MEATMASTER);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case SNOW_BALL:
                            if (Config.hasUnlockedKit(player, KitsType.SNOWMAN)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Snowman Kit!");
                                PacketPlayOutChat packet18 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Snowman" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet18);
                                activePlayer.selectKit(KitsType.SNOWMAN);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case SPIDER_EYE:
                            if (Config.hasUnlockedKit(player, KitsType.SPIDER)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Spider Kit!");
                                PacketPlayOutChat packet19 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Spider" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet19);
                                activePlayer.selectKit(KitsType.SPIDER);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case DIAMOND_PICKAXE:
                            if (Config.hasUnlockedKit(player, KitsType.SPELEOLOGIST)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Speleologist Kit!");
                                PacketPlayOutChat packet20 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Speleologist" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet20);
                                activePlayer.selectKit(KitsType.SPELEOLOGIST);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                        case DIAMOND_AXE:
                            if (Config.hasUnlockedKit(player, KitsType.ECOLOGIST)) {
                                player.sendMessage(ChatColor.GREEN + "You have selected Ecologist Kit!");
                                PacketPlayOutChat packet21 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Ecologist" + "\"}"), (byte) 2);
                                ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet21);
                                activePlayer.selectKit(KitsType.ECOLOGIST);
                            } else {
                                player.sendMessage(ChatColor.RED + "You haven't unlocked this kit yet!");
                            }
                            player.closeInventory();
                            break;
                            }
                         }
                     }
                        if (view.getTitle().equals("Buy Kits")) {
                            if (e.getCurrentItem() != null) {
                                e.setCancelled(true);
                                player.closeInventory();
                                String name = player.getName();
                                switch (e.getCurrentItem().getType()) {
                                    case ENDER_PEARL:
                                        if (!Config.hasUnlockedKit(player, KitsType.ENDERMAN)){
                                            if (Config.getCoins(name) >= 15000){
                                                Config.addKit(name, KitsType.ENDERMAN);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Enderman Kit!");
                                                Config.addCoins(name, -15000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case BOW :
                                        if (!Config.hasUnlockedKit(player, KitsType.ARCHER)){
                                            if (Config.getCoins(name) >= 40000){
                                                Config.addKit(name, KitsType.ARCHER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Archer Kit!");
                                                Config.addCoins(name, -40000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case CARROT_STICK :
                                        if (!Config.hasUnlockedKit(player, KitsType.PIGRIDER)){
                                            if (Config.getCoins(name) >= 15000){
                                                Config.addKit(name, KitsType.PIGRIDER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Pig Rider Kit!");
                                                Config.addCoins(name, -15000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case RAW_FISH :
                                        if (!Config.hasUnlockedKit(player, KitsType.ONEPOUNDFISH)){
                                            if (Config.getCoins(name) >= 25000){
                                                Config.addKit(name, KitsType.ONEPOUNDFISH);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the One-Pound-Fish Kit!");
                                                Config.addCoins(name, -25000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case DIAMOND_SWORD :
                                        if (!Config.hasUnlockedKit(player, KitsType.BERSERKER)){
                                            if (Config.getCoins(name) >= 45000){
                                                Config.addKit(name, KitsType.BERSERKER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Berserker Kit!");
                                                Config.addCoins(name, -45000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case GRILLED_PORK :
                                        if (!Config.hasUnlockedKit(player, KitsType.PIGMAN)){
                                            if (Config.getCoins(name) >= 25000){
                                                Config.addKit(name, KitsType.PIGMAN);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Pigman Kit!");
                                                Config.addCoins(name, -25000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case SKULL_ITEM :
                                        SkullMeta skullMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
                                        if (skullMeta.getOwner().equals("Sloth")) {
                                            if (!Config.hasUnlockedKit(player, KitsType.SLOTH)){
                                                if (Config.getCoins(name) >= 15000){
                                                    Config.addKit(name, KitsType.SLOTH);
                                                    player.sendMessage(ChatColor.GREEN + "You have successfully bought the Sloth Kit!");
                                                    Config.addCoins(name, -15000);
                                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                                }else{
                                                    player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                                }
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                            player.closeInventory();
                                        } else if (skullMeta.getOwner().equals("WitherSkeleton")) {
                                            if (!Config.hasUnlockedKit(player, KitsType.DREADLORD)){
                                                if (Config.getCoins(name) >= 35000){
                                                    Config.addKit(name, KitsType.DREADLORD);
                                                    player.sendMessage(ChatColor.GREEN + "You have successfully bought the Dreadlord Kit!");
                                                    Config.addCoins(name, -35000);
                                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                                }else{
                                                    player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                                }
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                            player.closeInventory();
                                        } else {
                                            if (!Config.hasUnlockedKit(player, KitsType.HEROBRINE)){
                                                if (Config.getCoins(name) >= 35000){
                                                    Config.addKit(name, KitsType.HEROBRINE);
                                                    player.sendMessage(ChatColor.GREEN + "You have successfully bought the Herobrine Kit!");
                                                    Config.addCoins(name, -35000);
                                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                                }else{
                                                    player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                                }
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                            player.closeInventory();
                                        }
                                        break;
                                    case DIAMOND_CHESTPLATE :
                                        if (!Config.hasUnlockedKit(player, KitsType.TANK)){
                                            if (Config.getCoins(name) >= 40000){
                                                Config.addKit(name, KitsType.TANK);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Tank Kit!");
                                                Config.addCoins(name, -40000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case FLINT_AND_STEEL :
                                        if (!Config.hasUnlockedKit(player, KitsType.PYROMANCER)){
                                            if (Config.getCoins(name) >= 35000){
                                                Config.addKit(name, KitsType.PYROMANCER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Pyromancer Kit!");
                                                Config.addCoins(name, -35000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case FISHING_ROD :
                                        if (!Config.hasUnlockedKit(player, KitsType.FISHERMAN)){
                                            if (Config.getCoins(name) >= 20000){
                                                Config.addKit(name, KitsType.FISHERMAN);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Fisherman Kit!");
                                                Config.addCoins(name, -20000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case DIAMOND_BOOTS :
                                        if (!Config.hasUnlockedKit(player, KitsType.SCOUT)){
                                            if (Config.getCoins(name) >= 45000){
                                                Config.addKit(name, KitsType.SCOUT);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Scout Kit!");
                                                Config.addCoins(name, -45000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case EGG :
                                        if (!Config.hasUnlockedKit(player, KitsType.FARMER)){
                                            if (Config.getCoins(name) >= 25000){
                                                Config.addKit(name, KitsType.FARMER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Farmer Kit!");
                                                Config.addCoins(name, -25000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case WEB :
                                        if (!Config.hasUnlockedKit(player, KitsType.TROLL)){
                                            if (Config.getCoins(name) >= 30000){
                                                Config.addKit(name, KitsType.TROLL);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Troll Kit!");
                                                Config.addCoins(name, -30000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case ROTTEN_FLESH :
                                        if (!Config.hasUnlockedKit(player, KitsType.ZOMBIE)){
                                            if (Config.getCoins(name) >= 30000){
                                                Config.addKit(name, KitsType.ZOMBIE);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Zombie Kit!");
                                                Config.addCoins(name, -30000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case TNT :
                                        if (!Config.hasUnlockedKit(player, KitsType.BOMBER)){
                                            if (Config.getCoins(name) >= 25000){
                                                Config.addKit(name, KitsType.BOMBER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Bomber Kit!");
                                                Config.addCoins(name, -25000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case COOKED_BEEF :
                                        if (!Config.hasUnlockedKit(player, KitsType.MEATMASTER)){
                                            if (Config.getCoins(name) >= 15000){
                                                Config.addKit(name, KitsType.MEATMASTER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Meatmaster Kit!");
                                                Config.addCoins(name, -15000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case SNOW_BALL :
                                        if (!Config.hasUnlockedKit(player, KitsType.SNOWMAN)){
                                            if (Config.getCoins(name) >= 15000){
                                                Config.addKit(name, KitsType.SNOWMAN);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Snowman Kit!");
                                                Config.addCoins(name, -15000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0, 1);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 0, 1);
                                        }
                                        player.closeInventory();
                                        break;
                                    case SPIDER_EYE :
                                        if (!Config.hasUnlockedKit(player, KitsType.SPIDER)){
                                            if (Config.getCoins(name) >= 30000){
                                                Config.addKit(name, KitsType.SPIDER);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Spider Kit!");
                                                Config.addCoins(name, -30000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case DIAMOND_PICKAXE :
                                        if (!Config.hasUnlockedKit(player, KitsType.SPELEOLOGIST)){
                                            if (Config.getCoins(name) >= 15000){
                                                Config.addKit(name, KitsType.SPELEOLOGIST);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Speleologist Kit!");
                                                Config.addCoins(name, -15000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                    case DIAMOND_AXE :
                                        if (!Config.hasUnlockedKit(player, KitsType.ECOLOGIST)){
                                            if (Config.getCoins(name) >= 15000){
                                                Config.addKit(name, KitsType.ECOLOGIST);
                                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Ecologist Kit!");
                                                Config.addCoins(name, -15000);
                                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                            }else{
                                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                            }
                                        }else{
                                            player.sendMessage(ChatColor.RED + "You have already unlocked this kit!");
                                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                        }
                                        player.closeInventory();
                                        break;
                                }
                            }
                        }

        if (view.getTitle().equals("Buy Perks")) {
            if (e.getCurrentItem() != null) {
                e.setCancelled(true);
                player.closeInventory();
                String name = player.getName();
                switch (e.getCurrentItem().getType()) {
                    case GOLDEN_CARROT:
                        if (item.getItemMeta().getDisplayName().startsWith(ChatColor.YELLOW + "Savior")){
                            if (!Config.hasUnlockedPerk(player, PerksType.SAVIOR)) {
                                if (Config.getCoins(name) >= 5000) {
                                    Config.addPerk(name, PerksType.SAVIOR);
                                    player.sendMessage(ChatColor.GREEN + "You have successfully bought the Savior Perk!");
                                    Config.addCoins(name, -5000);
                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        }else{
                            if (!Config.hasUnlockedPerk(player, PerksType.JUGGERNAUT)) {
                                if (Config.getCoins(name) >= 15000) {
                                    Config.addPerk(name, PerksType.JUGGERNAUT);
                                    player.sendMessage(ChatColor.GREEN + "You have successfully bought the Juggernaut Perk!");
                                    Config.addCoins(name, -15000);
                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                                } else {
                                    player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                    player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        }
                        player.closeInventory();
                        break;
                    case ARROW:
                        if (!Config.hasUnlockedPerk(player, PerksType.ARROW_RECOVERY)) {
                            if (Config.getCoins(name) >= 5000) {
                                Config.addPerk(name, PerksType.ARROW_RECOVERY);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Arrow Recovery Perk!");
                                Config.addCoins(name, -5000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case WOOD_SWORD:
                        if (!Config.hasUnlockedPerk(player, PerksType.WEAKNESS)) {
                            if (Config.getCoins(name) >= 5000) {
                                Config.addPerk(name, PerksType.WEAKNESS);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Weakness Perk!");
                                Config.addCoins(name, -5000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case DIAMOND_CHESTPLATE:
                        if (!Config.hasUnlockedPerk(player, PerksType.RESISTANCE_BOOST)) {
                            if (Config.getCoins(name) >= 5000) {
                                Config.addPerk(name, PerksType.RESISTANCE_BOOST);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Resistance Boost Perk!");
                                Config.addCoins(name, -5000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case BLAZE_POWDER:
                        if (!Config.hasUnlockedPerk(player, PerksType.BLAZING_ARROWS)) {
                            if (Config.getCoins(name) >= 7500) {
                                Config.addPerk(name, PerksType.BLAZING_ARROWS);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Blaazing Arrows Perk!");
                                Config.addCoins(name, -7500);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case FLINT_AND_STEEL:
                        if (!Config.hasUnlockedPerk(player, PerksType.FIRE_ASPECT)) {
                            if (Config.getCoins(name) >= 7500) {
                                Config.addPerk(name, PerksType.FIRE_ASPECT);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Fire Aspect Perk!");
                                Config.addCoins(name, -7500);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case EXP_BOTTLE:
                        if (!Config.hasUnlockedPerk(player, PerksType.KNOWLEDGE)) {
                            if (Config.getCoins(name) >= 10000) {
                                Config.addPerk(name, PerksType.KNOWLEDGE);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Knowledge Perk!");
                                Config.addCoins(name, -10000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case GOLDEN_APPLE:
                        if (!Config.hasUnlockedPerk(player, PerksType.LUCKY_CHARM)) {
                            if (Config.getCoins(name) >= 15000) {
                                Config.addPerk(name, PerksType.LUCKY_CHARM);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Lucky Charm Perk!");
                                Config.addCoins(name, -15000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case FIREBALL:
                        if (!Config.hasUnlockedPerk(player, PerksType.GAUNTLET)) {
                            if (Config.getCoins(name) >= 15000) {
                                Config.addPerk(name, PerksType.GAUNTLET);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Gauntlet Perk!");
                                Config.addCoins(name, -15000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case DIAMOND_SWORD:
                        if (!Config.hasUnlockedPerk(player, PerksType.BARBARIAN)) {
                            if (Config.getCoins(name) >= 20000) {
                                Config.addPerk(name, PerksType.BARBARIAN);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Barbarian Perk!");
                                Config.addCoins(name, -20000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case BOW:
                        if (!Config.hasUnlockedPerk(player, PerksType.MARKSMANSHIP)) {
                            if (Config.getCoins(name) >= 20000) {
                                Config.addPerk(name, PerksType.MARKSMANSHIP);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Marksmanship Perk!");
                                Config.addCoins(name, -20000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case ENDER_PEARL:
                        if (!Config.hasUnlockedPerk(player, PerksType.BLACK_MAGIC)) {
                            if (Config.getCoins(name) >= 20000) {
                                Config.addPerk(name, PerksType.BLACK_MAGIC);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Black Magic Perk!");
                                Config.addCoins(name, -20000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case ANVIL:
                        if (!Config.hasUnlockedPerk(player, PerksType.BULLDOZER)) {
                            if (Config.getCoins(name) >= 25000) {
                                Config.addPerk(name, PerksType.BULLDOZER);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Bulldozer Perk!");
                                Config.addCoins(name, -25000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case ENCHANTED_BOOK:
                        if (!Config.hasUnlockedPerk(player, PerksType.TELEKINESIS)) {
                            if (Config.getCoins(name) >= 25000) {
                                Config.addPerk(name, PerksType.TELEKINESIS);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Telekinesis Perk!");
                                Config.addCoins(name, -25000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case DIAMOND:
                        if (!Config.hasUnlockedPerk(player, PerksType.DIAMOND)) {
                            if (Config.getCoins(name) >= 30000) {
                                Config.addPerk(name, PerksType.DIAMOND);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Diamond Perk!");
                                Config.addCoins(name, -30000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case MOB_SPAWNER:
                        if (!Config.hasUnlockedPerk(player, PerksType.ARROW_SILVERFISH)) {
                            if (Config.getCoins(name) >= 15000) {
                                Config.addPerk(name, PerksType.ARROW_SILVERFISH);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Arrow Silverfish Perk!");
                                Config.addCoins(name, -15000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Perk!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                        }
                    }
                }
        if (view.getTitle().equals("Buy Panel Abilities")) {
            String name = player.getName();
            if (e.getCurrentItem() != null) {
                e.setCancelled(true);
                player.closeInventory();
                switch (e.getCurrentItem().getType()) {
                    case FEATHER:
                        if (!Config.hasUnlockedPanel(player, PanelType.SPEED)) {
                            if (Config.getCoins(name) >= 5000) {
                                Config.addPanel(name, PanelType.SPEED);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Speed Ability!");
                                Config.addCoins(name, -5000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case ENDER_PEARL:
                        if (!Config.hasUnlockedPanel(player, PanelType.TELEPORT)) {
                            if (Config.getCoins(name) >= 5000) {
                                Config.addPanel(name, PanelType.TELEPORT);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Teleport Ability!");
                                Config.addCoins(name, -5000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case GOLDEN_APPLE:
                        if (!Config.hasUnlockedPanel(player, PanelType.ABSORPTION)) {
                            if (Config.getCoins(name) >= 10000) {
                                Config.addPanel(name, PanelType.ABSORPTION);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Absorption Ability!");
                                Config.addCoins(name, -10000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case SPIDER_EYE:
                        if (!Config.hasUnlockedPanel(player, PanelType.POISON)) {
                            if (Config.getCoins(name) >= 10000) {
                                Config.addPanel(name, PanelType.POISON);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Poison Ability!");
                                Config.addCoins(name, -10000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case FISHING_ROD:
                        if (!Config.hasUnlockedPanel(player, PanelType.ROD)) {
                            if (Config.getCoins(name) >= 15000) {
                                Config.addPanel(name, PanelType.ROD);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Fishing Rod Ability!");
                                Config.addCoins(name, -15000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case DIAMOND_CHESTPLATE:
                        if (!Config.hasUnlockedPanel(player, PanelType.PROTECTION)) {
                            if (Config.getCoins(name) >= 15000) {
                                Config.addPanel(name, PanelType.PROTECTION);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Protection Ability!");
                                Config.addCoins(name, -15000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case DIAMOND_SWORD:
                        if (!Config.hasUnlockedPanel(player, PanelType.SHARPNESS)) {
                            if (Config.getCoins(name) >= 15000) {
                                Config.addPanel(name, PanelType.SHARPNESS);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Sharpness Ability!");
                                Config.addCoins(name, -15000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case TNT:
                        if (!Config.hasUnlockedPanel(player, PanelType.NUKE)) {
                            if (Config.getCoins(name) >= 15000) {
                                Config.addPanel(name, PanelType.NUKE);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Nuke Ability!");
                                Config.addCoins(name, -15000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                    case FIREBALL:
                        if (!Config.hasUnlockedPanel(player, PanelType.CLEAR_HEADS)) {
                            if (Config.getCoins(name) >= 25000) {
                                Config.addPanel(name, PanelType.CLEAR_HEADS);
                                player.sendMessage(ChatColor.GREEN + "You have successfully bought the Clear Heads Ability!");
                                Config.addCoins(name, -25000);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 10);
                            } else {
                                player.sendMessage(ChatColor.RED + "You don't have enough coins!");
                                player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "You have already unlocked this Ability!");
                            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10, 10);
                        }
                        player.closeInventory();
                        break;
                }
            }
        }
    }


    @EventHandler
    public void onSplit(SlimeSplitEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event){
        if(event.getEntity() instanceof Zombie || event.getEntity() instanceof Skeleton){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        Random rand = new Random();
        int i = rand.nextInt(3);
        if (i == 1){
            e.setCancelled(true);
        }else{
            e.setCancelled(false);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Random rand = new Random();
        int l = rand.nextInt(5);
        Player victim = (Player) e.getEntity();
        Entity damager = e.getDamager();

        if (e.getEntity() instanceof Player) {
            final Player entity = (Player) e.getEntity();
            EntityPlayer entityPlayer = ((CraftPlayer) entity).getHandle();
            final double health = Math.max(Math.ceil((entity.getHealth() + entityPlayer.getAbsorptionHearts()) - e.getFinalDamage()), 0);
            ItemStack skull = victim.getInventory().getItemInHand();

            if (damager instanceof Projectile) {
                if (((Projectile) damager).getShooter() instanceof Player) {
                    if (victim.getHealth() - e.getDamage() < 0) {
                        ((Player) ((Projectile) damager).getShooter()).sendMessage(ChatColor.GRAY +
                                victim.getName() + " is on " + ChatColor.RED + health + "❤" + ChatColor.GRAY + "!");
                    } else {
                        ((Player) ((Projectile) damager).getShooter()).sendMessage(ChatColor.GRAY +
                                victim.getName() + " is on " + ChatColor.RED + health + "❤" + ChatColor.GRAY + "!");
                    }

                }

            }
            int g = rand.nextInt(15);
            int a = rand.nextInt(10);
            ItemStack itemStack1 = ((Player) damager).getItemInHand();
            if (g == 1) {
                if (itemStack1.getType() == Material.AIR && Config.hasUnlockedPerk((Player) damager, PerksType.WEAKNESS)) {
                    victim.sendMessage(ChatColor.GREEN + damager.getName() + " gave you Weakness!");
                    damager.sendMessage(ChatColor.GREEN + "Your Weakness perk has activated!");
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 4));
                }
            }

            if (a == 1) {
                if (itemStack1.getType() == Material.AIR && Config.hasUnlockedPerk((Player) damager, PerksType.GAUNTLET)) {
                    e.setDamage(8.25);
                    ((Player) damager).playSound(damager.getLocation(), Sound.ANVIL_BREAK, 10, 10);
                    ((Player) victim).playSound(damager.getLocation(), Sound.ANVIL_BREAK, 10, 10);
                }
            }
        }
    }

    @EventHandler
    void onExplode(EntityDamageByEntityEvent e) {
        Random rand = new Random();
        int l = rand.nextInt(5);
        Player victim = (Player) e.getEntity();
        Player damager = (Player) e.getDamager();
        ItemStack itemStack1 = damager.getItemInHand();

        if (l == 1 && itemStack1.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "Bomber's Sword") && itemStack1.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            victim.playSound(victim.getLocation(), Sound.EXPLODE, 1, 1);
            victim.playEffect(victim.getLocation(), Effect.LARGE_SMOKE, 1);
            victim.playEffect(victim.getLocation(), Effect.EXPLOSION, 1);
            victim.setHealth(victim.getHealth() - 3);
            damager.playSound(damager.getLocation(), Sound.EXPLODE, 1, 1);
            damager.playEffect(victim.getLocation(), Effect.LARGE_SMOKE, 1);
            damager.playEffect(victim.getLocation(), Effect.EXPLOSION, 1);
        }
    }


    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack skull = player.getInventory().getItemInHand();

        boolean rightClick = (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR));
        if (rightClick && skull.getType() == Material.SKULL_ITEM && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
            if (player.getItemInHand().getAmount() == 1) {
                player.getInventory().setItemInHand(null);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 800, 1));
                player.playSound(player.getLocation(), Sound.EAT, 1, 1);

            } else if (rightClick && skull.getType() == Material.SKULL_ITEM && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2000, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 800, 1));
                player.playSound(player.getLocation(), Sound.EAT, 1, 1);
            }
        } else if (rightClick && skull.getType() == SKULL_ITEM && skull.getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Diamond Head")){
            if (player.getItemInHand().getAmount() == 1) {
                player.getInventory().setItemInHand(null);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2000, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 300, 1));
                player.playSound(player.getLocation(), Sound.EAT, 1, 1);

            }else if (rightClick && skull.getType() == Material.SKULL_ITEM && skull.getItemMeta().getDisplayName().equals(ChatColor.DARK_AQUA + "Diamond Head")) {
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.ABSORPTION);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2000, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 300, 1));
                player.playSound(player.getLocation(), Sound.EAT, 1, 1);
            }

        } else {
            if (rightClick && skull.getType() == Material.SKULL_ITEM) {
                if (player.getItemInHand().getAmount() == 1) {
                    player.getInventory().setItemInHand(null);
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
                    player.playSound(player.getLocation(), Sound.EAT, 1, 1);

                } else {
                    player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                    player.removePotionEffect(PotionEffectType.REGENERATION);
                    player.removePotionEffect(PotionEffectType.SPEED);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));
                    player.playSound(player.getLocation(), Sound.EAT, 1, 1);

                }
            }
        }
        if (rightClick && skull.getType() == BOW && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Kit selector" + ChatColor.GRAY + " (Right Click)")) {
            player.performCommand("kitsinventory");
        } else if (rightClick && skull.getType() == Material.EMERALD && skull.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Shop" + ChatColor.GRAY + " (Right Click)")) {
            player.performCommand("shop");
        } else if (rightClick && skull.getType() == Material.CHEST && skull.getItemMeta().getDisplayName().equals(ChatColor.RED + "Control Panel")) {
            cmdPanel = Bukkit.createInventory(null, 27, "Control Panel");
            cmdPanel.clear();
            ItemStack clearHeads = new ItemStack(FIREBALL);
            ItemMeta clearHeadsMeta = clearHeads.getItemMeta();
            clearHeadsMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.MAGIC + "aa" + ChatColor.RESET + ChatColor.RED + " Destroy all heads " + ChatColor.YELLOW + "" + ChatColor.MAGIC + "aa" + ChatColor.RESET + ChatColor.YELLOW + " (Click)");
            clearHeadsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            clearHeadsMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            clearHeads.setItemMeta(clearHeadsMeta);

            ItemStack nuke = new ItemStack(TNT);
            ItemMeta nukeMeta = nuke.getItemMeta();
            nukeMeta.setDisplayName(ChatColor.DARK_RED + "Nuke all players  " + ChatColor.YELLOW + "(Click)");
            nuke.setItemMeta(nukeMeta);

            ItemStack randomTp = new ItemStack(ENDER_PEARL);
            ItemMeta randomTpMeta = randomTp.getItemMeta();
            randomTpMeta.setDisplayName(ChatColor.BLUE + "Teleport all players randomly " + ChatColor.YELLOW + "(Click)");
            randomTp.setItemMeta(randomTpMeta);

            ItemStack speed = new ItemStack(FEATHER);
            ItemMeta speedMeta = speed.getItemMeta();
            speedMeta.setDisplayName(ChatColor.WHITE + "Give players the Speed effect " + ChatColor.YELLOW + "(Click)");
            speed.setItemMeta(speedMeta);

            ItemStack sharp = new ItemStack(DIAMOND_SWORD);
            ItemMeta sharpMeta = sharp.getItemMeta();
            sharpMeta.setDisplayName(ChatColor.AQUA + "Enchant all items Sharpness " + ChatColor.YELLOW + "(Click)");
            sharp.setItemMeta(sharpMeta);

            ItemStack prot = new ItemStack(DIAMOND_CHESTPLATE);
            ItemMeta protMeta = prot.getItemMeta();
            protMeta.setDisplayName(ChatColor.AQUA + "Enchant all items Protection " + ChatColor.YELLOW + "(Click)");
            prot.setItemMeta(protMeta);

            ItemStack xp = new ItemStack(FISHING_ROD);
            ItemMeta xpMeta = xp.getItemMeta();
            xpMeta.setDisplayName(ChatColor.AQUA + "Give all players a fishing rod " + ChatColor.YELLOW + "(Click)");
            xp.setItemMeta(xpMeta);

            ItemStack absorption = new ItemStack(GOLDEN_APPLE);
            ItemMeta absorptionMeta = absorption.getItemMeta();
            absorptionMeta.setDisplayName(ChatColor.YELLOW + "Give all players the Absorption effect " + ChatColor.YELLOW + "(Click)");
            absorption.setItemMeta(absorptionMeta);

            ItemStack poison = new ItemStack(SPIDER_EYE);
            ItemMeta poisonMeta = poison.getItemMeta();
            poisonMeta.setDisplayName(ChatColor.DARK_GREEN + "Give all players the Poison effect " + ChatColor.YELLOW + "(Click)");
            poison.setItemMeta(poisonMeta);

            ItemStack space1 = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getData());
            space1.getItemMeta().setDisplayName(" ");

            for (int i = 0; i < 27; ++i) {
                cmdPanel.setItem(i, space1);
            }

            cmdPanel.setItem(9, absorption);
            cmdPanel.setItem(10, prot);
            cmdPanel.setItem(11, sharp);
            cmdPanel.setItem(12, speed);
            cmdPanel.setItem(13, clearHeads);
            cmdPanel.setItem(14, randomTp);
            cmdPanel.setItem(15, nuke);
            cmdPanel.setItem(16, xp);
            cmdPanel.setItem(17, poison);

            /*
            for (ItemStack item: cmdPanelItems) {
                cmdPanel.setItem(cmdPanel.firstEmpty(), item);
            }
             */
            player.openInventory(cmdPanel);
        }
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent e){
        if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.EGG)){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onTnt(PlayerInteractEvent e){
        Player player = e.getPlayer();
        ItemStack skull = player.getInventory().getItemInHand();

        boolean rightClick = (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR));
        if (rightClick && skull.getType() == Material.TNT) {
            if (player.getItemInHand().getAmount() == 1) {
                player.getInventory().setItemInHand(null);
                player.playSound(player.getLocation(), Sound.EXPLODE, 20, 20);
                player.playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 10);
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                for (Entity entity : player.getNearbyEntities(20, 20, 20)){
                    if (entity instanceof Player){
                        Player p = (Player) entity;
                        p.setHealth(p.getHealth()-4);
                        p.playSound(p.getLocation(), Sound.EXPLODE, 20, 20);
                        p.playEffect(p.getLocation(), Effect.EXPLOSION_LARGE, 10);
                    }
                }
            } else if (rightClick && skull.getType() == Material.TNT) {
                player.playSound(player.getLocation(), Sound.EXPLODE, 20, 20);
                player.playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 10);
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                for (Entity entity : player.getNearbyEntities(20, 20, 20)){
                    if (entity instanceof Player){
                        Player p = (Player) entity;
                        p.setHealth(p.getHealth()-4);
                        p.playSound(p.getLocation(), Sound.EXPLODE, 20, 20);
                        p.playEffect(p.getLocation(), Effect.EXPLOSION_LARGE, 10);
                    }
                }
            }
        }

        if (rightClick && skull.getType() == Material.WEB) {
            if (player.getItemInHand().getAmount() == 1) {
                player.getInventory().setItemInHand(null);
                player.playSound(player.getLocation(), Sound.EXPLODE, 20, 20);
                player.playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 10);
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                for (Entity entity : player.getNearbyEntities(20, 20, 20)){
                    if (entity instanceof Player){
                        Player p = (Player) entity;
                        p.setHealth(p.getHealth()-1);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                        player.playSound(player.getLocation(), Sound.EXPLODE, 20, 20);
                        player.playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 10);
                    }
                }
            } else if (rightClick && skull.getType() == Material.WEB) {
                player.playSound(player.getLocation(), Sound.EXPLODE, 20, 20);
                player.playEffect(player.getLocation(), Effect.EXPLOSION, 10);
                player.getInventory().getItemInHand().setAmount(player.getInventory().getItemInHand().getAmount() - 1);
                for (Entity entity : player.getNearbyEntities(20, 20, 20)){
                    if (entity instanceof Player){
                        Player p = (Player) entity;
                        p.setHealth(p.getHealth()-1);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 1));
                        p.playSound(p.getLocation(), Sound.EXPLODE, 20, 20);
                        p.playEffect(p.getLocation(), Effect.EXPLOSION_LARGE, 10);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack skull = player.getInventory().getItemInHand();
        Location location = player.getLocation();

        boolean rightClick = (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR));
        if (skull.getType() == GOLD_SWORD && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pigman Sword")) {
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2));

            if (rightClick && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pigman Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                if (player.getHealth() <= 2) {
                    player.sendMessage(ChatColor.RED + "You don't have enough health");
                } else {
                    List<Entity> near = player.getNearbyEntities(5, 5, 5);
                    player.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
                    player.playEffect(player.getLocation(), Effect.SMOKE, 2);
                    player.playSound(player.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2, 3);
                    player.setHealth(player.getHealth() - 2);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0));
                    for (Entity entity : near) {
                        if (entity instanceof Player) {
                            if (((Player) entity).getHealth() <= 2) {
                                ((Player) entity).setHealth(0);
                            }
                            ((Player) entity).setHealth(((Player) entity).getHealth() - 2);
                        }
                    }
                }
            }

        }

    }


    @EventHandler
    public void onClicked(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack skull = player.getInventory().getItemInHand();
        Random rand = new Random();
        int v = rand.nextInt(20);
        int b = rand.nextInt(8);
        int c = rand.nextInt(25);
        int g = rand.nextInt(5);
        int h = rand.nextInt(6);
        int l = rand.nextInt(6);
        int j = h + 2;
        boolean rightClick = (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR));
        boolean leftClick = (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR));

        if (rightClick && g == 1 && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Zombie Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL) && v <= 5) {
            player.setHealth(player.getHealth() + j);
            player.sendMessage(ChatColor.DARK_GREEN + "Your Zombie Sword healed you for " + j + " health!");
            player.playSound(player.getLocation(), Sound.ZOMBIE_HURT, 1, 1);
        }
        if (player.getHealth() > 2) {
            if (rightClick && v == 1 && skull.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Herobrine's Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
            } else if (rightClick && b == 1 && skull.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Herobrine's Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                List<Entity> near = player.getNearbyEntities(5, 5, 5);
                for (Entity entity : near) {
                    if (entity instanceof Player) {
                        if (((Player) entity).getHealth() <= 2) {
                            ((Player) entity).setHealth(0);
                        }
                        ((Player) entity).setHealth(((Player) entity).getHealth() - 3);
                        entity.getWorld().strikeLightning(entity.getLocation());
                    }
                }
            }
        }

        if (rightClick && c == 1 && skull.getItemMeta().getDisplayName().equals(ChatColor.RED + "Berserkers Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 140, 0));
            player.sendMessage(ChatColor.RED + "Your Berserkers Sword gave you strength!");

        }

        if (rightClick && skull.getItemMeta().getDisplayName().equals(ChatColor.GRAY + "Spider Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            if (player.getHealth() > 3){
                Vector velo = player.getVelocity();
                velo.setX(player.getEyeLocation().toVector().getX()+1);
                velo.setY(1);
                player.setVelocity(velo);
                player.setHealth(player.getHealth() - 3);

            }
        }

        if (rightClick && skull.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "Dreadlord Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            if (player.getHealth() > 0.20) {
                Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(),
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch());
                WitherSkull witherSkull = player.getWorld().spawn(loc, WitherSkull.class);
                witherSkull.setShooter(player);
                player.setHealth(player.getHealth() - 0.20);
            }
        }

        /*
        if (rightClick && skull.getItemMeta().getDisplayName().equals(ChatColor.RED + "Pyromancer's Sword") && skull.containsEnchantment(Enchantment.FIRE_ASPECT)) {
            Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(),
                    player.getLocation().getYaw(),
                    player.getLocation().getPitch());
            Fireball fireball = player.getWorld().spawn(loc, Fireball.class);
            fireball.setShooter(player);
            player.setHealth(player.getHealth() - 0.20);
        }
         */
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (e.getEntity().getType().equals(EntityType.FIREBALL)) {
            e.setCancelled(true);
            for (Entity entity : e.getEntity().getNearbyEntities(20, 20, 20)) {
                if (entity instanceof Player) {
                    Player p = (Player) entity;
                    p.playEffect(e.getLocation(), Effect.EXPLOSION_HUGE, 10);
                    p.playSound(p.getLocation(), Sound.EXPLODE, 10, 10);
                }
            }
            for (Entity en : e.getEntity().getNearbyEntities(3, 3, 3)) {
                if (en instanceof Player) {
                    Player player = (Player) en;
                    player.setHealth(player.getHealth() - 3);
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        ItemStack skull = player.getInventory().getBoots();
        if (skull != null) {
            if ((skull.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Hermes' Boots") && skull.containsEnchantment(Enchantment.PROTECTION_FALL))) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1));
            }
        }
        if (player.getItemInHand() != null){
            if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Pyromancer's Sword") && skull.containsEnchantment(Enchantment.FIRE_ASPECT)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 1));
            }
        }
        if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pigman Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 4000, 4));
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
            e.setCancelled(true);
        }
        if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Shop" + ChatColor.GRAY + " (Right Click)")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPerkArrowShoot(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Random rand = new Random();
            int i = rand.nextInt(20);
            Player player = (Player) e.getEntity();
            if (i == 1 && Config.hasUnlockedPerk(player, PerksType.BLAZING_ARROWS)){
                e.getProjectile().setFireTicks(200);
            }
            if (i == 2 && Config.hasUnlockedPerk(player, PerksType.ARROW_RECOVERY)){
                player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            }
        }
    }

    @EventHandler
    public void explosiveBow(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Explosive Bow")) {
                //headHunt.explodeArrow((Projectile) e.getProjectile());
            }
        }
    }

    @EventHandler
    public void perkDmg(EntityDamageByEntityEvent e){
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player){
            Player damager = (Player) e.getDamager();
            Player victim = (Player) e.getEntity();
            Random rand = new Random();
            int i = rand.nextInt(28);
            int a = rand.nextInt(10);
            if (Config.hasUnlockedPerk(damager, PerksType.FIRE_ASPECT)) {
                if (i == 1) {
                    damager.sendMessage(ChatColor.RED + "Your Fire Aspect perk set " + victim.getName() + " on fire!");
                    victim.setFireTicks(100);
                }
            }

            if (Config.hasUnlockedPerk(damager, PerksType.ARROW_SILVERFISH)){
                if (a == 1){
                    if (damager.getItemInHand().getType() == BOW){
                        victim.getWorld().spawnCreature(victim.getLocation(), CreatureType.SILVERFISH);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        e.setFormat(ChatColor.WHITE + "[" + Config.getStarLevel(e.getPlayer().getName()) + "✰] " + e.getPlayer().getName() + ": " + e.getMessage());
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e){
        Projectile projectile = e.getEntity();
        if (projectile.getShooter() instanceof Player){
            Player player = (Player) projectile.getShooter();
            if (player.getItemInHand().getType() == SNOW_BALL){
                new BukkitRunnable() {
                    @Override
                    public void run () {
                        player.getInventory().setItemInHand(new ItemStack(SNOW_BALL, 64));
                    }
                }.runTaskLater(headHunt, 1);
            }else if (player.getItemInHand().getType() == Material.EGG){
                new BukkitRunnable() {
                    @Override
                    public void run () {
                        player.getInventory().setItemInHand(new ItemStack(EGG, 64));
                    }
                }.runTaskLater(headHunt, 1);
            }
        }
    }
}