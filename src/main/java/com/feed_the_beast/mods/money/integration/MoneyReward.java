package com.feed_the_beast.mods.money.integration;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.icon.Icon;
import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftbquests.net.MessageDisplayRewardToast;
import com.feed_the_beast.ftbquests.quest.Quest;
import com.feed_the_beast.ftbquests.quest.reward.Reward;
import com.feed_the_beast.ftbquests.quest.reward.RewardType;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.FTBMoneyConfig;
import com.feed_the_beast.mods.money.FloatMoneyHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author LatvianModder
 */
public class MoneyReward extends Reward
{
	public static RewardType TYPE;

	public long value = 1L;
	public int randomBonus = 0;
	public double valueDouble = 1.0;
	public double randomBonusDouble = 0.0;

	public MoneyReward(Quest quest)
	{
		super(quest);
	}

	@Override
	public RewardType getType()
	{
		return TYPE;
	}

	@Override
	public void writeData(NBTTagCompound nbt)
	{
		if (FTBMoneyConfig.general.use_double_precision)
		{
			nbt.setDouble("ftb_money_double", valueDouble);

			if (randomBonusDouble > 0.0)
			{
				nbt.setDouble("random_bonus_double", randomBonusDouble);
			}
		}
		else
		{
			nbt.setLong("ftb_money", value);

			if (randomBonus > 0)
			{
				nbt.setInteger("random_bonus", randomBonus);
			}
		}
	}

	@Override
	public void readData(NBTTagCompound nbt)
	{
		if (nbt.hasKey("ftb_money_double"))
		{
			valueDouble = FloatMoneyHelper.normalize(nbt.getDouble("ftb_money_double"));
			randomBonusDouble = FloatMoneyHelper.normalize(nbt.getDouble("random_bonus_double"));
			value = FloatMoneyHelper.toInternal(valueDouble);
			randomBonus = (int) FloatMoneyHelper.toInternal(randomBonusDouble);
		}
		else
		{
			value = nbt.getLong("ftb_money");
			randomBonus = nbt.getInteger("random_bonus");
			valueDouble = FloatMoneyHelper.fromInternal(value);
			randomBonusDouble = FloatMoneyHelper.fromInternal(randomBonus);
		}
	}

	@Override
	public void writeNetData(DataOut data)
	{
		super.writeNetData(data);
		data.writeBoolean(FTBMoneyConfig.general.use_double_precision);
		
		if (FTBMoneyConfig.general.use_double_precision)
		{
			data.writeDouble(valueDouble);
			data.writeDouble(randomBonusDouble);
		}
		else
		{
			data.writeVarLong(value);
			data.writeVarInt(randomBonus);
		}
	}

	@Override
	public void readNetData(DataIn data)
	{
		super.readNetData(data);
		boolean isDouble = data.readBoolean();
		
		if (isDouble)
		{
			valueDouble = FloatMoneyHelper.normalize(data.readDouble());
			randomBonusDouble = FloatMoneyHelper.normalize(data.readDouble());
			value = FloatMoneyHelper.toInternal(valueDouble);
			randomBonus = (int) FloatMoneyHelper.toInternal(randomBonusDouble);
		}
		else
		{
			value = data.readVarLong();
			randomBonus = data.readVarInt();
			valueDouble = FloatMoneyHelper.fromInternal(value);
			randomBonusDouble = FloatMoneyHelper.fromInternal(randomBonus);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getConfig(ConfigGroup config)
	{
		super.getConfig(config);
		config.addLong("value", () -> value, v -> {
			value = v;
			valueDouble = FloatMoneyHelper.fromInternal(v);
		}, 1L, 1L, Long.MAX_VALUE).setDisplayName(new TextComponentTranslation("ftbquests.reward.ftbmoney.money"));
		config.addInt("random_bonus", () -> randomBonus, v -> {
			randomBonus = v;
			randomBonusDouble = FloatMoneyHelper.fromInternal(v);
		}, 0, 0, Integer.MAX_VALUE).setDisplayName(new TextComponentTranslation("ftbquests.reward.random_bonus"));
	}

	@Override
	public void claim(EntityPlayerMP player, boolean notify)
	{
		double addedDouble;

		if (FTBMoneyConfig.general.use_double_precision)
		{
			addedDouble = FloatMoneyHelper.add(valueDouble, player.world.rand.nextDouble() * randomBonusDouble);
		}
		else
		{
			long added = value + player.world.rand.nextInt(randomBonus + 1);
			addedDouble = FloatMoneyHelper.fromInternal(added);
		}

		FTBMoney.addMoneyDouble(player, addedDouble);

		if (notify)
		{
			new MessageDisplayRewardToast(id, FTBMoney.moneyComponentAuto(addedDouble), Icon.getIcon("ftbmoney:textures/beastcoinmini.png")).sendTo(player);
		}
	}

	@Override
	public String getAltTitle()
	{
		if (FTBMoneyConfig.general.use_double_precision)
		{
			if (randomBonusDouble > 0.0)
			{
				return TextFormatting.GOLD + FloatMoneyHelper.format(valueDouble) + " - " + FloatMoneyHelper.format(FloatMoneyHelper.add(valueDouble, randomBonusDouble));
			}
			return TextFormatting.GOLD + FloatMoneyHelper.format(valueDouble);
		}

		if (randomBonus > 0)
		{
			return TextFormatting.GOLD + FTBMoney.moneyString(value) + " - " + FTBMoney.moneyString(value + randomBonus);
		}

		return TextFormatting.GOLD + FTBMoney.moneyString(value);
	}

	@Override
	public String getButtonText()
	{
		if (FTBMoneyConfig.general.use_double_precision)
		{
			if (randomBonusDouble > 0.0)
			{
				return String.format("%.2f-%.2f", valueDouble, FloatMoneyHelper.add(valueDouble, randomBonusDouble));
			}
			return String.format("%.2f", valueDouble);
		}

		if (randomBonus > 0)
		{
			return randomBonus + "-" + Long.toUnsignedString(value + randomBonus);
		}

		return Long.toUnsignedString(value);
	}

	public double getValueDouble()
	{
		return FTBMoneyConfig.general.use_double_precision ? valueDouble : FloatMoneyHelper.fromInternal(value);
	}

	public void setValueDouble(double val)
	{
		valueDouble = FloatMoneyHelper.normalize(val);
		value = FloatMoneyHelper.toInternal(val);
	}

	public double getRandomBonusDouble()
	{
		return FTBMoneyConfig.general.use_double_precision ? randomBonusDouble : FloatMoneyHelper.fromInternal(randomBonus);
	}

	public void setRandomBonusDouble(double val)
	{
		randomBonusDouble = FloatMoneyHelper.normalize(val);
		randomBonus = (int) FloatMoneyHelper.toInternal(val);
	}
}