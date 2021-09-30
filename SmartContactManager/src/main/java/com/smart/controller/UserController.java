package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String username = principal.getName();
		System.out.println("username:- " + username);

		// get the user by username
		User user = this.userRepository.getUserByUserName(username);
		System.out.println(user);

		model.addAttribute("user", user);

	}

	// dashboard home
	@RequestMapping("/index")
	public String dashBoard(Model model, Principal principal) {
		model.addAttribute("title", "Home | Smart Contact Manager");
		return "normal/user_dashboard";
	}

	// open add contact form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// processing and uploading the file
			if (file.isEmpty()) {
				// if the file is empty, then try our message
				System.out.println("file is empty");
				contact.setImage("default.png");
			} else {
				// upload the file to the folder and update the name to the contact
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsoluteFile() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is Uploaded..");

			}

			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("Data:- " + contact);
			System.out.println("data added into database");

			// message success
			session.setAttribute("message", new Message("Your contac is added !! Add more...", "success"));

		} catch (Exception e) {
			System.out.println("ERROR:- " + e.getMessage());

			// message error
			session.setAttribute("message", new Message("Something went wrong !! Try again...", "danger"));
		}

		return "normal/add_contact_form";
	}

	// show contact handler
	// per page = 5[n];
	// current page=0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {

		model.addAttribute("title", "Show User Contacts");
		// fetch contact list

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// currentPage-page
		// contact per page -5
		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		model.addAttribute("title", "Contact Detail | Smart Contact Manager");

		System.out.println("cid:- " + cId);

		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		//
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal/contact_detail";
	}

	// delete contact handler
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid, Model model, Principal principal,
			HttpSession httpSession) {

		Contact contact = this.contactRepository.findById(cid).get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			contact.setUser(null);
			this.contactRepository.delete(contact);
			httpSession.setAttribute("message", new Message("Contact delete Successfully...", "success"));

		} else {
			httpSession.setAttribute("message", new Message("Something went wrong...", "danger"));
		}

		return "redirect:/user/show-contacts/0";
	}

	// open update form handler
	@PostMapping("update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {

		model.addAttribute("title", "Update Contact");

		Contact contact = this.contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);

		return "normal/update_contact";
	}

}
