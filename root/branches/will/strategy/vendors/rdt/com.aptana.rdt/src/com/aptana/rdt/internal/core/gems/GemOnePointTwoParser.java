package com.aptana.rdt.internal.core.gems;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.core.gems.Gem;

public class GemOnePointTwoParser extends GemParser {

	public GemOnePointTwoParser() {
		super();
	}

	public GemOnePointTwoParser(String string) {
		super(string);
	}

	public Set<Gem> parseOutGems(List<String> lines) {
		Set<Gem> gems = new HashSet<Gem>();
		if (lines == null || lines.isEmpty())
			return gems;

		String line = lines.get(0);
		if (line.startsWith("ERROR:"))
			return gems;

		for (int curLineIndex = 0; curLineIndex < lines.size();) {
			String nameAndVersion = lines.get(curLineIndex);
			String metadata = "";
			if ((curLineIndex + 1) < lines.size()) {
				metadata = lines.get(curLineIndex + 1);
			}
			// read until we hit end of list or empty line
			int j = 2;
			while (true) {
				if ((curLineIndex + j) >= lines.size())
					break; // if there is no next line, break out
				String nextLine = lines.get(curLineIndex + j);
				if (nextLine.trim().length() == 0)
					break; // if line is empty, break out
				metadata += " " + nextLine.trim(); // add line to
				// description
				j++; // move to next line
			}

			String description = "";
			j++;
			if ((curLineIndex + j) < lines.size()) {
				description = lines.get(curLineIndex + j);
			}
			j++;
			// read until we hit end of list or empty line
			while (true) {
				if ((curLineIndex + j) >= lines.size())
					break; // if there is no next line, break out
				String nextLine = lines.get(curLineIndex + j);
				if (nextLine.trim().length() == 0)
					break; // if line is empty, break out
				description += " " + nextLine.trim(); // add line to
				// description
				j++; // move to next line
			}

			int openParen = nameAndVersion.indexOf('(');
			if (openParen == -1) {
				AptanaRDTPlugin
						.log("Bad gems output format, no opening parenthesis for version: "
								+ lines);
				return gems;
			}
			int closeParen = nameAndVersion.indexOf(')');
			String name = nameAndVersion.substring(0, openParen);
			String version = nameAndVersion
					.substring(openParen + 1, closeParen);
			if (version.indexOf(",") != -1) {
				String[] versions = version.split(", ");
				for (int y = 0; y < versions.length; y++)
					gems
							.add(new Gem(name.trim(), versions[y], description
									.trim()));
			} else {
				gems.add(new Gem(name.trim(), version, description.trim()));
			}
			curLineIndex += (j + 1);
		}
		return gems;
	}

}
