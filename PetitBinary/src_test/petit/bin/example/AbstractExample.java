package petit.bin.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.anno.StructMember;
import petit.bin.sinks.impl.InOutByteBuffer;
import petit.bin.util.ReflectionUtil;
import petit.bin.util.ReflectionUtil.VisibilityConstraint;

public abstract class AbstractExample {
	
	/**
	 * an instance of {@link BinaryAccessorFactory}
	 */
	public static final BinaryAccessorFactory FACTORY;
	
	private static final char[][] HEX_TABLE;
	
	static {
		HEX_TABLE = new char[0x100][2];
		for (int i = 0; i < 0x100; i++) {
			HEX_TABLE[i][0] = "0123456789ABCDEF".charAt(i / 0x10);
			HEX_TABLE[i][1] = "0123456789ABCDEF".charAt(i % 0x10);
		}
		
		BinaryAccessorFactory tmp = null;
		try {
			tmp = new BinaryAccessorFactory();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Cannot create " + BinaryAccessorFactory.class.getCanonicalName());
			System.exit(0);
		}
		FACTORY = tmp;
	}
	
	/**
	 * Returns a formatted hex-decimal string. The following text will be returned.
	 * <pre>
	 *       | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +A +B  +C +D +E +F
	 *      0| E3 81 82 E3  81 82 E3 81  82 E3 81 82  E3 81 82 E3 | ã  ã  ã  ã  ã  ã|
	 *     10| 81 82 E3 81  82 E3 81 82  E3 81 82 E3  81 82 E3 81 |   ã  ã  ã  ã  ã |
	 *     20| 82 E3 81 82  E3 81 82 E3  81 82 E3 81  82 E3 81 82 |  ã  ã  ã  ã  ã  |
	 *     30| ...
	 * </pre>
	 * 
	 * @param buf a buffer to display
	 * @return a formatted hex-decimal string
	 */
	public static final String dumpData(final ByteBuffer buf) {
		final String header = "      | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +A +B  +C +D +E +F";
		final char[] line_hex = new char[3 * 0x10/* hex string */ + 1 * 4 /* and paddings ' ' */];
		final char[] line_chr = new char[1 * 0x10/* character  */];
		final StringWriter sw = new StringWriter();
		final PrintWriter ps = new PrintWriter(sw);
		int ptr = 0;
		
		while (buf.remaining() > 0) {
			if ((ptr % 0x10) == 0)
				ps.println(header);
			ps.printf("%6X|", ptr * 0x10L);
			
			Arrays.fill(line_hex, ' ');
			Arrays.fill(line_chr, ' ');
			for (int i = 0, hex_idx = 0, chr_idx = 0; buf.remaining() > 0 && i <= 0xf; i++) {
				final int v = buf.get() & 0xff;
				if ((i % 4) == 0)
					hex_idx++;
				line_hex[hex_idx++] = HEX_TABLE[v][0];
				line_hex[hex_idx++] = HEX_TABLE[v][1];
				hex_idx++;
				
				if (Character.isDigit(v) || Character.isLetter(v))
					line_chr[chr_idx++] = (char) v;
				else
					chr_idx++;
			}
			
			ps.printf("%s| %s|", new String(line_hex), new String(line_chr));
			if (buf.remaining() > 0)
				ps.println();
			ptr++;
		}
		
		return sw.toString();
	}
	
	/**
	 * Tests serialization.
	 * This method serialize "ao" to a "buffer",
	 * deserialize the "buffer" to "obj",
	 * and then checks whether all fields of "ao" and "obj" are equal to another or not.
	 * 
	 * @param ao an object to check
	 * @return serialized data
	 * @throws RuntimeException
	 */
	public static final ByteBuffer checkSerializedObject(final Object ao) throws RuntimeException {
		if (ao == null)
			throw new NullPointerException("Argument ao must not be null");
		
		try {
			final BinaryAccessor<Object> ba = FACTORY.getBinaryAccessor(ao.getClass());
			final InOutByteBuffer buf = new InOutByteBuffer();
			
			// serialize to buf
			ba.writeTo(null, ao, buf);
			
			// deserialize to obj
			final Object obj = ba.readFrom(null, new InOutByteBuffer(buf.getFlippedShallowCopy()));
			
			// check all of the ao's fields are equal to obj
			for (final Field field : ReflectionUtil.getVisibleFields(ao.getClass(), VisibilityConstraint.INHERITED_CLASS_VIEWPOINT, null, null)) {
				if (!field.isAnnotationPresent(StructMember.class)) {
					System.out.println(field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " : skip");
					continue;
				}
				field.setAccessible(true);
				final Object ao_field = field.get(ao);
				final Object obj_field = field.get(obj);
				System.out.println(field.getDeclaringClass().getCanonicalName() + "#" + field.getName() +
						" : " + ((ao_field == null ? obj_field == null : ao_field.equals(obj_field)) ? "ok" : (ao_field + " != " + obj_field)));
			}
			return buf.getFlippedShallowCopy();
		} catch (Exception e) {
			throw new RuntimeException("Exception caused while checking read/write object", e);
		}
	}
	
}
