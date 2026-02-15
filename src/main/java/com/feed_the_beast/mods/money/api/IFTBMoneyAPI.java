package com.feed_the_beast.mods.money.api;

import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FTB Money API 接口
 * 提供金钱系统的主要操作接口
 * 
 * <p>此接口定义了FTB Money模组的核心功能，
 * 包括金钱的获取、设置、增加和减少操作。
 * 支持整数和浮点数两种模式。</p>
 * 
 * <p>使用示例：</p>
 * <pre>{@code
 * IFTBMoneyAPI api = FTBMoneyAPI.getInstance();
 * 
 * // 获取玩家金钱
 * long money = api.getMoney(player);
 * 
 * // 设置玩家金钱（浮点数）
 * api.setMoneyDouble(player, 100.50);
 * 
 * // 增加金钱
 * api.addMoney(player, 50);
 * 
 * // 减少金钱
 * api.removeMoney(player, 25.75);
 * }</pre>
 * 
 * @author FTB Money Team
 * @since 1.0.0
 */
public interface IFTBMoneyAPI
{
	/**
	 * 获取API实例
	 * 
	 * @return FTB Money API实例
	 * @throws IllegalStateException 如果API尚未初始化
	 */
	@Nonnull
	static IFTBMoneyAPI getInstance()
	{
		IFTBMoneyAPI instance = FTBMoneyAPI.getInstance();
		if (instance == null)
		{
			throw new IllegalStateException("FTB Money API has not been initialized yet!");
		}
		return instance;
	}

	/**
	 * 检查API是否已初始化
	 * 
	 * @return 如果API已初始化返回true
	 */
	static boolean isInitialized()
	{
		return FTBMoneyAPI.getInstance() != null;
	}

	/**
	 * 获取玩家的金钱数量（整数）
	 * 
	 * @param player 目标玩家
	 * @return 玩家的金钱数量，如果玩家为null返回0
	 */
	long getMoney(@Nullable EntityPlayer player);

	/**
	 * 获取玩家的金钱数量（浮点数）
	 * 
	 * <p>返回的浮点数值保留了小数精度，
	 * 适用于需要精确金额显示的场景。</p>
	 * 
	 * @param player 目标玩家
	 * @return 玩家的金钱数量（浮点数），如果玩家为null返回0.0
	 */
	double getMoneyDouble(@Nullable EntityPlayer player);

	/**
	 * 设置玩家的金钱数量（整数）
	 * 
	 * <p>如果设置的金额小于等于0，将清除玩家的金钱数据。
	 * 此操作会自动同步到客户端。</p>
	 * 
	 * @param player 目标玩家
	 * @param money 要设置的金钱数量
	 */
	void setMoney(@Nonnull EntityPlayer player, long money);

	/**
	 * 设置玩家的金钱数量（浮点数）
	 * 
	 * <p>浮点数金额会被转换为内部存储格式，
	 * 精度由FloatMoneyHelper.DEFAULT_SCALE决定（默认2位小数）。
	 * 此操作会自动同步到客户端。</p>
	 * 
	 * @param player 目标玩家
	 * @param money 要设置的金钱数量（浮点数）
	 */
	void setMoneyDouble(@Nonnull EntityPlayer player, double money);

	/**
	 * 增加玩家的金钱（整数）
	 * 
	 * <p>如果增加的金额为负数，将实际减少金钱。
	 * 此操作会自动同步到客户端。</p>
	 * 
	 * @param player 目标玩家
	 * @param amount 要增加的金额
	 * @return 操作后的金钱数量
	 */
	long addMoney(@Nonnull EntityPlayer player, long amount);

	/**
	 * 增加玩家的金钱（浮点数）
	 * 
	 * <p>如果增加的金额为负数，将实际减少金钱。
	 * 此操作会自动同步到客户端。</p>
	 * 
	 * @param player 目标玩家
	 * @param amount 要增加的金额（浮点数）
	 * @return 操作后的金钱数量（浮点数）
	 */
	double addMoneyDouble(@Nonnull EntityPlayer player, double amount);

	/**
	 * 减少玩家的金钱（整数）
	 * 
	 * <p>如果减少的金额超过玩家当前拥有的金钱，
	 * 金钱将变为0。此操作会自动同步到客户端。</p>
	 * 
	 * @param player 目标玩家
	 * @param amount 要减少的金额
	 * @return 操作后的金钱数量
	 */
	long removeMoney(@Nonnull EntityPlayer player, long amount);

