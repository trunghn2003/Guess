package com.Group13.demo.Controller;


import com.Group13.demo.Model.Player;
import com.Group13.demo.Service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private PlayerService playerService;

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        playerService.register(username, password);
        return "Registration successful for user: " + username;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Optional<Player> player = playerService.login(username, password);
        return player.isPresent() ? "Login successful!" : "Invalid credentials.";
    }

    @PostMapping("/save-score")
    public String saveScore(@RequestParam String username, @RequestParam int score) {
        playerService.saveScore(username, score);
        return "Score saved for user: " + username;
    }
}

