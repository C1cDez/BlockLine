package com.cicdez.blockline.code;

import net.minecraft.command.CommandException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;

public class BLException extends IOException {
    public BLException(String msg, BlockPos pos) {
        super(msg + (pos != null ? " (on " + pos + ")" : ""));
    }

    public CommandException toCommandException() {
        return new CommandException(this.getMessage());
    }
}
