package com.itech4kids.mc.headhunt;

import com.itech4kids.mc.headhunt.Commands.*;
import com.itech4kids.mc.headhunt.Objects.*;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.Item;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Scoreboard;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static org.bukkit.Material.EMERALD;

public class HeadHunt extends JavaPlugin {
    public static Logger log = Bukkit.getLogger();
    private static final int joinTime = 30; // 0.5 minute
    private static final int battleTime = 300; // 10 minutes
    private static final int deathMatchTime = 120; // 2 minutes

    public Map<String, ActivePlayer> players;
    public Arena battleArena;
    public Arena deathMatchArena;


    public CountdownTimer joinTimer;
    public CountdownTimer gameTimer;
    public CountdownTimer deathMatchTimer;

    public GameState gameState;
    public int kits;
    public int perks;
    public int abilities;


    @Override
    public void onEnable() {
        players = new HashMap<>();
        battleArena = new Arena(this);
        deathMatchArena = new Arena(this);
        new Config(this);
        log.info("HeadHunt is enabled");
        loadRecipe();
        loadDiamondRecipe();
        this.kits = KitsType.values().length - 1;
        this.perks = PerksType.values().length;
        this.abilities = PanelType.values().length;
        onJoin();

        // register commands
        this.getCommand("yell").setExecutor(new YellCmd(this));
        this.getCommand("join").setExecutor(new JoinCmd(this));
        this.getCommand("setSpawn").setExecutor(new SetSpawnCmd(this));
        this.getCommand("setDMSpawn").setExecutor(new SetDMSpawnCmd(this));
        this.getCommand("shop").setExecutor(new ShopCmd(this));
        this.getCommand("kitsInventory").setExecutor(new KitCmd(this));
        this.getCommand("unlockall").setExecutor(new UnlockCmd(this));
        this.getCommand("setLobbySpawn").setExecutor(new SpawnCmd(this));
        this.getCommand("addPlayerStat").setExecutor(new StatCmd(this));

        // register event Listener
        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        gameState = GameState.INIT;

        // Load the spawn locations
        loadSpawnLocations();
    }


    @Override
    public void onDisable() {
        saveSpawnLocations();
    }

    private void loadSpawnLocations() {
        saveDefaultConfig();

        for (int index = 1; index <= 2; index++) {
            String worldName = getConfig().getString("arena.world" + index);
            if (worldName != null) {
                World w = getServer().getWorld(worldName);
                double x = getConfig().getDouble("arena.x" + index);
                double y = getConfig().getDouble("arena.y" + index);
                double z = getConfig().getDouble("arena.z" + index);
                Location location = new Location(w, x, y, z);
                setArenaSpawn(index, location);
            }
        }

        for (int index = 1; index <= 2; index++) {
            String worldName = getConfig().getString("dmarena.world" + index);
            if (worldName != null) {
                World w = getServer().getWorld(worldName);
                double x = getConfig().getDouble("dmarena.x" + index);
                double y = getConfig().getDouble("dmarena.y" + index);
                double z = getConfig().getDouble("dmarena.z" + index);
                Location location = new Location(w, x, y, z);
                setDMArenaSpawn(index, location);
            }
        }
    }

    private void saveSpawnLocations() {
        // save the location to the file
        for (int index = 1; index <= 2; index++) {
            Location location = battleArena.getCorner(index);
            if (location != null) {
                getConfig().set("arena.world" + index, location.getWorld().getName());
                getConfig().set("arena.x" + index, location.getX());
                getConfig().set("arena.y" + index, location.getY());
                getConfig().set("arena.z" + index, location.getZ());
            }
        }
        for (int index = 1; index <= 2; index++) {
            Location location = deathMatchArena.getCorner(index);
            if (location != null) {
                getConfig().set("dmarena.world" + index, location.getWorld().getName());
                getConfig().set("dmarena.x" + index, location.getX());
                getConfig().set("dmarena.y" + index, location.getY());
                getConfig().set("dmarena.z" + index, location.getZ());
            }
        }
        log.info("Saved the spawn location to the config.yml");
        saveConfig();
        reloadConfig();
        saveDefaultConfig();
    }

    public void addPlayer(ActivePlayer player) {
        if (!players.containsKey(player.getBukkitPlayer().getName())) {
            players.put(player.getBukkitPlayer().getName(), player);
        }
    }

    public ActivePlayer getPlayer(String name) {
        return players.get(name);
    }

    public int getJoinTime() {
        return joinTime;
    }

