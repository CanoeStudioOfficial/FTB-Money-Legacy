package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author LatvianModder
 */
public class MessageSell extends MessageToServer
{
	private int tab;
	private int id;
	private int count;

	public MessageSell()
	{
	}

	public MessageSell(ShopEntry e, int c)
	{
		tab = e.tab.getIndex();
		id = e.getIndex();
		count = c;
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeVarInt(tab);
		data.writeVarInt(id);
		data.writeVarInt(count);
	}

	@Override
	public void readData(DataIn data)
	{
		tab = data.readVarInt();
		id = data.readVarInt();
		count = data.readVarInt();
	}

	@Override
	public void onMessage(EntityPlayerMP player)
	{
		if (Shop.SERVER == null || tab >= Shop.SERVER.tabs.size())
		{
			return;
		}

		ShopTab t = Shop.SERVER.tabs.get(tab);

		if (id >= t.entries.size())
		{
			return;
		}

		ShopEntry entry = t.entries.get(id);

		if (entry.disabledServer && player.getServer().isDedicatedServer())
		{
			return;
		}

		// Check if entry has sell price
		if (entry.sell <= 0L)
		{
			return;
		}

		long money = FTBMoney.getMoney(player);
		ItemStack requiredStack = entry.stack;
		int requiredCount = requiredStack.getCount() * count;

		// Count available items in player inventory
		int availableCount = 0;
		for (ItemStack stack : player.inventory.mainInventory)
		{
			if (!stack.isEmpty() && stack.isItemEqual(requiredStack) && ItemStack.areItemStackTagsEqual(stack, requiredStack))
			{
				availableCount += stack.getCount();
			}
		}

		if (availableCount < requiredCount)
		{
			return;
		}

		// Remove items from inventory
		int remainingToRemove = requiredCount;
		AtomicLong totalSellPrice = new AtomicLong(0L);
		AtomicInteger actuallyRemoved = new AtomicInteger(0);

		for (int i = 0; i < player.inventory.mainInventory.size() && remainingToRemove > 0; i++)
		{
			ItemStack stack = player.inventory.mainInventory.get(i);
			if (!stack.isEmpty() && stack.isItemEqual(requiredStack) && ItemStack.areItemStackTagsEqual(stack, requiredStack))
			{
				int removeFromStack = Math.min(stack.getCount(), remainingToRemove);
				stack.shrink(removeFromStack);
				remainingToRemove -= removeFromStack;
				actuallyRemoved.addAndGet(removeFromStack);

				if (stack.isEmpty())
				{
					player.inventory.mainInventory.set(i, ItemStack.EMPTY);
				}
			}
		}

		// Calculate and give money
		int stacksSold = actuallyRemoved.get() / requiredStack.getCount();
		totalSellPrice.set(stacksSold * entry.sell);
		FTBMoney.setMoney(player, money + totalSellPrice.get());
	}
}
