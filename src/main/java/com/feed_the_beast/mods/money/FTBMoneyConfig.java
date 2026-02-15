package com.feed_the_beast.mods.money;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBMoney.MOD_ID)
@Config(modid = FTBMoney.MOD_ID, category = "")
public class FTBMoneyConfig
{
	public static final General general = new General();

	public static class General
	{
		@Config.RequiresMcRestart
		@Config.Comment({"true - use config/ftbmoneyshop.nbt file;", "false - use world/data/ftbmoneyshop.nbt file."})
		public boolean use_config_store = true;

		@Config.Comment({
			"Enable floating point precision for money values.",
			"When enabled, money values can have decimal places (e.g., 100.50).",
			"When disabled, money values are integers only (backward compatible mode).",
			"Default: false (integer mode for backward compatibility)"
		})
		public boolean use_double_precision = false;

		@Config.Comment({
			"Number of decimal places for floating point money display.",
			"Only effective when use_double_precision is true.",
			"Valid range: 0-6. Default: 2"
		})
		@Config.RangeInt(min = 0, max = 6)
		public int decimal_places = 2;
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBMoney.MOD_ID))
		{
			ConfigManager.sync(FTBMoney.MOD_ID, Config.Type.INSTANCE);
			FloatMoneyHelper.setPrecisionScale(FTBMoneyConfig.general.decimal_places);
		}
	}
}