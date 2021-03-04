package com.niciel.superduperitems.book;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class BookGui {


    public final ItemStack is;
    public final BookMeta im;


    public BookGui() {
        is = new ItemStack(Material.WRITTEN_BOOK);
        im = (BookMeta) is.getItemMeta();
    }




    public void open(Player p) {

    }

    public static String addReturns(String s, int maxLength)
    {
        String newString = "";
        int ind = 0;
        while(ind < s.length())
        {
            String temp = s.substring(ind, Math.min(s.length(), ind+maxLength));
            int lastSpace = temp.lastIndexOf(" ");
            int firstNewline = temp.indexOf("\n");
            if(firstNewline>-1)
            {
                newString += temp.substring(0, firstNewline + 1);
                ind += firstNewline + 1;
            }
            else if(lastSpace>-1)
            {
                newString += temp.substring(0, lastSpace + 1) + "\n";
                ind += lastSpace + 1;
            }
            else
            {
                newString += temp + "\n";
                ind += maxLength;
            }
        }
        return newString;
    }


}
