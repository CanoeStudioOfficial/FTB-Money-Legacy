package com.feed_the_beast.mods.money.gui;

import com.feed_the_beast.ftblib.lib.config.ConfigGroup;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.gui.*;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfig;
import com.feed_the_beast.ftblib.lib.gui.misc.GuiEditConfigValue;
import com.feed_the_beast.ftblib.lib.icon.Color4I;
import com.feed_the_beast.ftblib.lib.icon.ItemIcon;
import com.feed_the_beast.ftblib.lib.util.misc.MouseButton;
import com.feed_the_beast.ftbquests.client.ClientQuestFile;
import com.feed_the_beast.ftbquests.quest.QuestObject;
import com.feed_the_beast.mods.money.FTBMoney;
import com.feed_the_beast.mods.money.FTBMoneyClientConfig;
import com.feed_the_beast.mods.money.net.MessageBuy;
import com.feed_the_beast.mods.money.net.MessageEditShopEntry;
import com.feed_the_beast.mods.money.net.MessageSell;
import com.feed_the_beast.mods.money.shop.ShopEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author LatvianModder
 */
public class ButtonShopEntry extends Button
{
	public static final Comparator<ButtonShopEntry> COMPARATOR = (o1, o2) -> FTBMoneyClientConfig.general.sort.comparator.compare(o1.entry, o2.entry);

	public final ShopEntry entry;
	public final int locked;

	public ButtonShopEntry(Panel panel, ShopEntry e)
	{
		super(panel, e.stack.getRarity().getColor() + e.stack.getDisplayName(), ItemIcon.getItemIcon(e.stack));
		entry = e;
		QuestObject lock = ClientQuestFile.INSTANCE.get(entry.lock);
		locked = (entry.lock == 0 || lock != null && lock.isComplete(ClientQuestFile.INSTANCE.self)) ? (entry.disabledServer && !Minecraft.getMinecraft().isSingleplayer()) ? 1 : 0 : 2;
		if (entry.buy >= 1 && entry.sell >= 1) {
			setWidth(Math.max(panel.getGui().getTheme().getStringWidth(title), 
				Math.max(panel.getGui().getTheme().getStringWidth(FTBMoney.moneyString(entry.buy)),
					panel.getGui().getTheme().getStringWidth(FTBMoney.moneyString(entry.sell))) + 32));
		}
		else if (entry.buy >= 1) {
			setWidth(Math.max(panel.getGui().getTheme().getStringWidth(title), panel.getGui().getTheme().getStringWidth(FTBMoney.moneyString(entry.buy))) + 32);
		}
		else if (entry.sell >= 1) {
			setWidth(Math.max(panel.getGui().getTheme().getStringWidth(title), panel.getGui().getTheme().getStringWidth(FTBMoney.moneyString(entry.sell))) + 32);
		}
		else if (entry.sell == 0 && entry.buy == 0) {
			setWidth(Math.max(panel.getGui().getTheme().getStringWidth(title), panel.getGui().getTheme().getStringWidth(I18n.format("ftbmoney.shop.entry.free"))) + 32);
		}
		setHeight(32);
	}

