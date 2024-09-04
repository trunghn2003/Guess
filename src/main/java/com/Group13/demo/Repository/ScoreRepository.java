package com.Group13.demo.Repository;


import com.Group13.demo.Model.Score;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<Score, Long> {
}