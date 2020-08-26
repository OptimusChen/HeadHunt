package com.itech4kids.mc.headhunt;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class HeadHunt extends JavaPlugin {
    public static Logger log = Bukkit.getLogger();
    private static final int joinTime = 30; // 0.5 minute
    private static final int battleTime = 300; // 10 minutes
    private static final int deathMatchTime = 120; // 2 minutes

    private Map<String, ActivePlayer> players;
    public Arena battleArena;
    public Arena deathMatchArena;

    public CountdownTimer joinTimer;
    public CountdownTimer gameTimer;
    public CountdownTimer deathMatchTimer;

    public GameState gameState;

    private static HeadHunt instance;

    public static HeadHunt getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        players = new HashMap<>();
        battleArena = new Arena(this);
        deathMatchArena = new Arena(this);
        HeadHunt.instance = this;

        log.info("HeadHunt is enabled");
        loadRecipe();

        // register commands
        this.getCommand("yell").setExecutor(new YellCmd(this));
        this.getCommand("join").setExecutor(new JoinCmd(this));
        this.getCommand("setSpawn").setExecutor(new SetSpawnCmd(this));
        this.getCommand("setDMSpawn").setExecutor(new SetDMSpawnCmd(this));
        this.getCommand("shop").setExecutor(new ShopCmd(this));
        this.getCommand("kitsInventory").setExecutor(new KitCmd(this));

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
        // Save a copy of the default config.yml if one is not there
        saveDefaultConfig();

        for (int index = 1; index <= 2; index ++) {
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

        for (int index = 1; index <= 2; index ++) {
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
        for (int index = 1; index <= 2; index ++) {
            Location location = battleArena.getCorner(index);
            if (location != null) {
                getConfig().set("arena.world" + index, location.getWorld().getName());
                getConfig().set("arena.x" + index, location.getX());
                getConfig().set("arena.y" + index, location.getY());
                getConfig().set("arena.z" + index, location.getZ());
            }
        }
        for (int index = 1; index <= 2; index ++) {
            Location location = deathMatchArena.getCorner(index);
            if (location != null) {
                getConfig().set("dmarena.world" + index, location.getWorld().getName());
                getConfig().set("dmarena.x" + index, location.getX());
                getConfig().set("dmarena.y" + index, location.getY());
                getConfig().set("dmarena.z" + index, location.getZ());
            }
        }
        log.info("Save the spawn location to the config.yml");
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
            player.sendTitle(ChatColor.YELLOW + "Join HeadHunt Now!",  "Type /join");
        }
        gameState = GameState.WAIT_JOIN;
    }

    public void telePortAllPlayers(Arena arena, String message) {
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            player.getBukkitPlayer().sendTitle(message, null);
            Location location = arena.getASpawnLocation();
            player.getBukkitPlayer().setMaxHealth(20.0);
            player.getBukkitPlayer().setHealth(20.0);
            log.info("Player " + entry.getKey() + " teleporting to " + location);
            player.TelePortTo(location);
        }
    }

    private void setupAllPlayers() {
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer activePlayer = entry.getValue();
            Player player =  activePlayer.getBukkitPlayer();
            /* clear the player's inventory */
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 600,0));
            player.setGameMode(GameMode.ADVENTURE);
            player.setLevel(0);
            /* give the players start inventory */
            activePlayer.giveStartKits();
            /* set the health of the player to maximum */
            player.setHealth(20);
            player.setSaturation(20);
            /* deop the player */
            activePlayer.isOp = player.isOp();
            player.setOp(false);
        }
    }

    public void startGame() {
        if (players.size() < 2) {
            Bukkit.broadcastMessage("Not enough players joined. The game start is aborted.");
            gameState = GameState.INIT;
            players.clear();
            updateScoreBoard();
            World world = getServer().getWorlds().get(0);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setTime(1000);
            return;
        }

        /* setup all players */
        setupAllPlayers();

        /* teleport all the active players to the arena */
        telePortAllPlayers(battleArena, "Teleporting to the battle field");
        gameState = GameState.BATTLE;
        /* Start a 10 minutes timer to start battle */
        gameTimer = new CountdownTimer(this,
                battleTime,
                () -> Bukkit.broadcastMessage(ChatColor.YELLOW + "HeadHunt Battle starts Now!"),
                () -> startDeathMatch(),
                (t) -> updateScoreBoard()
        );
        gameTimer.scheduleTimer();
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

    public void removePlayer(String playerName) {
        ActivePlayer player = players.get(playerName);
        if (player != null) {
            players.remove(playerName);
            player.restoreItems();
            player.getBukkitPlayer().setOp(player.isOp);
        }

        if ( (players.size() == 1) && (gameState == GameState.DEATH_MATCH) ) {
            declareWinner();
        }
    }

    private void startDeathMatch() {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Battle Timer is up, now go to death match!");

        // get the max head count from all the players
        int maxHeadCount = getMostHeadCount();

        // remove the players without max head count from the active players map
        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            if (player.getHeadCount() < maxHeadCount) {
                removePlayer(entry.getKey());
            }
        }

        /* teleport all the active players to the death match arena */
        telePortAllPlayers(deathMatchArena, "Teleporting to the death match");

        gameState = GameState.DEATH_MATCH;

        // Check whether there is only one player left
        if (players.size() == 1) {
            declareWinner();
        } else {
            /* Start a 2 minutes timer to start battle */
            deathMatchTimer = new CountdownTimer(this,
                    deathMatchTime,
                    () -> Bukkit.broadcastMessage(ChatColor.YELLOW + "HeadHunt death match starts Now!"),
                    () -> declareWinner(),
                    (t) -> updateScoreBoard()
            );

            deathMatchTimer.scheduleTimer();
        }
    }

    private void declareWinner() {

        for (Map.Entry<String, ActivePlayer> entry : players.entrySet()) {
            ActivePlayer player = entry.getValue();
            Bukkit.broadcastMessage(ChatColor.GOLD + "The winner is " + player.getBukkitPlayer().getName());
            // remove the player
            removePlayer(entry.getKey());
        }
        gameState = GameState.INIT;
        updateScoreBoard();
    }

    private String getTimeString(int time) {
        int hour = time/3600;
        int min = (time%3600)/60;
        int second = time%60;

        String s;
        if (hour > 0) {
            s = String.format("%02d:%02d:%02d", hour, min, second);
        } else {
            s = String.format("%02d:%02d", min, second);
        }
        return s;
    }

    private int displayInitScoreBoard(int scoreNum, Objective objective) {
        Score score = objective.getScore(ChatColor.YELLOW + "Use /yell to");
        score.setScore(scoreNum--);
        score = objective.getScore(ChatColor.YELLOW + "call players to join");
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

    public void updateScoreBoard() {
        for (Player p : Bukkit.getOnlinePlayers()) {        // setup scoreboard
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();
            Objective objective = board.registerNewObjective("Head Hunt", "Display ");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.WHITE + "-------Head Hunt-------");
            Score score;
            int scoreNum = 10;

            score = objective.getScore(ChatColor.YELLOW + " ");
            score.setScore(scoreNum--);

            switch (gameState) {
                case INIT:
                    scoreNum =  displayInitScoreBoard(scoreNum, objective);
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


        /*
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkzN2UxYzQ1YmI4ZGEyOWIyYzU2NGR=="));
        Field field;
        try {
            field = itemMeta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(itemMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException x){
            x.printStackTrace();
        }

         */

        ShapedRecipe recipe = new ShapedRecipe(skull);
        recipe.shape("^^^","^&^","^^^");
        recipe.setIngredient('^', Material.GOLD_INGOT);
        recipe.setIngredient('&', Material.SKULL_ITEM, (byte) SkullType.PLAYER.ordinal());

        this.getServer().addRecipe(recipe);
    }

    public void calculateDamageApplied(double damage, double points, double toughness) {
        double withArmorReduction = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);

    }
   /* public void damagePlayer(Player p, double damage) {
        double points = p.getAttribute(Attribute.GENERIC_ARMOR).getValue();
        double toughness = p.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS).getValue();
        PotionEffect effect = p.getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
        int resistance = effect == null ? 0 : effect.getAmplifier();
        int epf = Util.getEPF(p.getInventory());

        p.damage(calculateDamageApplied(damage, points, toughness, resistance, epf));
    }

    public double calculateDamageApplied(double damage, double points, double toughness, int resistance, int epf) {
        double withArmorAndToughness = damage * (1 - Math.min(20, Math.max(points / 5, points - damage / (2 + toughness / 4))) / 25);
        double withResistance = withArmorAndToughness * (1 - (resistance * 0.2));
        double withEnchants = withResistance * (1 - (Math.min(20.0, epf) / 25));
        return withEnchants;
    }

    public static int getEPF(PlayerInventory inv) {
        ItemStack helm = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack legs = inv.getLeggings();
        ItemStack boot = inv.getBoots();

        return (helm != null ? helm.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (chest != null ? chest.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (legs != null ? legs.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0) +
                (boot != null ? boot.getEnchantmentLevel(Enchantment.DAMAGE_ALL) : 0);
    }

    */

}
