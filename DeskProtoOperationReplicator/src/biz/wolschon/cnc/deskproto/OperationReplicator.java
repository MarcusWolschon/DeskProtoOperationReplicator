/**
 * 
 */
package biz.wolschon.cnc.deskproto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mw2_7
 *
 */
public class OperationReplicator {

	private static final int MAXRECURSION = 4;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		final File in = new File("E:\\Aktuelle_Projekte\\Eclipse-Workspace\\DeskProtoOperationReplicator\\testdata\\input.dpj") ;
		final File out = new File("E:\\Aktuelle_Projekte\\Eclipse-Workspace\\DeskProtoOperationReplicator\\testdata\\output.dpj") ;
		try {
			final DeskprotoProject project = new DeskprotoProject(in);
			
			
			// find all operations with a name that the work "REPLICATE"
			final Collection<PartSection> parts = project.getProject().getParts();
			for (PartSection partSection : parts) {
				List<OperationSection> currentOperations = new LinkedList<>();
				for (OperationSection operation : partSection.getOperations()) {
					if (operation.getName().toString().contains("REPLICATE")) {
						currentOperations.add(operation);
					}
				}
				
				if (currentOperations.isEmpty()) {
					continue; // next part
				}
				
				int recursionLevel = 1;
				// append always to the last operation.
				// So that all operations of a recursion level are to be executed before
				// the first operation of the next recursion level.
				OperationSection appendTo = currentOperations.get(currentOperations.size() - 1);
				List<OperationSection> createdOperations = new LinkedList<>();

				while (recursionLevel < MAXRECURSION) {
					int[] counter = new int[] {0};
					for (OperationSection op : currentOperations) {
						createdOperations.add(op);
						final OperationSection replicant = replicateOperation(partSection, op, appendTo, recursionLevel, counter);
						createdOperations.add(replicant);
						appendTo = replicant;
					}
					currentOperations = createdOperations;
					createdOperations = new LinkedList<>();
					recursionLevel++;
				}

			}
			
			
			
			final FileWriter o = new FileWriter(out);
			try {
				project.write(o);
			} finally {
				o.close();				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Create a copy of the given operation and attach it to the part.<br/>
	 * The copy will have have the same toolpath distance but a minY offset
	 * shifted by half the toolpath distance in recursion level 1.
	 * A quarter of the toolpath distance in recursion level 2.
	 * ...
	 * @param appendTo 
	 * 
	 */
	private static OperationSection replicateOperation(final PartSection partSection,
			final OperationSection operation,
			final OperationSection appendTo,
			final int recursionLevel,
			final int[] counter) {
		final OperationSection copy = operation.clone(partSection,
				operation.getName().toString().replaceFirst("\\-COPY.*", "") + "-COPY Level" + recursionLevel + " Op" + counter[0]);
		counter[0]++;
		double step = operation.getToolpathDistance();
		final double pow = Math.pow(2.0d, recursionLevel);
		final double offset = step/pow;
		
		//TODO: all of this is experimental
		
		//get axis and if the min or max needs to change from the operation parameters
		final int code = operation.getStrategyCode();
		
		if (code == OperationSection.STRATEGY.PARALLELX.getCode()) {
			//TODO: if this is an operation in XYZ
			//double minY = operation.getSegmentMinY();
			//copy.setSegmentMinY(minY + offset);

			// if this is an operation in XAZ
			double minA = operation.getSegmentMinA();
			copy.setSegmentMinA((minA + offset) % 360d);
		} else if (code == OperationSection.STRATEGY.PARALLELXREVERSED.getCode()) {
			//TODO: if this is an operation in XYZ
			//double maxY = operation.getSegmentMaxY();
			//copy.setSegmentMaxY(maxY - offset);

			// if this is an operation in XAZ
			double maxA = operation.getSegmentMaxA();
			copy.setSegmentMaxA((maxA - offset) % 360d);
		} else if (code == OperationSection.STRATEGY.PARALLELA.getCode()) {
			double minX = operation.getSegmentMinX();
			copy.setSegmentMinX(minX + offset);
		} else if (code == OperationSection.STRATEGY.CROSSWISE.getCode()) {
			double minX = operation.getSegmentMinX();
			copy.setSegmentMinX(minX + offset);
			double minY = operation.getSegmentMinY();
			copy.setSegmentMinY(minY + offset);
			double minA = operation.getSegmentMinA();
			copy.setSegmentMinA((minA + offset) % 360d);
		} else if (code == OperationSection.STRATEGY.PARALLELAREVERSED.getCode()) {
			double maxX = operation.getSegmentMaxX();
			copy.setSegmentMaxA(maxX - offset);
		} else {
			//TODO: figure out the codes for more strategies
			double minX = operation.getSegmentMinX();
			copy.setSegmentMinX(minX + offset);
			
		}
		

		partSection.addOperation(copy, appendTo);
		return copy;
	}

}
