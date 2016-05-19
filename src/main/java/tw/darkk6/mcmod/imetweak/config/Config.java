package tw.darkk6.mcmod.imetweak.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import tw.darkk6.mcmod.imetweak.IMETweakMod;
import tw.darkk6.mcmod.imetweak.util.Lang;

public class Config {
	
	public static Config instance=null; 
	private static ArrayList<String> propOrder=null;
	public static Config getInstance(File file){
		if(instance==null) instance=new Config(file);
		return instance;
	}
	
	
	public Configuration file;
	// 一般使用者設定
	private Property mainEnabled;
	private Property notDisable;//沒有 TextField 的不要 disableIME , 一律改用切換 中/英 (Shift)
	private Property guiEnable;//那些 gui 是預設要將 中/英 模式切換為上次狀態的
	//ASM 設定
	private Property colorFix;
	//內部設定 - 那些 GUI Class 有,那些沒有
	private Property hasTextList;
	private Property noTextList;
	
	//方便存取用的 field
	public List<String> guiHasText,guiNoText,guiAutoSwitch;
	public boolean noDisable,modEnabled;
	public boolean useColorFix;
	
	private Config(File file){
		this.file = new Configuration(file);
		reload();
	}
	public void update(){
		save(true);
	}
	
	public void save(boolean reload){
		file.save();
		if(reload) reload();
	}
	
	//提供給  EventHandler.hasTextField() 儲存用的 method
	public void addGUICache(Class<? extends GuiScreen> clz , boolean withTextField){
		if(withTextField){
			guiHasText.add(clz.getName());
			hasTextList.set(guiHasText.toArray(new String[0]));
		}else{
			guiNoText.add(clz.getName());
			noTextList.set(guiNoText.toArray(new String[0]));
		}
		update();
	}
	
	private void reload() {
		
		this.file.load();
		
		boolean setupOrder = false;
		if(propOrder==null){
			setupOrder = true;
			propOrder=new ArrayList<String>();
		}
		
		file.setCategoryComment("general",Lang.get("imetweak.setting.general.comment"));
		
		this.mainEnabled = this.file.get("general", Lang.get("imetweak.setting.enable"), true,Lang.get("imetweak.setting.enable.comment"));
		this.mainEnabled.set(this.mainEnabled.getBoolean());
		
		this.notDisable = this.file.get("general", Lang.get("imetweak.setting.notDisable"), false,Lang.get("imetweak.setting.notDisable.comment"));
		this.notDisable.set(this.notDisable.getBoolean());
		
		this.guiEnable = this.file.get(
				"general",
				Lang.get("imetweak.setting.guiEnableIME"),
				IMETweakMod.DEFAULT_ENABLE_LIST,
				Lang.get("imetweak.setting.guiEnableIME.comment")
			);
		this.guiEnable.set(this.guiEnable.getStringList());
		
		this.colorFix = this.file.get("general", Lang.get("imetweak.setting.useColorFix") , false ,Lang.get("imetweak.setting.useColorFix.comment"));
		this.colorFix.set(this.colorFix.getBoolean());
		
		//設定順序
		if(setupOrder){
			propOrder.add(Lang.get("imetweak.setting.enable"));
			propOrder.add(Lang.get("imetweak.setting.notDisable"));
			propOrder.add(Lang.get("imetweak.setting.guiEnableIME"));
			propOrder.add(Lang.get("imetweak.setting.useColorFix"));
			file.setCategoryPropertyOrder("general", propOrder);
		}
		
		file.setCategoryComment("internal",Lang.get("imetweak.setting.internal.comment"));
		
		this.hasTextList = this.file.get(
				"internal",
				Lang.get("imetweak.setting.hasTextList"),
				new String[0],
				Lang.get("imetweak.setting.hasTextList.comment")
			);
		this.hasTextList.set(this.hasTextList.getStringList());
		
		this.noTextList = this.file.get(
				"internal",
				Lang.get("imetweak.setting.noTextList"),
				new String[0],
				Lang.get("imetweak.setting.noTextList.comment")
			);
		this.noTextList.set(this.noTextList.getStringList());
		
		//將資料寫入方便存取的 field
		modEnabled = mainEnabled.getBoolean();
		guiAutoSwitch = new ArrayList<String>(Arrays.asList(guiEnable.getStringList()));
		guiHasText = new ArrayList<String>(Arrays.asList(hasTextList.getStringList()));
		guiNoText = new ArrayList<String>(Arrays.asList(noTextList.getStringList()));
		noDisable = notDisable.getBoolean();
		useColorFix = colorFix.getBoolean();
		
		this.file.save();
	}
}
