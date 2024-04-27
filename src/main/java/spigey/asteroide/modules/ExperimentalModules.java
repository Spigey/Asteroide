package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.events.SendMessageEvent;

import java.util.List;

import static spigey.asteroide.util.banstuff;

public class ExperimentalModules extends Module {
    public ExperimentalModules() {
        super(AsteroideAddon.CATEGORY, "experimental-features", "Experimental features that are still in development");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> word_filter = sgGeneral.add(new BoolSetting.Builder()
        .name("Word Filter")
        .description("Filters words you send in the chat to prevent getting banned")
        .defaultValue(false)
        .build()
    );
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("WordFilter: messages to filter")
        .description("Filter these messages")
        .defaultValue("cum", "sex", "dick", "nigga", "nigger", "retard", "hitler")
        .visible(() -> word_filter.get())
        .build()
    );
    private final Setting<Boolean> woblox = sgGeneral.add(new BoolSetting.Builder()
        .name("WordFilter: Roblox-like Replacement")
        .description("Filters the message to look more like roblox filtering")
        .defaultValue(false)
        .visible(() -> word_filter.get())
        .build()
    );
    private final Setting<String> replacement = sgGeneral.add(new StringSetting.Builder()
        .name("WordFilter: filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("@$#!?&")
        .visible(() -> !woblox.get() && word_filter.get())
        .build()
    );
    private final Setting<String> roblock = sgGeneral.add(new StringSetting.Builder()
        .name("WordFilter: roblox-like filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("#")
        .visible(() -> woblox.get() && word_filter.get())
        .build()
    );
    private final Setting<Boolean> death_notifier = sgGeneral.add(new BoolSetting.Builder()
        .name("Death Notifier")
        .description("Tells you when someone dies including their coordinates")
        .defaultValue(false)
        .build()
    );
    private boolean activated = false;
    @Override
    public void onActivate() {
        banstuff();
        if(!word_filter.get()){return;}
        if(activated){return;}
        MeteorClient.EVENT_BUS.subscribe(this);
        info("Subscribed! Hit that bell too.");
        activated = true;
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        if(word_filter.get()){return;}
        if(!isActive()){return;}
        String[] datshit = event.message.split(" ");
        String message = "";
        for(int i = 0; i < datshit.length; i++){
            for(int j = 0; j < messages.get().size(); j++){
                if(datshit[i].toLowerCase().contains(messages.get().get(j).toLowerCase())) {
                    if (woblox.get()) {
                        String temp = "";
                        for(int k = 0; k < datshit[i].length(); k++){
                            temp += roblock.get();
                        }
                        datshit[i] = temp;
                    } else {
                        datshit[i] = replacement.get();
                    }
                }
            }
        }
        for(int i = 0; i < datshit.length; i++){
            message += datshit[i] + " ";
        }
        event.message = message.trim();
    }
    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        banstuff();
        if(event.packet instanceof EntitiesDestroyS2CPacket packet && death_notifier.get()){
            List<Integer> entityIds = packet.getEntityIds();
            for(int entityId : entityIds){
                assert mc.world != null;
                Entity entity = mc.world.getEntityById(entityId);
                assert entity != null;
                if(!(entity instanceof PlayerEntity) && !(entity instanceof OtherClientPlayerEntity)){return;}
                if(entity == mc.player){return;}
                String[] EntityString = entity.toString().split(",");
                /* for(int i = 0; i < EntityString.length; i++){
                    if(!roundValues.get()){return;}
                    EntityString[i] = EntityString[i].replaceAll("...$", "");
                } */
                info("Player " + entity.toString().split("'")[1] + " died at X:" + EntityString[2].replace("x=", "") + ", Y:" + EntityString[3].replace("y=", "") + ", Z:" + EntityString[4].replace("z=", "").substring(0, EntityString[4].indexOf("]") - 2));
            }
        }
    }
}
