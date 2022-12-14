package com.example.store.web.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.store.exception.LoginFailureException;
import com.example.store.exception.StoreException;
import com.example.store.service.UserService;
import com.example.store.utils.SessionUtils;
import com.example.store.vo.User;
import com.example.store.web.form.UserRegisterForm;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class HomeController {
	
	private final UserService userService;
	
    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
    	model.addAttribute("userRegisterForm", new UserRegisterForm());
        return "registerForm";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute(name = "userRegisterForm") @Valid UserRegisterForm form, BindingResult errors) {
    	
    	if (errors.hasErrors()) {
    		return "registerForm";
    	}
    	
    	try {
    		User user = form.formToUser();
    		userService.addNewUser(user);
    		return "redirect:/completed";
    		
    	} catch (StoreException e) {
    		errors.rejectValue("email", null, e.getMessage());
    		return "registerForm";
    	}
    }

    @GetMapping("/completed")
    public String registerCompleted() {
        return "completed";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "loginForm";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam(name = "email") String email, @RequestParam(name = "password") String password) {
    	try {
    		User user = userService.login(email, password);
    		SessionUtils.addAttribute("LOGIN_USER", user);
    	} catch (LoginFailureException e) {
    		return "redirect:/login?error=failure";
    	}
    	return "redirect:/";
    }
    
    @RequestMapping("/logout")
    public String logout() {
    	SessionUtils.clearAllAttributes();
    	
    	return "redirect:/";
    }
}
