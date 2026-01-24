package net.quepierts.els;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(EndfieldLoginScreenMod.MODID)
public class EndfieldLoginScreenMod {
    public static final String MODID = "els";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EndfieldLoginScreenMod(IEventBus modEventBus, ModContainer modContainer) {

    }
}
