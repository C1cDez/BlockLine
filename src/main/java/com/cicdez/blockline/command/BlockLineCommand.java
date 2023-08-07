package com.cicdez.blockline.command;

import com.cicdez.blockline.code.BlockDefinitionHolder;
import com.cicdez.blockline.code.CodeSession;
import com.cicdez.blockline.code.Utils;
import net.minecraft.command.*;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockLineCommand extends CommandBase {
    public BlockLineCommand() {}
    
    @Override
    public String getName() {
        return "blockline";
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        return "blockline [<x> <y> <z>]";
    }
    
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        BlockPos pos = sender.getPosition();
        if (args.length == 1) {
            if (args[0].equals("tutorial")) {
                Utils.print(sender, BlockDefinitionHolder.getTutorial());
                return;
            }
        }
        if (args.length > 3) {
            pos = parseBlockPos(sender, args, 0, false);
        }
        
        CodeSession session = new CodeSession(server, pos, getDirection(sender.getEntityWorld(), pos));
        session.run(sender);
    }
    private EnumFacing getDirection(World world, BlockPos pos) throws CommandException {
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            BlockPos destPos = pos.offset(facing, 1);
            if (world.getBlockState(destPos).getBlock() == BlockDefinitionHolder.getProgramStart()) {
                return facing;
            }
        }
        throw new WrongUsageException("Not found Program Start '" + BlockDefinitionHolder.getProgramStart() + "'");
    }
}
