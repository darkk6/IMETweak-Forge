package tw.darkk6.mcmod.imetweak.asm;

import java.util.ArrayList;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import tw.darkk6.mcmod.imetweak.util.Log;

public class IMETweakTransformer implements IClassTransformer {

	private static final boolean isDev = Boolean.parseBoolean(System.getProperty("isDev","false"));
	//目標 Class 的 Full ClassName
	private static final String TARGET_CLASS = "net.minecraft.util.ChatAllowedCharacters";
	private static final String TARGET_CLASS_SRG = "f";
	
	//目標 Method 的各種可能名稱
	private static final String REAL_METHOD_NAME = "isAllowedCharacter";
	private static final String OBS_METHOD_NAME = "func_71566_a";//透過 MCP conf 檔案查出
	private static final String SRG_METHOD_NAME = "a";//透過 MCP conf 與 srg 檔案查出
	
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		//只有底下這個 Class 需要 Patch，不是就直接返回
		if( !TARGET_CLASS.equals(transformedName) ) return basicClass;		
		Log.info("Core mod class found : {"+name+"} as known ["+transformedName+"]");
		return startPatch(basicClass);
	}

	private byte[] startPatch(byte[] bytes){
		//建立 ClassNode 與 ClassReader 準備存取這個 Class (尋找 Methd)
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		/*尋找我們要的 method , 實際遊戲階段， method name 有可能會是 func_xxxx 或者 srg 模式，
		 * 可以透過先把所有 method 列出來的方法確認到底為何；也可以直接從 MCP 的 srg,exc 檔案交叉比對
		 * 把所有的可能都放進去
		 */
		ArrayList<String> methodNameList=new ArrayList<String>();
		methodNameList.add(REAL_METHOD_NAME);
		methodNameList.add(OBS_METHOD_NAME);
		methodNameList.add(SRG_METHOD_NAME);
		
		// 目標的 signature 是 (C)Z => boolean isAllowedCharacter(char)
		// C 代表 char , Z 代表 boolean
		String desc = "(C)Z"; // desc => descriptor
		MethodNode method = null;
		for (MethodNode m : classNode.methods) {
			if( methodNameList.contains(m.name) && (m.desc.equals(desc))){
				method = m;
				break;
			}
		}
		//如果沒有找到 Method , 要傳回原本的資料
		if (method == null) {
			Log.info("Can not find method "+REAL_METHOD_NAME+" !!");
			return bytes;
		}
		//找到 Method , 準備進行 Inject
		Log.info("Inside Method : "+method.name+desc);
		
		//先找到 IRETURN
		InsnList insnList = method.instructions;
		AbstractInsnNode targetNode = null;
		// IRETURN 是 1 byte code , 根據 ASM 分類 type 是屬於 INSN
		for (int i = 0; i < insnList.size(); i++) {
			AbstractInsnNode node = insnList.get(i);
			if (node.getType() == AbstractInsnNode.INSN && node.getOpcode() == Opcodes.IRETURN) {
				targetNode = node;
				break;
			}
		}
		//如果沒有找到我們要的 node 就傳回原本的內容
		if (targetNode == null) {
			Log.info("Can not find target \"IRETURN\"!!");
			return bytes;
		}
		/*
		 * 根據 Java Byte Code , invokestatic 會從 stack 取出對應參數數量的數值來呼叫該 method
		 * 到 IRETURN 之前， stack 應該儲存了原本的結果，因此這邊先 ILOAD_0 可以取出傳入 isAllowedCharacter
		 * 的字元為何後， stack 中就有  ori_result,char_in ，接著以這兩個做為參數呼叫 Handler
		 * 再將這兩個 Opcode 依序插入 IRETURN 之前即可 (注意順序是 boolean , char)
		 */
		AbstractInsnNode iload=new VarInsnNode(Opcodes.ILOAD, 0);
		AbstractInsnNode invoke=new MethodInsnNode(
											Opcodes.INVOKESTATIC, 
											"tw/darkk6/mcmod/imetweak/asm/InjectHandler", 
											"handleIsAllowedCharacter", 
											"(ZC)Z",
											false
										);
		InsnList inject=new InsnList();
		inject.add(iload);
		inject.add(invoke);
		insnList.insertBefore(targetNode,inject);
		//注入 Code 完成，取得完整 Bytecode
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		byte[] result = writer.toByteArray();
		if (result == null) {
			Log.info("Inject Fail!!");
			return bytes;
		}
		Log.info("Inject DONE");
		
		return result;
	}
}