	@Override
	public void onClicked(MouseButton button)
	{
		GuiHelper.playClickSound();
		GuiShop gui = (GuiShop) getGui();

		if (button.isLeft())
		{
			// Left click: Buy
			if (locked == 0 || entry.tab.shop.file.get().canEdit())
			{
				if (entry.buy <= 0) {
					return;
				}
				int maximum = (int) Math.min(1024L, entry.buy <= 0L ? 1024L : FTBMoney.getMoney(Minecraft.getMinecraft().player) / entry.buy);
				new GuiEditConfigValue("count", new ConfigInt(1, 1, maximum), (value, set) -> {
					gui.openGui();
					if (set)
					{
						new MessageBuy(entry, value.getInt()).sendToServer();
					}
				}).openGui();
			}
		}
		else if (button.isMiddle())
		{
			// Middle click: Sell
			if (locked == 0 || entry.tab.shop.file.get().canEdit())
			{
				if (entry.sell <= 0) {
					return;
				}
				int current_items = 0;
				for (ItemStack next : Minecraft.getMinecraft().player.inventory.mainInventory) {
					if (!next.isEmpty() && next.isItemEqual(entry.stack) && ItemStack.areItemStackTagsEqual(next, entry.stack)) {
						current_items += next.getCount();
					}
				}

				int maximum = (int) Math.min(1024L, current_items / entry.stack.getCount());
				if (maximum <= 0) {
					return;
				}
				new GuiEditConfigValue("count", new ConfigInt(1, 1, maximum), (value, set) -> {
					gui.openGui();
					if (set)
					{
						new MessageSell(entry, value.getInt()).sendToServer();
					}
				}).openGui();
			}
		}
		else if (button.isRight() && entry.tab.shop.file.get().canEdit())
		{
			List<ContextMenuItem> contextMenu = new ArrayList<>();

			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.edit"), GuiIcons.SETTINGS, () -> {
				ConfigGroup group = ConfigGroup.newGroup("ftbmoney").setDisplayName(new TextComponentTranslation("sidebar_button.ftbmoney.shop"));
				ConfigGroup g = group.getGroup("shop").getGroup("entry");
				entry.getConfig(g);
				new GuiEditConfig(group, (g1, sender) -> new MessageEditShopEntry(entry, false).sendToServer()).openGui();
				gui.refreshWidgets();
			}));

			contextMenu.add(new ContextMenuItem(I18n.format("selectServer.delete"), GuiIcons.REMOVE, () -> gui.openYesNo(I18n.format("delete_item", entry.stack.getDisplayName()), "", () -> {
				new MessageEditShopEntry(entry, true).sendToServer();
				entry.tab.entries.remove(entry);
				gui.refreshWidgets();
			})));

			gui.openContextMenu(contextMenu);
		}
	}

	@Override
	public WidgetType getWidgetType()
	{
		if (locked > 0 && !entry.tab.shop.file.get().canEdit())
		{
			return WidgetType.DISABLED;
		}

		return super.getWidgetType();
	}

	@Override
	public void addMouseOverText(List<String> list)
	{
		if (locked == 2)
		{
			list.add(I18n.format("ftbmoney.shop.entry.locked"));
			QuestObject object = ClientQuestFile.INSTANCE.get(entry.lock);

			if (object != null)
			{
				list.add(I18n.format("ftbmoney.shop.entry.requires") + ": " + object.getObjectType().getColor() + object.getTitle());
			}
		}

		if (locked < 2 || entry.tab.shop.file.get().canEdit())
		{
			if (entry.disabledServer && !Minecraft.getMinecraft().isSingleplayer())
			{
				list.add(TextFormatting.RED + I18n.format("ftbmoney.shop.entry.disabled_server"));
			}

			GuiHelper.addStackTooltip(entry.stack, list);
			
			// Add buy/sell hints
			list.add("");
			if (entry.buy > 0) {
				list.add(TextFormatting.GREEN + I18n.format("ftbmoney.shop.entry.click_buy"));
			}
			if (entry.sell > 0) {
				list.add(TextFormatting.RED + I18n.format("ftbmoney.shop.entry.click_sell"));
			}
		}
	}

	@Override
	public void draw(Theme theme, int x, int y, int w, int h)
	{
		drawBackground(theme, x, y, w, h);

		if (locked == 2 && !entry.tab.shop.file.get().canEdit())
		{
			GuiIcons.LOCK.draw(x + 4, y + 4, 16, 16);
			theme.drawString("???", x + 24, y + 3, theme.getContentColor(getWidgetType()), Theme.SHADOW);
			theme.drawString(TextFormatting.GOLD + "\u0398 " + TextFormatting.OBFUSCATED + "000,000,000", x + 24, y + 13, Color4I.WHITE, Theme.SHADOW);
			return;
		}

		String t = title;

		int mw = w - 24;

		if (theme.getStringWidth(t) > mw)
		{
			t = theme.trimStringToWidth(t, mw);
		}

		drawIcon(theme, x + 4, y + 4, 16, 16);
		theme.drawString(t, x + 24, y + 3, theme.getContentColor(getWidgetType()), Theme.SHADOW);
		
		// Draw buy price
		if (entry.buy > 0) {
			theme.drawString(TextFormatting.GREEN + I18n.format("ftbmoney.buy") + ": " + TextFormatting.GOLD + FTBMoney.moneyString(entry.buy), x + 24, y + 13, Color4I.WHITE, Theme.SHADOW);
		}
		
		// Draw sell price
		if (entry.sell > 0) {
			int sellY = entry.buy > 0 ? y + 23 : y + 13;
			theme.drawString(TextFormatting.RED + I18n.format("ftbmoney.sell") + ": " + TextFormatting.GOLD + FTBMoney.moneyString(entry.sell), x + 24, sellY, Color4I.WHITE, Theme.SHADOW);
		}
	}

	@Override
	@Nullable
	public Object getIngredientUnderMouse()
	{
		return locked < 2 ? entry.stack : null;
	}
}