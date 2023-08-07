package com.cicdez.blockline.code;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BlockDefinitionHolder {
    private static final ImmutableMap<Block, ICodeTask> TASKS_MAP = ImmutableMap.<Block, ICodeTask>builder()
            .put(getPrint(), CodeTasks.PRINT)
            .put(getCreateVariable(), CodeTasks.CREATE_VARIABLE)
            .put(getNumberOperation(), CodeTasks.NUMBER_OPERATOR)
            .put(getBooleanOperator(), CodeTasks.BOOLEAN_OPERATOR)
            .put(getNumberComparator(), CodeTasks.NUMBER_COMPARATOR)
            .put(getJump(), CodeTasks.JUMP)
            .put(getIf(), CodeTasks.IF_CONDITION)
            .build();
    
    
    public static ICodeTask getTask(Block block) {
        return TASKS_MAP.getOrDefault(block, CodeTasks.NOTHING);
    }
    
    
    public static Block getProgramStart() {
        return Blocks.DIAMOND_BLOCK;
    }
    public static Block getProgramEnd() {
        return Blocks.BEDROCK;
    }
    public static Block getJump() {
        return Blocks.SNOW;
    }
    
    public static Block getPrint() {
        return Blocks.COBBLESTONE;
    }
    
    public static Block getCreateVariable() {
        return Blocks.SANDSTONE;
    }
    public static Block getStringType() {
        return Blocks.STONEBRICK;
    }
    public static Block getByteType() {
        return Blocks.NETHER_BRICK;
    }
    public static Block getBooleanType() {
        return Blocks.END_BRICKS;
    }
    public static Block getVariableName() {
        return Blocks.BRICK_BLOCK;
    }

    public static Block getSeparator() {
        return Blocks.MAGMA;
    }
    
    public static Block getNumberOperation() {
        return Blocks.BOOKSHELF;
    }
    public static Block getPlusSign() {
        return Blocks.BONE_BLOCK;
    }
    public static Block getMinusSign() {
        return Blocks.NETHERRACK;
    }
    public static Block getMultiplicationSign() {
        return Blocks.CAULDRON;
    }
    public static Block getDivisionSign() {
        return Blocks.QUARTZ_BLOCK;
    }

    public static Block getBooleanOperator() {
        return Blocks.COAL_BLOCK;
    }
    public static Block getBooleanNot() {
        return Blocks.GOLD_ORE;
    }
    public static Block getBooleanAnd() {
        return Blocks.SOUL_SAND;
    }
    public static Block getBooleanOr() {
        return Blocks.JUKEBOX;
    }
    public static Block getBooleanXor() {
        return Blocks.PRISMARINE;
    }
    
    public static Block getNumberComparator() {
        return Blocks.EMERALD_BLOCK;
    }
    public static Block getComparisonEquals() {
        return Blocks.LAPIS_ORE;
    }
    public static Block getComparisonGreater() {
        return Blocks.PACKED_ICE;
    }
    public static Block getComparisonLess() {
        return Blocks.MYCELIUM;
    }
    
    public static Block getIf() {
        return Blocks.SEA_LANTERN;
    }
    
    public static Block getBytesEnd() {
        return Blocks.CLAY;
    }
    public static Block getEmpty() {
        return Blocks.AIR;
    }
    public static Block getTrue() {
        return Blocks.OBSIDIAN;
    }
    public static Block getFalse() {
        return Blocks.GLASS;
    }

    public static Block getSign() {
        return Blocks.REDSTONE_BLOCK;
    }



    public static void checkIsAuthorDump() throws InvocationTargetException, IllegalAccessException {
        Utils.log("Checking Blocks values");
        Map<Block, String> buffer = new HashMap<>();
        for (Method getter : BlockDefinitionHolder.class.getMethods()) {
            getter.setAccessible(true);
            if (getter.getReturnType() == Block.class) {
                Block block = (Block) getter.invoke(null);
                if (buffer.containsKey(block)) throw new RuntimeException("Author is dump! Method '" +
                        getter.getName() + "' return same block as method '" + buffer.get(block) + "'");
                buffer.put(block, getter.getName());
            }
        }
    }

    private static String TUTORIAL_INSTANCE = null;
    public static String getTutorial() {
        if (TUTORIAL_INSTANCE == null) {
            TUTORIAL_INSTANCE = generateTutorial();
        }
        return TUTORIAL_INSTANCE;
    }
    private static String generateTutorial() {
        Utils.log("Generate Tutorial");
        try {
            Map<Block, String> map = new HashMap<>();
            for (Method getter : BlockDefinitionHolder.class.getMethods()) {
                getter.setAccessible(true);
                if (getter.getReturnType() == Block.class) {
                    Block block = (Block) getter.invoke(null);
                    map.put(block, getter.getName().substring(3));
                }
            }
            return map.entrySet().stream().map(entry -> entry.getKey() + " = " + entry.getValue())
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
