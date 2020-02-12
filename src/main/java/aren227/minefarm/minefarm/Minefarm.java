package aren227.minefarm.minefarm;

import aren227.minefarm.Manager;
import aren227.minefarm.util.Sector;
import aren227.minefarm.util.String2Uuid;
import kr.laeng.datastorage.DataStorageAPI;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Minefarm {

    private UUID uuid;

    public Minefarm(UUID uuid){
        this.uuid = uuid;
    }

    public UUID getUniqueId(){
        return uuid;
    }

    public long getCreationTime(){
        return DataStorageAPI.getPluginData("minefarm", uuid).getLong("ct");
    }

    public void setCreationTime(long creationTime){
        DataStorageAPI.getPluginData("minefarm", uuid).set("ct", creationTime);
    }

    public Sector getSector(){
        FileConfiguration fc = DataStorageAPI.getPluginData("minefarm", uuid);
        return new Sector(fc.getInt("x"), fc.getInt("z"));
    }

    public void setSector(Sector sec){
        FileConfiguration fc = DataStorageAPI.getPluginData("minefarm", uuid);
        fc.set("x", sec.x);
        fc.set("z", sec.z);
    }

    public Location getSpawnLocation(){
        FileConfiguration fc = DataStorageAPI.getPluginData("minefarm", uuid);
        return new Location(Manager.getInstance().getMinefarmWorld(), fc.getDouble("sx"), fc.getDouble("sy"), fc.getDouble("sz"));
    }

    public boolean setSpawnLocation(Location location){
        Sector sector = getSector();
        if(!sector.isIn(location.getX(), location.getY(), location.getZ())) return false;

        FileConfiguration fc = DataStorageAPI.getPluginData("minefarm", uuid);
        fc.set("sx", location.getX());
        fc.set("sy", location.getY());
        fc.set("sz", location.getZ());

        return true;
    }

    public List<UUID> getPlayers(){
        if(!DataStorageAPI.getPluginData("minefarm", uuid).isSet("players")) return new ArrayList<>();
        return String2Uuid.toUuid(DataStorageAPI.getPluginData("minefarm", uuid).getStringList("players"));
    }

    public void setPlayers(List<UUID> uuids){
        DataStorageAPI.getPluginData("minefarm", uuid).set("players", String2Uuid.toString(uuids));
    }

    public void addPlayer(UUID playerUuid){
        List<UUID> list = getPlayers();
        list.add(playerUuid);
        setPlayers(list);

        List<UUID> list2 = Manager.getInstance().getMinefarms(playerUuid);
        list2.add(uuid);
        Manager.getInstance().setMinefarms(playerUuid, list2);
    }

    public List<UUID> getOps(){
        if(!DataStorageAPI.getPluginData("minefarm", uuid).isSet("ops")) return new ArrayList<>();
        return String2Uuid.toUuid(DataStorageAPI.getPluginData("minefarm", uuid).getStringList("ops"));
    }

    public void setOps(List<UUID> uuids){
        DataStorageAPI.getPluginData("minefarm", uuid).set("ops", String2Uuid.toString(uuids));
    }

    public void addOp(UUID playerUuid){
        List<UUID> list = getOps();
        list.add(playerUuid);
        setOps(list);
    }

    public void setMain(UUID playerUuid){
        DataStorageAPI.getPlayerData(playerUuid).set("currentMinefarm", uuid);
    }

    public void setName(String name){
        DataStorageAPI.getPluginData("minefarm", uuid).set("name", name);
    }

    public String getName(){
        return DataStorageAPI.getPluginData("minefarm", uuid).getString("name");
    }

}
