package petit.bin.anno.field.array;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.anno.ArraySizeIndicator;
import petit.bin.anno.DefaultFieldAnnotationType;
import petit.bin.anno.SupportType;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@DefaultFieldAnnotationType(int[].class)
@SupportType(int[].class)
public @interface Int32Array {
	
	public static final class _MA extends MemberAccessor {
		
		private final ArraySizeIndicator _size_ind;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) throws Exception {
			super(f);
			_size_ind = ArraySizeIndicator.getArraySizeIndicator(f);
		}
		
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final int size = _size_ind.getArraySize(src, inst, _field);
				int[] ar = (int[]) _field.get(inst);
				if (ar == null || ar.length != size)
					ar = new int[size];
				for (int i = 0; i < ar.length; ar[i++] = src.readInt32());
				_field.set(inst, ar);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			final int[] ar = (int[]) _field.get(inst);
			if (ar == null)
				return;
			
			for (int i = 0; i < ar.length; dst.writeInt32(ar[i++]));
		}
		
	}
	
}
