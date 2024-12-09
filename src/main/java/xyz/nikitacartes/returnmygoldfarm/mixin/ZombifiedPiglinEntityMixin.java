package xyz.nikitacartes.returnmygoldfarm.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombifiedPiglinEntity.class)
public abstract class ZombifiedPiglinEntityMixin {
    @Unique
    private final ZombifiedPiglinEntity piglin = (ZombifiedPiglinEntity) (Object) this;

    @Inject(method = "mobTick()V", at = @At(value = "RETURN"))
    private void addLastHurtByPlayerTime(CallbackInfo ci) {
        if (piglin.hasAngerTime()) {
            piglin.playerHitTimer = piglin.age;
        }
    }

    @Inject(method = "setTarget(Lnet/minecraft/entity/LivingEntity;)V", at = @At(value = "RETURN"))
    private void addTarget (LivingEntity target, CallbackInfo ci) {
        if (target instanceof PlayerEntity) {
            piglin.setAttacking((PlayerEntity)target);
        }
    }
}
