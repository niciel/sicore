package com.niciel.superduperitems;


import org.bukkit.util.Vector;



public class Test {

    public static byte numerAt(int i , double d) {
        double value = Math.abs(((double )(int )(d*Math.pow(10 ,i))));
        double upper = ((double) ((int) value/10))*10;
        d = value-upper;
        return (byte) d;



    }

    public enum RoundNumber {

        HALF_UP {

            @Override
            public double round(int places, double value) {
                double move = Math.pow(10,places);
                double v = value*move;
                v = (int) v;
                System.out.println("v " + v);
                byte position = numerAt(places+1 , value);
                if (position >=5) {
                    v += Math.signum(value);
                }
                v = v/move;
                return v;
            }
        };

        public double round(int places , double value) {
            return 0;
        }

    }

    public static void main(String[] args) {

        String test = "test1";

        String a = test.replaceAll("1" , " ojojo");
        System.out.println(test);


//        int i = 129;
//        byte b = (byte) i;
//        System.out.println("byte " + b);
//        int u = Byte.toUnsignedInt(b);
//        System.out.println("ubyte " +  u);


//        boolean flag;
//        flag = GsonSerializable.class.isAssignableFrom(Object.class);
//
//        System.out.println("flag "+ flag);

//        Vector a = new Vector(1,0,0);
//        Vector b = new Vector(0,0,0);
//        a.rotateAroundY(DegToRad*90);
//        System.out.println("a " + a + " angle " + a.angle(b)/DegToRad);
        ;
//        for (BlockFace f : BlockFace.values()) {
//            System.out.println("face: " + f + " mod " + f.getDirection());
//        }
    }



    public static Vector rotate(Vector v , Vector rotation) {
        Vector vec = v.clone();
        vec.rotateAroundX(rotation.getX()*DegToRad);
        vec.rotateAroundY(rotation.getY()*DegToRad);
        vec.rotateAroundZ(rotation.getZ()*DegToRad);
        return vec;
    }

    public static final double DegToRad = Math.PI/180;

    public double DegtoRad(double deg) {
        return deg*DegToRad;
    }

}
