package com.miku.coordinateselector;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MikuLocatorItem extends Item {
    public static final int MAX_USES = 10;

    public MikuLocatorItem(Properties properties) {
        super(properties.durability(MAX_USES));
    }

    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int remaining = MAX_USES - stack.getDamageValue();
        tooltip.add(Component.literal("剩余使用次数: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(remaining + "/" + MAX_USES).withStyle(ChatFormatting.AQUA)));
        
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("用于为虫洞稳定器指定传送目标。").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("与虫洞稳定器右键使用，打开坐标输入界面。").withStyle(ChatFormatting.GRAY));
        
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("—— 想去的地方，我会带你抵达。").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.literal("               —— 初音未来").withStyle(ChatFormatting.LIGHT_PURPLE));
        
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("不可修复  不可附魔").withStyle(ChatFormatting.RED));

        super.appendHoverText(stack, level, tooltip, flag);
    }
}
