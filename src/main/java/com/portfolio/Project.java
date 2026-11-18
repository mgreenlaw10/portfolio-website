package com.portfolio;

/** One project widget. {@code image} is a URL path such as /uploads/abc.png, or blank. */
public record Project(String id, String title, String github, String description, String image) {
}
