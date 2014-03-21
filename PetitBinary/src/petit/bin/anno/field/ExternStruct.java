package petit.bin.anno.field;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import petit.bin.BinaryAccessor;
import petit.bin.BinaryAccessorFactory;
import petit.bin.MemberAccessor;
import petit.bin.SerializationContext;
import petit.bin.sinks.BinaryInput;
import petit.bin.sinks.BinaryOutput;
import petit.bin.util.ReflectionUtil;
import petit.bin.util.ReflectionUtil.VisibilityConstraint;

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
		
		private final Method _resolver;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field field) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
			super(field);
			_ba_fac = ba_fac;
			
			final String method_name;
			if (!field.isAnnotationPresent(ExternStruct.class) || (method_name = field.getAnnotation(ExternStruct.class).value()) == null) {
				_fields_type_ba = ba_fac.getBinaryAccessor(field.getType());
				_resolver = null;
			} else {
				_fields_type_ba = null;
				final List<Method> found_methods = ReflectionUtil.getVisibleMethods(field.getDeclaringClass(), VisibilityConstraint.INHERITED_CLASS_VIEWPOINT, method_name, null, Object.class, Field.class);
				if (found_methods.isEmpty())
					throw new IllegalArgumentException("Cannot find concrete class resolver method: Class " + method_name + "(Object, Field)");
				_resolver = found_methods.get(0);
				_resolver.setAccessible(true);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException {
			try {
				if (_resolver != null) {
					final BinaryAccessor ba;
					final Object fields_instance = _resolver.invoke(inst, inst, _field);
					if (fields_instance == null)
						throw new IOException("Cannot create a concrete class instance(resolver method returns null)");
					
					ba = _ba_fac.getBinaryAccessor(fields_instance.getClass());
					if (ba == null)
						throw new IOException("Concrete class resolver method returns null");
					_field.set(inst, ba.readFrom(fields_instance, ctx, src));
				} else {
					_field.set(inst, _fields_type_ba.readFrom(ctx, src));
				}
				
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException {
			try {
				_fields_type_ba.writeTo(ctx, _field.get(inst), dst);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}
	
}
