package com.feed_the_beast.mods.money;

import com.feed_the_beast.mods.ftbmoney.Tags;
import com.feed_the_beast.mods.money.gui.EnumSortType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
@Config(modid = Tags.MOD_ID + "_client", category = "", name = "../local/client/" + Tags.MOD_ID)
public class FTBMoneyClientConfig
{
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Sorting type.")
		public EnumSortType sort = EnumSortType.PRICE_H_L;
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(Tags.MOD_ID + "_client"))
		{
			ConfigManager.sync(Tags.MOD_ID + "_client", Config.Type.INSTANCE);
		}
	}
}