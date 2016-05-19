package tw.darkk6.mcmod.imetweak.util;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface WinAPI extends StdCallLibrary{
	public static final WinAPI INSTANCE = (WinAPI)Native.loadLibrary("Imm32", WinAPI.class, W32APIOptions.UNICODE_OPTIONS);
	
	Pointer ImmGetContext(WinDef.HWND paramHWND);
	boolean ImmReleaseContext(WinDef.HWND paramHWND, Pointer paramPointer);
	
	int ImmGetOpenStatus(Pointer paramPointer);
	boolean ImmSetOpenStatus(Pointer paramPointer, boolean paramBoolean);
	
	Pointer ImmAssociateContext(WinDef.HWND paramHWND, int paramInt);
	Pointer ImmAssociateContext(WinDef.HWND paramHWND, Pointer paramPointer);

}
