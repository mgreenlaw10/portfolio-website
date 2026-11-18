package com.portfolio;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private final PortfolioStore store;

	public HomeController(PortfolioStore store) {
		this.store = store;
	}

	@GetMapping("/")
	public String home(Model model, Principal principal) {
		model.addAttribute("portfolio", store.get());
		model.addAttribute("sections", Section.values());
		model.addAttribute("loggedIn", principal != null);
		return "index";
	}
}
