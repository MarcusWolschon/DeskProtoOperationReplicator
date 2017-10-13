package biz.wolschon.cnc.deskproto;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


class ProjectSection extends Section {

	private static final String NUMBER_OF_PARTS = "NumberOfParts";
	private Set<PartSection> parts = new HashSet<>();
	private Map<CharSequence, PartSection> partsByIdentifier = new HashMap<>();

	
	public ProjectSection(final CharSequence identifier) {
		super(identifier);
	}
	
	public Collection<PartSection> getParts() {
		return parts;
	}

	/**
	 * @param ignoreStatistics used during file-parsing.
	 */
	protected void addPart(final PartSection part, final boolean ignoreStatistics) {
		this.parts.add(part);
		this.partsByIdentifier.put(part.getIdentifier(), part);
		if (!ignoreStatistics) {
			setInteger(NUMBER_OF_PARTS, this.parts.size());
		}
	}

	public void addPart(final PartSection part) {
		addPart(part, false);
	}
	
	public Collection<PartSection> getPartSection() {
		return parts;
	}

	public Integer getNumberOfParts() {
		return getInteger(NUMBER_OF_PARTS);
	}
	
	public PartSection getPartByIdentifier(final CharSequence key) {
		return partsByIdentifier.get(key);
	}
}