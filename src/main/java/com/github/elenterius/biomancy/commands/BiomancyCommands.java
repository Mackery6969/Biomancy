package com.github.elenterius.biomancy.commands;

import com.github.elenterius.biomancy.BiomancyMod;
import com.github.elenterius.biomancy.mixin.accessor.DisplayAccessor;
import com.github.elenterius.biomancy.mixin.accessor.StructureTemplateAccessor;
import com.github.elenterius.biomancy.mixin.accessor.TextDisplayAccessor;
import com.github.elenterius.biomancy.styles.TextStyles;
import com.github.elenterius.biomancy.util.ComponentUtil;
import com.google.common.base.Predicate;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@EventBusSubscriber(modid = BiomancyMod.MOD_ID)
public class BiomancyCommands {

	@SubscribeEvent
	public static void onRegisterCommands(RegisterCommandsEvent event) {
		register(event.getDispatcher());
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		Predicate<CommandSourceStack> spOrOP = source -> source != null && (source.getServer().isSingleplayer() || source.hasPermission(Commands.LEVEL_GAMEMASTERS));

		LiteralArgumentBuilder<CommandSourceStack> builder = Commands
				.literal("biomancy")
				.requires(spOrOP)
				.then(Commands.literal("dev").requires(ctx -> BiomancyMod.isDevModeEnabled())
						.then(Commands.literal("place_structures")
								.then(Commands.argument("origin", BlockPosArgument.blockPos()).executes(ctx -> {
									BlockPos origin = BlockPosArgument.getLoadedBlockPos(ctx, "origin");
									placeAllStructures(ctx, origin);

									return Command.SINGLE_SUCCESS;
								}))
						)
						.then(Commands.literal("dump_biome_temps").executes(ctx -> dumpBiomeTemperatureAndHumidity(ctx.getSource().registryAccess()) ? Command.SINGLE_SUCCESS : 0))
				);

		LiteralCommandNode<CommandSourceStack> cmd = dispatcher.register(builder);
		dispatcher.register(Commands.literal("bio").redirect(cmd));
	}

