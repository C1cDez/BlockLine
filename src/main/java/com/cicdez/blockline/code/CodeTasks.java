package com.cicdez.blockline.code;

import net.minecraft.util.EnumFacing;

import java.util.concurrent.atomic.AtomicReference;

public final class CodeTasks {
    public static final ICodeTask NOTHING = (session, server, world, facing, facings, sender, current) -> Utils.log("NOTHING");
    
    public static final ICodeTask PRINT = (session, server, world, facing, facings, sender, current) -> {
        int fIndex = world.getBlockState(current.offset(facings[0])).getBlock() !=
                BlockDefinitionHolder.getEmpty() ? 0 : 1;
        EnumFacing vFacing = facings[fIndex];
        
        boolean variableMode = world.getBlockState(current.up(1)).getBlock() == BlockDefinitionHolder.getCreateVariable();
        
        if (variableMode) {
            int name = Utils.getName(current, world, vFacing);
            Object variableValue = session.variables.getVariable(name);
            if (variableValue != null) Utils.print(sender, variableValue.toString());
            else throw new BLException("Variable '" + name + "' is not found!", current);
        } else {
            StringBuilder text = new StringBuilder();
            Utils.readBytes(current, world, vFacing, (b, i) -> text.append((char) (byte) b));
            Utils.print(sender, text.toString());
        }
    };
    
    public static final ICodeTask CREATE_VARIABLE = (session, server, world, facing, facings, sender, current) -> {
        int fIndex = world.getBlockState(current.offset(facings[0])).getBlock() ==
                BlockDefinitionHolder.getVariableName() ? 0 : 1;
        EnumFacing nameFacing = facings[fIndex], valueFacing = facings[1 - fIndex];
        
        int name = Utils.getName(current, world, nameFacing);
        Object value = Utils.getValue(current, world, valueFacing);

        session.variables.putVariable(name, value);
    };
    
    public static final ICodeTask NUMBER_OPERATOR = (session, server, world, facing, facings, sender, current) -> {
        EnumFacing nameFacing = Utils.getSignedFacing(facings[0], facings[1], world, current),
                valueFacing = nameFacing.getOpposite();

        int name = Utils.getName(current, world, nameFacing);
        byte value = Utils.calculateNumberOperationOfVariables(session.variables, current, world, valueFacing);

        session.variables.putVariable(name, value);
    };

    public static final ICodeTask BOOLEAN_OPERATOR = (session, server, world, facing, facings, sender, current) -> {
        EnumFacing nameFacing = Utils.getSignedFacing(facings[0], facings[1], world, current),
                valueFacing = nameFacing.getOpposite();
        
        int name = Utils.getName(current, world, nameFacing);
        boolean value = Utils.calculateBooleanOperations(session.variables, current, world, valueFacing);
        
        session.variables.putVariable(name, value);
    };
    
    public static final ICodeTask JUMP = (session, server, world, facing, facings, sender, current) -> {
        int fIndex = world.getBlockState(current.offset(facings[0])).getBlock() !=
                BlockDefinitionHolder.getEmpty() ? 0 : 1;
        EnumFacing valueFacing = facings[fIndex];
        
        boolean backward = world.getBlockState(current.up(1)).getBlock() == BlockDefinitionHolder.getTrue();
        
        AtomicReference<Byte> countRef = new AtomicReference<>((byte) 0);
        Utils.readBytes(current, world, valueFacing, (b, i) -> countRef.set(b));
        byte count = countRef.get();
        
        session.jump(backward, count);
    };
    
    public static final ICodeTask IF_CONDITION = (session, server, world, facing, facings, sender, current) -> {
        EnumFacing conditionFacing = Utils.getSignedFacing(facings[0], facings[1], world, current),
                elseSkipFacing = conditionFacing.getOpposite();
        Utils.log("Condition facing: " + conditionFacing + ", skip facing: " + elseSkipFacing);
        
        boolean condition = Utils.calculateBooleanOperations(session.variables, current, world, conditionFacing);
        Utils.log("Condition: " + condition);
        
        if (!condition) {
            byte skip = Utils.calculateNumberOperationOfVariables(session.variables, current, world, elseSkipFacing);
            session.jump(false, skip);
        }
    };
    
    public static final ICodeTask NUMBER_COMPARATOR = (session, server, world, facing, facings, sender, current) -> {
        EnumFacing nameFacing = Utils.getSignedFacing(facings[0], facings[1], world, current),
                valueFacing = nameFacing.getOpposite();
        
        int name = Utils.getName(current, world, nameFacing);
        boolean value = Utils.calculateBooleanNumberOperations(session.variables, current, world, valueFacing);
        
        session.variables.putVariable(name, value);
    };
}
