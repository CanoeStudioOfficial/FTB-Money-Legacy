package com.feed_the_beast.mods.money.command;

import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.FloatMoneyHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandSetMoney extends CommandBase
{
	@Override
	public String getName()
	{
		return "setmoney";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.setmoney.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 0)
		{
			return Collections.emptyList();
		}
		else if (isUsernameIndex(args, args.length - 1))
		{
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length < 2)
		{
			throw new WrongUsageException(getUsage(sender));
		}

		EntityPlayerMP player = getPlayer(server, sender, args[0]);
		double money = FTBMoney.getMoneyDouble(player);

		ITextComponent playerName = sender.getDisplayName();
		playerName.getStyle().setColor(TextFormatting.BLUE);

		String amountStr = args[1];
		
		if (amountStr.startsWith("~"))
		{
			double add = FloatMoneyHelper.parse(amountStr.substring(1));
			
			if (add < 0)
			{
				add = FloatMoneyHelper.clamp(add, -money, FloatMoneyHelper.MAX_VALUE);
			}

			if (!FloatMoneyHelper.isZero(add))
			{
				FTBMoney.addMoneyDouble(player, add);
			}

			ITextComponent amountComponent = FTBMoney.moneyComponentDouble(Math.abs(add));
			sender.sendMessage(new TextComponentString("").appendSibling(playerName).appendText(add < 0.0 ? " - " : " + ").appendSibling(amountComponent));

			if (player != sender)
			{
				player.sendStatusMessage(new TextComponentString("").appendSibling(playerName).appendText(add < 0.0 ? " - " : " + ").appendSibling(amountComponent), true);
			}
		}
		else
		{
			double set = FloatMoneyHelper.parse(amountStr);

			if (!FloatMoneyHelper.equals(set, money))
			{
				FTBMoney.setMoneyDouble(player, set);
			}

			ITextComponent amountComponent = FTBMoney.moneyComponentDouble(set);
			sender.sendMessage(new TextComponentString("").appendSibling(playerName).appendText(" = ").appendSibling(amountComponent));

			if (player != sender)
			{
				player.sendStatusMessage(new TextComponentString("").appendSibling(playerName).appendText(" = ").appendSibling(amountComponent), true);
			}
		}
	}
}