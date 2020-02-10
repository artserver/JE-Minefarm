package aren227.minefarm;


import aren227.configmanager.ConfigManager;
import aren227.configmanager.ConfigSession;
import aren227.minefarm.generator.MinefarmGenerator;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.Sector;
import kr.laeng.datastorage.DataStorageAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Manager {

    public static final String WORLD_NAME = "minefarm";
    public static final int MINEFARM_DIST = 256;
    public static final int MINEFARM_CHUNK_DIST = MINEFARM_DIST / 16;
    public static final int MINEFARM_Y = 50;

    public static Manager instance;

    private Plugin plugin;

    private HashMap<UUID, Minefarm> minefarms = new HashMap<>();

    public Manager(Plugin plugin){
        instance = this;

        this.plugin = plugin;
    }

    public static Manager getInstance(){
        return instance;
    }

    public Plugin getPlugin(){
        return plugin;
    }

    public World getMinefarmWorld(){
        World skyblockWorld = plugin.getServer().getWorld(WORLD_NAME);

        if(skyblockWorld == null){ //월드가 존재하지 않습니다
            skyblockWorld = WorldCreator
                    .name(WORLD_NAME)
                    .type(WorldType.FLAT)
                    .generateStructures(false)
                    .environment(World.Environment.NORMAL)
                    .generator(new MinefarmGenerator())
                    .createWorld();
            skyblockWorld.save();
        }

        return skyblockWorld;
    }

    public void goToMinefarm(Player player){
        if(!hasMinefarm(player)) createMinefarm(player);

        player.teleport(getMinefarm(player).getSpawnLocation());
        player.sendTitle(ChatColor.GREEN + getMinefarm(player).getName(), "에 오신 것을 환영합니다!", 10, 40, 10);
    }

    public boolean hasMinefarm(Player player){
        return DataStorageAPI.getPlayerData(player).isSet("minefarm");
    }

    public void createMinefarm(Player player){
        if(hasMinefarm(player)) return;

        UUID uuid = UUID.randomUUID();
        while(DataStorageAPI.exists("minefarm", uuid)) uuid = UUID.randomUUID(); //개쫄려ㅋㅋㅋㅋㅋㅋㅋ

        Minefarm minefarm = new Minefarm(uuid);
        minefarm.setCreationTime(System.currentTimeMillis());

        Sector sector = getNextSector();
        minefarm.setSector(sector);
        minefarm.setSpawnLocation(sector.getDefaultSpawnLocation());
        minefarm.setOwner(player.getUniqueId());
        minefarm.setName(player.getName() + "의 마인팜");

        DataStorageAPI.getPlayerData(player).set("minefarm", uuid.toString());

        minefarms.put(uuid, minefarm);

        plugin.getLogger().info("마인팜 생성 요청 처리됨. UUID = " + uuid.toString());
        plugin.getLogger().info("소유자 = " + player.getName());
    }

    public Minefarm getMinefarm(Player player){
        if(!hasMinefarm(player)) return null;

        UUID uuid = UUID.fromString(DataStorageAPI.getPlayerData(player).getString("minefarm"));

        if(minefarms.containsKey(uuid)) return minefarms.get(uuid);

        Minefarm minefarm = new Minefarm(uuid);

        minefarms.put(uuid, minefarm);

        return minefarm;
    }

    private Sector getNextSector(){
        ConfigSession cs = ConfigManager.getConfigSession(plugin);
        if(!cs.isSet("sx") || !cs.isSet("sz")){
            cs.set("sx", 0);
            cs.set("sz", 0);
            cs.save();
            return new Sector(0, 0);
        }

        int sx = cs.getInt("sx");
        int sz = cs.getInt("sz");

        //생성 방법 : (0, 0), (1, 0), (0, 1), (2, 0), (1, 1), (0, 2), (3, 0), ...
        if(sx == 0){
            sx = sz + 1;
            sz = 0;
        }

        cs.set("sx", sx);
        cs.set("sz", sz);
        cs.save();
        return new Sector(sx, sz);
    }

}
