package waterArchery.HesapEsle.Komutlar;

import com.tjplaysnow.discord.object.Bot;
import com.tjplaysnow.discord.object.CommandSpigotManager;
import com.tjplaysnow.discord.object.ThreadSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import waterArchery.HesapEsle.ConfigMain;
import waterArchery.HesapEsle.HesapEsleMain;

import java.util.HashMap;
import java.util.Random;

public class MainCommand implements CommandExecutor {

    public static HashMap<Integer, Player> kod = new HashMap<>();
    public static HashMap<Integer, Player> Rolkod = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender o, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("hesapeşle")|cmd.getName().equalsIgnoreCase("hesapesle")){
            o.sendMessage(ConfigMain.oyunPrefix + " §eHesap eşlemek için: §b/eşle §eyazabilirsiniz.");
            o.sendMessage(ConfigMain.oyunPrefix + " §eRolleri eşlemek için: §b/eşle rol §eyazabilirsiniz.");
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("esle") | cmd.getName().equalsIgnoreCase("verify")
                | cmd.getName().equalsIgnoreCase("eşle")) {
            if (o instanceof Player) {
                if (args.length == 0) {
                    if(!HesapEsleMain.aktif){
                        o.sendMessage("Token girilmediği için bot devredışı.");
                        o.sendMessage("Tokeni girip sunucuyu kapatıp açmalısınız");
                        return false;
                    }
                    if (HesapEsleMain.data.get("Data." + ((Player) o).getPlayer().getName()) == null) {
                        int code = 0;
                        for (int sayi = 1; sayi > 0; sayi++) {
                            code = new Random().nextInt(9999999);
                            if (kod.get(code) == null) {
                                break;
                            }
                        }
                        o.sendMessage(ConfigMain.oyunPrefix + " " + ConfigMain.EslemeKodunuz.replace("%kod%", code + ""));
                        o.sendMessage(ConfigMain.oyunPrefix + " " + ConfigMain.DiscordaGirin.replace("%kod%", code + ""));
                        if (kod.get(code) == null) {
                            kod.put(code, (Player) o);
                        } else {
                            kod.replace(code, (Player) o);
                        }
                    } else {
                        o.sendMessage(ConfigMain.oyunPrefix + " §eHesabını zaten eşlemişsin!");

                    }
                }
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("reload")){
                        if(o.hasPermission("hesapesle.admin")){
                            HesapEsleMain.plugin.reloadConfig();
                            ConfigMain.ConfigCekici();
                            o.sendMessage(ConfigMain.oyunPrefix + " §eDosyalar yenilendi!");
                        }
                        else{
                            o.sendMessage(ConfigMain.oyunPrefix + " §eYetkiniz yok!");
                        }

                    }
                    if(args[0].equalsIgnoreCase("rol")){
                        int rolCode = 0;
                        for (int sayi = 1; sayi > 0; sayi++) {
                            rolCode = new Random().nextInt(9999999);
                            if (Rolkod.get(rolCode) == null) {
                                break;
                            }

                        }
                        o.sendMessage(ConfigMain.oyunPrefix + " §eRollerinizi almak için kodunuz: §b" + rolCode);
                        o.sendMessage(ConfigMain.oyunPrefix + " " + ConfigMain.RolDiscordaGirin.replace("%kod%", rolCode + ""));
                        if (Rolkod.get(rolCode) == null) {
                            Rolkod.put(rolCode, (Player) o);
                        } else {
                            Rolkod.replace(rolCode, (Player) o);
                        }
                    }
                }
            }

        }

        return false;
    }

}
