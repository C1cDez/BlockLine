package com.cicdez.blockline.code;

import com.cicdez.blockline.BlockLineMod;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class Utils {
    public static final int MAX_SUGGESTED_LENGTH = 1 << 12;
    private static void dropExceptionInfinity(BlockPos pos) throws BLException {
        throw new BLException("Length of reading line maybe Infinity! You forgot about BYTES_END '" +
                BlockDefinitionHolder.getBytesEnd() + "'", pos);
    }
    
    public static void print(ICommandSender sender, String text) {
        sender.sendMessage(new TextComponentString(text));
    }
    
    public static void log(String text) {
        BlockLineMod.logger.info(text);
//        sender.sendMessage(new TextComponentString(text));
    }
    
    public static byte getByte(boolean[] bits) {
        int v = 0;
        for (int i = 0; i < bits.length; i++) {
            v |= bits[i] ? (1 << (bits.length - 1 - i)) : 0;
        }
        return (byte) v;
    }
    private static byte getByteObj(Object[] objBits) {
        boolean[] bits = new boolean[objBits.length];
        for (int i = 0; i < bits.length; i++) {
            bits[i] = (boolean) objBits[i];
        }
        return getByte(bits);
    }
    
    public static void readBytes(BlockPos pos, World world, EnumFacing facing,
                                 BiConsumer<Byte, Integer> operator) throws BLException {
        int suglen = 0;
        
        int c = -1;
        boolean[] bitsBuffer = new boolean[8];
        BlockPos destPos = pos;
        while (world.getBlockState(destPos).getBlock() != BlockDefinitionHolder.getBytesEnd()) {
            suglen++;
            if (suglen > MAX_SUGGESTED_LENGTH) dropExceptionInfinity(destPos);
            
            destPos = destPos.offset(facing, 1);
            c++;
            bitsBuffer[c % bitsBuffer.length] = world.getBlockState(destPos).getBlock() == BlockDefinitionHolder.getTrue();
            if (c != 0 && (c % 8) == 0) {
                operator.accept(getByte(bitsBuffer), c / 8);
            }
        }
    }
    
    public static EnumFacing[] getBoth(EnumFacing facing) throws BLException {
        if (facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
            return new EnumFacing[] {EnumFacing.EAST, EnumFacing.WEST};
        if (facing == EnumFacing.EAST || facing == EnumFacing.WEST)
            return new EnumFacing[] {EnumFacing.NORTH, EnumFacing.SOUTH};
        throw new BLException("What happened to the universe?", null);
    }
    
    public static int getName(BlockPos pos, World world, EnumFacing facing) {
        int name = 0;
        BlockPos destPos = pos.offset(facing, 1);
        while (world.getBlockState(destPos).getBlock() == BlockDefinitionHolder.getVariableName()) {
            name++;
            destPos = destPos.offset(facing, 1);
        }
        return name;
    }

    public static Object getValue(BlockPos pos, World world, EnumFacing facing) throws BLException {
        Block type = world.getBlockState(pos.up(1)).getBlock();
        if (type == BlockDefinitionHolder.getByteType()) {
            AtomicReference<Byte> bytE = new AtomicReference<>((byte) 0);
            Utils.readBytes(pos, world, facing, (by, i) -> bytE.set(by));
            return bytE.get();
        }
        if (type == BlockDefinitionHolder.getBooleanType()) {
            BlockPos boolPos = pos.offset(facing, 1);
            return world.getBlockState(boolPos).getBlock() == BlockDefinitionHolder.getTrue();
        }
        if (type == BlockDefinitionHolder.getStringType()) {
            StringBuilder text = new StringBuilder();
            Utils.readBytes(pos, world, facing, (b, i) -> text.append((char) (byte) b));
            return text.toString();
        }
        throw new BLException("Idk what type you mean: '" + type + "'", pos);
    }

    public static byte calculateNumberOperationOfVariables(VariablesMap map, BlockPos pos,
                                                           World world, EnumFacing facing) throws BLException {
        int suglen = 0;
        
        List<Block> blockBuffer = new ArrayList<>();
        OperatedStream<Byte> expression = new OperatedStream<>();

        BlockPos destPos = pos;
        while (world.getBlockState(destPos).getBlock() != BlockDefinitionHolder.getBytesEnd()) {
            suglen++;
            if (suglen > MAX_SUGGESTED_LENGTH) dropExceptionInfinity(destPos);
            
            destPos = destPos.offset(facing, 1);
            Block block = world.getBlockState(destPos).getBlock();

            if (block == BlockDefinitionHolder.getSeparator() || block == BlockDefinitionHolder.getBytesEnd()) {
                Utils.log("Separator! Current buffer: " + blockBuffer.stream().map(Block::toString)
                        .collect(Collectors.joining(", ")));

                byte value;
                if (blockBuffer.size() > 0 && blockBuffer.get(0) == BlockDefinitionHolder.getVariableName()) {
                    int name = blockBuffer.size();
                    if (!map.hasVariable(name) || !(map.getVariable(name) instanceof Number))
                        throw new BLException("Invalid variable! '" + name + "'", destPos);
                    value = (byte) map.getVariable(name);
                } else {
                    value = getByteObj(blockBuffer.stream()
                            .map(blck -> blck == BlockDefinitionHolder.getTrue()).toArray());
                }
                Utils.log("Value: " + value);
                expression.put(value);


                if (block != BlockDefinitionHolder.getBytesEnd()) {
                    MathOperation operation = MathOperation.byBlock(world.getBlockState(destPos.up(1))
                            .getBlock(), destPos);
                    expression.putOperator(operation);
                }

                blockBuffer.clear();
            }
            else blockBuffer.add(block);
        }
        return expression.solve();
    }
    
    public static boolean calculateBooleanOperations(VariablesMap map, BlockPos pos,
                                                     World world, EnumFacing facing) throws BLException {
        int suglen = 0;
        
        List<Block> blockBuffer = new ArrayList<>();
        OperatedStream<Boolean> expression = new OperatedStream<>();
        
        BlockPos destPos = pos;
        while (world.getBlockState(destPos).getBlock() != BlockDefinitionHolder.getBytesEnd()) {
            suglen++;
            if (suglen > MAX_SUGGESTED_LENGTH) dropExceptionInfinity(destPos);
            
            destPos = destPos.offset(facing, 1);
            Block block = world.getBlockState(destPos).getBlock();
            
            if (block == BlockDefinitionHolder.getSeparator() || block == BlockDefinitionHolder.getBytesEnd()) {
                Utils.log("Separator! Current buffer: " + blockBuffer.stream().map(Block::toString)
                        .collect(Collectors.joining(", ")));
                
                boolean value;
                if (blockBuffer.size() == 1) {
                    Block prevBlock = blockBuffer.get(0);
                    value = prevBlock == BlockDefinitionHolder.getTrue();
                } else {
                    int name = blockBuffer.size();
                    if (!map.hasVariable(name) || !(map.getVariable(name) instanceof Boolean))
                        throw new BLException("Invalid variable! '" + name + "'", destPos);
                    value = (boolean) map.getVariable(name);
                }
                expression.put(value);
                
                if (block != BlockDefinitionHolder.getBytesEnd()) {
                    BooleanOperation operation = BooleanOperation.byBlock(world.getBlockState(destPos.up(1))
                            .getBlock(), destPos);
                    expression.putOperator(operation);
                }
                
                blockBuffer.clear();
            } else blockBuffer.add(block);
        }
        return expression.solve();
    }
    
    public static boolean calculateBooleanNumberOperations(VariablesMap map, BlockPos pos,
                                                           World world, EnumFacing facing) throws BLException {
        int suglen = 0;
        
        List<Block> blockBuffer = new ArrayList<>();
        
        int numIndex = -1;
        byte[] numbers = new byte[2];
        BooleanOperation.NumberBooleanOperator operator = BooleanOperation.NumberBooleanOperator.NULL;
        
        BlockPos destPos = pos;
        while (world.getBlockState(destPos).getBlock() != BlockDefinitionHolder.getBytesEnd()) {
            suglen++;
            if (suglen > MAX_SUGGESTED_LENGTH) dropExceptionInfinity(destPos);
            
            destPos = destPos.offset(facing, 1);
            Block block = world.getBlockState(destPos).getBlock();
            
            if (block == BlockDefinitionHolder.getSeparator() || block == BlockDefinitionHolder.getBytesEnd()) {
                numIndex++;
                if (numIndex > numbers.length - 1)
                    throw new BLException("More than 2 numbers are in comparator line!", destPos);
                
                Utils.log("Separator! Current buffer: " + blockBuffer.stream().map(Block::toString)
                        .collect(Collectors.joining(", ")));
                
                byte value;
                if (blockBuffer.size() > 0 && blockBuffer.get(0) == BlockDefinitionHolder.getVariableName()) {
                    int name = blockBuffer.size();
                    if (!map.hasVariable(name) || !(map.getVariable(name) instanceof Number))
                        throw new BLException("Invalid variable! '" + name + "'", destPos);
                    value = (byte) map.getVariable(name);
                } else {
                    value = getByteObj(blockBuffer.stream()
                            .map(blck -> blck == BlockDefinitionHolder.getTrue()).toArray());
                }
                
                numbers[numIndex] = value;
                
                if (block != BlockDefinitionHolder.getBytesEnd()) {
                    operator = BooleanOperation.NumberBooleanOperator.byBlock(world.getBlockState(destPos.up(1))
                            .getBlock(), destPos);
                }
                
                blockBuffer.clear();
            } else blockBuffer.add(block);
        }
        
        return operator.accept(numbers[0], numbers[1]);
    }

    public static EnumFacing getSignedFacing(EnumFacing f1, EnumFacing f2,
                                             World world, BlockPos current) throws BLException {
        BlockPos up = current.up(1);
        if (world.getBlockState(up.offset(f1)).getBlock() == BlockDefinitionHolder.getSign()) return f1;
        if (world.getBlockState(up.offset(f2)).getBlock() == BlockDefinitionHolder.getSign()) return f2;
        throw new BLException("Not found facing sign", up);
    }
}
