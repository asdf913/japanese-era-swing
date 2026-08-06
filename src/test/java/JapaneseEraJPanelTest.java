import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.apache.bcel.generic.Instruction;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.toolfactory.narcissus.Narcissus;

public class JapaneseEraJPanelTest {

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