import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.JapaneseEra;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.SIPUSH;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.google.common.base.Strings;

import io.github.toolfactory.narcissus.Narcissus;

public class JapaneseEraJPanel extends JPanel {

	private static final long serialVersionUID = 1810789541222187125L;

	private JapaneseEraJPanel()
			throws InstantiationException, IllegalAccessException, InvocationTargetException, ParseException {
		//
		init();
		//
	}

	private void init()
			throws InstantiationException, IllegalAccessException, InvocationTargetException, ParseException {
		//
		final Map<String, YearMonthDay> yearMonthDays = getJapaneseEraSinceDates();
		//
		final DefaultTableModel dtm = new DefaultTableModel(
				new Object[] { "", "Abbr", "Name", "Emoji", "Year", "Month", "Day" }, 0);
		//
		final JTable jTable = new JTable(dtm);
		//
		final JScrollPane jsp = new JScrollPane(jTable);
		//
		try {
			//
			if (Narcissus.getObjectField(this, Container.class.getDeclaredField("component")) == null) {
				//
				return;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		add(jsp);
		//
		if (entrySet(yearMonthDays) != null) {
			//
			Constructor<?> constructor = null;
			//
			YearMonthDay yearMonthDay = null;
			//
			DateFormat df1 = null, df2 = null;
			//
			final Map<String, Character> characterMap = getCharacterMapByNamePrefix("SQUARE ERA NAME");
			//
			final String commonPrefix = commonPrefix(characterMap.keySet());
			//
			Iterable<Entry<String, Character>> entrySet = null;
			//
			String key;
			//
			final Map<Object, Object> eraAbbreviationMap = getEraAbbreviationMap();
			//
			List<Entry<Object, Object>> eraAbbreviationList = null;
			//
			Entry<Object, Object> eraAbbreviationEntry = null;
			//
			for (final Entry<String, YearMonthDay> entry : entrySet(yearMonthDays)) {
				//
				if ((yearMonthDay = getValue(entry)) == null) {
					//
					continue;
					//
				} // if
					//
				key = getKey(entry);
				//
				if (df1 == null) {
					//
					if (constructor == null) {
						//
						final List<Constructor<?>> list = toList(filter(
								testAndApply(Objects::nonNull, Locale.class.getConstructors(), Arrays::stream, null),
								c -> c != null && Arrays.equals(c.getParameterTypes(),
										new Class<?>[] { String.class, String.class, String.class })));
						//
						if (IterableUtils.size(list) > 1) {
							//
							throw new IllegalStateException();
							//
						} else if (IterableUtils.size(list) == 1) {
							//
							constructor = IterableUtils.get(list, 0);
							//
						} // if
							//
					} // if
						//
					df1 = new SimpleDateFormat("G", cast(Locale.class, newInstance(constructor, "ja", "JP", "JP")));
					//
				} // if
					//
				if (IterableUtils.size(eraAbbreviationList = toList(filter(stream(entrySet(eraAbbreviationMap)),
						x -> Objects.equals(StringUtils.upperCase(Objects.toString(getKey(x))), getKey(entry))))) > 1) {
					//
					throw new IllegalStateException();
					//
				} // if
					//
				eraAbbreviationEntry = testAndApply(x -> IterableUtils.size(x) == 1, eraAbbreviationList,
						x -> IterableUtils.get(x, 0), null);
				//
				dtm.addRow(new Object[] { key, getValue(eraAbbreviationEntry),
						format(df1,
								parse(df2 = ObjectUtils.getIfNull(df2, () -> new SimpleDateFormat("yyyyMMdd")),
										Objects.toString(yearMonthDay))),
						getCharacter(entrySet = ObjectUtils.getIfNull(entrySet, () -> entrySet(characterMap)),
								commonPrefix, key),
						yearMonthDay.year, yearMonthDay.month, yearMonthDay.day });
				//
			} // for
				//
		} // if
			//
		jsp.setPreferredSize(new Dimension((int) jsp.getPreferredSize().getWidth(),
				(dtm.getRowCount() + 1) * (jTable.getRowHeight() + 2)));
		//
	}

	private static <K, V> Collection<Entry<K, V>> entrySet(final Map<K, V> instance) {
		return instance != null ? instance.entrySet() : null;
	}

	private static Character getCharacter(final Iterable<Entry<String, Character>> entries, final String commonPrefix,
			final String entryKey) {
		//
		final List<Entry<String, Character>> list = toList(filter(
				testAndApply(Objects::nonNull, entries != null ? entries.spliterator() : null,
						x -> StreamSupport.stream(x, false), null),
				x -> getKey(x) != null && getKey(x).endsWith(entryKey)));
		//
		if (IterableUtils.size(list) == 1) {
			//
			return getValue(IterableUtils.get(list, 0));
			//
		} else if (entries != null) {
			//
			String k, s;
			//
			for (final Entry<String, Character> entry2 : entries) {
				//
				if (entry2 == null) {
					//
					continue;
					//
				} // if
					//
				if (startsWith(org.apache.commons.lang3.Strings.CS, k = getKey(entry2), commonPrefix)
						&& StringUtils.isNotBlank(
								Strings.commonPrefix(s = StringUtils.substringAfter(k, commonPrefix), entryKey))
						&& StringUtils.isNotBlank(Strings.commonSuffix(s, entryKey))) {
					//
					return getValue(entry2);
					//
				} // if
					//
			} // for
				//
			for (final Entry<String, Character> entry2 : entries) {
				//
				if (entry2 == null) {
					//
					continue;
					//
				} // if
					//
				if (StringUtils.isNotBlank(
						Strings.commonPrefix(StringUtils.substringAfter(getKey(entry2), commonPrefix), entryKey))) {
					//
					return getValue(entry2);
					//
				} // if
					//
			} // for
				//
		} // if
			//
		return null;
		//
	}

	private static <K> K getKey(final Entry<K, ?> instance) {
		return instance != null ? instance.getKey() : null;
	}

	private static <T> Stream<T> stream(final Collection<T> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static boolean startsWith(final org.apache.commons.lang3.Strings instance, final CharSequence str,
			final CharSequence prefix) {
		return instance != null && instance.startsWith(str, prefix);
	}

	private static <V> V getValue(final Entry<?, V> instance) {
		return instance != null ? instance.getValue() : null;
	}

	private static String commonPrefix(final Iterable<String> iterable) {
		//
		boolean first = true;
		//
		String commonPrefix = null;
		//
		for (int i = 0; i < IterableUtils.size(iterable) - 1; i++) {
			//
			if (first) {
				//
				commonPrefix = Strings.commonPrefix(IterableUtils.get(iterable, i), IterableUtils.get(iterable, i + 1));
				//
				first = false;
				//
			} else {
				//
				commonPrefix = Strings.commonPrefix(commonPrefix, IterableUtils.get(iterable, i));
				//
			} // if
				//
		} // for
			//
		return commonPrefix;
		//
	}

	private static Map<String, Character> getCharacterMapByNamePrefix(final String prefix) {
		//
		Map<String, Character> map = null;
		//
		String name = null;
		//
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			//
			try {
				//
				if (startsWith(org.apache.commons.lang3.Strings.CS, name = Character.getName(i), prefix)) {
					//
					put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), name, Character.valueOf((char) i));
					//
				} // if
					//
			} catch (final IllegalArgumentException e) {
				//
				break;
				//
			} // try
				//
		} // for
			//
		return map;
		//
	}

