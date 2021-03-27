package com.niciel.superduperitems.gsonadapter;

import java.io.*;

public final class GsonSimpleFileUtils {


    /**
     *
     * @param f         file to load (direct path to file)
     * @param toLoad    return type if T isnt accessible from loaded object method will return null
     * @param <T>       T expected object type
     * @return
     */
    public static <T> T loadIfExists(File f , Class<T> toLoad) {
        if (f.exists()) {
            if (f.getName().endsWith(".yml")) {
                Object o = null;
                try {
                    FileReader reader = new FileReader(f);
                    BufferedReader r = new BufferedReader(reader);
                    final StringBuilder sb = new StringBuilder();
                    String st = r.readLine();
                    int iteration = 0;
                    while (st != null) {
                        sb.append(st);
                        st = r.readLine();
                    }
                    r.close();
                    reader.close();
                    o = GsonManager.getInstance().fromJson(sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                if (toLoad.isAssignableFrom(o.getClass()))
                    return (T) o;
            }
        }
        return null;
    }

    /**
     *
     * @param f         path to folder
     * @param name      name of file (without extension)
     * @param toLoad    return type if T isnt accessible from loaded object method will return null
     * @param <T>       T expected object type
     * @return
     */
    public static <T> T loadIfExists(File f , String name , Class<T> toLoad) {
        File nf = new File(f , name + ".yml");
        return loadIfExists(nf,toLoad);
    }


    /**
     *
     * @param f         file to directory
     * @param name      name of file without extension
     * @param o         object to save
     * @return          true if successfully saved
     */
    public static boolean saveOrChange(File f  , String name, Object o) {
        if (f.exists() == false)
            f.mkdirs();
        File nf = new File(f, name+".yml");
        FileWriter w = null;
        try {
            w = new FileWriter(nf);
            w.write(GsonManager.getInstance().toJson(o).toString());
            w.flush();
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
