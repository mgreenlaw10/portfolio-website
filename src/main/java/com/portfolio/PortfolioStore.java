package com.portfolio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.databind.json.JsonMapper;

/**
 * Holds the site content and saves every change to data/portfolio.json.
 * The first time the app runs, the content is copied from application.yml.
 */
@Service
public class PortfolioStore {

	private final Path file;
	private final JsonMapper json;
	private volatile Portfolio portfolio;

	public PortfolioStore(@Value("${app.data-dir}") Path dataDir, JsonMapper json, PortfolioProperties seed)
			throws IOException {
		this.file = dataDir.resolve("portfolio.json");
		this.json = json;
		if (Files.exists(file)) {
			this.portfolio = json.readValue(Files.readString(file), Portfolio.class);
		} else {
			Files.createDirectories(dataDir);
			this.portfolio = fromSeed(seed);
			save();
		}
	}

	public Portfolio get() {
		return portfolio;
	}

	public Optional<Project> findProject(Section section, String id) {
		return portfolio.projects(section).stream().filter(p -> p.id().equals(id)).findFirst();
	}

	public synchronized void updateProfile(String name, String description, String photo) {
		portfolio = new Portfolio(name, photo, description,
				portfolio.currentProjects(), portfolio.highlightedProjects());
		save();
	}

	/** Replaces the project with the same id, or adds it to the end of the section if it's new. */
	public synchronized void saveProject(Section section, Project project) {
		List<Project> projects = new ArrayList<>(portfolio.projects(section));
		int index = -1;
		for (int i = 0; i < projects.size(); i++) {
			if (projects.get(i).id().equals(project.id())) {
				index = i;
			}
		}
		if (index >= 0) {
			projects.set(index, project);
		} else {
			projects.add(project);
		}
		portfolio = portfolio.withProjects(section, projects);
		save();
	}

	/** Updates just the private-repo flag of one project, if it still exists. */
	public synchronized void setPrivateRepo(Section section, String id, Boolean privateRepo) {
		findProject(section, id)
				.filter(p -> !java.util.Objects.equals(p.privateRepo(), privateRepo))
				.ifPresent(p -> saveProject(section, p.withPrivateRepo(privateRepo)));
	}

	private void save() {
		try {
			Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
			Files.writeString(tmp, json.writerWithDefaultPrettyPrinter().writeValueAsString(portfolio));
			Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not save " + file, e);
		}
	}

	private static Portfolio fromSeed(PortfolioProperties seed) {
		return new Portfolio(seed.name(), seed.photo(), seed.description(),
				toProjects(seed.currentProjects()), toProjects(seed.highlightedProjects()));
	}

	private static List<Project> toProjects(List<PortfolioProperties.Project> seeds) {
		return seeds.stream()
				.map(p -> new Project(UUID.randomUUID().toString(), p.title(), p.github(), p.description(), p.image(),
						null))
				.toList();
	}
}
