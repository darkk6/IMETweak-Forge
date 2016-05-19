package tw.darkk6.mcmod.imetweak.util;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;

public class IME {
	
	//這個將在 init 的時候由 Util.doSearchMCWindow() 指派
	protected static WinDef.HWND MC_hWnd=null;
	
	private static WinAPI winapi = WinAPI.INSTANCE;
	private static IME instance = null;
	
	public static IME getInstance(){
		if(instance==null){
			if(MC_hWnd==null) Util.doSearchMCWindow();
			if(MC_hWnd==null) return null;
			instance = new IME(MC_hWnd);
		}
		return instance;
	}
	
	
	private WinDef.HWND hWnd = null;
	private Pointer lastHIMC = null;
	
	private IME(WinDef.HWND hWnd){
		this.hWnd=hWnd;
	}
	
/** 這兩個指的應為否允許使用 IME , 而非切換 **/
	public void disableIME(){
		if (lastHIMC != null) return;
		Pointer hImc = immGetContext();
		try{
			lastHIMC = winapi.ImmAssociateContext(hWnd, 0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			immReleaseContext(hWnd,hImc);
		}
	}
	public void enableIME(){
		if (lastHIMC == null) return;
		Pointer hImc = immGetContext();
		try{
			winapi.ImmAssociateContext(hWnd, lastHIMC);
			lastHIMC = null;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			immReleaseContext(hWnd,hImc);
		}
	}
/** 底下這兩個是在中文輸入模式下，是 "中" 還是 "A" **/
	public boolean isIMEOpen(){
		Pointer hImc = immGetContext();
		boolean result=false;
		try{
			result = (winapi.ImmGetOpenStatus(hImc) != 0);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			immReleaseContext(hWnd,hImc);
		}
		return result;
	}
	public void setIsIMEOpen(boolean isOpen){
		Pointer hImc = immGetContext();
		try{
			winapi.ImmSetOpenStatus(hImc, isOpen);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			immReleaseContext(hWnd,hImc);
		}
	}
	
	
	private Pointer immGetContext(){
		Pointer himc = winapi.ImmGetContext(hWnd);
		return himc;
	}
	private void immReleaseContext(WinDef.HWND hWnd,Pointer hImc){
		winapi.ImmReleaseContext(hWnd, hImc);
	}
}
