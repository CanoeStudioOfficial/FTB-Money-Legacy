package com.feed_the_beast.mods.money.net;

import com.feed_the_beast.ftblib.lib.io.DataIn;
import com.feed_the_beast.ftblib.lib.io.DataOut;
import com.feed_the_beast.ftblib.lib.net.MessageToClient;
import com.feed_the_beast.ftblib.lib.net.NetworkWrapper;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.FloatMoneyHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 浮点数金钱更新消息
 * 用于在服务器和客户端之间同步浮点数精度的金钱数据
 * 
 * @author FTB Money Team
 */
public class MessageUpdateMoneyDouble extends MessageToClient
{
	private double money;

	public MessageUpdateMoneyDouble()
	{
	}

	public MessageUpdateMoneyDouble(double m)
	{
		money = FloatMoneyHelper.normalize(m);
	}

	@Override
	public NetworkWrapper getWrapper()
	{
		return FTBMoneyNetHandler.NET;
	}

	@Override
	public void writeData(DataOut data)
	{
		data.writeDouble(money);
	}

	@Override
	public void readData(DataIn data)
	{
		money = FloatMoneyHelper.normalize(data.readDouble());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onMessage()
	{
		FTBMoney.setMoneyDouble(Minecraft.getMinecraft().player, money);
	}
}
