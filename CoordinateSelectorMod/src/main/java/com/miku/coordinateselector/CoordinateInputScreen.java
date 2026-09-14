package com.miku.coordinateselector;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class CoordinateInputScreen extends Screen {
    private final BlockPos stabilizerPos;
    private EditBox xBox;
    private EditBox yBox;
    private EditBox zBox;
    private Component errorMessage = null;

    public CoordinateInputScreen(BlockPos stabilizerPos) {
        super(Component.literal("初音未来 · 空间坐标指定"));
        this.stabilizerPos = stabilizerPos;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // X 输入框（初始为空，带浅灰占位提示）
        this.xBox = new EditBox(this.font, centerX - 100, centerY - 45, 200, 20, Component.literal("X"));
        this.xBox.setHint(Component.literal("输入目标 X 坐标...").withStyle(ChatFormatting.DARK_GRAY));
        this.xBox.setMaxLength(12);
        this.addRenderableWidget(this.xBox);

        // Y 输入框
        this.yBox = new EditBox(this.font, centerX - 100, centerY - 15, 200, 20, Component.literal("Y"));
        this.yBox.setHint(Component.literal("输入目标 Y 坐标...").withStyle(ChatFormatting.DARK_GRAY));
        this.yBox.setMaxLength(12);
        this.addRenderableWidget(this.yBox);

        // Z 输入框
        this.zBox = new EditBox(this.font, centerX - 100, centerY + 15, 200, 20, Component.literal("Z"));
        this.zBox.setHint(Component.literal("输入目标 Z 坐标...").withStyle(ChatFormatting.DARK_GRAY));
        this.zBox.setMaxLength(12);
        this.addRenderableWidget(this.zBox);

        // 【确定】按钮
        this.addRenderableWidget(Button.builder(Component.literal("确定").withStyle(ChatFormatting.GREEN), btn -> {
            this.onConfirm();
        }).bounds(centerX - 100, centerY + 48, 95, 20).build());

        // 【取消】按钮
        this.addRenderableWidget(Button.builder(Component.literal("取消").withStyle(ChatFormatting.RED), btn -> {
            this.onClose();
        }).bounds(centerX + 5, centerY + 48, 95, 20).build());
    }

    private void onConfirm() {
        String xs = this.xBox.getValue().trim();
        String ys = this.yBox.getValue().trim();
        String zs = this.zBox.getValue().trim();

        if (xs.isEmpty() || ys.isEmpty() || zs.isEmpty()) {
            this.errorMessage = Component.literal("§c请填满全部三个坐标框！");
            return;
        }

        try {
            int x = Integer.parseInt(xs);
            int y = Integer.parseInt(ys);
            int z = Integer.parseInt(zs);

            // 发送网络包给服务端注入数据并扣除耐久
            ModNetwork.CHANNEL.sendToServer(new SetTargetPacket(this.stabilizerPos, x, y, z));
            this.onClose();
        } catch (NumberFormatException e) {
            this.errorMessage = Component.literal("§c坐标必须是合法的整数！");
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // 背景半透明暗底与初音青色边框
        guiGraphics.fill(centerX - 115, centerY - 72, centerX + 115, centerY + 78, 0xD510141E);
        guiGraphics.renderOutline(centerX - 115, centerY - 72, 230, 150, 0xFF39C5BB);

        // 标题
        guiGraphics.drawCenteredString(this.font, Component.literal("§b✦ 指定虫洞目标坐标 ✦"), centerX, centerY - 64, 0xFFFFFF);

        // 错误提示
        if (this.errorMessage != null) {
            guiGraphics.drawCenteredString(this.font, this.errorMessage, centerX, centerY + 36, 0xFF5555);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
