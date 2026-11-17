package com.portfolio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private final PortfolioProperties portfolio;

	public HomeController(PortfolioProperties portfolio) {
		this.portfolio = portfolio;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("portfolio", portfolio);
		return "index";
	}
}
