package com.cicdez.blockline.code;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiFunction;

public enum BooleanOperation implements OperatedStream.Operator<Boolean> {
    NEGATIVE((a, b) -> !a, BlockDefinitionHolder.getBooleanNot()),
    AND((a, b) -> a & b, BlockDefinitionHolder.getBooleanAnd()),
    OR((a, b) -> a | b, BlockDefinitionHolder.getBooleanOr()),
    XOR((a, b) -> a ^ b, BlockDefinitionHolder.getBooleanXor()),
    ;
    
    private final BiFunction<Boolean, Boolean, Boolean> function;
    private final Block block;
    
    BooleanOperation(BiFunction<Boolean, Boolean, Boolean> function, Block block) {
        this.function = function;
        this.block = block;
    }
    
    @Override
    public Boolean accept(Boolean previous, Boolean next) {
        return function.apply(previous, next);
    }
    
    public static BooleanOperation byBlock(Block block, BlockPos pos) throws BLException {
        for (BooleanOperation operation : values()) {
            if (operation.block == block) {
                Utils.log("Getting operation '" + operation + "' of block '" + block + "' on pos '" + pos + "'");
                return operation;
            }
        }
        throw new BLException("Can't define operation of block " + block, pos);
    }
    
    
    public static enum NumberBooleanOperator implements OperatedStream.IOperator<Byte, Boolean> {
        EQUALS(Byte::equals, BlockDefinitionHolder.getComparisonEquals()),
        GREATER((a, b) -> a > b, BlockDefinitionHolder.getComparisonGreater()),
        LESS((a, b) -> a < b, BlockDefinitionHolder.getComparisonLess()),
        
        NULL((a, b) -> false, BlockDefinitionHolder.getEmpty())
        ;
        
        private final BiFunction<Byte, Byte, Boolean> function;
        private final Block block;
        
        NumberBooleanOperator(BiFunction<Byte, Byte, Boolean> function, Block block) {
            this.function = function;
            this.block = block;
        }
        
        public static NumberBooleanOperator byBlock(Block block, BlockPos pos) throws BLException {
            for (NumberBooleanOperator operation : values()) {
                if (operation.block == block) {
                    Utils.log("Getting operation '" + operation + "' of block '" + block + "' on pos '" + pos + "'");
                    return operation;
                }
            }
            throw new BLException("Can't define operation of block " + block, pos);
        }
        
        @Override
        public Boolean accept(Byte previous, Byte next) {
            return function.apply(previous, next);
        }
    }
}
