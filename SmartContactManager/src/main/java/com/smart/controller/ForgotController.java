package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.service.EmailService;

@Controller
public class ForgotController {
	Random random = new Random(1000);
	@Autowired
	private EmailService emailService;

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
			session.setAttribute("otp", otp);
			return "verify_otp";
		} else {
			session.setAttribute("message", "Check your email id...");

			return "forgot_password";
		}

	}



}
