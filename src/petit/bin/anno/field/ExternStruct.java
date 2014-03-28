package petit.bin.anno.field;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.anno.FieldObjectInstantiator;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStruct {
	
	/**
	 * Specify a method which is used for resolving concrete class instance of the field.<br />
	 * If the value is null the concrete class is treated as the field's type.<br />
	 * The method must be defined as the following signature.
	 * <pre>
	 * [Object which extends this field's type] [method name]({@link Object}, {@link Field})
	 * </pre>
	 * 
	 * @return name of concrete class resolver method
	 */
	public abstract String value();
	
	@SuppressWarnings("rawtypes")
	public static final class _MA extends MemberAccessor {
		
		private final BinaryAccessorFactory _ba_fac;
		
		private final BinaryAccessor _fields_type_ba;
		
		private final FieldObjectInstantiator _instor;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field field) throws Exception {
			super(field);
			_ba_fac = ba_fac;
			
			final ExternStruct anno = field.getAnnotation(ExternStruct.class);
			if (anno != null && anno.value() != null) {
				_instor = FieldObjectInstantiator.getResolver(field.getType(), field.getDeclaringClass(), anno.value());
				_fields_type_ba = null;
			} else {
				_instor = FieldObjectInstantiator.getResolver(field.getType(), null, null);
				_fields_type_ba = ba_fac.getBinaryAccessor(field.getType());
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException {
			try {
				final Object object = _instor.getConcreteClassInstance(inst, inst, _field);
				
				if (object == null) {
					_field.set(inst, null);
				} else if (_fields_type_ba == null) {
					final BinaryAccessor<Object> ba = _ba_fac.getBinaryAccessor(object.getClass());
					_field.set(inst, ba.readFrom(ctx, object, src));
				} else {
					_field.set(inst, _fields_type_ba.readFrom(ctx, object, src));
				}
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException {
			try {
				final Object obj = _field.get(inst);
				if (obj == null)
					throw new NullPointerException("Cannot write null field: " + _field + " is null");
				
				if (_fields_type_ba == null) {
					final BinaryAccessor ba = _ba_fac.getBinaryAccessor(obj.getClass());
					ba.writeTo(ctx, obj, dst);
				} else {
					_fields_type_ba.writeTo(ctx, obj, dst);
				}
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}
	
}
