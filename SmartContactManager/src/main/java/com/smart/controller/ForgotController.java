package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.service.EmailService;

@Controller
public class ForgotController {

	Random random = new Random(1000);

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepository;

	// email id form open handler
	@GetMapping("/forgot")
	public String openEmailForm() {

		return "forgot_password";
	}

	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email, HttpSession session) {
		System.out.println("EMAIL:- " + email);

		// generating 4 digit of OTP

		int otp = random.nextInt(9999);
		System.out.println("OTP:- " + otp);

		// write code for send otp to email..
		String subject = "OTP from SCM";
		String message = "Your OTP is:- " + otp;
		String to = email;

		boolean flag = this.emailService.sendEmail(subject, message, to);
		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		} else {
			session.setAttribute("message", "Check your email id...");

			return "forgot_password";
		}

	}

	// verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		int myOtp = (int) session.getAttribute("myOtp");
		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {
			// password change form

			User user = this.userRepository.getUserByUserName(email);
			if (user == null) {
				// send error message
				session.setAttribute("message", "User does not exist with this email id...");

				return "forgot_password";

			} else {
				// send change password form

			}

			return "password_change_form";
		} else {

			session.setAttribute("message", "You have entered wrong OTP");
			return "verify_otp";
		}
	}

}
