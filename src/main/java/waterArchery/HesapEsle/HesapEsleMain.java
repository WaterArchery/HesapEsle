package waterArchery.HesapEsle;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
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
    /*
    void botKomutEkle(){
        bot.addCommand(new ProgramCommand() {
            @Override
            public boolean run(User user, MessageChannel channel, Guild guild, String label, List<String> args) {
                rolEsle(user,channel,guild,label,args);
                return false;

            }
            @Override
            public Permission getPermissionNeeded() {
                return Permission.MESSAGE_WRITE;
            }

            @Override
            public String getLabel() {
                return "rol";
            }

            @Override
            public String getDescription() {
                return "Rolleri eşleme komutu";
            }

        });
        for(ProgramCommand komut : bot.getCommands()){
            Bukkit.getConsoleSender().sendMessage(komut.getLabel());

        }
    }
    */
    public static void rolEsle(User user, MessageChannel channel, Guild guild, String label, List<String> args){
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
                guild.addRoleToMember(guild.getMember(user),guild.getJDA().getRoleById(Long.parseLong(parcalar[1]))).complete();
                Oyuncu.sendMessage(ConfigMain.oyunPrefix + " §eOyundaki yetkiniz Discordda verildi!");
                channel.sendMessage("Oyun içi rollerin Discord üzerinden verildi "  + "<@" + user.getIdLong() + "> ").complete()
                        .delete().completeAfter(15,TimeUnit.SECONDS);
                MainCommand.Rolkod.remove(arg);
                return;
            }
        }

    }

    public static void dogrula(User user, MessageChannel channel, Guild guild, String label, List<String> args){
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
            if(guild.getMember(user).getRoles().contains(guild.getRoleById(ConfigMain.EslendiRolID))){
                channel.sendMessage("Bu Discord hesabı ile zaten eşleme yapmışsınız!").complete().delete().completeAfter(5, TimeUnit.SECONDS);
                return;
            }
        }

        Player Oyuncu = MainCommand.kod.get(arguman);
        if(Bukkit.getPlayer(MainCommand.kod.get(arguman).getName())==null){
            channel.sendMessage(ConfigMain.OyuncuOffline).complete().delete().completeAfter(5, TimeUnit.SECONDS);
            return;
        }
        if (HesapEsleMain.data.get("Data." + ((Player) Oyuncu).getPlayer().getName()) != null) {
            channel.sendMessage("Bu hesap zaten eşlenmiş!").complete().delete().completeAfter(5,TimeUnit.SECONDS);
            return;
        }
        if(ConfigMain.OzelMesaj){
            mesajGonder(user,Oyuncu);
        }
        channel.sendMessage(ConfigMain.Eslendi.replace("%minecraft%",Oyuncu.getPlayer().getName()
        ).replace("%discord%","<@!" + user.getId()+">")).complete();
        rolVer(guild,user,Oyuncu);
        MainCommand.kod.remove(arguman);
        guild.addRoleToMember(guild.getMember(user),guild.getJDA().getRoleById(ConfigMain.EslendiRolID)).complete();
        Oyuncu.sendMessage(ConfigMain.oyunPrefix + " " + ConfigMain.HesapEslendi.replace("%discord%",user.getName()));
        data.set("Data." + Oyuncu.getPlayer().getName(),true);
        dataSave();
        komutUygula(Oyuncu);
    }

    static void rolVer(Guild guild, User user, Player oyuncu){
        for(String YetkiVeID : plugin.getConfig().getStringList("Oyun." + "Yetkiler")){
            String[] parcalar = YetkiVeID.split(" / ");
            if(guild.getRoleById(Long.parseLong(parcalar[1])) == null){
                continue;
            }
            if(oyuncu.hasPermission(parcalar[0])){
                guild.addRoleToMember(guild.getMember(user),guild.getJDA().getRoleById(Long.parseLong(parcalar[1]))).complete();
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
