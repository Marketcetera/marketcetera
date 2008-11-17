package com.aptana.rdt.core.gems;



public class GemRequirement {

	private String name;
	private String versionDependency;

	public GemRequirement(String name, String versionDependency) {
		this.name = name;
		this.versionDependency = versionDependency;
	}
	
	private String getRule() {
		return versionDependency.split(" ")[0];
	}
	
	private Version getVersion() {
		return new Version(versionDependency.split(" ")[1]);
	}
	
	public String toString() {
		return name + " (" + versionDependency + ")";
	}

	public String getName() {
		return name;
	}

	public boolean meetsRequirements(String version) {
		Version gemVersion = new Version(version);
		if (getRule().equals("=")) {
			return gemVersion.equals(getVersion());
		} else if (getRule().equals(">=")) {
			return gemVersion.isGreaterThanOrEqualTo(getVersion());
		} else if (getRule().equals("<=")) {
			return gemVersion.isLessThanOrEqualTo(getVersion());
		} else if (getRule().equals(">")) {
			return gemVersion.isGreaterThan(getVersion());
		} else if (getRule().equals("<")) {
			return gemVersion.isLessThan(getVersion());
		}
		return false;
	}
}
