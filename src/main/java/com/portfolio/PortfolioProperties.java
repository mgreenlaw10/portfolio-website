package com.portfolio;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Everything shown on the page. Edit the values in src/main/resources/application.yml.
 */
@ConfigurationProperties(prefix = "portfolio")
public record PortfolioProperties(
		String name,
		String photo,
		String description,
		List<Project> currentProjects,
		List<Project> highlightedProjects) {

	public PortfolioProperties {
		currentProjects = currentProjects == null ? List.of() : currentProjects;
		highlightedProjects = highlightedProjects == null ? List.of() : highlightedProjects;
	}

	public record Project(String title, String github, String description, String image) {
	}
}
