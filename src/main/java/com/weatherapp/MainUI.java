package com.weatherapp;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("ALL")
public class MainUI {
    private final JFrame frame;
    private final JPanel contentPanel;
    private final JComboBox<String> citySelector;
    private final JButton fetchButton;
    private final JLabel statusLabel;
    private final JProgressBar loadingBar;
    private final JLabel background;
    private final JLabel petalOverlay;
    private final JLabel title;
    private final JToggleButton langToggle;
    private Timer fadeTimer;
    private Point initialClick;
    private boolean isJapanese = true;

    public MainUI() {
        frame = new JFrame("天気トレンド - Japan Weather");
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 850);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        background = new JLabel();
        background.setBounds(0, 0, 1400, 850);
        background.setIcon(new ImageIcon(getClass().getResource("/bg_japan_style.jpg")));
        frame.setContentPane(background);
        frame.getContentPane().setLayout(null);

        petalOverlay = new JLabel(new ImageIcon(getClass().getResource("/sakura_fall.gif")));
        petalOverlay.setBounds(0, 0, 1400, 850);
        frame.getContentPane().add(petalOverlay);

        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setOpaque(false);
        contentPanel.setBounds(450, 250, 500, 300);

        title = new JLabel("日本の天気", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 36));
        title.setForeground(new Color(255, 255, 255));
        title.setBounds(100, 10, 300, 50);
        contentPanel.add(title);

        citySelector = new JComboBox<>(new String[]{
                "Tokyo", "Osaka", "Kyoto", "Sapporo", "Fukuoka",
                "Nagoya", "Yokohama", "Sendai", "Hiroshima", "Kobe"
        });
        citySelector.setBounds(150, 80, 200, 30);
        contentPanel.add(citySelector);

        fetchButton = new JButton();
        fetchButton.setBounds(150, 130, 200, 30);
        fetchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contentPanel.add(fetchButton);

        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        loadingBar.setBounds(150, 170, 200, 20);
        contentPanel.add(loadingBar);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setBounds(150, 200, 200, 30);
        statusLabel.setForeground(Color.WHITE);
        contentPanel.add(statusLabel);

        frame.getContentPane().add(contentPanel);

        langToggle = new JToggleButton("日本語 / English");
        langToggle.setBounds(1240, 780, 110, 25);
        langToggle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        langToggle.setFocusPainted(false);
        frame.getContentPane().add(langToggle);

        langToggle.addActionListener(e -> {
            isJapanese = !isJapanese;
            updateLanguage();
        });

        updateLanguage(); // Initial language

        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int thisX = frame.getLocation().x;
                int thisY = frame.getLocation().y;
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                frame.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        frame.setOpacity(0f);
        frame.setVisible(true);
        fadeInFrame();

        fetchButton.addActionListener(e -> {
            Object selected = citySelector.getSelectedItem();
            if (selected != null) {
                String city = selected.toString();
                loadingBar.setVisible(true);
                statusLabel.setText((isJapanese ? "取得中: " : "Fetching: ") + city);

                new Thread(() -> {
                    String json = WeatherApiClient.fetchWeatherData(city);
                    if (json != null) {
                        WeatherData data = WeatherDataParser.parseWeatherData(json, city);
                        if (data != null) {
                            WeatherDatabaseManager.insertWeatherData(data);
                            double[] pastTemps = {
                                    data.getTemperature(), data.getTemperature() + 1,
                                    data.getTemperature() - 1, data.getTemperature() + 2,
                                    data.getTemperature() - 2
                            };
                            double[] predicted = LinearRegressionModel.predict(pastTemps);
                            SwingUtilities.invokeLater(() -> {
                                showChartAndDetails(city, predicted, data);
                                String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                                statusLabel.setText((isJapanese ? "完了: " : "Done: ") + city + (isJapanese ? " - 最終更新: " : " - Updated: ") + time);
                                loadingBar.setVisible(false);
                            });
                        } else {
                            SwingUtilities.invokeLater(() -> {
                                statusLabel.setText(isJapanese ? "解析失敗: " + city : "Parsing failed: " + city);
                                loadingBar.setVisible(false);
                            });
                        }
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText(isJapanese ? "取得失敗: " + city : "Failed to fetch: " + city);
                            loadingBar.setVisible(false);
                        });
                    }
                }).start();
            } else {
                statusLabel.setText(isJapanese ? "都市が選択されていません。" : "No city selected.");
            }
        });
    }

    private void updateLanguage() {
        if (isJapanese) {
            title.setText("日本の天気");
            fetchButton.setText("天気を取得");
        } else {
            title.setText("Weather in Japan");
            fetchButton.setText("Get Weather");
        }
    }

    private void showChartAndDetails(String city, double[] temps, WeatherData data) {
        JFrame popup = new JFrame("Weather Info - " + city);
        popup.setSize(1000, 500);
        popup.setLayout(new GridLayout(1, 2));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < temps.length; i++) {
            dataset.addValue(temps[i], "Temperature", "Day " + (i + 1));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Temperature Trends in " + city, "Day", "Temp (°C)", dataset,
                PlotOrientation.VERTICAL, false, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);

        String info = String.format(
                "都市 / City: %s\n気温 / Temperature: %.1f°C\n体感 / Feels Like: %.1f°C\n湿度 / Humidity: %d%%\n気圧 / Pressure: %dhPa\n風速 / Wind Speed: %.1f m/s\n雲量 / Cloud Cover: %s%%\n天気 / Condition: %s",
                data.getCity(),
                data.getTemperature(),
                data.getFeelsLike(),
                data.getHumidity(),
                data.getPressure(),
                data.getWindSpeed(),
                data.getCloudCover() != null ? data.getCloudCover() : "--",
                data.getWeatherCondition()
        );

        JTextArea infoArea = new JTextArea(info);
        infoArea.setFont(new Font("Meiryo", Font.PLAIN, 14));
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        JScrollPane infoScroll = new JScrollPane(infoArea);

        popup.add(chartPanel);
        popup.add(infoScroll);

        popup.setLocationRelativeTo(null);
        popup.setVisible(true);
    }

    private void fadeInFrame() {
        fadeTimer = new Timer(30, null);
        fadeTimer.addActionListener(new ActionListener() {
            float opacity = 0f;
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                frame.setOpacity(Math.min(opacity, 1f));
                if (opacity >= 1f) {
                    fadeTimer.stop();
                }
            }
        });
        fadeTimer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainUI::new);
    }
}
