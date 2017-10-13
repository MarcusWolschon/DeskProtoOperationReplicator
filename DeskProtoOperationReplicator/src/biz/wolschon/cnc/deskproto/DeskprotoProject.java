package biz.wolschon.cnc.deskproto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeskprotoProject {

	private static final Pattern PROJECTNAME = Pattern.compile("DeskProto Project");
	private static final Pattern PARTNAME = Pattern.compile("Part[0-9]");
	private static final Pattern OPERATIONNAME = Pattern.compile("(Part[0-9]) Operation[0-9]");

	/**
	 * Sections of the Windows .ini style project file in the order of their apearance.
	 */
//	private List<Section> sections = new LinkedList<>();
	/**
	 * All sections in {@link #sections} that are {@link ProjectSection}s.
	 */
	private List<ProjectSection> projects = new LinkedList<>();
	/**
	 * The last {@link ProjectSection} we found (usually there should be only one in total.
	 */
	private ProjectSection currentProject;
	
	
	public DeskprotoProject(final File projectFile)  throws IOException {
		parse(projectFile);
	}
	
	
	public ProjectSection getProject() {
		return currentProject;
	}


	private void parse(final File projectFile)  throws IOException {
		final BufferedReader in = new BufferedReader(new FileReader(projectFile));
		try {
			Section currentSection = null;
			String line;
			int lineNumber = -1;
			while ((line = in.readLine()) != null) {
				lineNumber++;
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("[") && line.endsWith("]")) {
					final Section lastSection = currentSection;
					currentSection = createSection(line.subSequence(1,  line.length() - 1));
//					sections.add(currentSection);
					if (lastSection != null) {
						lastSection.setNextSection(currentSection);
					}
					continue;
				}
				
				final int index = line.indexOf('=');
				if (index <= 0 ) {
					System.err.println("Line " + lineNumber + " is missing a '=' character");
					currentSection.addEntry(line, null);
					continue;
				}
				currentSection.addEntry(line.subSequence(0,  index), line.subSequence(index + 1, line.length()));
				
			}
			

			System.err.println("Parsed " + lineNumber + " lines.");
//			System.err.println("Found " + sections.size() + " sections");
			System.err.println("Found " + currentProject.getParts().size() + " Parts");
			for (PartSection part : currentProject.getParts()) {
				System.err.println("Part '" + part.getName() + "' ID='" + part.getIdentifier() + "' has " + part.getOperations().size() + " Operations");	
				for (OperationSection operation : part.getOperations()) {
					System.err.println("Part '" + part.getName() + "' has operation '" + operation.getName() + "' ID=" + operation.getIdentifier());
					System.err.println("  It uses tool '" + operation.getTool() + "' with a tool path distance of " + operation.getToolpathDistance());
				}
			}
			
			
		} finally {
			in.close();
		}
	}
	
	
	public void write(final Writer out) throws IOException {
		Section currentSection = currentProject;
		while (currentSection != null) {
			currentSection.write(out);
			currentSection = currentSection.getNextSection();
		}
	}
	

	static class Entry {
		CharSequence key;
		CharSequence value;
		public Entry(final CharSequence key, final CharSequence value) {
			this.key = key;
			this.value = value;
		}
		public void write(final Writer out) throws IOException {			
			out.write(key.toString());
			out.write(" = ");
			out.write(value.toString());
			out.write("\r\n");
		}
	}
	
	
	/**
	 * Look at the name and decide what type of section to create.
	 * @see PartSection
	 */
	private Section createSection(final CharSequence name) {
		
		if(PROJECTNAME.matcher(name).matches()) {
			final ProjectSection project = new ProjectSection(name);
			this.projects.add(project);
			this.currentProject = project;
			return project;
		}
		
		if (PARTNAME.matcher(name).matches()) {
			final PartSection part = new PartSection(name);
			if (currentProject != null) {
				currentProject.addPart(part, true);
			} else {
				System.err.println("Project file contains a part but no project header");
			}
			return part;
		}
		
		final Matcher matcher = OPERATIONNAME.matcher(name);
		if (matcher.matches()) {
			final CharSequence partID = matcher.group(1);
			final PartSection part = currentProject.getPartByIdentifier(partID);
			final OperationSection operation = new OperationSection(name);
			if (part == null) {
				System.err.println("Part '" + partID + "' not found for operation '" + name + "'");
			} else {
				part.addOperation(operation, true, null);
			}
			return operation;
		}
		return new Section(name);
	}
	
}
