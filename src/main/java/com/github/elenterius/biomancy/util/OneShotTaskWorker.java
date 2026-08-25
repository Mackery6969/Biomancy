package com.github.elenterius.biomancy.util;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.WorldWorkerManager;

import java.util.function.Consumer;

public abstract class OneShotTaskWorker implements WorldWorkerManager.IWorker {

	@Override
	public boolean hasWork() {
		return false;
	}

	@Override
	public boolean doWork() {
		doTask();
		return false;
	}

	public abstract void doTask();

	public static void onNextTick(LivingEntity livingEntity, Consumer<LivingEntity> task) {
		WorldWorkerManager.addWorker(new OneShotTaskWorker() {
			@Override
			public void doTask() {
				if (livingEntity.isAlive()) {
					task.accept(livingEntity);
				}
			}
		});
	}

}
