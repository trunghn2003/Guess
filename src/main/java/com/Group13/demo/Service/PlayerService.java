package com.Group13.demo.Service;

import com.Group13.demo.Model.Player;
import com.Group13.demo.Model.Score;
import com.Group13.demo.Repository.PlayerRepository;
import com.Group13.demo.Repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    public Player register(String username, String password) {
        Player player = new Player();
        player.setUsername(username);
        player.setPassword(password);
        return playerRepository.save(player);
    }

    public Optional<Player> login(String username, String password) {
        Optional<Player> player = playerRepository.findByUsername(username);
        if (player.isPresent() && player.get().getPassword().equals(password)) {
            return player;
        }
        return Optional.empty();
    }

    public void saveScore(String username, int score) {
        Optional<Player> player = playerRepository.findByUsername(username);
        player.ifPresent(p -> {
            Score newScore = new Score();
            newScore.setPlayer(p);
            newScore.setScore(score);
            scoreRepository.save(newScore);
        });
    }
}

