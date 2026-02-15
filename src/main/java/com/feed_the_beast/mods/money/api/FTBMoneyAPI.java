package com.feed_the_beast.mods.money.api;

import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.FloatMoneyHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FTB Money API 实现类
 * 提供金钱系统的具体实现
 * 
 * <p>此类实现了IFTBMoneyAPI接口的所有方法，
 * 是FTB Money模组的主要API入口点。</p>
 * 
 * <p>使用方式：</p>
 * <pre>{@code
 * // 方式1：通过接口获取实例（推荐）
 * IFTBMoneyAPI api = IFTBMoneyAPI.getInstance();
 * 
 * // 方式2：直接获取实例
 * FTBMoneyAPI api = FTBMoneyAPI.getInstance();
 * 
 * // 方式3：检查是否初始化后获取
 * if (IFTBMoneyAPI.isInitialized()) {
 *     IFTBMoneyAPI api = IFTBMoneyAPI.getInstance();
 * }
 * }</pre>
 * 
 * @author FTB Money Team
 * @since 1.0.0
 */
public final class FTBMoneyAPI implements IFTBMoneyAPI
{
	private static FTBMoneyAPI instance;

	/**
	 * 获取API实例
	 * 
	 * @return FTB Money API实例，如果未初始化返回null
	 */
	@Nullable
	public static FTBMoneyAPI getInstance()
	{
		return instance;
	}

	/**
	 * 初始化API
	 * 此方法由FTBMoney模组在启动时自动调用
	 */
	public static void init()
	{
		if (instance == null)
		{
			instance = new FTBMoneyAPI();
		}
	}

	private FTBMoneyAPI()
	{
	}

	@Override
	public long getMoney(@Nullable EntityPlayer player)
	{
		if (player == null)
		{
			return 0L;
		}
		return FTBMoney.getMoney(player);
	}

	@Override
	public double getMoneyDouble(@Nullable EntityPlayer player)
	{
		if (player == null)
		{
			return 0.0;
		}
		return FloatMoneyHelper.fromInternal(FTBMoney.getMoney(player));
	}

	@Override
	public void setMoney(@Nonnull EntityPlayer player, long money)
	{
		FTBMoney.setMoney(player, Math.max(0L, money));
	}

	@Override
	public void setMoneyDouble(@Nonnull EntityPlayer player, double money)
	{
		long internal = FloatMoneyHelper.toInternal(money);
		FTBMoney.setMoney(player, internal);
	}

	@Override
	public long addMoney(@Nonnull EntityPlayer player, long amount)
	{
		long current = getMoney(player);
		long newAmount = Math.max(0L, current + amount);
		setMoney(player, newAmount);
		return newAmount;
	}

	@Override
	public double addMoneyDouble(@Nonnull EntityPlayer player, double amount)
	{
		double current = getMoneyDouble(player);
		double newAmount = FloatMoneyHelper.add(current, amount);
		newAmount = FloatMoneyHelper.clamp(newAmount, 0.0, FloatMoneyHelper.getMaxValue());
		setMoneyDouble(player, newAmount);
		return newAmount;
	}

	@Override
	public long removeMoney(@Nonnull EntityPlayer player, long amount)
	{
		long current = getMoney(player);
		long newAmount = Math.max(0L, current - amount);
		setMoney(player, newAmount);
		return newAmount;
	}

	@Override
	public double removeMoneyDouble(@Nonnull EntityPlayer player, double amount)
	{
		double current = getMoneyDouble(player);
		double newAmount = FloatMoneyHelper.subtract(current, amount);
		newAmount = FloatMoneyHelper.clamp(newAmount, 0.0, FloatMoneyHelper.getMaxValue());
		setMoneyDouble(player, newAmount);
		return newAmount;
	}

	@Override
	public boolean hasMoney(@Nullable EntityPlayer player, long amount)
	{
		if (player == null || amount <= 0L)
		{
			return false;
		}
		return getMoney(player) >= amount;
	}

	@Override
	public boolean hasMoneyDouble(@Nullable EntityPlayer player, double amount)
	{
		if (player == null || FloatMoneyHelper.isNegative(amount) || FloatMoneyHelper.isZero(amount))
		{
			return false;
		}
		return FloatMoneyHelper.compare(getMoneyDouble(player), amount) >= 0;
	}

	@Override
	public boolean tryRemoveMoney(@Nonnull EntityPlayer player, long amount)
	{
		if (!hasMoney(player, amount))
		{
			return false;
		}
		removeMoney(player, amount);
		return true;
	}

	@Override
	public boolean tryRemoveMoneyDouble(@Nonnull EntityPlayer player, double amount)
	{
		if (!hasMoneyDouble(player, amount))
		{
			return false;
		}
		removeMoneyDouble(player, amount);
		return true;
	}

	@Override
	@Nonnull
	public String formatMoney(long money)
	{
		return FTBMoney.moneyString(money);
	}

	@Override
	@Nonnull
	public String formatMoneyDouble(double money)
	{
		return FloatMoneyHelper.format(money);
	}

	@Override
	public long parseMoney(@Nullable String str)
	{
		if (str == null || str.trim().isEmpty())
		{
			return 0L;
		}
		
		String trimmed = str.trim();
		
		try
		{
			if (trimmed.startsWith("~"))
			{
				return Long.parseLong(trimmed.substring(1).replace(",", ""));
			}
			return Long.parseLong(trimmed.replace(",", ""));
		}
		catch (NumberFormatException e)
		{
			return (long) FloatMoneyHelper.parse(str);
		}
	}

	@Override
	public double parseMoneyDouble(@Nullable String str)
	{
		return FloatMoneyHelper.parse(str);
	}

	@Override
	public boolean transferMoney(@Nonnull EntityPlayer from, @Nonnull EntityPlayer to, long amount)
	{
		if (amount <= 0L)
		{
			return false;
		}
		
		if (!hasMoney(from, amount))
		{
			return false;
		}
		
		removeMoney(from, amount);
		addMoney(to, amount);
		
		return true;
	}

	@Override
	public boolean transferMoneyDouble(@Nonnull EntityPlayer from, @Nonnull EntityPlayer to, double amount)
	{
		if (FloatMoneyHelper.isNegative(amount) || FloatMoneyHelper.isZero(amount))
		{
			return false;
		}
		
		if (!hasMoneyDouble(from, amount))
		{
			return false;
		}
		
		removeMoneyDouble(from, amount);
		addMoneyDouble(to, amount);
		
		return true;
	}

	@Override
	public int getPrecisionScale()
	{
		return FloatMoneyHelper.DEFAULT_SCALE;
	}

	@Override
	public long getMaxMoney()
	{
		return Long.MAX_VALUE;
	}

	@Override
	public double getMaxMoneyDouble()
	{
		return FloatMoneyHelper.getMaxValue();
	}
}
