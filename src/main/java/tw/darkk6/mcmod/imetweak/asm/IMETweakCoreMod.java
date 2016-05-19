package tw.darkk6.mcmod.imetweak.asm;

import java.util.Map;

import tw.darkk6.mcmod.imetweak.config.Reference;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion(Reference.MC_VER)
@IFMLLoadingPlugin.Name(Reference.MOD_NAME)
@IFMLLoadingPlugin.TransformerExclusions({"tw.darkk6.mcmod.imetweak.asm"})
public class IMETweakCoreMod implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{IMETweakTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		//不需要，沒有要使用 DummyModContainer
		return null;
	}

	@Override
	public String getSetupClass() {
		//不需要
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		//暫時應該不需要
	}
	@Override
	public String getAccessTransformerClass() {
		//不需要
		return null;
	}

}