	private static void placeAllStructures(CommandContext<CommandSourceStack> ctx, BlockPos origin) {
		ServerLevel level = ctx.getSource().getLevel();
		StructureTemplateManager structureManager = level.getServer().getStructureManager();
		ReloadableServerRegistries.Holder lootManager = level.getServer().reloadableRegistries();

		List<ResourceLocation> templateIds = structureManager.listTemplates()
				.filter(id -> id.getNamespace().equals(BiomancyMod.MOD_ID))
				.toList();

		ctx.getSource().sendSystemMessage(ComponentUtil.literal("Placing %,d structure templates...".formatted(templateIds.size())));
		AtomicInteger structuresPlaced = new AtomicInteger();

		Map<String, List<ResourceLocation>> templatesByFolder = new HashMap<>();
		for (ResourceLocation templateId : templateIds) {
			String path = templateId.getPath();
			int index = path.lastIndexOf('/');
			String folder = index == -1 ? "root" : path.substring(0, index);
			templatesByFolder.computeIfAbsent(folder, k -> new ArrayList<>()).add(templateId);
		}

		final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(origin);
		final BlockPos structurePos = new BlockPos(1, 1, 1);
		final int gap = 5;
		final int maxLength = (48 + gap) * 10;

		BiomancyMod.LOGGER.debug("Found {} template folders...", templatesByFolder.size());
		templatesByFolder.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(folder -> {
			int length = 0;
			int maxDepth = 0;

			List<ResourceLocation> templatesInFolder = folder.getValue();
			templatesInFolder.sort(ResourceLocation::compareTo);

			BiomancyMod.LOGGER.debug("Placing {} templates from folder '{}'...", templatesInFolder.size(), folder.getKey());
			for (ResourceLocation templateId : templatesInFolder) {
				Optional<StructureTemplate> optional = structureManager.get(templateId);
				if (optional.isEmpty()) continue;

				StructureTemplate template = optional.get();
				Vec3i templateSize = template.getSize();

				BlockPos secondPos = cursor.offset(1 + templateSize.getX(), 1 + templateSize.getY(), 1 + templateSize.getZ());
				List<Display> displays = level.getEntitiesOfClass(Display.class, new AABB(Vec3.atLowerCornerOf(cursor), Vec3.atLowerCornerOf(secondPos)));
				BiomancyMod.LOGGER.debug("Discarding {} old display entities...", displays.size());
				for (Display display : displays) {
					display.discard();
				}

				BiomancyMod.LOGGER.debug("Removing old blocks...");
				BlockState airBlockState = Blocks.AIR.defaultBlockState();
				for (BlockPos pos : BlockPos.betweenClosed(cursor, secondPos)) {
					if (level.getBlockState(pos) != airBlockState) {
						level.setBlock(pos, airBlockState, Block.UPDATE_ALL);
					}
				}

				BiomancyMod.LOGGER.debug("Creating platform...");
				int n = 0;
				for (BlockPos pos : BlockPos.betweenClosed(cursor, cursor.offset(1 + templateSize.getX(), 0, 1 + templateSize.getZ()))) {
					BlockState state = n++ % 2 == 0 ? Blocks.LIGHT_GRAY_CONCRETE.defaultBlockState() : Blocks.GRAY_CONCRETE.defaultBlockState();
					level.setBlock(pos, state, Block.UPDATE_ALL);
				}

				boolean success = false;

				BiomancyMod.LOGGER.debug("Placing template '{}'...", templateId);
				level.setBlock(cursor, Blocks.STRUCTURE_BLOCK.defaultBlockState(), Block.UPDATE_ALL);
				if (level.getBlockEntity(cursor) instanceof StructureBlockEntity sbe) {
					sbe.setMode(StructureMode.LOAD);
					sbe.setStructurePos(structurePos);
					sbe.setStructureName(templateId);

					try {
						sbe.placeStructure(level);
						success = true;
						structuresPlaced.getAndIncrement();
					}
					catch (Exception e) {
						ctx.getSource().sendFailure(ComponentUtil.literal("Failed to place structure: " + templateId).withStyle(TextStyles.ERROR));
						BiomancyMod.LOGGER.error("Failed to place structure '{}'", templateId, e);
					}
					sbe.setChanged();
				}

				if (success) {
					BiomancyMod.LOGGER.debug("Checking for chest loot tables...");
					List<StructureTemplate.StructureBlockInfo> blockInfos = ((StructureTemplateAccessor) template).biomancy$getPalettes().get(0).blocks();
					for (StructureTemplate.StructureBlockInfo blockInfo : blockInfos) {
						CompoundTag tag = blockInfo.nbt();
						if (tag == null || !tag.contains(RandomizableContainerBlockEntity.LOOT_TABLE_TAG, Tag.TAG_STRING)) continue;

						String lootTableId = tag.getString(RandomizableContainerBlockEntity.LOOT_TABLE_TAG);
						long lootTableSeed = tag.getLong(RandomizableContainerBlockEntity.LOOT_TABLE_SEED_TAG);

						boolean lootableExsists = false;
						ResourceLocation id = ResourceLocation.tryParse(lootTableId);
						if (id != null) {
							LootTable lootTable = lootManager.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
							lootableExsists = lootTable != LootTable.EMPTY;
						}

						BlockPos pos = blockInfo.pos();
						int x = cursor.getX() + structurePos.getX() + pos.getX();
						int y = cursor.getY() + structurePos.getY() + pos.getY();
						int z = cursor.getZ() + structurePos.getZ() + pos.getZ();

						MutableComponent text = ComponentUtil.literal(lootTableId);

						if (lootableExsists) {
							BlockEntity blockEntity = level.getBlockEntity(new BlockPos(x, y, z));
							if (blockEntity != null) {
								CompoundTag data = blockEntity.saveWithoutMetadata(level.registryAccess());
								if (!data.contains(RandomizableContainerBlockEntity.LOOT_TABLE_TAG, Tag.TAG_STRING)) {
									text.append("\n").append(ComponentUtil.literal("LootTable is missing!").withStyle(TextStyles.ERROR));
								}
							}
						}
						else {
							text.withStyle(TextStyles.ERROR);
						}

						if (lootTableSeed != 0) {
							text.append("\nSeed: " + lootTableSeed);
						}

						spawnTextDisplay(level, x + 0.5d, y + 0.25d, z + 0.5d, Display.TextDisplay.Align.CENTER, text);
					}

					double x = cursor.getX() + 0.5d;
					double y = cursor.getY() + 1.25d;
					double z = cursor.getZ() + 0.5d;
					spawnTextDisplay(level, x, y, z, Display.TextDisplay.Align.CENTER, ComponentUtil.literal(templateId.toString()));
				}

				cursor.move(templateSize.getX() + gap, 0, 0);
				length += templateSize.getX() + gap;
				maxDepth = Math.max(maxDepth, templateSize.getZ());

				if (length > maxLength) {
					cursor.set(origin.getX(), origin.getY(), cursor.getZ() + maxDepth + gap);
					length = 0;
					maxDepth = 0;
				}
			}

			cursor.set(origin.getX(), origin.getY(), cursor.getZ() + maxDepth + gap * 2);
		});

		ctx.getSource().sendSystemMessage(ComponentUtil.literal("Placed %,d/%,d structure templates!".formatted(structuresPlaced.get(), templateIds.size())));
	}

	private static void spawnTextDisplay(ServerLevel level, double x, double y, double z, Display.TextDisplay.Align textAlignment, Component text) {
		Display.TextDisplay textDisplay = EntityType.TEXT_DISPLAY.create(level);
		if (textDisplay != null) {
			textDisplay.setPos(x, y, z);

			TextDisplayAccessor accessor = (TextDisplayAccessor) textDisplay;
			accessor.biomancy$setText(text);

			byte styleFlags = accessor.biomancy$getStyleFlags();
			styleFlags = switch (textAlignment) {
				case CENTER -> styleFlags;
				case LEFT -> (byte) (styleFlags | 8);
				case RIGHT -> (byte) (styleFlags | 16);
			};
			accessor.biomancy$setStyleFlags(styleFlags);

			DisplayAccessor displayAccessor = (DisplayAccessor) textDisplay;
			displayAccessor.biomancy$setBillboardConstraints(Display.BillboardConstraints.CENTER);

			level.addFreshEntity(textDisplay);
		}
	}

	private static boolean dumpBiomeTemperatureAndHumidity(RegistryAccess registryAccess) {
		BiomancyMod.LOGGER.info("dumping biome default temperatures to biome_temperatures.csv...");
		try {
			Stream<String> stringStream = registryAccess.lookupOrThrow(Registries.BIOME).listElements()
					.map(entry -> "%s,%s,%s".formatted(entry.key().location(), entry.value().getBaseTemperature(), entry.value().getModifiedClimateSettings().downfall()));

			Files.write(Paths.get("biome_temperatures.csv"), (Iterable<String>) stringStream::iterator);
			return true;
		}
		catch (Exception e) {
			BiomancyMod.LOGGER.error("Failed to dump biome temps!", e);
		}
		return false;
	}

}

