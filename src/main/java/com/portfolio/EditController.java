package com.portfolio;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/** Edit pages for the profile and project widgets. Only reachable when logged in (see SecurityConfig). */
@Controller
@RequestMapping("/admin")
public class EditController {

	private final PortfolioStore store;
	private final ImageStorage images;

	public EditController(PortfolioStore store, ImageStorage images) {
		this.store = store;
		this.images = images;
	}

	// ---- Profile (photo, name, description) ----

	@GetMapping("/profile")
	public String editProfile(Model model) {
		Portfolio p = store.get();
		model.addAttribute("name", p.name());
		model.addAttribute("description", p.description());
		model.addAttribute("photo", p.photo());
		return "edit-profile";
	}

	@PostMapping("/profile")
	public String publishProfile(@RequestParam String name, @RequestParam String description,
			@RequestParam(required = false) MultipartFile photo, Model model) throws IOException {
		String photoPath = store.get().photo();
		try {
			String uploaded = images.save(photo);
			if (uploaded != null) {
				photoPath = uploaded;
			}
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("name", name);
			model.addAttribute("description", description);
			model.addAttribute("photo", photoPath);
			return "edit-profile";
		}
		store.updateProfile(name.strip(), description.strip(), photoPath);
		return "redirect:/";
	}

	// ---- Projects ----

	@GetMapping("/projects/{section}/new")
	public String newProject(@PathVariable String section, Model model) {
		model.addAttribute("section", section(section));
		model.addAttribute("project", new Project(null, "", "", "", ""));
		return "edit-project";
	}

	@GetMapping("/projects/{section}/{id}")
	public String editProject(@PathVariable String section, @PathVariable String id, Model model) {
		Section s = section(section);
		Project project = store.findProject(s, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		model.addAttribute("section", s);
		model.addAttribute("project", project);
		return "edit-project";
	}

	@PostMapping("/projects/{section}")
	public String publishProject(@PathVariable String section,
			@RequestParam(required = false) String id,
			@RequestParam String title,
			@RequestParam String github,
			@RequestParam String description,
			@RequestParam(required = false) MultipartFile image,
			Model model) throws IOException {
		Section s = section(section);
		boolean isNew = !StringUtils.hasText(id);
		String imagePath = isNew ? "" : store.findProject(s, id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)).image();
		try {
			String uploaded = images.save(image);
			if (uploaded != null) {
				imagePath = uploaded;
			}
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			model.addAttribute("section", s);
			model.addAttribute("project", new Project(isNew ? null : id, title, github, description, imagePath));
			return "edit-project";
		}
		store.saveProject(s, new Project(isNew ? UUID.randomUUID().toString() : id,
				title.strip(), github.strip(), description.strip(), imagePath));
		return "redirect:/";
	}

	private static Section section(String slug) {
		return Section.fromSlug(slug).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}
}