	/**
	 * 减少玩家的金钱（浮点数）
	 * 
	 * <p>如果减少的金额超过玩家当前拥有的金钱，
	 * 金钱将变为0。此操作会自动同步到客户端。</p>
	 * 
	 * @param player 目标玩家
	 * @param amount 要减少的金额（浮点数）
	 * @return 操作后的金钱数量（浮点数）
	 */
	double removeMoneyDouble(@Nonnull EntityPlayer player, double amount);

	/**
	 * 检查玩家是否有足够的金钱（整数）
	 * 
	 * @param player 目标玩家
	 * @param amount 需要的金额
	 * @return 如果玩家有足够的金钱返回true
	 */
	boolean hasMoney(@Nullable EntityPlayer player, long amount);

	/**
	 * 检查玩家是否有足够的金钱（浮点数）
	 * 
	 * @param player 目标玩家
	 * @param amount 需要的金额（浮点数）
	 * @return 如果玩家有足够的金钱返回true
	 */
	boolean hasMoneyDouble(@Nullable EntityPlayer player, double amount);

	/**
	 * 尝试从玩家扣除金钱（整数）
	 * 
	 * <p>如果玩家没有足够的金钱，操作将失败且不会扣除任何金额。</p>
	 * 
	 * @param player 目标玩家
	 * @param amount 要扣除的金额
	 * @return 如果扣除成功返回true，否则返回false
	 */
	boolean tryRemoveMoney(@Nonnull EntityPlayer player, long amount);

	/**
	 * 尝试从玩家扣除金钱（浮点数）
	 * 
	 * <p>如果玩家没有足够的金钱，操作将失败且不会扣除任何金额。</p>
	 * 
	 * @param player 目标玩家
	 * @param amount 要扣除的金额（浮点数）
	 * @return 如果扣除成功返回true，否则返回false
	 */
	boolean tryRemoveMoneyDouble(@Nonnull EntityPlayer player, double amount);

	/**
	 * 格式化金钱为显示字符串（整数）
	 * 
	 * <p>返回格式如 "Θ 1,234" 的字符串。</p>
	 * 
	 * @param money 金钱数量
	 * @return 格式化后的字符串
	 */
	@Nonnull
	String formatMoney(long money);

	/**
	 * 格式化金钱为显示字符串（浮点数）
	 * 
	 * <p>返回格式如 "Θ 1,234.56" 的字符串。</p>
	 * 
	 * @param money 金钱数量（浮点数）
	 * @return 格式化后的字符串
	 */
	@Nonnull
	String formatMoneyDouble(double money);

	/**
	 * 解析字符串为金钱数量（整数）
	 * 
	 * <p>支持多种输入格式，如 "1000", "1,000", "~500" 等。
	 * 解析失败时返回0。</p>
	 * 
	 * @param str 要解析的字符串
	 * @return 解析后的金钱数量
	 */
	long parseMoney(@Nullable String str);

	/**
	 * 解析字符串为金钱数量（浮点数）
	 * 
	 * <p>支持多种输入格式，如 "100.50", "1,000.25", "~500.75" 等。
	 * 解析失败时返回0.0。</p>
	 * 
	 * @param str 要解析的字符串
	 * @return 解析后的金钱数量（浮点数）
	 */
	double parseMoneyDouble(@Nullable String str);

	/**
	 * 在两个玩家之间转账（整数）
	 * 
	 * <p>如果发送方没有足够的金钱，转账将失败。</p>
	 * 
	 * @param from 发送方玩家
	 * @param to 接收方玩家
	 * @param amount 转账金额
	 * @return 如果转账成功返回true
	 */
	boolean transferMoney(@Nonnull EntityPlayer from, @Nonnull EntityPlayer to, long amount);

	/**
	 * 在两个玩家之间转账（浮点数）
	 * 
	 * <p>如果发送方没有足够的金钱，转账将失败。</p>
	 * 
	 * @param from 发送方玩家
	 * @param to 接收方玩家
	 * @param amount 转账金额（浮点数）
	 * @return 如果转账成功返回true
	 */
	boolean transferMoneyDouble(@Nonnull EntityPlayer from, @Nonnull EntityPlayer to, double amount);

	/**
	 * 获取浮点数精度小数位数
	 * 
	 * @return 精度小数位数（默认为2）
	 */
	int getPrecisionScale();

	/**
	 * 获取最大金钱数量
	 * 
	 * @return 最大金钱数量
	 */
	long getMaxMoney();

	/**
	 * 获取最大金钱数量（浮点数）
	 * 
	 * @return 最大金钱数量（浮点数）
	 */
	double getMaxMoneyDouble();
}
