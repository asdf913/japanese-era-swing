import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NOP;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.function.FailableFunction;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;

class JapaneseEraJPanelTest {

	private static Method METHOD_GET_NAME, METHOD_GET_CLASS = null;

	@BeforeSuite
	void beforeSuite() throws NoSuchMethodException {
		//
		final Class<?> clz = JapaneseEraJPanel.class;
		//
		(METHOD_GET_NAME = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_GET_CLASS = clz.getDeclaredMethod("getClass", Object.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean put, test, add, containsKey;

		private Integer length;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			final String name = getName(method);
			//
			if (Boolean.logicalAnd(Objects.equals(name, "toString"),
					method != null && method.getParameterCount() == 0)) {
				//
				return null;
				//
			} // if
				//
			if (proxy instanceof Iterable) {
				//
				if (Objects.equals(name, "iterator")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "spliterator")) {
					//
					return null;
					//
				} // if
					//
			} // if
				//
			if (proxy instanceof Map) {
				//
				if (Objects.equals(name, "put")) {
					//
					return put;
					//
				} else if (Objects.equals(name, "get")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "entrySet")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "containsKey")) {
					//
					return containsKey;
					//
				} // if
					//
			} else if (proxy instanceof Predicate) {
				//
				if (Objects.equals(name, "test")) {
					//
					return test;
					//
				} // if
					//
			} else if (proxy instanceof ConstantPushInstruction) {
				//
				if (Objects.equals(name, "getValue")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof Entry) {
				//
				if (Objects.equals(name, "getValue")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "getKey")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof CharSequence) {
				//
				if (Objects.equals(name, "length")) {
					//
					return length;
					//
				} // if
					//
			} else if (proxy instanceof Collection) {
				//
				if (Objects.equals(name, "add")) {
					//
					return add;
					//
				} else if (Objects.equals(name, "stream")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof FailableFunction) {
				//
				if (Objects.equals(name, "apply")) {
					//
					return null;
					//
				} // if
					//
			} else if (proxy instanceof Stream) {
				//
				if (Objects.equals(name, "toList")) {
					//
					return null;
					//
				} else if (Objects.equals(name, "filter")) {
					//
					return proxy;
					//
				} // if
					//
			} // if
				//
			throw new Throwable(name);
			//
		}

	}

