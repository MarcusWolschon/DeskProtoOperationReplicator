package biz.wolschon.cnc.deskproto;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class PartSection extends Section {
	private static final String NUMBER_OF_OPERATIONS = "NumberOfOperations";

	/**
	 * Operations in the order of reading
	 */
	private List<OperationSection> operations = new LinkedList<>();
	
	
	public PartSection(final CharSequence identifier) {
		super(identifier);
	}
	
	/**
	 * @param ignoreStatistics used during file-parsing.
	 * @param appendTo if non-null, insert after this one
	 */
	protected void addOperation(final OperationSection operation, final boolean ignoreStatistics, final Section appendTo) {
			
		if (!ignoreStatistics) {
			if (operation.getNextSection() != null) {
				System.err.println("Operation to add already contains a next section value.");
			}

			setInteger(NUMBER_OF_OPERATIONS, this.operations.size() + 1);
			if (appendTo != null) {
				operation.setNextSection(appendTo.getNextSection());
				appendTo.setNextSection(operation);
			} else if (operations.isEmpty()) {
				operation.setNextSection(getNextSection());
				setNextSection(operation);
			} else {
				final OperationSection last = operations.get(operations.size() - 1);
				operation.setNextSection(last.getNextSection());
				last.setNextSection(operation);
			}
		}

		this.operations.add(operation);
	}

	/**
	 * 
	 * @param appendTo if non-null, insert after this one
	 */
	public void addOperation(final OperationSection part, final Section appendTo) {
		addOperation(part, false, appendTo);
	}

	public void addOperation(final OperationSection part) {
		addOperation(part, false, null);
	}

	
	public Collection<OperationSection> getOperations() {
		return operations;
	}

	public CharSequence getName() {
		return getString("Name");
	}

	public Integer getNumberOfOperations() {
		return getInteger(NUMBER_OF_OPERATIONS);
	}
}