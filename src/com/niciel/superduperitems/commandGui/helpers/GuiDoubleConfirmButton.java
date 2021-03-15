package com.niciel.superduperitems.commandGui.helpers;

import com.niciel.superduperitems.commandGui.GuiCommandArgs;
import com.niciel.superduperitems.commandGui.IGuiCommandObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class GuiDoubleConfirmButton implements GuiCommandArgs, IGuiCommandObject {




    private Consumer<Player> accpet;
    private Consumer<Player> reject;
    private String rejectS;
    private String acceptS;


    private TextComponent send;


    public GuiDoubleConfirmButton(Consumer<Player> accept , String acceptString , Consumer<Player> reject  , String rejectString) {
        this.accpet = accept;
        this.reject = reject;
        this.rejectS = rejectString;
        this.acceptS = acceptString;
    }

    @Override
    public void onCommand(Player p, String[] args , int deep) {
        if (args.length > deep) {
            String ar = args[deep];
            if (ar.contentEquals("reject")) {
                this.reject.accept(p);
                return;
            }
            else if (ar.contentEquals("accept")) {
                this.accpet.accept(p);
                return;
            }
        }
        send(p);
    }

    @Override
    public void init(String command) {
        send = new TextComponent("-=-=-=-");
        send.setColor(ChatColor.GRAY);
        TextComponent in = new TextComponent(rejectS);
        in.setColor(ChatColor.RED);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , command + " reject"));
        send.addExtra(in);
        send.addExtra("-=-=-=");
        in = new TextComponent(acceptS);
        in.setColor(ChatColor.GREEN);
        in.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND , command + " accept"));
        send.addExtra(in);
    }

    public void send(Player p) {
        if (send != null)
            p.spigot().sendMessage(send);
        else {
            p.sendMessage(ChatColor.RED + " BLAD !! niezainicjowana zmienna init");
        }
    }

}
