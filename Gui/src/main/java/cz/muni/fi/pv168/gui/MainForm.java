package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.Main;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.impl.CauldronManagerImpl;
import cz.muni.fi.pv168.impl.HellManagerImpl;
import cz.muni.fi.pv168.impl.SinnerManagerImpl;
import org.jdesktop.swingx.JXDatePicker;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Clock;
import java.time.ZoneId;

/**
 * Created by cechy on 26.04.2016.
 */
public class MainForm {
    private CauldronManagerImpl cauldronManager;
    private HellManagerImpl hellManager;
    private SinnerManagerImpl sinnerManager;

    private JPanel panel1;
    private JButton sinnersButton;
    private JButton cauldronsButton;
    private JButton exitButton;
    private JTabbedPane tabbedPane1;
    private JTable sinnersTable;
    private JButton addSinnerButton;
    private JButton deleteButton;
    private JTextField textFieldFirstName;
    private JTextField textFieldSin;
    private JTextField textFieldLastName;
    private JCheckBox signedContractWithDevilCheckBox;
    private JXDatePicker releaseDate;
    private JTable cauldronsTable;
    private JButton deleteCauldronButton;
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
                if(signedContractWithDevilCheckBox.isSelected()){
                    releaseDate.setEnabled(false);
                    releaseDate.setDate(null);
                }
                if(!signedContractWithDevilCheckBox.isSelected()) releaseDate.setEnabled(true);
            }
        });
        addSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //data correctness check
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
                if (!signedContractWithDevilCheckBox.isSelected() && (releaseDate.getDate() == null)){
                    correctionLabelSinner.setText("Please input signed contract or release date");
                    correctionLabelSinner.setForeground(Color.RED);
                }

                //add Sinner
                try {
                    Sinner sinner = new Sinner();
                    sinner.setFirstName(textFieldFirstName.getText());
                    sinner.setLastName(textFieldLastName.getText());
                    sinner.setSin(textFieldSin.getText());
                    if(releaseDate.getDate() != null)
                        sinner.setReleaseDate(releaseDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    sinner.setSignedContractWithDevil(signedContractWithDevilCheckBox.isSelected());

                    sinnerManager.createSinner(sinner);

                    SinnerTableModel model = (SinnerTableModel) sinnersTable.getModel();
                    model.addSinner(sinner);
                }catch(NumberFormatException ex){
                    correctionLabelCauldron.setText("Cant parse given input: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                }
            }
        });
        addCauldronButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //data correctness check
                correctionLabelCauldron.setText("Correct");
                correctionLabelCauldron.setForeground(Color.GREEN);

                if(textFieldCapacity.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input capacity");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }
                if(textFieldWaterTemperature.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input water temperature");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }
                if(textFieldHellFloor.getText().isEmpty()){
                    correctionLabelCauldron.setText("Please input hell floor");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }

                //add cauldron
                try {
                    Cauldron cauldron = new Cauldron();
                    cauldron.setCapacity(Integer.parseInt(textFieldCapacity.getText()));
                    cauldron.setHellFloor(Integer.parseInt(textFieldHellFloor.getText()));
                    cauldron.setWaterTemperature(Integer.parseInt(textFieldWaterTemperature.getText()));

                    cauldronManager.createCauldron(cauldron);

                    CauldronTableModel model = (CauldronTableModel) cauldronsTable.getModel();
                    model.addCauldron(cauldron);
                }catch(NumberFormatException ex){
                    correctionLabelCauldron.setText("Cant parse given input: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                }
            }
        });
        deleteCauldronButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = cauldronsTable.getSelectedRow();
                if(selectedRow != -1) {
                    try {
                        long id = (long) cauldronsTable.getValueAt(selectedRow, 0);
                        cauldronManager.deleteCauldron(cauldronManager.findCauldronById(id));
                        CauldronTableModel model = (CauldronTableModel) cauldronsTable.getModel();
                        model.removeCauldron(id);
                    } catch (ServiceFailureException ex) {
                        correctionLabelCauldron.setText("Cannot delete cauldron");
                        correctionLabelCauldron.setForeground(Color.RED);
                    }
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

        DataSource dataSource = Main.createMemoryDatabase();
        cauldronManager = new CauldronManagerImpl(dataSource);
        hellManager = new HellManagerImpl(dataSource);
        sinnerManager = new SinnerManagerImpl(dataSource, Clock.systemDefaultZone());

        CauldronTableModel cauldronTableModel = new CauldronTableModel();
        for(Cauldron cauldron : cauldronManager.findAllCauldrons()){
            cauldronTableModel.addCauldron(cauldron);
        }
        cauldronsTable = new JTable(cauldronTableModel);

        SinnerTableModel sinnerTableModel = new SinnerTableModel();
        for(Sinner sinner : sinnerManager.findAllSinners()){
            sinnerTableModel.addSinner(sinner);
        }
        sinnersTable = new JTable(sinnerTableModel);
    }

    //TODO deltovat sinnera + celou tabulku topeni v kotli + čudlíky
}