	private static Date parse(final DateFormat instance, final String string) throws ParseException {
		return instance != null ? instance.parse(string) : null;
	}

	private static String format(final DateFormat instance, final Date date) {
		return instance != null ? instance.format(date) : null;
	}

	private static <T> T newInstance(final Constructor<T> instance, final Object... args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return instance != null ? instance.newInstance(args) : null;
	}

	private static class YearMonthDay {

		private int year, month, day;

		@Override
		public String toString() {
			//
			final StringBuilder sb = new StringBuilder(StringUtils.leftPad(Integer.toString(year), 4, '0'));
			//
			sb.append(StringUtils.leftPad(Integer.toString(month), 2, '0'));
			//
			return Objects.toString(sb.append(StringUtils.leftPad(Integer.toString(day), 2, '0')));
			//
		}

	}

	private static Map<String, YearMonthDay> getJapaneseEraSinceDates() {
		//
		Map<String, YearMonthDay> map = null;
		//
		final Class<?> clz = JapaneseEra.class;
		//
		try (final InputStream is = getResourceAsStream(clz,
				String.format("/%1$s.class", replace(getName(clz), ".", "/")))) {
			//
			final List<Method> ms = toList(filter(Arrays.stream(getMethods(new ClassParser(is, null).parse())),
					m -> Objects.equals(getName(m), "<clinit>")));
			//
			final int size = IterableUtils.size(ms);
			//
			if (size > 1) {
				//
				throw new IllegalStateException();
				//
			} // if
				//
			final Method m = size == 1 ? IterableUtils.get(ms, 0) : null;
			//
			final Instruction[] ins = m != null ? getInstructions(new MethodGen(m, null, null).getInstructionList())
					: null;
			//
			Instruction in = null;
			//
			SIPUSH sipush = null;
			//
			final int length = ins != null ? ins.length : 0;
			//
			Number year, month, day = null;
			//
			YearMonthDay yearMonthDay = null;
			//
			PUTSTATIC putStatic = null;
			//
			ConstantPoolGen cpg = null;
			//
			for (int i = 0; ins != null && i < length; i++) {
				//
				if ((in = ArrayUtils.get(ins, i)) == null) {
					//
					continue;
					//
				} // if
					//
				if ((sipush = cast(SIPUSH.class, in)) != null && length > i + 5) {
					//
					year = getValue(sipush);
					//
					month = getNumberValue(ArrayUtils.get(ins, i + 1));
					//
					day = getNumberValue(ArrayUtils.get(ins, i + 2));
					//
					if ((putStatic = cast(PUTSTATIC.class, ArrayUtils.get(ins, i + 5))) != null) {
						//
						if (year == null || month == null || day == null) {
							//
							continue;
							//
						} // if
							//
						(yearMonthDay = new YearMonthDay()).year = year.intValue();
						//
						yearMonthDay.month = month.intValue();
						//
						yearMonthDay.day = day.intValue();
						//
						if (cpg == null && m != null) {
							//
							cpg = new ConstantPoolGen(m.getConstantPool());
							//
						} // if
							//
						put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), putStatic.getFieldName(cpg),
								yearMonthDay);
						//
					} // if
						//
				} // if
					//
			} // for
				//
		} catch (final IOException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return map;
		//
	}

	private static Method[] getMethods(final JavaClass instance) {
		return instance != null ? instance.getMethods() : null;
	}

	private static Instruction[] getInstructions(final InstructionList instance) {
		return instance != null ? instance.getInstructions() : null;
	}

	private static Number getValue(final ConstantPushInstruction instance) {
		return instance != null ? instance.getValue() : null;
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
	}

	private static String getName(final FieldOrMethod instance) {
		return instance != null ? instance.getName() : null;
	}

	private static InputStream getResourceAsStream(final Class<?> instance, final String name) {
		return instance != null ? instance.getResourceAsStream(name) : null;
	}

	private static <K, V> void put(final Map<K, V> instance, final K key, final V value) {
		if (instance != null) {
			instance.put(key, value);
		}
	}

	private static Number getNumberValue(final Instruction in) {
		//
		Number number = null;
		//
		if ((in instanceof ICONST iconst && (number = getValue(iconst)) != null)
				|| (in instanceof BIPUSH bipush && (number = getValue(bipush)) != null)) {
			//
			return number;
			//
		} // if
			//
		throw new IllegalStateException();
		//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

	private static String replace(final String instance, final CharSequence target, final CharSequence replacement) {
		return instance != null ? instance.replace(target, replacement) : null;
	}

	public static void main(final String[] args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, ParseException {
		//
		final JFrame jFrame = !GraphicsEnvironment.isHeadless() ? new JFrame() : null;
		//
		if (jFrame != null) {
			//
			final JapaneseEraJPanel instance = new JapaneseEraJPanel();
			//
			jFrame.add(instance);
			//
			jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			//
			pack(jFrame);
			//
			if (!isTestMode()) {
				//
				jFrame.setVisible(true);
				//
			} // if
				//
		} // if
			//
	}

	private static boolean isTestMode() {
		try {
			return Class.forName("org.testng.annotations.Test") != null;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	private static void pack(final JFrame instance) {
		//
		if (instance == null || GraphicsEnvironment.isHeadless()) {
			//
			return;
			//
		} // if
			//
		final Field field = testAndApply(x -> IterableUtils.size(x) == 1,
				toList(filter(stream(FieldUtils.getAllFieldsList(getClass(instance))),
						f -> Objects.equals(getName(f), "objectLock"))),
				x -> IterableUtils.get(x, 0), null);
		//
		if (field == null || Narcissus.getField(instance, field) != null) {
			//
			instance.pack();
			//
		} // if
			//
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static <T, R, E extends Throwable> R testAndApply(final Predicate<T> predicate, final T value,
			final FailableFunction<T, R, E> functionTrue, final FailableFunction<T, R, E> functionFalse) throws E {
		return test(predicate, value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T> boolean test(final Predicate<T> instance, final T value) {
		return instance != null && instance.test(value);
	}

	private static <T, R, E extends Throwable> R apply(final FailableFunction<T, R, E> instance, final T value)
			throws E {
		return instance != null ? instance.apply(value) : null;
	}

	private static <T> List<T> toList(final Stream<T> instance) {
		return instance != null ? instance.toList() : null;
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		return instance != null ? instance.filter(predicate) : instance;
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	private static Map<Object, Object> getEraAbbreviationMap() {
		//
		Map<Object, Object> map = null;
		//
		try (final InputStream is = getResourceAsStream(Object.class,
				"/sun/util/calendar/LocalGregorianCalendar.class")) {
			//
			final List<Method> ms = toList(filter(Arrays.stream(getMethods(new ClassParser(is, null).parse())),
					m -> Objects.equals(getName(m), "<clinit>")));
			//
			final int size = IterableUtils.size(ms);
			//
			if (size > 1) {
				//
				throw new IllegalStateException();
				//
			} // if
				//
			final Method m = testAndApply(x -> IterableUtils.size(x) == 1, ms, x -> IterableUtils.get(x, 0), null);
			//
			final Instruction[] ins = m != null ? getInstructions(new MethodGen(m, null, null).getInstructionList())
					: null;
			//
			final int length = ins != null ? ins.length : 0;
			//
			Integer index = null;
			//
			for (int i = 0; ins != null && i < length; i++) {
				//
				if (!(ArrayUtils.get(ins, i) instanceof PUTSTATIC)) {
					//
					continue;
					//
				} // if
					//
				if (index != null) {
					//
					throw new IllegalStateException();
					//
				} // if
					//
				index = Integer.valueOf(i);
				//
			} // for
				//
			ConstantPoolGen cpg = null;
			//
			Object key = null, value = null;
			//
			for (int i = 0; ins != null && i < Math.max(length, intValue(index, 0)); i++) {
				//
				if (cpg == null) {
					//
					cpg = new ConstantPoolGen(m.getConstantPool());
					//
				} // if
					//
				if (ArrayUtils.get(ins, i) instanceof LDC ldc && ldc != null) {
					//
					if (i > 0 && ArrayUtils.get(ins, i - 1) instanceof LDC) {
						//
						value = ldc.getValue(cpg);
						//
					} else {
						//
						key = ldc.getValue(cpg);
						//
					} // if
						//
					put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), key, value);
					//
				} // if
					//
			} // for
				//
		} catch (final IOException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return map;
		//
	}

	private static int intValue(final Number instance, final int defaultValue) {
		return instance != null ? instance.intValue() : defaultValue;
	}

}