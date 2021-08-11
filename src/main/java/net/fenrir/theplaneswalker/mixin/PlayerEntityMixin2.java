package net.fenrir.theplaneswalker.mixin;

import net.fenrir.theplaneswalker.origins.TCPowers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin2 extends LivingEntity {

    float lastFallDistance = 0;
    @Shadow
    @Final
    private PlayerAbilities abilities;

    protected PlayerEntityMixin2(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void increaseStat(Identifier stat, int amount);

    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    public void handleDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (TCPowers.FLYING.isActive((PlayerEntity) (Object) this)) {
            lastFallDistance = 0;
            if (fallDistance >= 2.0F) {
                this.increaseStat(Stats.FALL_ONE_CM, (int) Math.round((double) fallDistance * 100.0D));
            }

            cir.setReturnValue(super.handleFallDamage(fallDistance, damageMultiplier, damageSource));
        }
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void cancelNoFallDistance(Vec3d movementInput, CallbackInfo ci) {
        if (TCPowers.FLYING.isActive((PlayerEntity) (Object) this)) {
            if (this.abilities.flying) {
                if (this.fallDistance > lastFallDistance) {
                    lastFallDistance = this.fallDistance;
                }
            } else {
                if (lastFallDistance > 0 && this.fallDistance > 0) {
                    this.fallDistance += lastFallDistance;
                    lastFallDistance = 0;
                }
            }
        }
    }
}
