package com.cicdez.blockline.code;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public enum MathOperation implements OperatedStream.Operator<Byte> {
    NULL((a, b) -> a, BlockDefinitionHolder.getEmpty()),
    ADDITION((a, b) -> (byte) (a + b), BlockDefinitionHolder.getPlusSign()),
    SUBTRACT((a, b) -> (byte) (a - b), BlockDefinitionHolder.getMinusSign()),
    MULTIPLICATION((a, b) -> (byte) (a * b), BlockDefinitionHolder.getMultiplicationSign()),
    DIVISION((a, b) -> (byte) (a / b), BlockDefinitionHolder.getDivisionSign())
    ;

    private final BiFunction<Byte, Byte, Byte> function;
    private final Block block;

    MathOperation(BiFunction<Byte, Byte, Byte> function, Block block) {
        this.function = function;
        this.block = block;
    }
    
    @Override
    public Byte accept(Byte previous, Byte next) {
        return function.apply(previous, next);
    }

    public static MathOperation byBlock(Block block, BlockPos pos) throws BLException {
        for (MathOperation operation : values()) {
            if (operation.block == block) {
                Utils.log("Getting operation '" + operation + "' of block '" + block + "' on pos '" + pos + "'");
                return operation;
            }
        }
        throw new BLException("Can't define operation of block " + block, pos);
    }
}
