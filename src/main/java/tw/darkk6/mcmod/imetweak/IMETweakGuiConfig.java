package tw.darkk6.mcmod.imetweak;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import tw.darkk6.mcmod.imetweak.config.Config;
import tw.darkk6.mcmod.imetweak.config.Reference;
import tw.darkk6.mcmod.imetweak.util.Lang;

public class IMETweakGuiConfig extends GuiConfig {
	public IMETweakGuiConfig(GuiScreen parent) {
		super(parent,
				new ConfigElement(Config.instance.file.getCategory("general")).getChildElements(),
				Reference.MODID,
				false,//需要重新進入世界 ?
				false,//需要重新啟動 MC ?
				Lang.get("imetweak.setting.gui.title")//標題
			);
		// 設定標題 2 顯示文字
		// GuiConfig.getAbridgedConfigPath() 可以把檔案路徑改成  .minecraft/ 底下的對應路徑呈現
		this.titleLine2 = GuiConfig.getAbridgedConfigPath(Config.instance.file.toString());
	}
}
