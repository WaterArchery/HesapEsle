package waterArchery.HesapEsle.Eventler;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import waterArchery.HesapEsle.ConfigMain;
import waterArchery.HesapEsle.HesapEsleMain;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class EsleEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e)
    {

        if(e.getChannel().getIdLong() == ConfigMain.hesapkanalID){
            if(e.getAuthor().equals(e.getJDA().getSelfUser())){

                return;
            }
            if(!e.getMessage().getContentRaw().substring(0,1).equalsIgnoreCase(HesapEsleMain.PREFIX)){
                e.getMessage().delete().complete();
                e.getChannel().sendMessage("Oyundan /eşle yazıp kodunuzu aldıktan sonra bu kanala !eşle kod şeklinde yazmalısınız.")
                        .complete().delete().completeAfter(5, TimeUnit.SECONDS);
                return;
            }
            String label = "";
            String mesaj = e.getMessage().getContentRaw();
            for(int tekrar = 0; tekrar <mesaj.length();tekrar++){
                if(tekrar == mesaj.length() - 1){
                    label = label + mesaj.charAt(tekrar);
                    mesaj = mesaj.replace(label, "");
                    break;
                }
                if(mesaj.substring(tekrar,tekrar+1).equalsIgnoreCase(" ")){
                    mesaj = mesaj.replace(label + " ", "");
                    break;
                }
                else{
                    label = label + mesaj.substring(tekrar,tekrar+1);
                }
            }
            String[] args = mesaj.split(" ");

            HesapEsleMain.dogrula(e.getMember(),e.getChannel(),e.getGuild(),label, Arrays.asList(args));

        }
        if(e.getChannel().getIdLong() == ConfigMain.rolkanalID){
            if(e.getAuthor().equals(e.getJDA().getSelfUser())){
                return;
            }
            if(!e.getMessage().getContentRaw().substring(0,1).equalsIgnoreCase(HesapEsleMain.PREFIX)){
                e.getMessage().delete().complete();
                e.getChannel().sendMessage("Oyundan /eşle rol yazıp kodunuzu aldıktan sonra bu kanala !rol kod şeklinde yazmalısınız.")
                        .complete().delete().completeAfter(5, TimeUnit.SECONDS);
                return;
            }
            String label = "";
            String mesaj = e.getMessage().getContentRaw();
            for(int tekrar = 0; tekrar <mesaj.length();tekrar++){
                if(tekrar == mesaj.length() - 1){
                    label = label + mesaj.charAt(tekrar);
                    mesaj = mesaj.replace(label, "");
                    break;
                }
                if(mesaj.substring(tekrar,tekrar+1).equalsIgnoreCase(" ")){
                    mesaj = mesaj.replace(label + " ", "");
                    break;
                }
                else{
                    label = label + mesaj.substring(tekrar,tekrar+1);
                }
            }
            String[] args = mesaj.split(" ");

            HesapEsleMain.rolEsle(e.getMember(),e.getChannel(),e.getGuild(),label, Arrays.asList(args));

        }
    }
}
