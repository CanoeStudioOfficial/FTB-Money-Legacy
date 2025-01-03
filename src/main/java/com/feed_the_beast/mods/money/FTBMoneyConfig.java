package com.feed_the_beast.mods.money;

import com.feed_the_beast.mods.ftbmoney.Tags;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
@Config(modid = Tags.MOD_ID, category = "")
public class FTBMoneyConfig
{
	public static final General general = new General();

	public static class General
	{
		@Config.RequiresMcRestart
		@Config.Comment({"true - use config/ftbmoneyshop.nbt file;", "false - use world/data/ftbmoneyshop.nbt file."})
		public boolean use_config_store = true;
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(Tags.MOD_ID))
		{
			ConfigManager.sync(Tags.MOD_ID, Config.Type.INSTANCE);
		}
	}
}