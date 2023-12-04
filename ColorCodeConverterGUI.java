import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ColorCodeConverterGUI extends JFrame {

    private JTextArea outputTextArea;
    private Color lastSelectedColor;

    public ColorCodeConverterGUI() {
        super("Color Code Converter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 400);
        initializeComponents();
        setVisible(true);
    }

    private void initializeComponents() {
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Color Code Converter");
        titleLabel.setBounds(200, 10, 200, 25);
        panel.add(titleLabel);

        JButton colorPickerButton = new JButton("Choose Color");
        colorPickerButton.setBounds(20, 50, 150, 25);
        panel.add(colorPickerButton);

        outputTextArea = new JTextArea();
        outputTextArea.setBounds(20, 90, 500, 150);
        panel.add(outputTextArea);

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(20, 250, 80, 25);
        panel.add(clearButton);

        JButton copyButton = new JButton("Copy");
        copyButton.setBounds(110, 250, 80, 25);
        panel.add(copyButton);

        JButton loadButton = new JButton("Load");
        loadButton.setBounds(200, 250, 80, 25);
        panel.add(loadButton);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(290, 250, 80, 25);
        panel.add(saveButton);

        JButton exitButton = new JButton("Exit");
        exitButton.setBounds(380, 250, 80, 25);
        panel.add(exitButton);

        colorPickerButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(null, "Pick a Color", Color.white);
            if (selectedColor != null) {
                lastSelectedColor = selectedColor;
                updateColorOutput(selectedColor);
            }
        });

        clearButton.addActionListener(e -> outputTextArea.setText(""));

        copyButton.addActionListener(e -> {
            if (lastSelectedColor != null) {
                copyToClipboard();
            }
        });

        loadButton.addActionListener(e -> loadColorCode());

        saveButton.addActionListener(e -> {
            if (lastSelectedColor != null) {
                saveColorCode();
            }
        });

        exitButton.addActionListener(e -> System.exit(0));
    }

    private void updateColorOutput(Color color) {
        String hex = "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
        String rgb = "RGB(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
        String cmyk = convertRgbToCmyk(color);
        outputTextArea.setText("HEX: " + hex + "\n" + rgb + "\n" + cmyk);
    }

    private String convertRgbToCmyk(Color color) {
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        float k = Math.min(1.0f, Math.min(1.0f - r, Math.min(1.0f - g, 1.0f - b)));
        if (k == 1) {
            return "CMYK(0%, 0%, 0%, 100%)";
        }
        float c = (1.0f - r - k) / (1.0f - k);
        float m = (1.0f - g - k) / (1.0f - k);
        float y = (1.0f - b - k) / (1.0f - k);
        c = (float) (Math.round(c * 10000) / 100.0);
        m = (float) (Math.round(m * 10000) / 100.0);
        y = (float) (Math.round(y * 10000) / 100.0);
        k = (float) (Math.round(k * 10000) / 100.0);
        return String.format("CMYK(%.0f%%, %.0f%%, %.0f%%, %.0f%%)", c, m, y, k);
    }

    private void copyToClipboard() {
        String[] options = {"HEX", "RGB", "CMYK"};
        String choice = (String) JOptionPane.showInputDialog(
                this, "Choose the format to copy:",
                "Copy Color Code", JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice != null) {
            String textToCopy = "";
            switch (choice) {
                case "HEX":
                    textToCopy = "#" + Integer.toHexString(lastSelectedColor.getRGB()).substring(2).toUpperCase();
                    break;
                case "RGB":
                    textToCopy = "RGB(" + lastSelectedColor.getRed() + ", " + lastSelectedColor.getGreen() + ", " + lastSelectedColor.getBlue() + ")";
                    break;
                case "CMYK":
                    textToCopy = convertRgbToCmyk(lastSelectedColor);
                    break;
            }
            StringSelection selection = new StringSelection(textToCopy);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
    }

    private void loadColorCode() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Color Code");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(fileToLoad))) {
                StringBuilder fileContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line).append("\n");
                }
                outputTextArea.setText(fileContent.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading the color code: " + ex.getMessage(), "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveColorCode() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Color Code");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(outputTextArea.getText());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving the color code: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ColorCodeConverterGUI::new);
    }
}

