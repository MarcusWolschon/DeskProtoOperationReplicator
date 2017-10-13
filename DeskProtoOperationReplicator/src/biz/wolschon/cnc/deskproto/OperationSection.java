package biz.wolschon.cnc.deskproto;

import biz.wolschon.cnc.deskproto.DeskprotoProject.Entry;

class OperationSection extends Section {

	public OperationSection(CharSequence identifier) {
		super(identifier);
	}
	
	public CharSequence getName() {
		return getString("Name");
	}

	public CharSequence getTool() {
		return getString("Cutter");
	}

	public Double getToolpathDistance() {
		return getDouble("ToolpathDistance");
	}
	
	public Double getSegmentMinX() {
		return getDouble("SegmentMinimumX");
	}

	public void setSegmentMinX(double d) {
		setDouble("SegmentMinimumX", d);	
	}
	
	public Double getSegmentMaxX() {
		return getDouble("SegmentMaximumX");
	}

	public void setSegmentMaxX(double d) {
		setDouble("SegmentMaximumX", d);	
	}

	public Double getSegmentMinY() {
		return getDouble("SegmentMinimumY");
	}

	public void setSegmentMinY(double d) {
		setDouble("SegmentMinimumY", d);	
	}

	public Double getSegmentMaxY() {
		return getDouble("SegmentMaximumY");
	}

	public void setSegmentMaxY(double d) {
		setDouble("SegmentMaximumY", d);	
	}


	public Double getSegmentMinA() {
		return getDouble("SegmentMinimumAngle");
	}

	public void setSegmentMinA(double d) {
		setDouble("SegmentMinimumAngle", d);	
	}

	public Double getSegmentMaxA() {
		return getDouble("SegmentMaximumAngle");
	}

	public void setSegmentMaxA(double d) {
		setDouble("SegmentMaximumAngle", d);	
	}

	public Double getSegmentMinZ() {
		return getDouble("SegmentMinimumZ");
	}
	
	public OperationSection clone(final PartSection part, final CharSequence newName) {
		final OperationSection returnMe = new OperationSection(part.getIdentifier() + " Operation" + part.getOperations().size());
		for (Entry entry : super.getEntries()) {
			returnMe.addEntry(entry.key, entry.value);
		}
		returnMe.setString("Name", newName);
		return returnMe;
	}

	public int getStrategyCode() {
		return getInteger("Strategy");
	}
	

	public enum STRATEGY {
		PARALLELX(0),
		PARALLELA(1),
		PARALLELXREVERSED(10),
		PARALLELAREVERSED(11),
		CROSSWISE(11);
		
		private int code;
		private STRATEGY(final int code) {
			this.code = code;
		}
		
		public int getCode() {
			return code;
		}
	}
}