    public Map getPlayers() {
        return players;
    }

    public void setArenaSpawn(int index, Location location) {
        try {
            battleArena.setCorner(index, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDMArenaSpawn(int index, Location location) {
        try {
            deathMatchArena.setCorner(index, location);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayJoinMessage() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(ChatColor.YELLOW + "Join HeadHunt Now!", "Type /join");
        }
        gameState = GameState.WAIT_JOIN;
    }

    public void apocalypse(Arena arena, String s1, String s2) {
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            player.getBukkitPlayer().sendTitle(s1, s2);
            Location location = arena.getASpawnLocation();
            player.getBukkitPlayer().setMaxHealth(20.0);
            player.getBukkitPlayer().setHealth(20.0);
            player.TelePortTo(location);
            spawnMobs(arena);
            if (Config.hasUnlockedPerk(player.getBukkitPlayer(), PerksType.RESISTANCE_BOOST)) {
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
            }
        }
    }

    public void special(Arena arena, String s1, String s2) throws IOException {
        giveControlPanel();
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            ItemStack itemStack = new ItemStack(Material.CHEST);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Control Panel");
            itemStack.setItemMeta(itemMeta);
            net.minecraft.server.v1_8_R3.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
            setMaxStackSize(item.getItem(), 1);
            player.getBukkitPlayer().getInventory().addItem(itemStack);
            player.getBukkitPlayer().sendTitle(s1, s2);
            Location location = arena.getASpawnLocation();
            player.getBukkitPlayer().setMaxHealth(20.0);
            player.getBukkitPlayer().setHealth(20.0);
            player.TelePortTo(location);
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§6+100 Coins! (Participation)" + "\"}"), (byte) 2);
            ((CraftPlayer) player.getBukkitPlayer()).getHandle().playerConnection.sendPacket(packet);
            Config.addCoins(player.getBukkitPlayer().getName(), 75);
            Config.addXp(player.getBukkitPlayer().getName(), 10, "Participation");
            if (Config.hasUnlockedPerk(player.getBukkitPlayer(), PerksType.RESISTANCE_BOOST)) {
                player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
            }
        }
    }


    public void setMaxStackSize(Item item, int i){
        try {

            Field field = Item.class.getDeclaredField("maxStackSize");
            field.setAccessible(true);
            field.setInt(item, i);

        } catch (Exception e) {
        }
    }

    public void telePortAllPlayers(Arena arena, String message, String message2) throws IOException {
        Random rand = new Random();
        int r = rand.nextInt(5);
        int i = rand.nextInt(5);
        if (i == 1) {
            special(arena, ChatColor.RED + "" + ChatColor.BOLD + "Special Event!", null);
        } else if (i == 0) {
            for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
                ActivePlayer player = entry.getValue();
                player.getBukkitPlayer().sendTitle(message, null);
                Location location = arena.getASpawnLocation();
                player.getBukkitPlayer().setMaxHealth(20.0);
                player.getBukkitPlayer().setHealth(20.0);
                player.TelePortTo(location);
                PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§6+100 Coins! (Participation)" + "\"}"), (byte) 2);
                ((CraftPlayer) player.getBukkitPlayer()).getHandle().playerConnection.sendPacket(packet);
                Config.addCoins(player.getBukkitPlayer().getName(), 75);
                Config.addXp(player.getBukkitPlayer().getName(), 10, "Participation");
                if (Config.hasUnlockedPerk(player.getBukkitPlayer(), PerksType.RESISTANCE_BOOST)) {
                    player.getBukkitPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
                }
            }
        }
    }

    private void setupAllPlayers() {
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer activePlayer = entry.getValue();
            Player player = activePlayer.getBukkitPlayer();
            /* clear the player's inventory */
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.ADVENTURE);
            player.setLevel(0);
            /* give the players start inventory */
            activePlayer.giveStartKits();
            /* set the health of the player to maximum */
            player.setHealth(20);
            player.setSaturation(20);
            /* deop the player */
        }
    }

    public void startGame() throws IOException {
        if (players.size() < 2) {
            for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
                entry.getValue().getBukkitPlayer().sendMessage("Not enough players joined, start has been aborted.");
                ItemStack shop = new ItemStack(EMERALD);
                ItemMeta shopMeta = shop.getItemMeta();
                shopMeta.setDisplayName(ChatColor.GREEN + "Shop" + ChatColor.GRAY + " (Right Click)");
                shop.setItemMeta(shopMeta);
                entry.getValue().getBukkitPlayer().getInventory().clear();
                entry.getValue().getBukkitPlayer().getInventory().setArmorContents(null);
                entry.getValue().getBukkitPlayer().getInventory().setItem(4, shop);
                gameState = GameState.INIT;
                World world = entry.getValue().getBukkitPlayer().getWorld();
                updateScoreBoard();
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setTime(1000);
                players.clear();
                return;
            }
        }

        /* setup all players */
        setupAllPlayers();

        /* teleport all the active players to the arena */
        telePortAllPlayers(battleArena, ChatColor.GREEN + "" + ChatColor.YELLOW  + "Prepare to Fight!", null);
        gameState = GameState.BATTLE;
        /* Start a 10 minutes timer to start battle */
        gameTimer = new CountdownTimer(this,
                battleTime,
                () -> broadcastMessage(ChatColor.YELLOW + "HeadHunt Battle starts Now!"),
                () -> {
                    try {
                        startDeathMatch();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                },
                (t) -> {
                    try {
                        updateScoreBoard();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        gameTimer.scheduleTimer();
    }

    public void broadcastMessage(String message) {
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            entry.getValue().getBukkitPlayer().sendMessage(message);
        }
    }

    private int getMostHeadCount() {
        int maxHeadCount = 0;
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            if (player.getHeadCount() > maxHeadCount) {
                maxHeadCount = player.getHeadCount();
            }
        }
        return maxHeadCount;
    }

    public void removePlayer(String playerName) throws IOException {
        ActivePlayer player = players.get(playerName);
        if (player != null) {
            players.remove(playerName);
        }

        if ((players.size() == 1) && (gameState == GameState.DEATH_MATCH)) {
            declareWinner();
        }
    }

    private void startDeathMatch() throws IOException {
        // get the max head count from all the players
        int maxHeadCount = getMostHeadCount();
        updateScoreBoard();

        // remove the players without max head count from the active players map
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            if (player.getHeadCount() < maxHeadCount) {
                removePlayer(entry.getKey());
            }
        }

        /* teleport all the active players to the death match arena */
        telePortAllPlayers(deathMatchArena, "Deathmatch has begun!", null);

        gameState = GameState.DEATH_MATCH;

        // Check whether there is only one player left
        if (players.size() == 1) {
            declareWinner();
        } else {
            /* Start a 2 minutes timer to start battle */
            deathMatchTimer = new CountdownTimer(this,
                    deathMatchTime,
                    () -> deathMatchStart(),
                    () -> {
                        try {
                            declareWinner();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    (t) -> {
                        try {
                            updateScoreBoard();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );

            deathMatchTimer.scheduleTimer();
        }
    }

    private void deathMatchStart() {
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            broadcastMessage(ChatColor.YELLOW + "HeadHunt death match starts Now!");
        }
    }

    private void endFight() throws IOException {
        gameState = GameState.INIT;
        updateScoreBoard();
        for (Player p : Bukkit.getWorld("world").getPlayers()) {
            p.teleport((Location) getConfig().get("lobby-spawn"));
            ItemStack shop = new ItemStack(EMERALD);
            ItemMeta shopMeta = shop.getItemMeta();
            shopMeta.setDisplayName(ChatColor.GREEN + "Shop" + ChatColor.GRAY + " (Right Click)");
            shop.setItemMeta(shopMeta);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.getInventory().setItem(4, shop);
            p.setGameMode(GameMode.ADVENTURE);
        }
        updateScoreBoard();
    }

    private void declareWinner() throws IOException {
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            broadcastMessage(ChatColor.GOLD + "The winner is " + player.getBukkitPlayer().getName());
            player.getBukkitPlayer().sendTitle(ChatColor.GREEN + "Victory!", null);
            player.addCoins(200);
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + "§6+100 Coins! (Win)" + "\"}"), (byte) 2);
            ((CraftPlayer) player.getBukkitPlayer()).getHandle().playerConnection.sendPacket(packet);
            Config.addWins(player.getBukkitPlayer().getName(), 1);
            Config.addCoins(player.getBukkitPlayer().getName(), 100);
            Config.addXp(player.getBukkitPlayer().getName(), 25, "Win");
            removePlayer(entry.getKey());
        }
        endFight();
        updateScoreBoard();
    }

    private String getTimeString(int time) {
        int hour = time / 3600;
        int min = (time % 3600) / 60;
        int second = time % 60;

        String s;
        if (hour > 0) {
            s = String.format("%02d:%02d:%02d", hour, min, second);
        } else {
            s = String.format("%02d:%02d", min, second);
        }
        return s;
    }

    private int displayInitScoreBoard(int scoreNum, Objective objective, Player player) throws IOException {
        Score score = objective.getScore(ChatColor.YELLOW + " ");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Star-Level: " + ChatColor.WHITE + Config.getStarLevel(player.getName()) + "✰");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Total-Xp: " + ChatColor.AQUA + Config.getXp(player.getName()));
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "     ");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Kills: " + ChatColor.GREEN + Config.getKills(player.getName()));
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Wins: " + ChatColor.GREEN + Config.getWins(player.getName()));
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Deaths: " + ChatColor.RED + Config.getDeaths(player.getName()));
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "  ");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Coins: " + ChatColor.GOLD + Config.getCoins(player.getName()));
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Kits: " + ChatColor.YELLOW + Config.getUnlockedKitsInt(player.getName()) + "/" + kits);
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Perks: " + ChatColor.YELLOW + Config.getUnlockedPerksInt(player.getName()) + "/" + perks);
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.WHITE + "Panel Items: " + ChatColor.YELLOW + Config.getUnlockedPanelsInt(player.getName()) + "/" + abilities);
        score.setScore(scoreNum--);

        return scoreNum;
    }

    private int displayWaitJoinScoreBoard(int scoreNum, Objective objective) {
        Score score = objective.getScore(ChatColor.YELLOW + "Waiting for players to join");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.YELLOW + "Use /join to join");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.YELLOW + "    ");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.YELLOW + "Number of Players: " + ChatColor.GOLD + players.size());
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.RED + "Join Time: " + ChatColor.GOLD + getTimeString(joinTimer.getSecondsLeft()));
        score.setScore(scoreNum--);

        return scoreNum;
    }

    private int displayBattleScoreBoard(int scoreNum, Objective objective) {
        Score score = objective.getScore(ChatColor.RED + "Battle in progress");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.YELLOW + "    ");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.RED + "Battle Time: " + ChatColor.GOLD + getTimeString(gameTimer.getSecondsLeft()));
        score.setScore(scoreNum--);

        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            String playerName = String.format("%12s", entry.getKey());
            score = objective.getScore(ChatColor.RED + playerName + ": " + ChatColor.GOLD + player.getHeadCount());
            score.setScore(scoreNum--);
        }
        return scoreNum;
    }

    private int displayDeathMatchScoreBoard(int scoreNum, Objective objective) {
        Score score = objective.getScore(ChatColor.RED + "Death Match in progress");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.YELLOW + "    ");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.RED + "Death Match Time: " + ChatColor.GOLD + getTimeString(deathMatchTimer.getSecondsLeft()));
        score.setScore(scoreNum--);

        return scoreNum;
    }

    public void updateScoreBoard() throws IOException {
        for (Player p : Bukkit.getOnlinePlayers()) {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("Head Hunt", "Display ");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "    Head Hunt");
            Score score;
            int scoreNum = 10;

            score = objective.getScore(ChatColor.YELLOW + " ");
            score.setScore(scoreNum--);

            switch (gameState) {
                case INIT:
                    scoreNum = displayInitScoreBoard(scoreNum, objective, p);
                    break;
                case WAIT_JOIN:
                    scoreNum = displayWaitJoinScoreBoard(scoreNum, objective);
                    break;
                case BATTLE:
                    scoreNum = displayBattleScoreBoard(scoreNum, objective);
                    break;
                case DEATH_MATCH:
                    scoreNum = displayDeathMatchScoreBoard(scoreNum, objective);
                    break;
                default:
                    break;
            }

            score = objective.getScore(ChatColor.YELLOW + "  ");
            score.setScore(scoreNum--);

            p.setScoreboard(board);
        }
    }

    public ActivePlayer findPlayer(String name) {
        return players.get(name);

    }

    public void loadRecipe() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Golden Head");
        itemMeta.setOwner("GeeHead");
        skull.setItemMeta(itemMeta);

        ShapedRecipe recipe = new ShapedRecipe(skull);
        recipe.shape("^^^", "^&^", "^^^");
        recipe.setIngredient('^', Material.GOLD_INGOT);
        recipe.setIngredient('&', Material.SKULL_ITEM, (byte) SkullType.PLAYER.ordinal());

        this.getServer().addRecipe(recipe);
    }

    public void loadDiamondRecipe() {
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal());
        SkullMeta itemMeta = (SkullMeta) skull.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_AQUA + "Diamond Head");
        itemMeta.setOwner("AncientDiamond");
        skull.setItemMeta(itemMeta);

        ShapedRecipe recipe2 = new ShapedRecipe(skull);
        recipe2.shape("^^^", "^&^", "^^^");
        recipe2.setIngredient('^', Material.DIAMOND);
        recipe2.setIngredient('&', Material.SKULL_ITEM, (byte) SkullType.PLAYER.ordinal());

        this.getServer().addRecipe(recipe2);
    }

    public void onJoin() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    updateScoreBoard();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(this, 20L, 20L);
    }

    public void explodeArrow(Projectile e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (e.isDead()) {
                    cancel();
                } else if (e.isOnGround()) {
                    for (Entity entity : e.getNearbyEntities(20, 20, 20)) {
                        if (entity instanceof Player) {
                            cancel();
                            Player p = (Player) e;
                            p.playEffect(e.getLocation(), Effect.EXPLOSION_HUGE, 10);
                            p.playSound(e.getLocation(), Sound.EXPLODE, 10, 10);
                            for (Entity entity1 : e.getNearbyEntities(3, 3, 3)) {
                                if (entity1 instanceof Player) {
                                    Player player1 = (Player) entity1;
                                    player1.setHealth(player1.getHealth() - 3);
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 5L, 5L);
    }

    public void updateStar(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {

                } else {
                    cancel();
                }
            }
        }.runTaskTimer(this, 5L, 5L);
    }

    public void spawnMobs(Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (gameState == GameState.INIT){
                    cancel();
                }else{
                    Random rand = new Random();
                    int i = rand.nextInt(40);
                    int a = rand.nextInt(4);
                    net.minecraft.server.v1_8_R3.World world = ((CraftWorld) arena.getASpawnLocation().getWorld()).getHandle();
                    Location l = arena.getASpawnLocation();
                    ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                    helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                    leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                    boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

                    if (i == 1){
                        if (a == 1){
                            EntityZombie zombie = new EntityZombie(world);
                            zombie.setBaby(false);
                            zombie.setEquipment(0, CraftItemStack.asNMSCopy(sword));
                            zombie.setEquipment(1, CraftItemStack.asNMSCopy(helmet));
                            zombie.setEquipment(2, CraftItemStack.asNMSCopy(chestplate));
                            zombie.setEquipment(3, CraftItemStack.asNMSCopy(leggings));
                            zombie.setEquipment(4, CraftItemStack.asNMSCopy(boots));
                            zombie.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6);
                            world.addEntity(zombie);
                            zombie.enderTeleportTo(l.getX(), l.getY(), l.getZ());
                        }else if (a == 2){
                            EntitySkeleton zombie = new EntitySkeleton(world);
                            zombie.setEquipment(1, CraftItemStack.asNMSCopy(helmet));
                            zombie.setEquipment(2, CraftItemStack.asNMSCopy(chestplate));
                            zombie.setEquipment(3, CraftItemStack.asNMSCopy(leggings));
                            zombie.setEquipment(4, CraftItemStack.asNMSCopy(boots));
                            zombie.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6);
                            world.addEntity(zombie);
                            zombie.enderTeleportTo(l.getX(), l.getY(), l.getZ());                        }else if (a == 3){
                        }else if (a == 0){
                            EntityBlaze zombie = new EntityBlaze(world);
                            zombie.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6);
                            world.addEntity(zombie);
                            zombie.enderTeleportTo(l.getX(), l.getY(), l.getZ());                        }
                        }else if (a == 3){
                            EntitySlime zombie = new EntitySlime(world);
                            zombie.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(6);
                            world.addEntity(zombie);
                            zombie.enderTeleportTo(l.getX(), l.getY(), l.getZ());                        }
                        }
                    }
                }.runTaskTimer(this, 5L, 5L);
            }

        public void giveControlPanel(){
            new BukkitRunnable() {
                @Override
                public void run () {
                    for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
                        ItemStack itemStack = new ItemStack(Material.CHEST);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.setDisplayName(ChatColor.RED + "Control Panel");
                        itemStack.setItemMeta(itemMeta);
                        net.minecraft.server.v1_8_R3.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
                        setMaxStackSize(item.getItem(), 1);
                        entry.getValue().getBukkitPlayer().getInventory().addItem(itemStack);
                        entry.getValue().getBukkitPlayer().playSound(entry.getValue().getBukkitPlayer().getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
                    }
                    broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "THe second Control Panel has been give out!");
                }
            }.runTaskLater(this, 3000);
        }
    }


