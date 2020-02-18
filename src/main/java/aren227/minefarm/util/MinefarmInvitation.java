package aren227.minefarm.util;

import kr.laeng.datastorage.DataStorageAPI;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class MinefarmInvitation {

    public UUID uuid;

    public UUID minefarmUuid;
    public UUID from;
    public UUID to;
    public long expireTime;

    private MinefarmInvitation(UUID uuid, UUID minefarmUuid, UUID from, UUID to, long expireTime){
        this.uuid = uuid;
        this.minefarmUuid = minefarmUuid;
        this.from = from;
        this.to = to;
        this.expireTime = expireTime;
    }

    public static MinefarmInvitation create(UUID minefarmUuid, UUID from, UUID to, long expireTime){
        UUID uuid = UUID.randomUUID();
        FileConfiguration fc = DataStorageAPI.getPluginData("minefarmInvitation", uuid);
        fc.set("minefarm", minefarmUuid.toString());
        fc.set("from", from.toString());
        fc.set("to", to.toString());
        fc.set("expire", expireTime);

        return new MinefarmInvitation(uuid, minefarmUuid, from, to, expireTime);
    }

    public static MinefarmInvitation load(UUID uuid){
        FileConfiguration fc = DataStorageAPI.getPluginData("minefarmInvitation", uuid);
        return new MinefarmInvitation(uuid, UUID.fromString(fc.getString("minefarm")), UUID.fromString(fc.getString("from")), UUID.fromString(fc.getString("to")), fc.getLong("expire"));
    }

}
