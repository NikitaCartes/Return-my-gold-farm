package xyz.nikitacartes.returnmygoldfarm.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
//? if >=1.21.11 {
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
//?} else {
/*import net.minecraft.world.entity.monster.ZombifiedPiglin;
*///?}
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglin.class)
public abstract class ZombifiedPiglinMixin {
    @Unique
    private final ZombifiedPiglin piglin = (ZombifiedPiglin) (Object) this;

    // 1.21.5 (25w02a) dropped both blocks below from vanilla ZombifiedPiglin, which is what
    // broke gold farms. Each injection restores one of them verbatim.
    @Inject(method = "customServerAiStep(Lnet/minecraft/server/level/ServerLevel;)V", at = @At("RETURN"))
    private void returnMyGoldFarm$keepHurtByPlayer(ServerLevel level, CallbackInfo ci) {
        if (piglin.isAngry()) {
            ((LivingEntityAccessor) (Object) piglin).setLastHurtByPlayerMemoryTime(piglin.tickCount);
        }
    }

    @Inject(method = "setTarget(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("RETURN"))
    private void returnMyGoldFarm$recordHurtByPlayer(LivingEntity target, CallbackInfo ci) {
        if (target instanceof Player player) {
            piglin.setLastHurtByPlayer(player, piglin.tickCount);
        }
    }
}
