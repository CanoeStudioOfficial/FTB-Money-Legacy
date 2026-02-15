package com.feed_the_beast.mods.money;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import com.feed_the_beast.mods.ftbmoney.Tags;
import com.feed_the_beast.mods.money.api.FTBMoneyAPI;
import com.feed_the_beast.mods.money.command.CommandImportItemsFromChest;
import com.feed_the_beast.mods.money.command.CommandMoney;
import com.feed_the_beast.mods.money.command.CommandPay;
import com.feed_the_beast.mods.money.command.CommandSetMoney;
import com.feed_the_beast.mods.money.net.FTBMoneyNetHandler;
import com.feed_the_beast.mods.money.net.MessageUpdateMoney;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = Tags.MOD_ID,
		name =Tags.MOD_NAME,
		version = Tags.VERSION,
		acceptableRemoteVersions = "*",
		dependencies = FTBLib.THIS_DEP + ";required-after:ftbquests"
)
public class FTBMoney
{
    public static final String MOD_ID = Tags.MOD_ID;

	public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

	@SidedProxy(serverSide = "com.feed_the_beast.mods.money.FTBMoneyCommon", clientSide = "com.feed_the_beast.mods.money.FTBMoneyClient")
	public static FTBMoneyCommon PROXY;

	@Mod.EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		FTBMoneyNetHandler.init();
		FTBMoneyAPI.init();
		PROXY.preInit();
	}

	@Mod.EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandMoney());
		event.registerServerCommand(new CommandPay());
		event.registerServerCommand(new CommandSetMoney());
		event.registerServerCommand(new CommandImportItemsFromChest());
	}

	public static long getMoney(EntityPlayer player)
	{
		long money = NBTUtils.getPersistedData(player, false).getLong("ftb_money");

		if (money == 0L)
		{
			money = player.getEntityData().getLong("ftb_money");

			if (money > 0L)
			{
				NBTUtils.getPersistedData(player, true).setLong("ftb_money", money);
				player.getEntityData().removeTag("ftb_money");
			}
		}

		return money;
	}

	public static void setMoney(EntityPlayer player, long money)
	{
		if (money <= 0L)
		{
			NBTUtils.getPersistedData(player, false).removeTag("ftb_money");
		}
		else
		{
			NBTUtils.getPersistedData(player, true).setLong("ftb_money", money);
		}

		if (!player.world.isRemote)
		{
			new MessageUpdateMoney(money).sendTo((EntityPlayerMP) player);
		}
	}
	public static double getMoneyDouble(EntityPlayer player)
	{
		return FloatMoneyHelper.fromInternal(getMoney(player));
	}

	public static void setMoneyDouble(EntityPlayer player, double money)
	{
		setMoney(player, FloatMoneyHelper.toInternal(money));
	}

	public static String moneyStringAdd(long money)
	{
		return String.format("\u0398 +%,d", money);
	}

	public static String moneyStringAddDouble(double money)
	{
		return "\u0398 +" + FloatMoneyHelper.format(money, false);
	}

	public static String moneyString(long money)
	{
		return String.format("\u0398 %,d", money);
	}

	public static String moneyStringDouble(double money)
	{
		return FloatMoneyHelper.format(money);
	}

	public static ITextComponent moneyComponent(long money)
	{
		ITextComponent component = new TextComponentString(moneyString(money));
		component.getStyle().setColor(TextFormatting.GOLD);
		return component;
	}

	public static ITextComponent moneyComponentDouble(double money)
	{
		ITextComponent component = new TextComponentString(moneyStringDouble(money));
		component.getStyle().setColor(TextFormatting.GOLD);
		return component;
	}

	public static boolean hasMoney(EntityPlayer player, long amount)
	{
		return getMoney(player) >= amount;
	}

	public static boolean hasMoneyDouble(EntityPlayer player, double amount)
	{
		return FloatMoneyHelper.compare(getMoneyDouble(player), amount) >= 0;
	}

	public static long addMoney(EntityPlayer player, long amount)
	{
		long newMoney = getMoney(player) + amount;
		setMoney(player, Math.max(0L, newMoney));
		return getMoney(player);
	}

	public static double addMoneyDouble(EntityPlayer player, double amount)
	{
		double newMoney = FloatMoneyHelper.add(getMoneyDouble(player), amount);
		newMoney = FloatMoneyHelper.clamp(newMoney, 0.0, FloatMoneyHelper.MAX_VALUE);
		setMoneyDouble(player, newMoney);
		return getMoneyDouble(player);
	}

	public static long removeMoney(EntityPlayer player, long amount)
	{
		long current = getMoney(player);
		setMoney(player, Math.max(0L, current - amount));
		return getMoney(player);
	}

	public static double removeMoneyDouble(EntityPlayer player, double amount)
	{
		double current = getMoneyDouble(player);
		double newMoney = FloatMoneyHelper.subtract(current, amount);
		newMoney = FloatMoneyHelper.clamp(newMoney, 0.0, FloatMoneyHelper.MAX_VALUE);
		setMoneyDouble(player, newMoney);
		return getMoneyDouble(player);
	}

	public static boolean tryRemoveMoney(EntityPlayer player, long amount)
	{
		if (!hasMoney(player, amount))
		{
			return false;
		}
		removeMoney(player, amount);
		return true;
	}

	public static boolean tryRemoveMoneyDouble(EntityPlayer player, double amount)
	{
		if (!hasMoneyDouble(player, amount))
		{
			return false;
		}
		removeMoneyDouble(player, amount);
		return true;
	}
}