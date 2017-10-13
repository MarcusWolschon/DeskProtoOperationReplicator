package biz.wolschon.cnc.deskproto;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import biz.wolschon.cnc.deskproto.DeskprotoProject.Entry;

/**
 * Generic type of section.
 */
class Section {

	private CharSequence identifier;

	/**
	 * Name-Value entries in this .ini file -style section in the order of their apearance.
	 */
	private List<Entry> entries = new LinkedList<>();

	/**
	 * {@link #entries} as a map indexed by key.<br/>
	 * In case of duplicate keys, only the last entry is stored.
	 */
	private Map<CharSequence, Entry> entriesByKey = new HashMap<>();
	
	/**
	 * The section after this one in the file.
	 */
	private Section nextSection;

	public Section(final CharSequence identifier) {
		this.identifier = identifier;
	}

	public void write(final Writer out) throws IOException {			
		out.write('[');
		out.write(identifier.toString());
		out.write("]\r\n");
		for (Entry entry : entries) {
			entry.write(out);
		}
		out.write("\r\n");
	}

	public CharSequence getIdentifier() {
		return identifier;
	}

	public void addEntry(final CharSequence k, final CharSequence v) {
		final String key = k.toString().trim();
		final String value = v.toString().trim();
		final Entry entry = new Entry(key, value);
		this.entries.add(entry);
		this.entriesByKey.put(key,  entry);
	}
	
	public Entry getLastEntryByKey(final CharSequence key) {
		return entriesByKey.get(key);
	}
	
	public CharSequence getString(final CharSequence key) {
		final Entry entry = getLastEntryByKey(key);
		if (entry == null) {
			return null;
		}
		return entry.value;
	}
	
	protected void setString(final String key, final CharSequence value) {
		final Entry entry = getLastEntryByKey(key);
		if (entry == null) {
			addEntry(key, value);
		} else {
			entry.value = value;
		}
	}
	
	protected void setDouble(final String key, final double value) {
		final Entry entry = getLastEntryByKey(key);
		if (entry == null) {
			addEntry(key, Double.toString(value));
		} else {
			entry.value = Double.toString(value);
		}
	}
	
	protected Double getDouble(final CharSequence key) {
		final Entry entry = getLastEntryByKey(key);
		if (entry == null) {
			return null;
		}
		try {
			return new Double(NumberFormat.getNumberInstance(Locale.US).parse(entry.value.toString()).doubleValue());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected void setInteger(final String key, final int value) {
		final Entry entry = getLastEntryByKey(key);
		if (entry == null) {
			addEntry(key, Integer.toString(value));
		} else {
			entry.value = Integer.toString(value);
		}
	}
	
	protected Integer getInteger(final CharSequence key) {
		final Entry entry = getLastEntryByKey(key);
		if (entry == null) {
			return null;
		}
		try {
			return new Integer(NumberFormat.getNumberInstance(Locale.US).parse(entry.value.toString()).intValue());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Collection<Entry> getEntries() {
		return entries;
	}

	public Section getNextSection() {
		return nextSection;
	}

	public void setNextSection(Section nextSection) {
		this.nextSection = nextSection;
	}

}