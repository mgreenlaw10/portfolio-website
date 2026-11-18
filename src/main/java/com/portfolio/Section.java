package com.portfolio;

import java.util.Arrays;
import java.util.Optional;

/** The two project sections on the home page. {@code slug} is used in URLs. */
public enum Section {
	CURRENT("current", "Current Projects"),
	HIGHLIGHTED("highlighted", "Highlighted Projects");

	private final String slug;
	private final String title;

	Section(String slug, String title) {
		this.slug = slug;
		this.title = title;
	}

	public String getSlug() {
		return slug;
	}

	public String getTitle() {
		return title;
	}

	public static Optional<Section> fromSlug(String slug) {
		return Arrays.stream(values()).filter(s -> s.slug.equals(slug)).findFirst();
	}
}
