package waterArchery.HesapEsle;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class ConfigMain {

    public static String botPrefix;
    public static String oyunPrefix;
    public static String token;
    public static String KodBulunamadi;
    public static String OyuncuOffline;
    public static String Eslendi;
    public static String EslemeKodunuz;
    public static String DiscordaGirin;
    public static String RolDiscordaGirin;
    public static String HesapEslendi;
    public static String OzelMesajBaslik;
    public static String OzelMesakIcerik;
    public static boolean OzelMesaj;
    public static boolean TekDiscordEsleme;
    public static Long hesapkanalID;
    public static Long rolkanalID;
    public static Long EslendiRolID;


    public static void ConfigCekici(){
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("HesapEsle");
        botPrefix = plugin.getConfig().getString("Discord." +"BotPrefix");
        token = plugin.getConfig().getString("Discord." + "BotToken");
        oyunPrefix = plugin.getConfig().getString("Oyun." + "Prefix").replace("&","§");
        KodBulunamadi = plugin.getConfig().getString("Discord." + "KodBulunamadi");
        OyuncuOffline = plugin.getConfig().getString("Discord." + "OyuncuOffline");
        Eslendi = plugin.getConfig().getString("Discord." + "Eslendi");
        OzelMesajBaslik = plugin.getConfig().getString("Discord." + "OzelMesajBaslik");
        OzelMesakIcerik = plugin.getConfig().getString("Discord." + "OzelMesakIcerik");
        EslemeKodunuz = plugin.getConfig().getString("Oyun." + "EslemeKodunuz").replace("&","§");
        DiscordaGirin = plugin.getConfig().getString("Oyun." + "DiscordaGirin").replace("&","§");
        RolDiscordaGirin = plugin.getConfig().getString("Oyun." + "RolDiscordaGirin").replace("&","§");
        HesapEslendi = plugin.getConfig().getString("Oyun." + "HesapEslendi").replace("&","§");

        OzelMesaj = plugin.getConfig().getBoolean("Discord." + "OzelMesaj");
        TekDiscordEsleme = plugin.getConfig().getBoolean("Discord." + "TekDiscordEsleme");

        hesapkanalID = plugin.getConfig().getLong("Discord." + "HesapEsleKanalID");
        rolkanalID = plugin.getConfig().getLong("Discord." + "RolEsleKanalID");
        EslendiRolID = plugin.getConfig().getLong("Discord." + "EslendiRolID");




    }

}
