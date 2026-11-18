package com.portfolio;

/**
 * One project widget. {@code image} is a URL path such as /uploads/abc.png, or blank.
 * {@code privateRepo} is filled in automatically by asking GitHub (null if unknown).
 */
public record Project(String id, String title, String github, String description, String image,
		Boolean privateRepo) {

	public Project withPrivateRepo(Boolean value) {
		return new Project(id, title, github, description, image, value);
	}
}
