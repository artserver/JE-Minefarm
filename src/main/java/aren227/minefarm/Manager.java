package aren227.minefarm;


import aren227.configmanager.ConfigManager;
import aren227.configmanager.ConfigSession;
import aren227.minefarm.generator.MinefarmGenerator;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import aren227.minefarm.util.Sector;
import aren227.minefarm.util.String2Uuid;
import kr.laeng.datastorage.DataStorageAPI;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public void goToCurrentMinefarm(Player player){
        if(!hasMinefarm(player.getUniqueId())){
            Minefarm minefarm = createMinefarm();
            minefarm.addPlayer(player.getUniqueId());
            minefarm.setMain(player.getUniqueId());
            minefarm.addOp(player.getUniqueId());
            minefarm.setName(player.getName() + "의 마인팜");
        }

        player.teleport(getCurrentMinefarm(player.getUniqueId()).getSpawnLocation());
        player.sendTitle(ChatColor.GREEN + getCurrentMinefarm(player.getUniqueId()).getName(), "에 오신 것을 환영합니다!", 10, 40, 10);
    }

    public boolean hasMinefarm(UUID playerUuid){
        if(!DataStorageAPI.getPlayerData(playerUuid).isSet("minefarms")) return false;
        List<String> list = DataStorageAPI.getPlayerData(playerUuid).getStringList("minefarms");
        return list.size() > 0;
    }

    public Minefarm createMinefarm(){
        UUID uuid = MinefarmID.generateUuid();

        Minefarm minefarm = new Minefarm(uuid);
        minefarm.setCreationTime(System.currentTimeMillis());

        Sector sector = getNextSector();
        minefarm.setSector(sector);
        minefarm.setSpawnLocation(sector.getDefaultSpawnLocation());

        minefarms.put(uuid, minefarm);

        plugin.getLogger().info("마인팜 생성 요청 처리됨. UUID = " + uuid.toString());

        return minefarm;
    }

    public Minefarm getCurrentMinefarm(UUID playerUuid){
        if(!hasMinefarm(playerUuid)) return null;

        return getMinefarmByUuid(UUID.fromString(DataStorageAPI.getPlayerData(playerUuid).getString("currentMinefarm")));
    }

    public List<UUID> getMinefarms(UUID playerUuid){
        if(!DataStorageAPI.getPlayerData(playerUuid).isSet("minefarms")) return new ArrayList<>();

        return String2Uuid.toUuid(DataStorageAPI.getPlayerData(playerUuid).getStringList("minefarms"));
    }

    public void setMinefarms(UUID playerUuid, List<UUID> uuids){
        DataStorageAPI.getPlayerData(playerUuid).set("minefarms", String2Uuid.toString(uuids));
    }

    public Minefarm getMinefarmByUuid(UUID minefarmUuid){
        if(minefarms.containsKey(minefarmUuid)) return minefarms.get(minefarmUuid);

        Minefarm minefarm = new Minefarm(minefarmUuid);

        minefarms.put(minefarmUuid, minefarm);

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
