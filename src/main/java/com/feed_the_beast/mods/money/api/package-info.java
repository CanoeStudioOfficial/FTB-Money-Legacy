/**
 * FTB Money API 包
 * 
 * <p>此包提供了FTB Money模组的公共API接口，
 * 允许其他模组与金钱系统进行交互。</p>
 * 
 * <h2>快速开始</h2>
 * <pre>{@code
 * // 获取API实例
 * IFTBMoneyAPI api = IFTBMoneyAPI.getInstance();
 * 
 * // 获取玩家金钱
 * long money = api.getMoney(player);
 * 
 * // 设置玩家金钱（支持浮点数）
 * api.setMoneyDouble(player, 100.50);
 * 
 * // 转账
 * api.transferMoney(fromPlayer, toPlayer, 50);
 * }</pre>
 * 
 * <h2>主要接口</h2>
 * <ul>
 *   <li>{@link com.feed_the_beast.mods.money.api.IFTBMoneyAPI} - 主要API接口</li>
 *   <li>{@link com.feed_the_beast.mods.money.api.FTBMoneyAPI} - API实现类</li>
 * </ul>
 * 
 * <h2>特性</h2>
 * <ul>
 *   <li>支持整数和浮点数两种金钱表示方式</li>
 *   <li>精确的浮点数运算，避免精度丢失</li>
 *   <li>跨平台兼容性</li>
 *   <li>完整的输入验证和错误处理</li>
 * </ul>
 * 
 * @author FTB Money Team
 * @since 1.0.0
 */
@javax.annotation.ParametersAreNonnullByDefault
package com.feed_the_beast.mods.money.api;
