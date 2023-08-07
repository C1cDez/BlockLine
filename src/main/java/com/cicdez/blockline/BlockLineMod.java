package com.cicdez.blockline;

import com.cicdez.blockline.code.BlockDefinitionHolder;
import com.cicdez.blockline.code.CodeSession;
import com.cicdez.blockline.command.BlockLineCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

@Mod(modid = BlockLineMod.MOD_ID, name = BlockLineMod.NAME, version = BlockLineMod.VERSION)
public class BlockLineMod {
    public static final String MOD_ID = "blockline";
    public static final String NAME = "BlockLine Mod";
    public static final String VERSION = "1.0.0";

    public static Logger logger;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Initialization BLOCK_LINE");
        try {
            BlockDefinitionHolder.checkIsAuthorDump();
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    @EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new BlockLineCommand());
    }
}
