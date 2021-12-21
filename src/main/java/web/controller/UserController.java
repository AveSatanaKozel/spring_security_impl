package web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import web.model.Role;
import web.model.User;
import web.service.RoleService;
import web.service.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller

public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin";
    }

    @GetMapping("/addnew")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "/adduser";
    }

    @PostMapping("/adduser")
    public String saveUser(@RequestParam("username") String username,
                           @RequestParam("city") String city,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           @RequestParam(required = false, name = "ROLE_ADMIN") String roleAdmin,
                           @RequestParam(required = false, name = "ROLE_USER") String roleUser) {

        Set<Role> roles = new HashSet<>();
        if (roleAdmin != null) {
            roles.add(new Role(2, roleAdmin));
        }
        System.out.println("adminsRole" + roles);
        if (roleUser != null) {
            roles.add(new Role(1, roleUser));
        }
        if (roleAdmin == null && roleUser == null) {
            roles.add(new Role(1, roleUser));
        }
        System.out.println(username);
        System.out.println(city);
        System.out.println(email);
        System.out.println(password);
        System.out.println(roleAdmin);
        System.out.println(roleUser);
        User user = new User(username, city, email, password, roles);

        System.out.println(user);

        try {
            userService.addUser(user);
        } catch (Exception ignored) {

        }
        return "redirect:/admin";
    }

    @GetMapping("/edituser/{id}")
    public String editUser(Model model,
                           @PathVariable("id") int id) {
        model.addAttribute("user", userService.getUser(id));
        return "edituser";
    }

    @PostMapping("/{id}")
    public String editUser(@ModelAttribute("user") User user, @PathVariable("id") int id,
                           @RequestParam(required = false, name = "ROLE_ADMIN") String roleAdmin,
                           @RequestParam(required = false, name = "ROLE_USER") String roleUser) {

        Set<Role> roles = new HashSet<>();
        if (roleAdmin != null) {
            roles.add(new Role(2, roleAdmin));

        }
        if (roleUser != null) {
            roles.add(new Role(1, roleUser));
        }
        if (roleAdmin == null && roleUser == null) {
            roles.add(new Role(1, roleUser));
        }

        user.setRoles(roles);

        userService.editUser(user);

        return "redirect:/admin";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(userService.getUser(id));
        return "redirect:/admin";
    }

    @GetMapping("/user")
    public ModelAndView showUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/")
    public String login() {
        return "login";
    }
}