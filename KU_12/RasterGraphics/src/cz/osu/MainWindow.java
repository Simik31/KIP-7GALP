package cz.osu;

import javax.swing.*;

public class MainWindow extends JPanel {

    private ImagePanel imagePanel;

    private V_RAM vram;

    public MainWindow() {
        initialize(1280, 720);

        vram = new V_RAM(200, 200);

        imagePanel.setImage(vram.getImage());

        new KU1(vram, imagePanel, true);
    }

    private void initialize(int width, int height) {
        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();

        imagePanel = new ImagePanel();
        imagePanel.setBounds(10, 10, width - 40, height - 60);
        this.add(imagePanel);

        JFrame frame = new JFrame("Raster Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setSize(width, height);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
