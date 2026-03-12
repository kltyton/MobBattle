package com.kltyton.mob_battle.mixin.client.render.entity.player;

import com.kltyton.mob_battle.entity.player.IClientPlayerEntityAccessor;
import com.kltyton.mob_battle.network.packet.PlayerSkillPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntity.class)
@Implements(@Interface(iface = IClientPlayerEntityAccessor.class, prefix = "iClient$"))
public abstract class ClientPlayerEntityMixin extends LivingEntity {
    protected ClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
    public void iClient$clientSend(String message) {
        ClientPlayNetworking.send(new PlayerSkillPayload(message, this.getId()));
    }
    public void iClient$setPerson(int person) {
        if (person == 1) MinecraftClient.getInstance().options.setPerspective(Perspective.FIRST_PERSON);
        else if (person == 2) MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
        else MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_FRONT);
    }
}
