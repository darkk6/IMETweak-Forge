package tw.darkk6.mcmod.imetweak;

import java.lang.reflect.Field;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tw.darkk6.mcmod.imetweak.config.Config;
import tw.darkk6.mcmod.imetweak.config.Reference;
import tw.darkk6.mcmod.imetweak.util.IME;

@SuppressWarnings("rawtypes")
public class EventHandler{
	private static Config config = Config.instance;
	
//============== Mod 設定檔儲存事件 =================
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e){
		if (Reference.MODID.equals(e.getModID())) {
			config.update();
			//如果停用這個 Mod , 就把 IME 給啟用
			if(!config.modEnabled){
				IME ime=IME.getInstance();
				if(ime==null) return;
				ime.enableIME();
			}
		}
	}
	
//==============  Open Gui 事件 =================
	//上一次的 GUI 是哪種 IME 模式(t中文/f英數)
	private boolean lastStatus=false;
	private Class lastGUIClass = null;
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent e){
		if(!config.modEnabled) return;
		IME ime=IME.getInstance();
		if(ime==null) return;
		GuiScreen gui=e.getGui();
		if( gui==null || gui.getClass()!=lastGUIClass){
			//切換 GUI 了 , 上一個 GUI 是否是要記錄狀態的 GUI
			if(lastGUIClass!=null && config.guiAutoSwitch.contains(lastGUIClass.getName()))
				lastStatus = ime.isIMEOpen();//是，就記錄剛剛的狀態
			lastGUIClass = (gui==null) ? null : gui.getClass();
		}
		
		if(hasTextField(gui)){
			//如果有 文字輸入區，就啟用輸入法
			ime.enableIME();
			//若是要自動切回的，就切到上次的狀態
			if(config.guiAutoSwitch.contains(gui.getClass().getName()))
				ime.setIsIMEOpen(lastStatus);
			else ime.setIsIMEOpen(false);
		}else{
			//沒有文字輸入區或者遊戲中(gui=null)
			if(config.noDisable){
				ime.enableIME();
				ime.setIsIMEOpen(false);
			}else
				ime.disableIME();
		}
	}
		
	/*
	 * 	判斷順序：
	 * 		GUI => null false
	 * 		是否在 autoSwitch 名單 , yes = true
	 * 		是否在 hasText , yes = true
	 * 		是否在 noText , yes = false
	 * 		透過 reflection 檢查是否有 TextField , yes/no = true/false
	 * 		程式出錯 true
	 */
	private boolean hasTextField(GuiScreen gui){
		if(gui==null) return false;
		if(config.guiAutoSwitch.contains(gui.getClass().getName())) return true;
		else if(config.guiHasText.contains(gui.getClass().getName())) return true;
		else if(config.guiNoText.contains(gui.getClass().getName())) return false;
		//如果沒有儲存就判斷
		try{
			Class<? extends GuiScreen> cls=gui.getClass();
			Field[] fields=cls.getDeclaredFields();
			for(Field f:fields){
				try{
					if(f.getType() == GuiTextField.class){
						config.addGUICache(cls, true);
						return true;
					}
				}catch(Exception e){}
			}
			config.addGUICache(cls, false);
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return true;//怕會誤判，所以傳回 true
		}
	}
}
