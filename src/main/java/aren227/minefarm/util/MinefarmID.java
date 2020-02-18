package aren227.minefarm.util;

import aren227.configmanager.ConfigManager;
import aren227.minefarm.Plugin;

import java.util.Random;
import java.util.UUID;

//모든 관리는 UUID로 이뤄짐
//그런데, uuid 생성시 MostSignificantBits 를 next()에서 얻은 int 값으로 사용
//이 int값을 통해 XXXXX 형식의 코드값을 구해낼 수 있음.

public class MinefarmID {

    public static MinefarmID instance;

    public static final int PRIME_ALL = 11881357;
    public static final int PRIME_SEED = 3930337;

    private int current;

    private Plugin plugin;

    public MinefarmID(Plugin plugin){
        instance = this;

        this.plugin = plugin;

        Random random = new Random();

        ConfigManager.getConfigSession(plugin).setDesc("minefarmIdCurrent", "현재 마인팜 ID 인덱스입니다.", random.nextInt(PRIME_ALL));

        current = ConfigManager.getConfigSession(plugin).getInt("minefarmIdCurrent");
    }

    public static void validate(String id) throws RuntimeException{
        if(id.length() != 5) throw new RuntimeException("마인팜 ID는 5자리의 대문자 영어로 구성되어 있습니다.");
        for(int i = 0; i < 5; i++){
            if('A' > id.charAt(i) || id.charAt(i) > 'Z') throw new RuntimeException("마인팜 ID는 5자리의 대문자 영어로 구성되어 있습니다.");
        }
    }

    public static String uuidToString(UUID uuid){
        int idx = (int)uuid.getMostSignificantBits();

        String id = "";

        for(int i = 4; i >= 0; i--){
            int t = idx / instance.pow(26, i);
            id += (char)((int)'A' + t);
            idx -= t * instance.pow(26, i);
        }

        return id;
    }

    public static UUID stringToUuid(String id){
        int idx = 0;
        for(int i = 0; i < 5; i++){
            idx += (id.charAt(i) - 'A') * instance.pow(26, 4 - i);
        }
        return new UUID(idx, 0);
    }

    public static UUID generateUuid(){
        return new UUID(instance.next(), 0);
    }

    public int next(){
        current = (current + PRIME_SEED) % PRIME_ALL;
        ConfigManager.getConfigSession(plugin).set("minefarmIdCurrent", current);
        return current;
    }

    //멍청한 O(n) 제곱 알고리즘
    public int pow(int a, int b){
        int c = 1;
        for(int i = 0; i < b; i++) c *= a;
        return c;
    }

}
