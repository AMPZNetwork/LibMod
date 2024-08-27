package com.ampznetwork.libmod.fabric.ticker;

import lombok.Value;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Value
public class TickerEntity extends Entity {
    Queue<Runnable> queue = new LinkedBlockingQueue<>();

    @Override
    public void tick() {
        super.tick();

        while (!queue.isEmpty())
            queue.poll().run();
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
}
