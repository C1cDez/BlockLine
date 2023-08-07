package com.cicdez.blockline.code;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface ICodeTask {
    void run(final CodeSession session, final MinecraftServer server, final World world, EnumFacing facing, final EnumFacing[] facings, final ICommandSender sender, BlockPos current) throws BLException;
}
