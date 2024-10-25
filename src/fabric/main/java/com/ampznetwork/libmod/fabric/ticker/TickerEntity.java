package com.ampznetwork.libmod.fabric.ticker;

import lombok.Value;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Value
public class TickerEntity extends Entity {
    Queue<Runnable> queue = new LinkedBlockingQueue<>();

    public TickerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public void tick() {
        super.tick();

        while (!queue.isEmpty())
            queue.poll().run();
    }

    /*1.21.1
    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
    }*/

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
    }
}
