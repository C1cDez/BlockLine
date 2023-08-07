package com.cicdez.blockline.code;

import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CodeSession {
    public final MinecraftServer server;
    public final World world;
    public final BlockPos blockPos;
    public final EnumFacing facing;
    
    private BlockPos current;
    private int len = 0;
    
    public final VariablesMap variables;
    
    public CodeSession(MinecraftServer server, BlockPos blockPos, EnumFacing facing) {
        this.server = server;
        this.world = server.getEntityWorld();
        this.blockPos = blockPos;
        this.facing = facing;
        this.current = blockPos.offset(facing);
        this.variables = new VariablesMap(this);
    }
    
    public void run(ICommandSender sender) throws CommandException {
        while (world.getBlockState(current).getBlock() != BlockDefinitionHolder.getProgramEnd()) {
            Block block = world.getBlockState(current).getBlock();
            Utils.log(current + " is " + block);
            try {
                ICodeTask task = BlockDefinitionHolder.getTask(block);
                task.run(this, server, world, facing, Utils.getBoth(facing), sender, current);
            } catch (BLException exception) {
                throw exception.toCommandException();
            }
            move();
        }
    }
    
    private void move() {
        current = current.offset(facing);
    }
    
    public void jump(boolean backward, byte count) throws BLException {
        byte length = backward ? (byte) -count : count;
        Utils.log("Jump to " + length + " from " + current);
        current = current.offset(facing, length);
    }
}
