package com.portfolio;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * When the app starts, re-checks every project's GitHub link so the "Private Repository"
 * button stays correct if a repo was made public or private since it was last published.
 */
@Component
@ConditionalOnProperty(name = "app.check-github-on-startup", havingValue = "true", matchIfMissing = true)
public class PrivateRepoRefresher {

	private final PortfolioStore store;
	private final GitHubRepoChecker github;

	public PrivateRepoRefresher(PortfolioStore store, GitHubRepoChecker github) {
		this.store = store;
		this.github = github;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void refresh() {
		for (Section section : Section.values()) {
			for (Project project : store.get().projects(section)) {
				github.isPrivate(project.github())
						.ifPresent(isPrivate -> store.setPrivateRepo(section, project.id(), isPrivate));
			}
		}
	}
}
