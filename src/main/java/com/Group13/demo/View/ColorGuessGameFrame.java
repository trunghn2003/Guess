package com.Group13.demo.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ColorGuessGameFrame extends JFrame {
    private List<Color> shownColors;
    private List<Color> allColors;
    private JPanel colorPanel;
    private JPanel buttonPanel;
    private JLabel instructionLabel;
    private JButton startButton;
    private int score;
    private int currentQuestion;
    private Timer selectionTimer;
    private JLabel timerLabel;
    private int selectionsMade;
    private String currentUsername;

    public ColorGuessGameFrame() {
        setTitle("Color Guess Game");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        instructionLabel = new JLabel("Press 'Start' to begin the game!", SwingConstants.CENTER);
        add(instructionLabel, BorderLayout.NORTH);

        colorPanel = new JPanel();
        colorPanel.setLayout(new GridLayout(1, 3));
        add(colorPanel, BorderLayout.CENTER);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3));
        add(buttonPanel, BorderLayout.SOUTH);

        startButton = new JButton("Start");
        startButton.addActionListener(new StartButtonListener());
        add(startButton, BorderLayout.WEST);

        timerLabel = new JLabel("Time Left: 10s", SwingConstants.CENTER);
        add(timerLabel, BorderLayout.EAST);

        shownColors = new ArrayList<>();
        allColors = new ArrayList<>();
        Collections.addAll(allColors, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK, Color.LIGHT_GRAY);

        score = 0;
        currentQuestion = 0;
        selectionsMade = 0;

        loginOrRegister();
    }

    private void loginOrRegister() {
        String[] options = {"Login", "Register"};
        int choice = JOptionPane.showOptionDialog(this, "Do you want to login or register?", "User Authentication",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            login();
        } else {
            register();
        }
    }

    private void login() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (sendPostRequest("/api/login", "username=" + username + "&password=" + password)) {
                currentUsername = username;
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + currentUsername + "!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
                login();
            }
        }
    }

    private void register() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
                "Username:", usernameField,
                "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (sendPostRequest("/api/register", "username=" + username + "&password=" + password)) {
                currentUsername = username;
                JOptionPane.showMessageDialog(this, "Registration successful! Welcome, " + currentUsername + "!");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists. Please try again.");
                register();
            }
        }
    }

    private boolean sendPostRequest(String endpoint, String params) {
        try {
            URL url = new URL("http://localhost:8080" + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream os = conn.getOutputStream();
            os.write(params.getBytes(StandardCharsets.UTF_8));
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Scanner scanner = new Scanner(conn.getInputStream());
                String response = scanner.nextLine();
                scanner.close();
                System.out.println("Response: " + response);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void startGameRound() {
        if (currentQuestion >= 10) {
            instructionLabel.setText("Game Over! Final Score: " + score);
            updateScore();
            startButton.setEnabled(true);
            return;
        }

        currentQuestion++;
        selectionsMade = 0;
        shownColors.clear();
        colorPanel.removeAll();
        buttonPanel.removeAll();

        // Randomly select 3 colors to show
        Collections.shuffle(allColors);
        for (int i = 0; i < 3; i++) {
            shownColors.add(allColors.get(i));
            JPanel colorDisplay = new JPanel();
            colorDisplay.setBackground(allColors.get(i));
            colorPanel.add(colorDisplay);
        }
        colorPanel.revalidate();
        colorPanel.repaint();

        // Wait for 2 seconds before showing the color buttons
        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showColorOptions();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void showColorOptions() {
        colorPanel.removeAll();
        colorPanel.revalidate();
        colorPanel.repaint();

        Collections.shuffle(allColors);
        for (Color color : allColors) {
            JButton colorButton = new JButton();
            colorButton.setBackground(color);
            colorButton.setPreferredSize(new Dimension(100, 100)); // Đặt kích thước lớn hơn cho các nút màu
            colorButton.addActionListener(new ColorButtonListener(color));
            buttonPanel.add(colorButton);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();

        instructionLabel.setText("Select the colors that were shown!");
        startSelectionTimer();
    }

    private void startSelectionTimer() {
        if (selectionTimer != null && selectionTimer.isRunning()) {
            selectionTimer.stop();
        }

        final int[] timeLeft = {10};
        timerLabel.setText("Time Left: " + timeLeft[0] + "s");

        selectionTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft[0]--;
                timerLabel.setText("Time Left: " + timeLeft[0] + "s");

                if (timeLeft[0] <= 0) {
                    selectionTimer.stop();
                    instructionLabel.setText("Time's up! Moving to the next round.");
                    startGameRound();
                }
            }
        });
        selectionTimer.start();
    }

    private void updateScore() {
        sendPostRequest("/api/save-score", "username=" + currentUsername + "&score=" + score);
    }

    private class StartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            score = 0;
            currentQuestion = 0;
            startGameRound();
        }
    }

    private class ColorButtonListener implements ActionListener {
        private Color color;

        public ColorButtonListener(Color color) {
            this.color = color;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();

            if (shownColors.contains(color)) {
                score++;
                instructionLabel.setText("Correct! Score: " + score);
            } else {
                instructionLabel.setText("Wrong! Score: " + score);
            }

            button.setEnabled(false); // Vô hiệu hóa nút sau khi đã chọn
            selectionsMade++;

            if (selectionsMade == 3) {
                if (selectionTimer != null) {
                    selectionTimer.stop();
                }
                startGameRound();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ColorGuessGameFrame game = new ColorGuessGameFrame();
            game.setVisible(true);
        });
    }
}
