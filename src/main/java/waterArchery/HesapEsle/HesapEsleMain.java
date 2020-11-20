package waterArchery.HesapEsle;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import waterArchery.HesapEsle.Eventler.EsleEvent;
import waterArchery.HesapEsle.Komutlar.MainCommand;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static waterArchery.HesapEsle.ConfigMain.ConfigCekici;

public final class HesapEsleMain extends JavaPlugin implements Listener, CommandExecutor {

    public static File dataFile;
    public static YamlConfiguration data;
    public JDA bot;

    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = Bukkit.getServer().getPluginManager().getPlugin("HesapEsle");
        dataOlustur();
        Bukkit.getConsoleSender().sendMessage("Hesap Eşle - Config dosyası ve data dosyaları oluşturuldu");
        ConfigCekici();
        Bukkit.getConsoleSender().sendMessage("Hesap Eşle - Config değerleri dosyadan çekildi");
        Bukkit.getServer().getPluginCommand("esle").setExecutor(new MainCommand());
        Bukkit.getServer().getPluginCommand("eşle").setExecutor(new MainCommand());
        Bukkit.getServer().getPluginCommand("verify").setExecutor(new MainCommand());
        Bukkit.getServer().getPluginCommand("hesapeşle").setExecutor(new MainCommand());
        Bukkit.getServer().getPluginCommand("hesapesle").setExecutor(new MainCommand());
        Bukkit.getConsoleSender().sendMessage("Hesap Eşle - Komutlar yüklendi!");
        botOlustur();
    }
    @Override
    public void onDisable() {
        dataFile = null;
        data = null;
        bot = null;
        ConfigMain.botPrefix = null;
        ConfigMain.token = null;
        ConfigMain.oyunPrefix = null;
        ConfigMain.hesapkanalID = null;
        ConfigMain.rolkanalID = null;
        ConfigMain.OyuncuOffline = null;
        ConfigMain.Eslendi = null;
        ConfigMain.EslemeKodunuz = null;
        ConfigMain.DiscordaGirin = null;
        ConfigMain.HesapEslendi = null;
        ConfigMain.OzelMesajBaslik = null;
        ConfigMain.OzelMesakIcerik = null;
        ConfigMain.OzelMesaj = false;

    }
    public static boolean aktif;
    static String TOKEN;
    public static String PREFIX;
    public void botOlustur() {
        plugin = getPlugin(HesapEsleMain.class);
        TOKEN = ConfigMain.token;
        PREFIX = ConfigMain.botPrefix;
        if(TOKEN.equalsIgnoreCase("")){
            aktif = false;
            plugin.getServer().getConsoleSender().sendMessage("Token girilmediği için bot devredışı bırakıldı");
            return;
        }
        aktif = true;
        try {
            bot = JDABuilder.createDefault(TOKEN).addEventListeners(new EsleEvent()).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        Bukkit.getConsoleSender().sendMessage("Hesap Eşle - Bot oluşturuldu ve ve bot komutları eklendi ");
        Bukkit.getConsoleSender().sendMessage("Hesap Eşle - Plugin aktif!");
    }

    public static void dataSave(){
        try {
            data.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("ConstantConditions")
    public static void rolEsle(Member member, MessageChannel channel, Guild guild, String label, List<String> args){
        if(channel.getIdLong() != ConfigMain.rolkanalID){
            channel.deleteMessageById(channel.getLatestMessageId()).complete();
            channel.sendMessage("<#" + ConfigMain.rolkanalID + "> Kanalına yazmanız gerekiyor").complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        if(!label.equalsIgnoreCase(PREFIX + "rol")){
            channel.sendMessage("Oyundan /eşle rol yazıp kodunuzu aldıktan sonra bu kanala !rol kod şeklinde yazmalısınız.")
                    .complete().delete().completeAfter(5,TimeUnit.SECONDS);
            return;
        }
        channel.deleteMessageById(channel.getLatestMessageId()).complete();
        int arg;
        try {
            arg = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            channel.sendMessage(ConfigMain.KodBulunamadi).complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        if(MainCommand.Rolkod.get(arg) ==null){
            channel.sendMessage(ConfigMain.KodBulunamadi).complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        Player Oyuncu = MainCommand.Rolkod.get(arg);

        for(String YetkiVeID : plugin.getConfig().getStringList("Oyun." + "Yetkiler")){
            String[] parcalar = YetkiVeID.split(" / ");
            if(guild.getRoleById(Long.parseLong(parcalar[1])) == null){
                continue;
            }
            if(Oyuncu.hasPermission(parcalar[0])){
                if(member ==null){
                    channel.sendMessage("Discord hesabı bulunamadı." + " " + member.getUser().getIdLong()).complete().delete().completeAfter(5, TimeUnit.SECONDS);
                    return;
                }
                if(guild.getRoleById(ConfigMain.EslendiRolID)==null){
                    channel.sendMessage("Discordda böyle bir rol bulunamadı" + " " + guild.getRoleById(ConfigMain.EslendiRolID)).complete().delete().completeAfter(5, TimeUnit.SECONDS);
                    return;
                }
                guild.addRoleToMember(member, guild.getJDA().getRoleById(Long.parseLong(parcalar[1]))).complete();
                Oyuncu.sendMessage(ConfigMain.oyunPrefix + " §eOyundaki yetkiniz Discordda verildi!");
                channel.sendMessage("Oyun içi rollerin Discord üzerinden verildi "  + "<@" + member.getUser().getIdLong() + "> ").complete()
                        .delete().completeAfter(15,TimeUnit.SECONDS);
                MainCommand.Rolkod.remove(arg);
                return;
            }
        }

    }

    @SuppressWarnings("ConstantConditions")
    public static void dogrula(Member member, MessageChannel channel, Guild guild, String label, List<String> args){
        if(channel.getIdLong() != ConfigMain.hesapkanalID){
            channel.deleteMessageById(channel.getLatestMessageId()).complete();
            channel.sendMessage("<#" + ConfigMain.hesapkanalID + "> Kanalına yazmanız gerekiyor").complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        if(!label.equalsIgnoreCase(PREFIX + "eşle")&!label.equalsIgnoreCase(PREFIX +"esle")){
            channel.sendMessage("Oyundan /eşle yazıp kodunuzu aldıktan sonra bu kanala !eşle kod şeklinde yazmalısınız.")
                    .complete().delete().completeAfter(5,TimeUnit.SECONDS);
            return;
        }
        int arguman;
        try {
            arguman = Integer.parseInt(args.get(0));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            channel.sendMessage(ConfigMain.KodBulunamadi).complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }

        channel.deleteMessageById(channel.getLatestMessageId()).complete();
        if(MainCommand.kod.get(arguman) ==null){
            channel.sendMessage(ConfigMain.KodBulunamadi).complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        if(ConfigMain.TekDiscordEsleme){
            if(member ==null){
                channel.sendMessage("Discord hesabı bulunamadı.").complete().delete().completeAfter(5, TimeUnit.SECONDS);
                return;
            }
            if(guild.getRoleById(ConfigMain.EslendiRolID)==null){
                channel.sendMessage("Discordda böyle bir rol bulunamadı" + " " + guild.getRoleById(ConfigMain.EslendiRolID)).complete().delete().completeAfter(5, TimeUnit.SECONDS);
                return;
            }
            try{
                if(member.getRoles().contains(guild.getRoleById(ConfigMain.EslendiRolID))){
                    channel.sendMessage("Bu Discord hesabı ile zaten eşleme yapmışsınız!").complete().delete().completeAfter(5, TimeUnit.SECONDS);
                    return;
                }
            }catch (NullPointerException e){
                //
            }
            guild.addRoleToMember(member,guild.getRoleById(ConfigMain.EslendiRolID)).complete();
        }

        Player Oyuncu = MainCommand.kod.get(arguman);
        if(data.get("Data." + Oyuncu.getName()) !=null){
            channel.sendMessage("Bu oyun hesabı ile zaten eşleme yapmışsınız!").complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        if(Bukkit.getPlayer(MainCommand.kod.get(arguman).getName())==null){
            channel.sendMessage(ConfigMain.OyuncuOffline).complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        if (HesapEsleMain.data.get("Data." + ((Player) Oyuncu).getPlayer().getName()) != null) {
            channel.sendMessage("Bu hesap zaten eşlenmiş!").complete().delete().completeAfter(5,TimeUnit.SECONDS);
            return;
        }
        if(ConfigMain.OzelMesaj){
            mesajGonder(member.getUser(),Oyuncu);
        }
        channel.sendMessage(ConfigMain.Eslendi.replace("%minecraft%",Oyuncu.getPlayer().getName()
        ).replace("%discord%","<@!" + member.getUser().getId()+">")).complete();
        data.set("Data." + Oyuncu.getPlayer().getName(),true);
        dataSave();
        rolVer(guild,member,Oyuncu);
        MainCommand.kod.remove(arguman);
        Oyuncu.sendMessage(ConfigMain.oyunPrefix + " " + ConfigMain.HesapEslendi.replace("%discord%",member.getUser().getName()));
        komutUygula(Oyuncu);
    }

    @SuppressWarnings("ConstantConditions")
    static void rolVer(Guild guild, Member member, Player oyuncu){
        for(String YetkiVeID : plugin.getConfig().getStringList("Oyun." + "Yetkiler")){
            String[] parcalar = YetkiVeID.split(" / ");
            if(guild.getRoleById(Long.parseLong(parcalar[1])) == null){
                continue;
            }
            if(member ==null){
                oyuncu.sendMessage("Discord hesabı bulunamadı." + " " + member.getUser().getIdLong());
                return;
            }
            if(guild.getRoleById(Long.parseLong(parcalar[1]))==null){
                oyuncu.sendMessage("Discordda böyle bir rol bulunamadı" + " " + guild.getRoleById(ConfigMain.EslendiRolID));
                return;
            }
            if(oyuncu.hasPermission(parcalar[0])){
                guild.addRoleToMember(member,guild.getRoleById(Long.parseLong(parcalar[1]))).complete();
                oyuncu.sendMessage(ConfigMain.oyunPrefix + " §eOyundaki yetkiniz Discordda verildi!");
                return;
            }
        }
    }
    public static void mesajGonder(User user, Player Oyuncu){
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.red);
        eb.setColor(new Color(0xF40C0C));
        eb.setColor(new Color(255, 0, 54));
        eb.setTitle(ConfigMain.OzelMesajBaslik, null);
        String Mesaj = ConfigMain.OzelMesakIcerik.replace("%minecraft%",Oyuncu.getPlayer().getName());
        Mesaj = Mesaj.replace("\\n", "\n");
        eb.setDescription(Mesaj);
        user.openPrivateChannel().queue((ozel) ->
        {
            ozel.sendMessage(eb.build()).queue();
        });
    }
    static void komutUygula(Player Oyuncu){
        for(String komut : plugin.getConfig().getStringList("Oyun." + "Komutlar")){
            String FinalKomut = komut.replace("%player%",Oyuncu.getPlayer().getName());
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getServer().dispatchCommand(plugin.getServer().getConsoleSender(),FinalKomut);
                }
            }, 20L);
        }
    }

    public void dataOlustur(){
        if(!new File(getDataFolder(),"config.yml").exists()){
            saveDefaultConfig();
        }
        data = new YamlConfiguration();
        dataFile = new File(getDataFolder(),"data.yml");
        if(!dataFile.exists()){
            saveResource("data.yml",false);
        }
        try {
            data.load(dataFile);
        } catch (IOException | NullPointerException |InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