	private static String getName(final Member instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_NAME != null ? METHOD_GET_NAME.invoke(null, instance) : null;
			if (obj == null) {
				return null;
			} else if (obj instanceof String string) {
				return string;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Class<?> getClass(final Object instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_CLASS != null ? METHOD_GET_CLASS.invoke(null, instance) : null;
			if (obj == null) {
				return null;
			} else if (obj instanceof Class clz) {
				return clz;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testNull() throws Throwable {
		//
		final Method[] ms = JapaneseEraJPanel.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Object result = null;
		//
		String name, toString = null;
		//
		Object[] os = null;
		//
		Collection<Object> collection = null;
		//
		JapaneseEraJPanel instance = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else {
					//
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			if (Objects.equals(name = m.getName(), "getNumberValue")
					&& Arrays.equals(parameterTypes, new Class<?>[] { Instruction.class })) {
				//
				final Method m1 = m;
				//
				final Object[] os1 = os;
				//
				Assert.assertThrows(IllegalArgumentException.class, () -> Narcissus.invokeStaticBooleanMethod(m1, os1));
				//
				continue;
				//
			} // if
				//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = Narcissus.invokeStaticMethod(m, os);
				//
			} else {
				//
				result = Narcissus
						.invokeMethod(
								instance = ObjectUtils.getIfNull(instance,
										() -> (JapaneseEraJPanel) Narcissus.allocateInstance(JapaneseEraJPanel.class)),
								m, os);
				//
			} // if
				//
			if (contains(Arrays.asList(Boolean.TYPE, Integer.TYPE), m.getReturnType())
					|| Boolean.logicalAnd(
							contains(Arrays.asList("getJapaneseEraSinceDates", "getEraAbbreviationMap"), name),
							m.getParameterCount() == 0)
					|| Boolean.logicalAnd(Objects.equals(name, "getCharacterMapByNamePrefix"),
							Arrays.equals(parameterTypes, new Class<?>[] { String.class }))) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	@Test
	void testNotNull() throws Throwable {
		//
		final Method[] ms = JapaneseEraJPanel.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object result = null;
		//
		String name, toString = null;
		//
		Object[] os = null;
		//
		Collection<Object> collection = null;
		//
		JapaneseEraJPanel instance = null;
		//
		IH ih = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "newInstance"),
							Arrays.equals(parameterTypes, new Class<?>[] { Constructor.class, Object[].class }))) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			System.out.println(m);
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Boolean.logicalOr(Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE),
						Objects.equals(parameterType, Number.class))) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, FieldOrMethod.class)) {
					//
					add(collection, Narcissus.allocateInstance(Field.class));
					//
				} else if (Objects.equals(parameterType, Class.class)) {
					//
					add(collection, Class.class);
					//
				} else if (Objects.equals(parameterType, Member.class)) {
					//
					add(collection, Object.class.getDeclaredMethod("toString"));
					//
				} else if (Objects.equals(parameterType, Strings.class)) {
					//
					add(collection, Strings.CS);
					//
				} else if (Objects.equals(parameterType, DateFormat.class)) {
					//
					add(collection, new SimpleDateFormat());
					//
				} else if (Objects.equals(parameterType, Instruction.class)) {
					//
					add(collection, new NOP());
					//
				} else if (parameterType != null && parameterType.isArray()) {
					//
					add(collection, Array.newInstance(parameterType != null ? parameterType.componentType() : null, 0));
					//
				} else if (parameterType != null && parameterType.isInterface()) {
					//
					if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
						//
						ih.put = ih.test = ih.add = ih.containsKey = Boolean.TRUE;
						//
						ih.length = Integer.valueOf(0);
						//
					} // if
						//
					add(collection, Reflection.newProxy(parameterType, ih));
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(parameterType));
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			toString = Objects.toString(m);
			//
			if (Objects.equals(name = m.getName(), "getNumberValue")
					&& Arrays.equals(parameterTypes, new Class<?>[] { Instruction.class })) {
				//
				final Method m1 = m;
				//
				final Object[] os1 = os;
				//
				Assert.assertThrows(IllegalArgumentException.class, () -> Narcissus.invokeStaticBooleanMethod(m1, os1));
				//
				continue;
				//
			} // if
				//
			if (Modifier.isStatic(m.getModifiers())) {
				//
				result = Narcissus.invokeStaticMethod(m, os);
				//
			} else {
				//
				result = Narcissus
						.invokeMethod(
								instance = ObjectUtils.getIfNull(instance,
										() -> (JapaneseEraJPanel) Narcissus.allocateInstance(JapaneseEraJPanel.class)),
								m, os);
				//
			} // if
				//
			if (contains(Arrays.asList(Boolean.TYPE, Integer.TYPE), m.getReturnType())
					|| Boolean
							.logicalAnd(contains(Arrays.asList("getJapaneseEraSinceDates", "getEraAbbreviationMap"),
									name), m.getParameterCount() == 0)
					|| Boolean.logicalAnd(Objects.equals(name, "getClass"),
							Arrays.equals(parameterTypes, new Class<?>[] { Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getName"),
							Boolean.logicalOr(Arrays.equals(parameterTypes, new Class<?>[] { Class.class }),
									Arrays.equals(parameterTypes, new Class<?>[] { Member.class })))
					|| Boolean.logicalAnd(Objects.equals(name, "format"),
							Arrays.equals(parameterTypes, new Class<?>[] { DateFormat.class, Date.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "filter"),
							Arrays.equals(parameterTypes, new Class<?>[] { Stream.class, Predicate.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getInstructions"),
							Arrays.equals(parameterTypes, new Class<?>[] { InstructionList.class }))) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static boolean contains(final Collection<?> items, final Object item) {
		return items != null && items.contains(item);
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
	}

	private static void clear(final Collection<?> instance) {
		if (instance != null) {
			instance.clear();
		}
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

}