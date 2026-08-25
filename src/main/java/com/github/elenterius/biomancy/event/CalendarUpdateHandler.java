package com.github.elenterius.biomancy.event;

import com.github.elenterius.biomancy.BiomancyMod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * We don't update the calendar on level ticks because we don't need the most up-to-date calendar.<br>
 * Eventual updates are enough as we don't require a precise calendar because we use it only for easter-eggs.
 *
 * @Note: We only use events that are called on both client and server side.
 */
@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public final class CalendarUpdateHandler {

	private CalendarUpdateHandler() {}

	@SubscribeEvent
	public static void onLoadLevel(final LevelEvent.Load event) {
		if (event.getLevel() instanceof Level level && level.dimension() == Level.OVERWORLD) {
			BiomancyMod.EVENT_CALENDAR.update();
		}
	}

	@SubscribeEvent
	public static void onPlayerWakeup(final PlayerWakeUpEvent event) {
		BiomancyMod.EVENT_CALENDAR.update();
	}

	/**
	 * @Note: We don't use {@link net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent PlayerLoggedInEvent} because it's only server sided
	 */
	@SubscribeEvent
	public static void onPlayerJoinLevel(final EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof Player) {
			BiomancyMod.EVENT_CALENDAR.update();
		}
	}

}
