package cz.osu;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainWindow extends JPanel{

    private ImagePanel imagePanel;
    private JLabel infoLabel;

    private V_RAM vram;

    private Triangle2D t1;
    private Triangle2D t2;

    public MainWindow(){

        initialize();
        
        vram = new V_RAM(100, 100);

        GraphicsOperations.fillBrightness(vram, 255);

        new KU_2(vram, imagePanel, 30);

        imagePanel.setImage(vram.getImage());
    }

    private void initialize(){

        setLayout(null);
        setFocusable(true);
        requestFocusInWindow();

        imagePanel = new ImagePanel();
        imagePanel.setBounds(10,60, 970, 600);
        this.add(imagePanel);


        //open image
        JButton button = new JButton();
        button.setBounds(150,10,120,30);
        button.setText("Load Image");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                openImage();
            }
        });

        this.add(button);


        //save image as PNG
        JButton button4 = new JButton();
        button4.setBounds(10,10,120,30);
        button4.setText("Save as PNG");
        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImageAsPNG();
            }
        });

        this.add(button4);

        infoLabel = new JLabel();
        infoLabel.setBounds(850,10,120,30);
        infoLabel.setText("Rotation");
        infoLabel.setFont(new Font(infoLabel.getName(), Font.BOLD, 20));

        this.add(infoLabel);

        this.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {

                if(e.getKeyChar() == 'r'){

                    infoLabel.setText("Rotation");
                }

                if(e.getKeyChar() == 't'){

                    infoLabel.setText("Translation");
                }

                if(e.getKeyChar() == 's'){

                    infoLabel.setText("Scale");
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();

                GraphicsOperations.fillBrightness(vram, 255);

                GraphicsOperations.drawTriangle(vram, t1, 128);

                if(infoLabel.getText().equals("Translation")){

                    if (key == KeyEvent.VK_LEFT) t2.applyMatrix(Matrix2D.createTranslationMatrix(-1, 0));

                    if (key == KeyEvent.VK_UP) t2.applyMatrix(Matrix2D.createTranslationMatrix(0, -1));

                    if (key == KeyEvent.VK_RIGHT) t2.applyMatrix(Matrix2D.createTranslationMatrix(1, 0));

                    if (key == KeyEvent.VK_DOWN) t2.applyMatrix(Matrix2D.createTranslationMatrix(0, 1));
                }

                if(infoLabel.getText().equals("Rotation")){

                    if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_UP) t2.applyMatrix(Matrix2D.createRotationMatrix(-1));

                    if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN) t2.applyMatrix(Matrix2D.createRotationMatrix(1));
                }

                if(infoLabel.getText().equals("Scale")){

                    if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_UP) t2.applyMatrix(Matrix2D.createScalingMatrix(1.05, 1.05));

                    if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_DOWN) t2.applyMatrix(Matrix2D.createScalingMatrix(1 / 1.05, 1 / 1.05));
                }

                GraphicsOperations.drawTriangle(vram, t2, 50);

                imagePanel.setImage(vram.getImage());
            }
        });

        JFrame frame = new JFrame("Raster Graphics");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setSize(1004, 705);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void openImage(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Load Image");

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            try {

                BufferedImage temp = ImageIO.read(file);

                if(temp != null){

                    imagePanel.setImage(temp);

                }else {

                    JOptionPane.showMessageDialog(null, "Unable to load image", "Open image: ", JOptionPane.ERROR_MESSAGE);
                }

            }catch (IOException e){

                e.printStackTrace();
            }
        }
    }

    private void saveImageAsPNG(){

        String userDir = System.getProperty("user.home");
        JFileChooser fc = new JFileChooser(userDir +"/Desktop");
        fc.setDialogTitle("Save Image as PNG");

        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();

            String fname = file.getAbsolutePath();

            if(!fname.endsWith(".png") ) file = new File(fname + ".png");

            try {

                ImageIO.write(imagePanel.getImage(), "png", file);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        new MainWindow();
    }
}
