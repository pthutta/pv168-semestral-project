package cz.muni.fi.pv168.gui;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by cechy on 26.04.2016.
 */
public class MainForm {
    private JPanel panel1;
    private JButton sinnersButton;
    private JButton cauldronsButton;
    private JButton exitButton;
    private JTabbedPane tabbedPane1;
    private JTable table1;
    private JButton addSinnerButton;
    private JButton deleteButton;
    private JTextField textFieldFirstName;
    private JTextField textFieldSin;
    private JTextField textFieldLastName;
    private JCheckBox signedContractWithDevilCheckBox;
    private JXDatePicker releaseDate;
    private JTable table2;
    private JButton deleteButton1;
    private JTable table3;
    private JButton releaseSinnerButton;
    private JButton boilSinnerButton;
    private JTextField textFieldCapacity;
    private JTextField textFieldWaterTemperature;
    private JTextField textFieldHellFloor;
    private JButton addCauldronButton;
    private JPanel MainJPanel;
    private JLabel correctionLabelSinner;
    private JLabel correctionLabelCauldron;
    private JLabel infoLabelSinner;
    private JLabel infoLabelCauldron;

    public MainForm() {
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        signedContractWithDevilCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(signedContractWithDevilCheckBox.isSelected()) releaseDate.setEnabled(false);
                if(!signedContractWithDevilCheckBox.isSelected()) releaseDate.setEnabled(true);
            }
        });
        addSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                correctionLabelSinner.setText("Correct");
                correctionLabelSinner.setForeground(Color.GREEN);
                if(textFieldFirstName.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input first name");
                    correctionLabelSinner.setForeground(Color.RED);
                }
                if(textFieldLastName.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input last name");
                    correctionLabelSinner.setForeground(Color.RED);
                }
                if(textFieldSin.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input sin");
                    correctionLabelSinner.setForeground(Color.RED);
                }
                if (!signedContractWithDevilCheckBox.isSelected() || !releaseDate.isValid()){
                    correctionLabelSinner.setText("Please input signed contract or release date");
                    correctionLabelSinner.setForeground(Color.RED);
                }
            }
        });
        addCauldronButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                correctionLabelCauldron.setText("Correct");
                correctionLabelCauldron.setForeground(Color.GREEN);
                if(textFieldCapacity.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input capacity");
                    correctionLabelCauldron.setForeground(Color.RED);
                }
                if(textFieldWaterTemperature.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input water temperature");
                    correctionLabelCauldron.setForeground(Color.RED);
                }
                if(textFieldHellFloor.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input hell floor");
                    correctionLabelCauldron.setForeground(Color.RED);
                }
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Hell Manager");
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setContentPane(new MainForm().MainJPanel);
                frame.setPreferredSize(new Dimension(800,600));
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private void createUIComponents() {
        releaseDate = new JXDatePicker();
    }
}
