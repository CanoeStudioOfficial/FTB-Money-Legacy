package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToServer;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.ftbquests.quest.ServerQuestFile;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.FloatMoneyHelper;
import com.feed_the_beast.mods.money.shop.Shop;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import com.feed_the_beast.mods.money.shop.ShopTab;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author LatvianModder
 */
public class MessageBuy extends MessageToServer
{
	private int tab;
	private int id;
	private int count;

	public MessageBuy()
	{
	}

	public MessageBuy(ShopEntry e, int c)
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
		ShopTab t = Shop.SERVER.tabs.get(tab);
		ShopEntry entry = t.entries.get(id);

		if (entry.disabledServer && player.getServer().isDedicatedServer())
		{
			return;
		}

		double money = FTBMoney.getMoneyAuto(player);
		double buyPrice = entry.getBuyPrice();
		double sellPrice = entry.getSellPrice();

		if (buyPrice >= 0.01)
		{
			double totalCost = FloatMoneyHelper.multiply(buyPrice, count);
			
			if (FloatMoneyHelper.compare(money, totalCost) >= 0 && entry.isUnlocked(Objects.requireNonNull(ServerQuestFile.INSTANCE.getData(player)).getFile()))
			{
				ItemStack stack = entry.stack;

				if (stack.getCount() * count <= stack.getMaxStackSize())
				{
					ItemHandlerHelper.giveItemToPlayer(player, ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() * count));
				}
				else
				{
					for (int i = 0; i < count; i++)
					{
						ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
					}
				}

				FTBMoney.removeMoneyAuto(player, totalCost);
			}
		}
		else if (sellPrice >= 0.01)
		{
			Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
			int slot = 0;
			int current_items = 0;
			
			for (ItemStack next : player.inventory.mainInventory)
			{
				if (next != null && next.isItemEqual(entry.stack))
				{
					current_items += next.getCount();
					items.put(slot, next);
				}
				slot++;
			}

			int requiredItems = entry.stack.getCount() * count;
			
			if (current_items >= requiredItems)
			{
				int remaining_items = requiredItems;
				int sold = 0;
				
				for (Map.Entry<Integer, ItemStack> entrys : items.entrySet())
				{
					ItemStack item = entrys.getValue();

					if (remaining_items <= 0)
					{
						break;
					}

					if (item.getCount() <= remaining_items)
					{
						sold += item.getCount();
						remaining_items -= item.getCount();
						item.shrink(item.getCount());
					}
					else
					{
						sold += remaining_items;
						item.shrink(remaining_items);
						remaining_items = 0;
					}
				}

				double totalEarnings = FloatMoneyHelper.multiply(sellPrice, sold / (double) entry.stack.getCount());
				FTBMoney.addMoneyAuto(player, totalEarnings);
			}
		}
		else if (FloatMoneyHelper.isZero(buyPrice) && FloatMoneyHelper.isZero(sellPrice))
		{
			ItemStack stack = entry.stack;

			if (stack.getCount() * count <= stack.getMaxStackSize())
			{
				ItemHandlerHelper.giveItemToPlayer(player, ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() * count));
			}
			else
			{
				for (int i = 0; i < count; i++)
				{
					ItemHandlerHelper.giveItemToPlayer(player, stack.copy());
				}
			}
		}
	}
}