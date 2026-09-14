package com.miku.coordinateselector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetTargetPacket {
    private final BlockPos stabilizerPos;
    private final int targetX;
    private final int targetY;
    private final int targetZ;

    public SetTargetPacket(BlockPos stabilizerPos, int targetX, int targetY, int targetZ) {
        this.stabilizerPos = stabilizerPos;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
    }

    public SetTargetPacket(FriendlyByteBuf buf) {
        this.stabilizerPos = buf.readBlockPos();
        this.targetX = buf.readInt();
        this.targetY = buf.readInt();
        this.targetZ = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.stabilizerPos);
        buf.writeInt(this.targetX);
        buf.writeInt(this.targetY);
        buf.writeInt(this.targetZ);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            // 距离与物品校验
            if (player.distanceToSqr(this.stabilizerPos.getX() + 0.5, this.stabilizerPos.getY() + 0.5, this.stabilizerPos.getZ() + 0.5) > 64.0) {
                return;
            }

            ItemStack held = player.getMainHandItem();
            if (!held.is(ModItems.MIKU_LOCATOR.get())) {
                return;
            }

            ServerLevel level = player.serverLevel();
            BlockEntity be = level.getBlockEntity(this.stabilizerPos);
            if (be == null) return;

            String currentDim = level.dimension().location().toString();

            // 写入 Wormhole 稳定器的目标槽位（注入 target0）
            CompoundTag tag = be.saveWithFullMetadata();
            ListTag targetsList = new ListTag();
            CompoundTag target0 = new CompoundTag();
            target0.putString("dimension", currentDim);
            target0.putInt("x", this.targetX);
            target0.putInt("y", this.targetY);
            target0.putInt("z", this.targetZ);
            target0.putString("name", "Target (" + this.targetX + ", " + this.targetY + ", " + this.targetZ + ")");
            targetsList.add(target0);

            tag.put("targets", targetsList);
            tag.putInt("selectedTarget", 0);
            tag.putInt("target", 0);
            // 兼容辅助字段
            tag.putInt("targetX", this.targetX);
            tag.putInt("targetY", this.targetY);
            tag.putInt("targetZ", this.targetZ);
            tag.putString("targetDimension", currentDim);

            be.load(tag);
            be.setChanged();
            level.sendBlockUpdated(this.stabilizerPos, be.getBlockState(), be.getBlockState(), 3);

            // 扣除 1 点耐久，达到 10 次损毁
            held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));

            // 音效反馈
            level.playSound(null, this.stabilizerPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.2F);
            level.playSound(null, this.stabilizerPos, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 0.6F, 1.5F);

            // 紫色传送门与女巫流光粒子
            double bx = this.stabilizerPos.getX() + 0.5;
            double by = this.stabilizerPos.getY() + 0.5;
            double bz = this.stabilizerPos.getZ() + 0.5;
            level.sendParticles(ParticleTypes.PORTAL, bx, by, bz, 50, 0.4, 0.4, 0.4, 0.8);
            level.sendParticles(ParticleTypes.WITCH, bx, by, bz, 20, 0.3, 0.3, 0.3, 0.1);

            player.sendSystemMessage(Component.literal("§a✨ 虫洞目标锁定成功！§f目标点：§eX:" + this.targetX + " Y:" + this.targetY + " Z:" + this.targetZ));
        });
        ctx.get().setPacketHandled(true);
    }
}
