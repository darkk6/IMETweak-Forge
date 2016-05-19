package tw.darkk6.mcmod.imetweak;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import jline.internal.Log;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import tw.darkk6.mcmod.imetweak.config.Config;
import tw.darkk6.mcmod.imetweak.config.Reference;
import tw.darkk6.mcmod.imetweak.util.Util;

@Mod(modid = Reference.MODID, version = Reference.MOD_VER, name = Reference.MOD_NAME, clientSideOnly = true , guiFactory = Reference.GUI_FACTORY)
public class IMETweakMod {
	@Instance(Reference.MODID)
	public static IMETweakMod mod;
	
	public static final String[] DEFAULT_ENABLE_LIST=new String[]{
				"net.minecraft.client.gui.inventory.GuiEditSign",
				"net.minecraft.client.gui.GuiScreenBook",
				"net.minecraft.client.gui.GuiChat"
			};
	
	private static int my_pid=0;
	public static int getProcessID() {
		if (my_pid == 0) {
			RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
			String vmName = bean.getName();
			int pid = Integer.valueOf(vmName.split("@")[0]).intValue();
			my_pid = pid;
		}
		return my_pid;
	}

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		Config.getInstance(e.getSuggestedConfigurationFile());
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		if (!Util.isWindows()) {
			Log.info("OS is not Windows, IME Tweak mod is disabled");
			return;
		}
		Util.doSearchMCWindow();
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}
}
