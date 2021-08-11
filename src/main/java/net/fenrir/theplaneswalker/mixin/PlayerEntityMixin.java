package net.fenrir.theplaneswalker.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntityMixin {

    @Shadow
    public abstract boolean isCreative();

    @Inject(method = "addExperience", at = @At("HEAD"), cancellable = true)
    public void noExperience1(int experience, CallbackInfo ci) {
        if (!this.isCreative()) {
            if (TCPowers.MALFORMED_SOUL.isActive((PlayerEntity) (Object) this)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "addExperienceLevels", at = @At("HEAD"), cancellable = true)
    public void noExperience2(int experience, CallbackInfo ci) {
        if (!this.isCreative()) {
            if (TCPowers.MALFORMED_SOUL.isActive((PlayerEntity) (Object) this)) {
                ci.cancel();
            }
        }
    }

    @Override
    public void noGliding(CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.OVERSPECIALIZATION.isActive((LivingEntity) (Object) this)) {
            cir.setReturnValue(false);
        }
    }


    @Override
    public void stopPushingAway(Entity entity, CallbackInfo ci) {
        if (TCPowers.PHASESHIFT.isActive((LivingEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Override
    public void stopPushingAwayFrom(Entity entity, CallbackInfo ci) {
        if (TCPowers.PHASESHIFT.isActive((LivingEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "attack",
            at = @At("HEAD"),
            cancellable = true
    )
    public void noAttack(Entity target, CallbackInfo ci) {
        if (TCPowers.PHASESHIFT.isActive(target) || TCPowers.PHASESHIFT.isActive((PlayerEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(
            method = "interact",
            at = @At("HEAD"),
            cancellable = true
    )
    public void noInteract(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (TCPowers.PHASESHIFT.isActive(entity) || TCPowers.PHASESHIFT.isActive((PlayerEntity) (Object) this)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Environment(EnvType.SERVER)
    @Override
    public void invisible(CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.PHASESHIFT.isActive((PlayerEntity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public void invisibleTo(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.PHASESHIFT.isActive((PlayerEntity) (Object) this) || TCPowers.PHASESHIFT.isActive(player)) {
            cir.setReturnValue(true);
        }
    }

}
