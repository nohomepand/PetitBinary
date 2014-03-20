package bin.test;

import java.lang.reflect.Method;

public class ReflectionTest {
	public static void main(String[] args) throws Exception {
		final Method m_str_obj = ReflectionTest.class.getDeclaredMethod("M", String.class, Object.class);
		final Method m_obj_str = ReflectionTest.class.getDeclaredMethod("M", Object.class, String.class);
		final Method m_obj_obj = ReflectionTest.class.getDeclaredMethod("M", Object.class, Object.class);
		Method m_str_str = null;
		try {
			m_str_str = ReflectionTest.class.getDeclaredMethod("M", String.class, String.class);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		System.out.println(m_str_obj);
		System.out.println(m_obj_str);
		System.out.println(m_obj_obj);
		System.out.println(m_str_str);
//		System.out.println(M("String", "String"));
	}
	
	public String M(String a1, Object a2) {
		return "M(String, Object)";
	}
	
	public String M(Object a1, String a2) {
		return "M(Object, String)";
	}
	
	public String M(Object a1, Object a2) {
		return "M(Object, Object)";
	}
	
}
