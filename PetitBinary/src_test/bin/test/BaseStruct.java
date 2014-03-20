package bin.test;
import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.SerializationContext;
import petit.bin.StructByteOrder;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.sinks.impl.InOutByteBuffer;


@Struct(byteOrder = StructByteOrder.BIG_ENDIAN)
@SuppressWarnings("unused")
public abstract class BaseStruct {
	
	@StructMember(0)
	public long base0;
	
	@StructMember(1)
	protected int base1;
	
	@StructMember(2)
	private int base2;
	
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static class SubA extends BaseStruct {
		@StructMember(value = 3, marker = "Mark1")
		private int subA1;
		
		@StructMember(4)
		private int subA2;
	}
	
	@Struct(byteOrder = StructByteOrder.NEUTRAL)
	public static class SubB extends BaseStruct {
		@StructMember(3)
		public int subB1;
	}
	
	public static void main(String[] args) throws Exception {
		final BinaryAccessorFactory baf = new BinaryAccessorFactory();
		final BinaryAccessor<SubA> baSubA = baf.getBinaryAccessor(SubA.class);
		final BinaryAccessor<SubB> baSubB = baf.getBinaryAccessor(SubB.class);
		
		final InOutByteBuffer buf = new InOutByteBuffer();
		final SubA subA = new SubA();
		subA.base0 = 0x10000001;
		subA.subA1 = 0x20000002;
		subA.subA2 = 0x30000003;
		final SerializationContext ctx = new SerializationContext();
		baSubA.writeTo(ctx, subA, buf);
		System.out.println(ctx.getMarker());
		Hoge.showBuffer(buf.getFlippedShallowCopy());
	}
	
}
