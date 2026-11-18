package com.portfolio;

import java.util.List;

/** Everything shown on the home page. Saved to data/portfolio.json. */
public record Portfolio(
		String name,
		String photo,
		String description,
		List<Project> currentProjects,
		List<Project> highlightedProjects) {

	public Portfolio {
		currentProjects = currentProjects == null ? List.of() : List.copyOf(currentProjects);
		highlightedProjects = highlightedProjects == null ? List.of() : List.copyOf(highlightedProjects);
	}

	public List<Project> projects(Section section) {
		return section == Section.CURRENT ? currentProjects : highlightedProjects;
	}

	public Portfolio withProjects(Section section, List<Project> projects) {
		return section == Section.CURRENT
				? new Portfolio(name, photo, description, projects, highlightedProjects)
				: new Portfolio(name, photo, description, currentProjects, projects);
	}
}
