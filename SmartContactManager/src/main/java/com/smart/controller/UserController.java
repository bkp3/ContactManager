package com.smart.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/index")
	public String dashBoard(Model model, Principal principal) {
		String username = principal.getName();
		System.out.println("username:- " + username);

		// get the user by username
		User user = this.userRepository.getUserByUserName(username);
		System.out.println(user);

		model.addAttribute("user", user);

		return "normal/user_dashboard";
	}

}
