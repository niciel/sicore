package com.niciel.superduperitems.utils;

import com.google.common.primitives.Ints;
import com.niciel.superduperitems.SDIPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SpigotCharTableUtils {

    private static byte[] high_4_bits;
    private static byte[] low_4_bits;
    private static Random random;

    private static  List<Byte> charsetTables;



    static {
        random = new Random();

        high_4_bits = new byte[16];
        low_4_bits = new byte[16];

        for (int b = 0 ; b < 16 ; b++) {
            high_4_bits[b] = (byte) (((byte) b) << 4);
            low_4_bits[b] = (byte) b;
        }

        charsetTables = new ArrayList<>();
        File file = SDIPlugin.instance.getDataFolder();
        file = new File(file , "charsets.yml");
        YamlConfiguration config ;
        if (file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
        }
        else {
            config = new YamlConfiguration();
            config.addDefault("list" , Arrays.asList("00" , "0a"));
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config.getStringList("list").forEach(s -> charsetTables.add(translateByte(s)));
    }

    public static String stringFromInt(int i) {
        return new String(Ints.toByteArray(i) , StandardCharsets.UTF_16BE);
    }

    public  static Integer intFromString(String s) {
        byte[] array = s.getBytes(StandardCharsets.UTF_16BE);
        if (array.length != 4)
            return null;
        return Ints.fromByteArray(array);
    }

    protected  static byte randomByte() {
        byte b[] = new  byte[1];
        random.nextBytes(b);
        return b[0];
    }

    public  static String getNextRandomID() {
        byte[] out = new byte[4];
        for (int i = 0 ; i < 4 ; i++) {
            if (i%2 == 0) {
                out[i] = charsetTables.get(random.nextInt(charsetTables.size()));
            }
            else {
                out[i] = randomByte();
            }
        }
        String ret = new String(out , StandardCharsets.UTF_16BE );
        return ret;
    }

    public static  String getNextRandomID(Collection<String> excluded) {
        String ret = getNextRandomID();
        if (excluded.contains(ret))
            getNextRandomID(excluded);
        return ret;
    }



    protected  static int translateChar(char c){
        if (c - '0' >= 0 && c-'9' <= 0) {
            return c-'0';
        }
        return 10 + (c-'a');
    }

    public static  byte translateByte(String a) {
        a = a.toLowerCase();
        Pattern p = Pattern.compile("[1234567890abcdef]");
        Matcher m = p.matcher(a);
        if (m.find()) {
            char first = a.charAt(0);
            char second = a.charAt(1);
            return get(translateChar(first) , translateChar(second));
        }
        else {
            return 0x0;
        }
    }


    public  static byte get(int a , int b) {
        if (a > 15)
            return 0x0;
        if (b > 15)
            return 0x0;
        return (byte) (high_4_bits[a] | low_4_bits[b]);
    }

    public static int charRunCount(String str, char c) {
        char last = 0;
        int counter = 0;
        for (int i = 0; i < str.length(); i++) {
            // whenever a run starts.
            if (last != c && str.charAt(i) == c)
                counter++;
            last = str.charAt(i);
        }
        return counter;
    }

}
