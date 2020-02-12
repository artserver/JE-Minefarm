package aren227.minefarm.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class String2Uuid {

    public static List<String> toString(List<UUID> uuids){
        List<String> list = new ArrayList<>();
        for(int i = 0; i < uuids.size(); i++){
            list.add(uuids.get(i).toString());
        }
        return list;
    }

    public static List<UUID> toUuid(List<String> strings){
        List<UUID> list = new ArrayList<>();
        for(int i = 0; i < strings.size(); i++){
            list.add(UUID.fromString(strings.get(i)));
        }
        return list;
    }

}
