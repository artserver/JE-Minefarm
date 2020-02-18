package aren227.minefarm.util;

public class Time2String {

    public static String getString(long ms){
        if(ms == 0) return "0초";
        ms /= 1000;
        if(ms < 1) return "1초";
        if(ms < 60) return ms + "초";
        if(ms < 60 * 60) return (ms / 60) + "분";
        if(ms < 60 * 60 * 24) return (ms / 3600) + "시간 " + ((ms / 60) % 60) + "분";
        else return (ms / (3600 * 24)) + "일 " + ((ms / 3600) % 24) + "시간 " + ((ms / 60) % 60) + "분";
    }

}
