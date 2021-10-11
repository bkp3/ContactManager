package com.smart.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotController {
	Random random = new Random(1000);

	// email id form open handler
	@GetMapping("/forgot")
	public String openEmailForm() {

		return "forgot_password";
	}

	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email) {
		System.out.println("EMAIL:- " + email);

		// generating 4 digit of OTP

		int otp = random.nextInt(9999);
		System.out.println("OTP:- " + otp);

		// write code for send otp to email..

		return "verify_otp";
	}

}
