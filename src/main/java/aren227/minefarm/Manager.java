package aren227.minefarm;


import aren227.configmanager.ConfigManager;
import aren227.configmanager.ConfigSession;
import aren227.minefarm.generator.MinefarmGenerator;
import aren227.minefarm.minefarm.Minefarm;
import aren227.minefarm.util.MinefarmID;
import aren227.minefarm.util.MinefarmInvitation;
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

//Player에 관한 함수 파라미터가 uuid인 경우, 오프라인 플레이어인 경우에도 사용 가능한 함수이고,
//Player 클래스를 직접 받는다면 온라인 플레이어인 경우에만 사용 가능하다는 의미이다.
public class Manager {

    public static final String WORLD_NAME = "minefarm";
    public static final int MINEFARM_DIST = 256;
    public static final int MINEFARM_CHUNK_DIST = MINEFARM_DIST / 16;
    public static final int MINEFARM_Y = 50;
    public static final int LEAP_COOL = 1000 * 15;

    public static final int MINEFARM_SIZE_TIER_3 = 64;
    public static final int MINEFARM_SIZE_TIER_2 = 128;
    public static final int MINEFARM_SIZE_TIER_1 = 256;

    public static final int MINEFARM_REP_TIER3 = 0;
    public static final int MINEFARM_REP_TIER2 = 100000;
    public static final int MINEFARM_REP_TIER1 = 5000000;

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

    public World getDefaultWorld(){
        return plugin.getServer().getWorld("world");
    }

    public void goToCurrentMinefarm(Player player) throws RuntimeException {
        if(!hasMinefarm(player.getUniqueId())){
            throw new RuntimeException("아직 소속된 마인팜이 없습니다.");
        }

        Minefarm minefarm = getCurrentMinefarm(player.getUniqueId());
        if(minefarm == null){
            throw new RuntimeException("소속된 마인팜이 존재하지만 거주 마인팜이 설정되지 않았습니다. 마인팜 리프를 통해 거주 마인팜을 선택해주세요.");
        }

        DataStorageAPI.getPlayerData(player).set("lastVisitMinefarm", minefarm.getUniqueId().toString());

        player.teleport(minefarm.getSpawnLocation());
        player.sendTitle(ChatColor.GREEN + minefarm.getName(), "에 오신 것을 환영합니다!", 10, 40, 10);
    }

    public void visitMinefarm(Player player, String code) throws RuntimeException {
        MinefarmID.validate(code);

        Minefarm minefarm = getMinefarmByUuid(MinefarmID.stringToUuid(code));
        if(minefarm == null) throw new RuntimeException("해당 ID(" + code + ")의 마인팜이 존재하지 않습니다.");

        if(!minefarm.getCanVisit()) throw new RuntimeException("해당 마인팜의 방문이 금지되어 있습니다.");

        player.sendMessage(minefarm.getName() + "으로 이동합니다!");

        player.teleport(minefarm.getSpawnLocation());
        player.sendTitle(ChatColor.GREEN + minefarm.getName(), "에 오신 것을 환영합니다!", 10, 40, 10);
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
        if(!DataStorageAPI.getPlayerData(playerUuid).isSet("currentMinefarm")) return null;
        if(!getMinefarms(playerUuid).contains(UUID.fromString(DataStorageAPI.getPlayerData(playerUuid).getString("currentMinefarm")))) return null;

        return getMinefarmByUuid(UUID.fromString(DataStorageAPI.getPlayerData(playerUuid).getString("currentMinefarm")));
    }

    public void leapMinefarm(UUID playerUuid, UUID minefarmUuid) throws RuntimeException{
        List<UUID> minefarms = getMinefarms(playerUuid);

        boolean pass = false;
        for(UUID uuid : minefarms){
            if(uuid.equals(minefarmUuid)){
                pass = true;
                break;
            }
        }

        if(!pass) throw new RuntimeException("현재 리프하려는 마인팜에 가입된 상태가 아닙니다.");

        if(getCurrentMinefarm(playerUuid) != null && getCurrentMinefarm(playerUuid).getUniqueId().equals(minefarmUuid)) throw new RuntimeException("지금 거주하는 마인팜으론 리프할 수 없습니다.");

        if(getLeapCooldown(playerUuid) > 0) throw new RuntimeException("아직 마인팜 리프를 사용할 수 없습니다.");

        Minefarm minefarm = getMinefarmByUuid(minefarmUuid);
        minefarm.setMain(playerUuid);

        setLeapCooldown(playerUuid, LEAP_COOL);
    }

    public long getLeapCooldown(UUID playerUuid){
        if(!DataStorageAPI.getPlayerData(playerUuid).isSet("banLeapUntil")) return 0;
        return Math.max(DataStorageAPI.getPlayerData(playerUuid).getLong("banLeapUntil") - System.currentTimeMillis(), 0);
    }

    public void setLeapCooldown(UUID playerUuid, long delayMs){
        DataStorageAPI.getPlayerData(playerUuid).set("banLeapUntil", System.currentTimeMillis() + delayMs);
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

        if(!DataStorageAPI.exists("minefarm", minefarmUuid)) return null;

        Minefarm minefarm = new Minefarm(minefarmUuid);
        minefarms.put(minefarmUuid, minefarm);

        return minefarm;
    }

    public Minefarm getInMinefarm(Player player){
        if(!player.getWorld().getName().equals(WORLD_NAME)) return null;
        if(!DataStorageAPI.getPlayerData(player).isSet("lastVisitMinefarm")) return null;

        return getMinefarmByUuid(UUID.fromString(DataStorageAPI.getPlayerData(player).getString("lastVisitMinefarm")));
    }

