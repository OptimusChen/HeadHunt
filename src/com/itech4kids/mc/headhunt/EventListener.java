package com.itech4kids.mc.headhunt;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

import static org.bukkit.Material.*;

public class EventListener implements Listener {
    private HeadHunt headHunt;

    public EventListener(HeadHunt headHunt) {
        HeadHunt.log.info("A event listener registered");
        this.headHunt = headHunt;
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        headHunt.updateScoreBoard();
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent e) {
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
        player.getWorld().dropItem(location, skull);
        /* Drop all the heads owned by the player */
        for (ItemStack i : player.getInventory().getContents()) {
            if ((i != null) && (i.getType() == Material.SKULL_ITEM)) {
                location.setX(location.getX() + 1);
                player.getWorld().dropItem(location, i);
                player.getInventory().remove(i);
            }
        }
        /* Give effects to the killer */
        ActivePlayer activeKiller = headHunt.findPlayer(player.getKiller().getName());

        Player killer = activeKiller.getBukkitPlayer();
        killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 400, 0));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0));
        killer.getInventory().addItem(new ItemStack(Material.GOLD_INGOT, 2));
        player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
        killer.setLevel(killer.getLevel() + 3);
        /* 10% chance to get sharpness enchantment */
        Random rand = new Random();
        ItemStack itemStack = killer.getItemInHand();
        int v = rand.nextInt(8);
        int c = rand.nextInt(10);
        int b = rand.nextInt(3);
        /*int h = rand.nextInt(7);
        int j = rand.nextInt(3);
        int k = rand.nextInt(2);
         */
        if (c == 1) {
            if (itemStack.getType() == Material.DIAMOND_SWORD) {
                if (killer.getItemInHand().containsEnchantment(Enchantment.DAMAGE_ALL)) {
                    int level = killer.getItemInHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                    killer.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, level + 1);
                } else {
                    killer.getItemInHand().addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                }
            }
        }

        if (b == 1) {
            killer.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
            killer.sendMessage(ChatColor.DARK_PURPLE + "You got a ender pearl!");
        }
        if (b == 1) {
            killer.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1));
            killer.sendMessage(ChatColor.GOLD + "You got a golden apple!");

        }

        if (v == 1) {
            if (itemStack.getType() == BOW) {
                int level = killer.getItemInHand().getEnchantmentLevel(Enchantment.ARROW_DAMAGE);
                killer.getItemInHand().addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, level + 1);
            } else {
                killer.getItemInHand().addEnchantment(Enchantment.ARROW_DAMAGE, 1);
            }

      /*      if (h ==1){
                killer.getInventory().addItem(gapple);
            }

            if (j == 1){
                killer.getInventory().addItem(xp);
            }

            if (k ==2){
                killer.getInventory().addItem(gold);
            }


 */
        }


        /* Give coins to the killer */
        activeKiller.addCoins(1);
    }

    @EventHandler
    public void PlayerReSpawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Material arrow = Material.ARROW;
        HeadHunt.log.info("Player " + player.getName() + " is respawned");
        HeadHunt.log.info("GameState " + headHunt.gameState);
        if (headHunt.gameState == GameState.BATTLE) {
            Location location = headHunt.battleArena.getASpawnLocation();
            HeadHunt.log.info("before" + e.getRespawnLocation() + "");
            e.setRespawnLocation(location);
            player.teleport(location);
            HeadHunt.log.info("after" + e.getRespawnLocation() + "Loc " + location);
        } else if (headHunt.gameState == GameState.DEATH_MATCH) {
            Location location = headHunt.deathMatchArena.getASpawnLocation();
            e.setRespawnLocation(location);
            player.setGameMode(GameMode.SPECTATOR);
            // remove this player from active player
            headHunt.removePlayer(player.getName());
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ActivePlayer activePlayer = headHunt.findPlayer(player.getName());
        ItemStack item = e.getCurrentItem();
        Inventory inv = e.getClickedInventory();
        InventoryView view = e.getView();

        if (view.getTitle().equals("HeadHunt Shop") && activePlayer.decCoins(1)) {
            inv.removeItem(item);
            player.getInventory().addItem(item);
        }

        if (view.getTitle().equals("HeadHunt Kits")) {
            if (e.getCurrentItem() != null) {
                e.setCancelled(true);

                switch (e.getCurrentItem().getType()) {
                    case ENDER_PEARL:
                        player.sendMessage(ChatColor.GREEN + "You have selected Enderman Kit!");
                        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Enderman" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet);
                        activePlayer.selectKit(KitsType.ENDERMAN);
                        player.closeInventory();
                        break;
                    case BOW:
                        player.sendMessage(ChatColor.GREEN + "You have selected Archer Kit!");
                        PacketPlayOutChat packet1 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Archer" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet1);
                        activePlayer.selectKit(KitsType.ARCHER);
                        break;
                    case DIAMOND_SWORD:
                        player.sendMessage(ChatColor.GREEN + "You have selected Berserker Kit!");
                        PacketPlayOutChat packet2 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Berserker" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet2);
                        activePlayer.selectKit(KitsType.BERSERKER);
                        break;
                    case IRON_CHESTPLATE:
                        player.sendMessage(ChatColor.GREEN + "You have selected Default Kit!");
                        PacketPlayOutChat packet3 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Default" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet3);
                        activePlayer.selectKit(KitsType.DEFAULT);
                    case GRILLED_PORK:
                        player.sendMessage(ChatColor.GREEN + "You have selected Pigman Kit!");
                        PacketPlayOutChat packet4 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Pigman" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet4);
                        activePlayer.selectKit(KitsType.PIGMAN);
                        break;
                    case SKULL_ITEM:
                        SkullMeta skullMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
                        if (skullMeta.getOwner().equals("Sloth")) {
                            player.sendMessage(ChatColor.GREEN + "You have selected Sloth Kit!");
                            PacketPlayOutChat packet11 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Sloth" + "\"}"), (byte) 2);
                            ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet11);
                            activePlayer.selectKit(KitsType.SLOTH);
                        } else if (skullMeta.getOwner().equals("WitherSkeleton")) {
                            player.sendMessage(ChatColor.GREEN + "You have selected Dreadlord Kit!");
                            PacketPlayOutChat packet16 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Dreadlord" + "\"}"), (byte) 2);
                            ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet16);
                            activePlayer.selectKit(KitsType.DREADLORD);
                        } else {
                            player.sendMessage(ChatColor.GREEN + "You have selected Herobrine Kit!");
                            PacketPlayOutChat packet5 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Herobrine" + "\"}"), (byte) 2);
                            ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet5);
                            activePlayer.selectKit(KitsType.HEROBRINE);
                        }
                        break;

                    case DIAMOND_CHESTPLATE:
                        player.sendMessage(ChatColor.GREEN + "You have selected Tank Kit!");
                        PacketPlayOutChat packet6 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Tank" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet6);
                        activePlayer.selectKit(KitsType.TANK);
                        break;
                    case FLINT_AND_STEEL:
                        player.sendMessage(ChatColor.GREEN + "You have selected Pyromancer Kit!");
                        PacketPlayOutChat packet7 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Pyromancer" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet7);
                        activePlayer.selectKit(KitsType.PYROMANCER);
                        break;
                    case FISHING_ROD:
                        player.sendMessage(ChatColor.GREEN + "You have selected Fisherman Kit!");
                        PacketPlayOutChat packet8 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Fisherman" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet8);
                        activePlayer.selectKit(KitsType.FISHERMAN);
                        break;
                    case DIAMOND_BOOTS:
                        player.sendMessage(ChatColor.GREEN + "You have selected Scout Kit!");
                        PacketPlayOutChat packet9 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Scout" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet9);
                        activePlayer.selectKit(KitsType.SCOUT);
                        break;
                    case EGG:
                        player.sendMessage(ChatColor.GREEN + "You have selected Farmer Kit!");
                        PacketPlayOutChat packet10 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Farmer" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet10);
                        activePlayer.selectKit(KitsType.FARMER);
                        break;
                    case CARROT_STICK:
                        player.sendMessage(ChatColor.GREEN + "You have selected Pig Rider Kit!");
                        PacketPlayOutChat packet12 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Pig Rider" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet12);
                        activePlayer.selectKit(KitsType.PIGRIDER);
                        break;
                    case WEB:
                        player.sendMessage(ChatColor.GREEN + "You have selected Troll Kit!");
                        PacketPlayOutChat packet13 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Troll" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet13);
                        activePlayer.selectKit(KitsType.TROLL);
                        break;
                    case RAW_FISH:
                        player.sendMessage(ChatColor.GREEN + "You have selected One-Pound-Fish Kit!");
                        PacketPlayOutChat packet14 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a One-Pound-Fish" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet14);
                        activePlayer.selectKit(KitsType.ONEPOUNDFISH);
                        break;
                    case ROTTEN_FLESH:
                        player.sendMessage(ChatColor.GREEN + "You have selected Zombie Kit!");
                        PacketPlayOutChat packet15 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Zombie" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet15);
                        activePlayer.selectKit(KitsType.ZOMBIE);
                        break;
                    case TNT:
                        player.sendMessage(ChatColor.GREEN + "You have selected Bomber Kit!");
                        PacketPlayOutChat packet16 = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§eSelected Kit:" + "§a Bomber" + "\"}"), (byte) 2);
                        ((CraftPlayer) e.getWhoClicked()).getHandle().playerConnection.sendPacket(packet16);
                        activePlayer.selectKit(KitsType.BOMBER);
                        break;
                }
            }
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
            final double health = Math.max(Math.ceil(entity.getHealth() - e.getFinalDamage()), 0);
            ItemStack skull = victim.getInventory().getItemInHand();

            //double health = Math.round(victim.getHealth()- e.getFinalDamage());

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
            if (g == 1) {
                if (damager instanceof Player) {
                    ItemStack itemStack1 = ((Player) damager).getItemInHand();
                    if (itemStack1.getType() == Material.AIR) {
                        victim.sendMessage(ChatColor.GREEN + damager.getName() + " stole your item!");
                        damager.sendMessage(ChatColor.GREEN + "You stole " + victim.getName() + "'s item!");
                        victim.getInventory().setItemInHand(null);
                        victim.getWorld().dropItem(victim.getLocation(), skull);

                    }

                }

            }

        }

    }

    @EventHandler void onExplode(EntityDamageByEntityEvent e){
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
        }

    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack skull = player.getInventory().getItemInHand();
        Location location = player.getLocation();

        boolean rightClick = (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR));
        if (skull.getType() == GOLD_SWORD && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pigman Sword")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 3000, 4));
            player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2));

            if (rightClick && skull.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pigman Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
                if (player.getHealth() <= 3) {
                    player.sendMessage(ChatColor.RED + "You don't have enough health");
                } else {
                    List<Entity> near = player.getNearbyEntities(5, 5, 5);
                    player.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
                    player.playEffect(player.getLocation(), Effect.SMOKE, 2);
                    player.playSound(player.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2, 3);
                    player.setHealth(player.getHealth() - 3);
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
        int c = rand.nextInt(30);
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
                player.setHealth(player.getHealth() - 2);
                for (Entity entity : near) {
                    if (entity instanceof Player) {
                        if (((Player) entity).getHealth() <= 2) {
                            ((Player) entity).setHealth(0);
                        }
                        ((Player) entity).setHealth(((Player) entity).getHealth() - 1);
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

        if (rightClick && skull.getItemMeta().getDisplayName().equals(ChatColor.DARK_GRAY + "Dreadlord Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(),
                    player.getLocation().getYaw(),
                    player.getLocation().getPitch());
            WitherSkull witherSkull = player.getWorld().spawn(loc, WitherSkull.class);
            witherSkull.setShooter(player);
            player.setHealth(player.getHealth() - 0.20);
            //player.performCommand("fireball skull");
        }

        if (rightClick && skull.getItemMeta().getDisplayName().equals(ChatColor.RED + "Pyromancer's Sword") && skull.containsEnchantment(Enchantment.FIRE_ASPECT)) {
            Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(2)).toLocation(player.getWorld(),
                    player.getLocation().getYaw(),
                    player.getLocation().getPitch());
            Fireball fireball = player.getWorld().spawn(loc, Fireball.class);
            fireball.setShooter(player);
            player.setHealth(player.getHealth() - 0.20);
            //player.performCommand("fireball skull");
        }

    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        ItemStack skull = player.getInventory().getBoots();
        if ((skull.getItemMeta().getDisplayName().equals(ChatColor.AQUA + "Hermes' Boots") && skull.containsEnchantment(Enchantment.PROTECTION_FALL))) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 1));
        }

        if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Pyromancer's Sword") && skull.containsEnchantment(Enchantment.FIRE_ASPECT)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 1));
        }

        if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Pigman Sword") && skull.containsEnchantment(Enchantment.DAMAGE_ALL)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 1));
        }

    }

    /*@EventHandler
    public void onHit(ProjectileHitEvent e) {
        Player player = (Player) e.getEntity().getShooter();

        if (e.getEntity() instanceof Arrow) {
            if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.RED + "Explosive Bow") && player.getItemInHand().containsEnchantment(Enchantment.ARROW_DAMAGE)) {
                player.playEffect(player.getLocation(), Effect.EXPLOSION_LARGE, 1);
                player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);

            }
        }
    }

     */
}

