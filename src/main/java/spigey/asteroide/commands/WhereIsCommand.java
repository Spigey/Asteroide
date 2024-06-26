package spigey.asteroide.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class WhereIsCommand extends Command {
    public WhereIsCommand() {
        super("whereis", "Makes you look at a specified player");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("player", PlayerListEntryArgumentType.create()).executes(context -> {
            GameProfile profile = PlayerListEntryArgumentType.get(context).getProfile();
            if((profile == null) || mc.world == null) {error("Player not found."); return SINGLE_SUCCESS;}
            for(Entity entity : mc.world.getEntities()){
                if(!(entity instanceof PlayerEntity)) continue;
                if(util.withoutStyle(entity.getName()).equals(profile.getName())){
                    ChatUtils.sendMsg(Text.of(String.format("§7Player found at §cX: %.0f§7, §aY: %.0f§7, §9Z: %.0f", entity.getX(), entity.getY(), entity.getZ())));
                    // double yaw = Math.toDegrees(Math.atan2((entity.getZ() - mc.player.getZ()), (entity.getX() - mc.player.getX())));
                    // double pitch = Math.max(-90, Math.min(90, Math.toDegrees(Math.atan2(entity.getY() - mc.player.getY(), Math.sqrt((entity.getX() - mc.player.getX()) * (entity.getX() - mc.player.getX()) + (entity.getZ() - mc.player.getZ()) * (entity.getZ() - mc.player.getZ()))))));
                    assert mc.player != null;
                    mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(entity.getX(), entity.getY() + 1.62, entity.getZ()));
                    AsteroideAddon.lastPos = new double[]{entity.getX(), entity.getY(), entity.getZ()};
                    return SINGLE_SUCCESS;
                }
            }
            ChatUtils.sendMsg(Text.of("§cPlayer not found, is it too far away?"));
            return SINGLE_SUCCESS;
        }));
    }
}