    public List<MinefarmInvitation> getMinefarmInvitations(UUID playerUuid){
        if(!DataStorageAPI.getPlayerData(playerUuid).isSet("minefarmInvitations")) return new ArrayList<>();

        List<UUID> invUuids = String2Uuid.toUuid(DataStorageAPI.getPlayerData(playerUuid).getStringList("minefarmInvitations"));
        List<MinefarmInvitation> result = new ArrayList<>();
        for(UUID uuid : invUuids){
            result.add(MinefarmInvitation.load(uuid));
        }

        return result;
    }

    public void setMinefarmInvitations(UUID playerUuid, List<MinefarmInvitation> list){
        List<UUID> invUuids = new ArrayList<>();
        for(MinefarmInvitation inv : list){
            invUuids.add(inv.uuid);
        }

        DataStorageAPI.getPlayerData(playerUuid).set("minefarmInvitations", String2Uuid.toString(invUuids));
    }

    public void createMinefarmInvitation(Player from, String target) throws RuntimeException{
        Minefarm minefarm = getCurrentMinefarm(from.getUniqueId());
        if(minefarm == null) throw new RuntimeException("현재 소속된 마인팜이 없습니다.");

        OfflinePlayer offlinePlayer = null;
        for(OfflinePlayer p : getPlugin().getServer().getOfflinePlayers()){
            if(p.getName().equals(target)){
                offlinePlayer = p;
                break;
            }
        }
        if(offlinePlayer == null) throw new RuntimeException("해당 플레이어가 존재하지 않습니다.");

        if(minefarm.isMember(offlinePlayer.getUniqueId())) throw new RuntimeException("이미 이 마인팜에 가입된 상태입니다.");

        List<MinefarmInvitation> list = getMinefarmInvitations(offlinePlayer.getUniqueId());
        list.add(MinefarmInvitation.create(minefarm.getUniqueId(), from.getUniqueId(), offlinePlayer.getUniqueId(), System.currentTimeMillis() + 60 * 60 * 1000));
        setMinefarmInvitations(offlinePlayer.getUniqueId(), list);

        Player onlinePlayer = getPlugin().getServer().getPlayer(target);
        if(onlinePlayer != null){
            onlinePlayer.sendMessage(ChatColor.GREEN + from.getName() + ChatColor.RESET + "님이 자신의 마인팜으로 " + ChatColor.GREEN + "초대" + ChatColor.RESET + "했습니다!");
            onlinePlayer.sendMessage(ChatColor.RED + "/mf " + ChatColor.RESET + "명령어로 마인팜 화면을 열고, " + ChatColor.GOLD + "책과 깃펜" + ChatColor.RESET + " 아이템을 클릭해서 초대장을 확인하세요!");
        }
    }

    public void processMinefarmInvitation(Player player, MinefarmInvitation inv, boolean accept) throws RuntimeException{
        if(!player.getUniqueId().equals(inv.to)) throw new RuntimeException("초대장의 대상 유저와 다릅니다.");

        List<MinefarmInvitation> list = getMinefarmInvitations(player.getUniqueId());
        int idx = -1;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).uuid.equals(inv.uuid)){
                idx = i;
                break;
            }
        }
        if(idx == -1) throw new RuntimeException("해당 초대장은 존재하지 않습니다.");

        list.remove(idx);
        setMinefarmInvitations(player.getUniqueId(), list);

        Minefarm minefarm = getMinefarmByUuid(inv.minefarmUuid);
        if(minefarm == null) throw new RuntimeException("해당 마인팜은 존재하지 않습니다.");

        //이미 초대된 경우
        if(minefarm.isMember(player.getUniqueId())){
            throw new RuntimeException("이미 초대된 마인팜입니다.");
        }

        if(accept){
            minefarm.addPlayer(player.getUniqueId());
            player.sendMessage("마인팜 초대를 " + ChatColor.GREEN + "수락" + ChatColor.RESET + "했습니다.");
            player.sendMessage("마인팜 " + ChatColor.GREEN + minefarm.getName() + ChatColor.RESET + "의 일원이 되었습니다!");
            player.sendMessage("마인팜 " + ChatColor.LIGHT_PURPLE + "리프" + ChatColor.RESET + "를 통해 해당 섬으로 이동할 수 있습니다.");
        }
        else{
            player.sendMessage("마인팜 초대를 " + ChatColor.RED + "거절" + ChatColor.RESET + "했습니다.");
        }
    }

    private Sector getNextSector(){
        ConfigSession cs = ConfigManager.getConfigSession(plugin);
        if(!cs.isSet("sx") || !cs.isSet("sz")){
            cs.set("sx", 0);
            cs.set("sz", 0);
            cs.save();
            return new Sector(0, 0, MINEFARM_SIZE_TIER_3);
        }

        int sx = cs.getInt("sx");
        int sz = cs.getInt("sz");

        //생성 방법 : (0, 0), (1, 0), (0, 1), (2, 0), (1, 1), (0, 2), (3, 0), ...
        if(sx == 0){
            sx = sz + 1;
            sz = 0;
        }
        else{
            sx--;
            sz++;
        }

        cs.set("sx", sx);
        cs.set("sz", sz);
        cs.save();
        return new Sector(sx, sz, MINEFARM_SIZE_TIER_3);
    }

}
