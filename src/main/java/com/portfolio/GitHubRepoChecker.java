package com.portfolio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * Asks GitHub whether a repository is public. GitHub answers "not found" for a private repo
 * when asked without logging in, so a 404 is treated as private.
 */
@Service
public class GitHubRepoChecker {

	private static final Pattern REPO_URL =
			Pattern.compile("^https?://(?:www\\.)?github\\.com/([\\w.-]+)/([\\w.-]+?)(?:\\.git)?(?:[/?#].*)?$");

	private final HttpClient http = HttpClient.newBuilder()
			.connectTimeout(Duration.ofSeconds(5))
			.followRedirects(HttpClient.Redirect.NORMAL)
			.build();

	/**
	 * Returns true if the repo is private, false if public, or empty if the link isn't a GitHub repo
	 * or GitHub couldn't be reached.
	 */
	public Optional<Boolean> isPrivate(String githubUrl) {
		if (githubUrl == null) {
			return Optional.empty();
		}
		Matcher m = REPO_URL.matcher(githubUrl.strip());
		if (!m.matches()) {
			return Optional.empty();
		}
		HttpRequest request = HttpRequest.newBuilder(
						URI.create("https://api.github.com/repos/" + m.group(1) + "/" + m.group(2)))
				.timeout(Duration.ofSeconds(5))
				.header("Accept", "application/vnd.github+json")
				.header("User-Agent", "portfolio-website")
				.method("HEAD", HttpRequest.BodyPublishers.noBody())
				.build();
		try {
			int status = http.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
			if (status == 200) {
				return Optional.of(false);
			}
			if (status == 404) {
				return Optional.of(true);
			}
			return Optional.empty(); // e.g. rate limited
		} catch (Exception e) {
			if (e instanceof InterruptedException) {
				Thread.currentThread().interrupt();
			}
			return Optional.empty();
		}
	}
}
