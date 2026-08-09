import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.JapaneseEra;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.google.common.base.Strings;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;

public class JapaneseEraJPanel extends JPanel implements ActionListener, DateChangeListener {

	private static final long serialVersionUID = 1810789541222187125L;

	private static final String VALUE = "value";

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface Note {
		String value();
	}

	@Note("Copy JSON")
	private AbstractButton btnCopyJson = null;

	@Note("Export")
	private AbstractButton btnExport = null;

	private AbstractButton cbPrettyJson, btnCopyEmoji, btnCopyHtml = null;

	private transient TableModel tm = null;

	private DatePicker datePicker = null;

	private Locale localeJapanese = null;

	private transient ComboBoxModel<String> cbm = null;

	@Note("Year")
	private JTextField tfYear = null;

	@Note("Month")
	private JTextField tfMonth = null;

	private JTextField tfDay = null;

	private ObjectMapper objectMapper = null;

	private JComboBox<Character> jcb = null;

	private JapaneseEraJPanel() throws ParseException {
		//
		init();
		//
	}

	private void init() throws ParseException {
		//
		setLayout(new MigLayout());
		//
		final Map<String, YearMonthDay> yearMonthDays = getJapaneseEraSinceDates();
		//
		final DefaultTableModel dtm = new DefaultTableModel(
				new Object[] { "", "Abbr", "Name", "Emoji", "Year", "Month", "Day" }, 0);
		//
		final JTable jTable = new JTable(tm = dtm);
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
		final String wrap = "wrap";
		//
		add(jsp, String.format("%1$s,span %2$s", wrap, 5));
		//
		Collection<String> names = null;
		//
		List<Character> characters = null;
		//
		if (entrySet(yearMonthDays) != null) {
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
			String key, name;
			//
			final Map<Object, Object> eraAbbreviationMap = getEraAbbreviationMap();
			//
			List<Entry<Object, Object>> eraAbbreviationList = null;
			//
			Entry<Object, Object> eraAbbreviationEntry = null;
			//
			Character character = null;
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
					df1 = new SimpleDateFormat("G", cast(Locale.class, getLocaleJapanese()));
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
				add(characters = ObjectUtils.getIfNull(characters, ArrayList::new),
						character = getCharacter(
								entrySet = ObjectUtils.getIfNull(entrySet, () -> entrySet(characterMap)), commonPrefix,
								key));
				//
				dtm.addRow(new Object[] { key, getValue(eraAbbreviationEntry),
						name = format(df1,
								parse(df2 = ObjectUtils.getIfNull(df2, () -> new SimpleDateFormat("yyyyMMdd")),
										Objects.toString(yearMonthDay))),
						character, yearMonthDay.year, yearMonthDay.month, yearMonthDay.day });
				//
				add(names = ObjectUtils.getIfNull(names, ArrayList::new), name);
				//
			} // for
				//
		} // if
			//
		jsp.setPreferredSize(new Dimension((int) jsp.getPreferredSize().getWidth(),
				(dtm.getRowCount() + 1) * (jTable.getRowHeight() + 2)));
		//
		add(new JLabel());
		//
		add(cbPrettyJson = new JCheckBox("Pretty JSON"), String.format("span %1$s", 2));
		//
		add(btnCopyJson = new JButton("Copy JSON"));
		//
		add(btnExport = new JButton("Export"), wrap);
		//
		add(new JLabel("Date"));
		//
		(datePicker = new DatePicker()).addDateChangeListener(this);
		//
		add(datePicker, String.format("%1$s,span %2$s", wrap, 3));
		//
		add(new JLabel("Japnese Date"));
		//
		final JComboBox<String> jcb1 = new JComboBox<>(cbm = new DefaultComboBoxModel<>(toArray(names, String[]::new)));
		//
		jcb1.setEnabled(false);
		//
		jcb1.setSelectedItem(null);
		//
		add(jcb1);
		//
		final JPanel panel = new JPanel();
		//
		panel.setLayout(new MigLayout());
		//
		panel.add(tfYear = new JTextField(), String.format("wmin %1$s", 50));
		//
		panel.add(new JLabel("年"));
		//
		panel.add(tfMonth = new JTextField(), String.format("wmin %1$s", 50));
		//
		panel.add(new JLabel("月"));
		//
		panel.add(tfDay = new JTextField(), String.format("wmin %1$s", 50));
		//
		panel.add(new JLabel("日"));
		//
		add(panel, String.format("span %1$s,%2$s", 3, wrap));
		//
		add(new JLabel("Emoji"));
		//
		(jcb = new JComboBox<>(new DefaultComboBoxModel<>(toArray(characters, Character[]::new))))
				.setSelectedItem(null);
		//
		jcb.addActionListener(this);
		//
		add(jcb);
		//
		add(btnCopyEmoji = new JButton("Copy"));
		//
		add(btnCopyHtml = new JButton("Copy HTML"));
		//
		forEach(map(
				filter(stream(FieldUtils.getAllFieldsList(getClass())),
						f -> AbstractButton.class.isAssignableFrom(getType(f))),
				f -> cast(AbstractButton.class, Narcissus.getField(this, f))), x -> addActionListener(x, this));
		//
		forEach(map(
				filter(stream(FieldUtils.getAllFieldsList(getClass())),
						f -> JTextComponent.class.isAssignableFrom(getType(f))),
				f -> cast(JTextComponent.class, Narcissus.getField(this, f))), x -> setEditable(x, false));
		//
	}

	private static Class<?> getType(final Field instance) {
		return instance != null ? instance.getType() : null;
	}

	private static <T> T[] toArray(final Collection<T> instance, final IntFunction<T[]> generator) {
		return instance != null ? instance.toArray(generator) : null;
	}

	private static <T> void setEditable(final JTextComponent instance, final boolean editable) {
		if (instance != null) {
			instance.setEditable(editable);
		}
	}

	private static <T> void forEach(final Stream<T> instance, final Consumer<? super T> action) {
		if (instance != null) {
			instance.forEach(action);
		}
	}

	private static <T, R> Stream<R> map(final Stream<T> instance, final Function<? super T, ? extends R> mapper) {
		return instance != null ? instance.map(mapper) : null;
	}

	private static void addActionListener(final AbstractButton instance, final ActionListener actionListener) {
		if (instance != null) {
			instance.addActionListener(actionListener);
		}
	}

	private Locale getLocaleJapanese() {
		//
		if (localeJapanese == null) {
			//
			final List<Constructor<?>> list = toList(
					filter(testAndApply(Objects::nonNull, Locale.class.getConstructors(), Arrays::stream, null),
							c -> Arrays.equals(getParameterTypes(c),
									new Class<?>[] { String.class, String.class, String.class })));
			//
			if (IterableUtils.size(list) > 1) {
				//
				throw new IllegalStateException();
				//
			} // if
				//
			try {
				//
				localeJapanese = cast(Locale.class,
						newInstance(
								testAndApply(x -> IterableUtils.size(x) == 1, list, x -> IterableUtils.get(x, 0), null),
								"ja", "JP", "JP"));
				//
			} catch (final InstantiationException | IllegalAccessException | InvocationTargetException e) {
				//
				throw new RuntimeException(e);
				//
			} // try
				//
		} // if
			//
		return localeJapanese;
		//
	}

	private static Class<?>[] getParameterTypes(final Executable instance) {
		return instance != null ? instance.getParameterTypes() : null;
	}

	private static <K, V> Collection<Entry<K, V>> entrySet(final Map<K, V> instance) {
		return instance != null ? instance.entrySet() : null;
	}

	private static Character getCharacter(final Iterable<Entry<String, Character>> entries, final String commonPrefix,
			final String entryKey) {
		//
		final List<Entry<String, Character>> list = toList(
				filter(testAndApply(Objects::nonNull, spliterator(entries), x -> StreamSupport.stream(x, false), null),
						x -> endsWith(getKey(x), entryKey)));
		//
		if (IterableUtils.size(list) == 1) {
			//
			return getValue(IterableUtils.get(list, 0));
			//
		} else if (iterator(entries) != null) {
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
			return getCharacter2(entries, commonPrefix, entryKey);
			//
		} // if
			//
		return null;
		//
	}

	private static Character getCharacter2(final Iterable<Entry<String, Character>> entries, final String commonPrefix,
			final String entryKey) {
		//
		if (iterator(entries) != null) {
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

	private static boolean endsWith(final String instance, final String suffix) {
		//
		try {
			//
			if (instance == null || Narcissus.getObjectField(instance, String.class.getDeclaredField(VALUE)) == null) {
				//
				return false;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.endsWith(suffix);
		//
	}

	private static <T> Iterator<T> iterator(final Iterable<T> instance) {
		return instance != null ? instance.iterator() : null;
	}

	private static <T> Spliterator<T> spliterator(final Iterable<T> instance) {
		return instance != null ? instance.spliterator() : null;
	}

	private static <K> K getKey(final Entry<K, ?> instance) {
		return instance != null ? instance.getKey() : null;
	}

	private static <T> Stream<T> stream(final Collection<T> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static boolean startsWith(final org.apache.commons.lang3.Strings instance, final CharSequence str,
			final CharSequence prefix) {
		//
		try {
			//
			if (instance == null
					|| (str instanceof String
							&& Narcissus.getObjectField(str, String.class.getDeclaredField(VALUE)) == null)
					|| (prefix instanceof String
							&& Narcissus.getObjectField(prefix, String.class.getDeclaredField(VALUE)) == null)) {
				//
				return false;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.startsWith(str, prefix);
		//
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
		//
		try {
			//
			if (instance == null || string == null
					|| Narcissus.getObjectField(string, String.class.getDeclaredField(VALUE)) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.parse(string);
		//
	}

	private static String format(final DateFormat instance, final Date date) {
		return instance != null && date != null ? instance.format(date) : null;
	}

	private static <T> T newInstance(final Constructor<T> instance, final Object... args)
			throws InstantiationException, IllegalAccessException, InvocationTargetException {
		return instance != null ? instance.newInstance(args) : null;
	}

	private static class YearMonthDay {

		@Note("Year")
		private int year;

		@Note("Month")
		private int month;

		private int day;

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
		try {
			//
			if (instance == null || FieldUtils.readField(instance, "constant_pool", true) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final IllegalAccessException e) {
			//
			return null;
			//
		} // try
			//
		return instance.getName();
		//
	}

	private static InputStream getResourceAsStream(final Class<?> instance, final String name) {
		//
		try {
			//
			if (instance == null || name == null
					|| Narcissus.getObjectField(name, String.class.getDeclaredField(VALUE)) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.getResourceAsStream(name);
		//
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
		//
		try {
			//
			if (instance == null || (instance instanceof String
					&& Narcissus.getObjectField(instance, String.class.getDeclaredField(VALUE)) == null)) {
				//
				return null;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.replace(target, replacement);
		//
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
			map = getEraAbbreviationMap(ins, index, m != null ? new ConstantPoolGen(m.getConstantPool()) : null);
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

	private static Map<Object, Object> getEraAbbreviationMap(final Instruction[] ins, final Number index,
			final ConstantPoolGen cpg) {
		//
		final int length = ins != null ? ins.length : 0;
		//
		Map<Object, Object> map = null;
		//
		Object key = null, value = null;
		//
		for (int i = 0; ins != null && i < Math.max(length, intValue(index, 0)); i++) {
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
		return map;
		//
	}

	private static int intValue(final Number instance, final int defaultValue) {
		return instance != null ? instance.intValue() : defaultValue;
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		//
		final Object source = getSource(evt);
		//
		if (Objects.equals(source, btnCopyJson)) {
			//
			final int columnCount = getColumnCount(tm);
			//
			List<Map<?, ?>> list = null;
			//
			Map<Object, Object> map = null;
			//
			Object value = null;
			//
			final Map<Integer, String> fieldNames = Map.of(Integer.valueOf(0), "englishName", Integer.valueOf(1),
					"abbreviation", Integer.valueOf(2), "japaneseName", Integer.valueOf(3), "emoji", Integer.valueOf(4),
					"year", Integer.valueOf(5), "month", Integer.valueOf(6), "day");
			//
			for (int i = 0; tm != null && i < tm.getRowCount(); i++) {
				//
				for (int j = 0; j < columnCount; j++) {
					//
					value = tm.getValueAt(i, j);
					//
					if (j == 0) {
						//
						add(list = ObjectUtils.getIfNull(list, ArrayList::new), map = new LinkedHashMap<>());
						//
					} // if
						//
					if (containsKey(fieldNames, Integer.valueOf(i))) {
						//
						put(map, get(fieldNames, Integer.valueOf(j)), value);
						//
					} else {
						//
						throw new IllegalStateException();
						//
					} // if
						//
				} // for
					//
			} // for
				//
			final Toolkit toolkit = Toolkit.getDefaultToolkit();
			//
			final Clipboard clipboard = toolkit != null && !GraphicsEnvironment.isHeadless()
					? toolkit.getSystemClipboard()
					: null;
			//
			if ((objectMapper = ObjectUtils.getIfNull(objectMapper, ObjectMapper::new)) != null
					&& isSelected(cbPrettyJson)) {
				//
				setContents(clipboard,
						new StringSelection(writeValueAsString(objectMapper.writerWithDefaultPrettyPrinter(), list)),
						null);
				//
			} else {
				//
				setContents(clipboard, new StringSelection(writeValueAsString(objectMapper, list)), null);
				//
			} // if
				//
		} else if (Objects.equals(source, btnExport)) {
			//
			try (final Workbook wb = new XSSFWorkbook();
					final OutputStream os = new FileOutputStream("JapaneseEra.xlsx")) {
				//
				Sheet sheet = null;
				//
				Row row = null;
				//
				Cell cell = null;
				//
				Object value = null;
				//
				final int columnCount = getColumnCount(tm);
				//
				for (int i = 0; tm != null && i < tm.getRowCount(); i++) {
					//
					if ((sheet = ObjectUtils.getIfNull(sheet, wb::createSheet)) == null) {
						//
						continue;
						//
					} // if
						//
					if (sheet.getPhysicalNumberOfRows() == 0
							&& (row = sheet.createRow(sheet.getPhysicalNumberOfRows())) != null) {
						//
						for (int j = 0; j < columnCount; j++) {
							//
							if ((cell = row.createCell(row.getPhysicalNumberOfCells())) == null) {
								//
								continue;
								//
							} // if
								//
							cell.setCellValue(tm.getColumnName(j));
							//
						} // for
							//
					} // if
						//
					if ((row = sheet.createRow(sheet.getPhysicalNumberOfRows())) == null) {
						//
						continue;
						//
					} // if
						//
					for (int j = 0; j < columnCount; j++) {
						//
						if ((cell = row.createCell(row.getPhysicalNumberOfCells())) == null) {
							//
							continue;
							//
						} // if
							//
						if ((value = tm.getValueAt(i, j)) instanceof String || value instanceof Character) {
							//
							cell.setCellValue(Objects.toString(value));
							//
						} else if (value instanceof Number number && number != null) {
							//
							cell.setCellValue(number.doubleValue());
							//
						} else {
							//
							throw new IllegalStateException(Objects.toString(value.getClass()));
							//
						} // if
							//
					} // for
						//
				} // for
					//
				if (!isTestMode()) {
					//
					wb.write(os);
					//
				} // if
					//
			} catch (final IOException e) {
				//
				throw new RuntimeException(e);
				//
			} // try
				//
		} else if (Objects.equals(source, btnCopyEmoji)) {
			//
			final Character character = cast(Character.class, getSelectedItem(jcb));
			//
			if (character != null) {
				//
				final Toolkit toolkit = Toolkit.getDefaultToolkit();
				//
				setContents(toolkit != null && !GraphicsEnvironment.isHeadless() ? toolkit.getSystemClipboard() : null,
						new StringSelection(new String(new char[] { character.charValue() })), null);
				//
			} // if
				//
		} else if (Objects.equals(source, btnCopyHtml)) {
			//
			final Character character = cast(Character.class, getSelectedItem(jcb));
			//
			if (character != null) {
				//
				final Toolkit toolkit = Toolkit.getDefaultToolkit();
				//
				setContents(toolkit != null && !GraphicsEnvironment.isHeadless() ? toolkit.getSystemClipboard() : null,
						new StringSelection(
								String.format("&#x%1$s;", Integer.toHexString((int) character.charValue()))),
						null);
				//
			} // if
				//
		} // if
			//
	}

	private static Object getSelectedItem(final JComboBox<?> instance) {
		//
		try {
			//
			if (instance == null
					|| Narcissus.getObjectField(instance, JComboBox.class.getDeclaredField("dataModel")) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.getSelectedItem();
		//
	}

	private static boolean isSelected(final AbstractButton instance) {
		return instance != null && instance.isSelected();
	}

	private static void setContents(final Clipboard instance, final Transferable contents, final ClipboardOwner owner) {
		if (instance != null) {
			instance.setContents(contents, owner);
		}
	}

	private static String writeValueAsString(final ObjectWriter instance, final Object value) {
		//
		try {
			//
			if (instance == null || Narcissus.getObjectField(instance,
					ObjectWriter.class.getDeclaredField("_generatorFactory")) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.writeValueAsString(value);
		//
	}

	private static String writeValueAsString(final ObjectMapper instance, final Object value) {
		//
		try {
			//
			if (instance == null || Narcissus.getObjectField(instance,
					ObjectMapper.class.getDeclaredField("_streamFactory")) == null) {
				//
				return null;
				//
			} // if
				//
		} catch (final NoSuchFieldException e) {
			//
			throw new RuntimeException(e);
			//
		} // try
			//
		return instance.writeValueAsString(value);
		//
	}

	private static int getColumnCount(final TableModel instance) {
		return instance != null ? instance.getColumnCount() : 0;
	}

	private static <V> V get(final Map<?, V> instance, final Object key) {
		return instance != null ? instance.get(key) : null;
	}

	private static boolean containsKey(final Map<?, ?> instance, final Object key) {
		return instance != null && instance.containsKey(key);
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
	}

	private static Object getSource(final EventObject instance) {
		return instance != null ? instance.getSource() : null;
	}

	@Override
	public void dateChanged(final DateChangeEvent event) {
		//
		if (Objects.equals(getSource(event), datePicker)) {
			//
			final String[] ss = StringUtils.split(
					format(new SimpleDateFormat("GG yy MM dd", getLocaleJapanese()), testAndApply(Objects::nonNull,
							event != null ? event.getNewDate() : null, java.sql.Date::valueOf, null)));
			//
			String s = null;
			//
			final List<Consumer<String>> cs = Arrays.asList(x -> setText(tfYear, x), x -> setText(tfMonth, x),
					x -> setText(tfDay, x));
			//
			Consumer<String> c = null;
			//
			for (int i = 0; ss != null && i < ss.length; i++) {
				//
				s = ArrayUtils.get(ss, i);
				//
				if (i == 0) {
					//
					setSelectedItem(cbm, s);
					//
				} else if (i <= IterableUtils.size(cs) && (c = IterableUtils.get(cs, i - 1)) != null) {
					//
					c.accept(s);
					//
				} else {
					//
					throw new IllegalStateException();
					//
				} // if
					//
			} // for
				//
		} // if
			//
	}

	private static DatePicker getSource(final DateChangeEvent instance) {
		return instance != null ? instance.getSource() : null;
	}

	private static void setSelectedItem(final ComboBoxModel<?> instance, final Object item) {
		if (instance != null) {
			instance.setSelectedItem(item);
		}
	}

	private static void setText(final JTextComponent instance, final String text) {
		//
		try {
			//
			if (instance == null
					|| (text != null && Narcissus.getObjectField(text, String.class.getDeclaredField(VALUE)) == null)) {
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
		instance.setText(text);
		//
	}

}