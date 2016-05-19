package tw.darkk6.mcmod.imetweak.asm;

import tw.darkk6.mcmod.imetweak.config.Config;


public class InjectHandler {

	public static boolean handleIsAllowedCharacter(boolean original,char character){
		Config config=Config.instance;
		if(config==null) return original;
		if(!config.useColorFix) return original;
		if(character=='§') return true;
		return original;
		
	}
}
