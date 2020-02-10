package aren227.minefarm.minefarm;

import aren227.minefarm.Manager;
import aren227.minefarm.util.Sector;
import kr.laeng.datastorage.DataStorageAPI;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

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

    public UUID getOwner(){
        return UUID.fromString(DataStorageAPI.getPluginData("minefarm", uuid).getString("owner"));
    }

    public void setOwner(UUID playerUuid){
        DataStorageAPI.getPluginData("minefarm", uuid).set("owner", playerUuid);
    }

    public void setName(String name){
        DataStorageAPI.getPluginData("minefarm", uuid).set("name", name);
    }

    public String getName(){
        return DataStorageAPI.getPluginData("minefarm", uuid).getString("name");
    }

}
