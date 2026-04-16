package com.feed_the_beast.mods.money.command;

import com.feed_the_beast.mods.money.FTBMoney;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Command to sell held item to the system
 */
public class CommandSell extends CommandBase
{
	@Override
	public String getName()
	{
		return "sell";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.ftbmoney.sell.usage";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ItemStack heldItem = player.getHeldItemMainhand();

		if (heldItem.isEmpty())
		{
			throw new CommandException("commands.ftbmoney.sell.no_item");
		}

		// This command is a placeholder - actual selling should be done through the shop GUI
		// This just shows the held item info
		player.sendMessage(new TextComponentTranslation("commands.ftbmoney.sell.holding", 
			heldItem.getDisplayName(), 
			heldItem.getCount(),
			FTBMoney.moneyString(0)));
		player.sendMessage(new TextComponentTranslation("commands.ftbmoney.sell.use_shop"));
	}
}
