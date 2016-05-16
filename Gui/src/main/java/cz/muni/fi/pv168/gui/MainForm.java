package cz.muni.fi.pv168.gui;

import cz.muni.fi.pv168.Cauldron;
import cz.muni.fi.pv168.Main;
import cz.muni.fi.pv168.Sinner;
import cz.muni.fi.pv168.exceptions.IllegalEntityException;
import cz.muni.fi.pv168.exceptions.ServiceFailureException;
import cz.muni.fi.pv168.impl.CauldronManagerImpl;
import cz.muni.fi.pv168.impl.HellManagerImpl;
import cz.muni.fi.pv168.impl.SinnerManagerImpl;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.DatePickerCellEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by cechy on 26.04.2016.
 */
public class MainForm {
    final static Logger log = LoggerFactory.getLogger(MainForm.class);

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
    private JButton deleteSinnerButton;
    private JTextField textFieldFirstName;
    private JTextField textFieldSin;
    private JTextField textFieldLastName;
    private JCheckBox signedContractWithDevilCheckBox;
    private JXDatePicker releaseDate;
    private JTable cauldronsTable;
    private JButton deleteCauldronButton;
    private JTable sinnerCauldronTable;
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

    //swing workers
    private CreateSinnerWorker createSinnerWorker;
    private CreateCauldronWorker createCauldronWorker;
    private DeleteSinnerWorker deleteSinnerWorker;
    private DeleteCauldronWorker deleteCauldronWorker;
    private ReleaseSinnerWorker releaseSinnerWorker;
    private UpdateAllSinnersWorker updateAllSinnersWorker;
    private UpdateAllCauldronsWorker updateAllCauldronsWorker;
    private BoilSinnerWorker boilSinnerWorker;
    private RefreshRelationTableWorker relationTableWorker;

    private class CreateSinnerWorker extends SwingWorker<Sinner, Void> {
        private Sinner sinner;

        public CreateSinnerWorker(Sinner sinner) {
            this.sinner = sinner;
        }

        @Override
        protected Sinner doInBackground() throws Exception {
            sinnerManager.createSinner(sinner);
            return sinner;
        }

        @Override
        protected void done() {
            addSinnerButton.setEnabled(true);
            createSinnerWorker = null;
            try {
                sinner = get();
                SinnerTableModel model = (SinnerTableModel) sinnersTable.getModel();
                model.addSinner(sinner);
                refreshSinnerCauldronTable();
            } catch (ExecutionException ex) {
                correctionLabelSinner.setText(ex.getCause() + "\n");
                correctionLabelSinner.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

    private class CreateCauldronWorker extends SwingWorker<Cauldron, Void> {
        private Cauldron cauldron;

        public CreateCauldronWorker(Cauldron cauldron) {
            this.cauldron = cauldron;
        }

        @Override
        protected Cauldron doInBackground() throws Exception {
            cauldronManager.createCauldron(cauldron);
            return cauldron;
        }

        @Override
        protected void done() {
            addCauldronButton.setEnabled(true);
            createCauldronWorker = null;
            try {
                cauldron = get();
                CauldronTableModel model = (CauldronTableModel) cauldronsTable.getModel();
                model.addCauldron(cauldron);

            } catch (ExecutionException ex) {
                correctionLabelCauldron.setText(ex.getCause() + "\n");
                correctionLabelCauldron.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

    private class DeleteSinnerWorker extends SwingWorker<Void, Void> {
        private Long id;

        public DeleteSinnerWorker(Long id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground() throws Exception {
            sinnerManager.deleteSinner(sinnerManager.findSinnerById(id));
            return null;
        }

        @Override
        protected void done() {
            deleteSinnerButton.setEnabled(true);
            deleteSinnerWorker = null;
            try {
                get();
                SinnerTableModel model = (SinnerTableModel) sinnersTable.getModel();
                model.removeSinner(id);
                refreshSinnerCauldronTable();

            } catch (ExecutionException ex) {
                correctionLabelSinner.setText(ex.getCause() + "\n");
                correctionLabelSinner.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

    private class DeleteCauldronWorker extends SwingWorker<Void, Void> {
        private Long id;

        public DeleteCauldronWorker(Long id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground() throws Exception {
            cauldronManager.deleteCauldron(cauldronManager.findCauldronById(id));
            return null;
        }

        @Override
        protected void done() {
            deleteCauldronButton.setEnabled(true);
            deleteCauldronWorker = null;
            try {
                get();
                CauldronTableModel model = (CauldronTableModel) cauldronsTable.getModel();
                model.removeCauldron(id);

            } catch (ExecutionException ex) {
                correctionLabelCauldron.setText(ex.getCause() + "\n");
                correctionLabelCauldron.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

    private class UpdateAllSinnersWorker extends SwingWorker<Void, Void> {
        private java.util.List<Sinner> sinners;

        public UpdateAllSinnersWorker(java.util.List<Sinner> sinners) {
            this.sinners = sinners;
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (Sinner sinner : sinners) {
                sinnerManager.updateSinner(sinner);
            }
            return null;
        }

        @Override
        protected void done() {
            updateAllSinnersWorker = null;
            try {
                get();

            } catch (ExecutionException ex) {
                correctionLabelSinner.setText(ex.getCause() + "\n");
                correctionLabelSinner.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

    private class UpdateAllCauldronsWorker extends SwingWorker<Void, Void> {
        private java.util.List<Cauldron> cauldrons;

        public UpdateAllCauldronsWorker(java.util.List<Cauldron> cauldrons) {
            this.cauldrons = cauldrons;
        }

        @Override
        protected Void doInBackground() throws Exception {
            for (Cauldron cauldron : cauldrons) {
                List<Sinner> sinners = hellManager.findSinnersInCauldron(cauldron);
                cauldronManager.updateCauldron(cauldron);

                if (cauldron.getCapacity() < sinners.size()) {
                    for (Sinner sinner : sinners) {
                        hellManager.releaseSinnerFromCauldron(sinner);
                    }
                }
            }
            return null;
        }

        @Override
        protected void done() {
            updateAllCauldronsWorker = null;
            try {
                get();
                refreshSinnerCauldronTable();

            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            } catch (ExecutionException ex) {
                correctionLabelCauldron.setText(ex.getCause() + "\n");
                correctionLabelCauldron.setForeground(Color.RED);
                log.error(ex.getMessage());
            }
        }
    }

    private class ReleaseSinnerWorker extends SwingWorker<Void, Void> {
        private Long id;

        public ReleaseSinnerWorker(Long id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground() throws Exception {
            hellManager.releaseSinnerFromCauldron(sinnerManager.findSinnerById(id));
            return null;
        }

        @Override
        protected void done() {
            releaseSinnerButton.setEnabled(true);
            releaseSinnerWorker = null;
            try {
                get();
                SinnerCauldronTableModel model = (SinnerCauldronTableModel) sinnerCauldronTable.getModel();
                model.releaseSinner(id);

            } catch (ExecutionException ex) {
                correctionLabelCauldron.setText(ex.getCause() + "\n");
                correctionLabelCauldron.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

    private class BoilSinnerWorker extends SwingWorker<Void, Void> {
        private Long sinnerId;
        private Long cauldronId;
        private Sinner sinner;
        private Cauldron cauldron;

        public BoilSinnerWorker(Long sinnerId, Long cauldronId) {
            this.sinnerId = sinnerId;
            this.cauldronId = cauldronId;
        }

        @Override
        protected Void doInBackground() throws Exception {
            sinner = sinnerManager.findSinnerById(sinnerId);
            cauldron = cauldronManager.findCauldronById(cauldronId);
            hellManager.boilSinnerInCauldron(sinner, cauldron);
            return null;
        }

        @Override
        protected void done() {
            boilSinnerButton.setEnabled(true);
            boilSinnerWorker = null;
            try {
                get();
                SinnerCauldronTableModel model = (SinnerCauldronTableModel)sinnerCauldronTable.getModel();
                Relation relation = new Relation(sinner.getId(), sinner.getFirstName() + " " + sinner.getLastName(), cauldronId);
                model.updateRelation(relation);

            } catch (ExecutionException ex) {
                correctionLabelCauldron.setText(ex.getCause() + "\n");
                correctionLabelCauldron.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

    private class RefreshRelationTableWorker extends SwingWorker<Void, Void> {
        private List<Relation> relations = new ArrayList<>();

        @Override
        protected Void doInBackground() throws Exception {
            List<Sinner> sinners = sinnerManager.findAllSinners();
            for (Sinner sinner : sinners) {
                Cauldron cauldron = hellManager.findCauldronWithSinner(sinner);
                Long cauldronId = null;
                if (cauldron != null) {
                    cauldronId = cauldron.getId();
                }
                relations.add(new Relation(sinner.getId(), sinner.getFirstName() + " " + sinner.getLastName(), cauldronId));
            }
            return null;
        }

        @Override
        protected void done() {
            relationTableWorker = null;
            try {
                get();
                ((SinnerCauldronTableModel)sinnerCauldronTable.getModel()).clearTable();
                for (Relation relation : relations) {
                    ((SinnerCauldronTableModel)sinnerCauldronTable.getModel()).addRelation(relation);
                }

            } catch (ExecutionException ex) {
                correctionLabelCauldron.setText(ex.getCause() + "\n");
                correctionLabelCauldron.setForeground(Color.RED);
                log.error(ex.getMessage());
            } catch (InterruptedException ex) {
                log.error(ex.getMessage());
                throw new RuntimeException("Operation interrupted (this should never happen)",ex);
            }
        }
    }

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
                    return;
                }
                if(textFieldLastName.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input last name");
                    correctionLabelSinner.setForeground(Color.RED);
                    return;
                }
                if(textFieldSin.getText().isEmpty()){
                    correctionLabelSinner.setText("Please input sin");
                    correctionLabelSinner.setForeground(Color.RED);
                    return;
                }
                if (!signedContractWithDevilCheckBox.isSelected() && (releaseDate.getDate() == null)){
                    correctionLabelSinner.setText("Please input signed contract or release date");
                    correctionLabelSinner.setForeground(Color.RED);
                    return;
                }

                //add Sinner
                try {
                    Sinner sinner = new Sinner();
                    sinner.setFirstName(textFieldFirstName.getText());
                    sinner.setLastName(textFieldLastName.getText());
                    sinner.setSin(textFieldSin.getText());
                    if(releaseDate.getDate() != null) {
                        sinner.setReleaseDate(releaseDate.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }
                    sinner.setSignedContractWithDevil(signedContractWithDevilCheckBox.isSelected());

                    if(createSinnerWorker != null) {
                        log.error("IllegalStateException: Operation 'Create sinner' is in progress");
                        throw new IllegalStateException("Operation is in progress");
                    }
                    addSinnerButton.setEnabled(false);

                    createSinnerWorker = new CreateSinnerWorker(sinner);
                    createSinnerWorker.execute();

                } catch(NumberFormatException ex){
                    correctionLabelCauldron.setText("Can't parse given input: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                    log.error("Can't parse given input: " + ex.getMessage());
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

                    if (createCauldronWorker != null) {
                        log.error("IllegalStateException: Operation 'Create cauldron' is in progress");
                        throw new IllegalStateException("Operation is in progress");
                    }
                    addCauldronButton.setEnabled(false);

                    createCauldronWorker = new CreateCauldronWorker(cauldron);
                    createCauldronWorker.execute();

                } catch(NumberFormatException ex){
                    correctionLabelCauldron.setText("Cant parse given input: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                    log.error("Can't parse given input: " + ex.getMessage());
                }
            }
        });
        deleteCauldronButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = cauldronsTable.getSelectedRow();
                if(selectedRow != -1) {
                    try {
                        if (deleteCauldronWorker != null) {
                            log.error("IllegalStateException: Operation 'Delete cauldron' is in progress");
                            throw new IllegalStateException("Operation is in progress");
                        }
                        deleteCauldronButton.setEnabled(false);

                        long id = (long) cauldronsTable.getValueAt(selectedRow, 0);

                        deleteCauldronWorker = new DeleteCauldronWorker(id);
                        deleteCauldronWorker.execute();

                    } catch (ServiceFailureException ex) {
                        correctionLabelCauldron.setText("Cannot delete cauldron");
                        correctionLabelCauldron.setForeground(Color.RED);
                        log.error(ex.getMessage());
                    }
                }
            }
        });
        releaseSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = sinnerCauldronTable.getSelectedRow();
                if(selectedRow != -1) {
                    try {
                        if (releaseSinnerWorker != null) {
                            log.error("IllegalStateException: Operation 'Release sinner' is in progress");
                            throw new IllegalStateException("Operation is in progress");
                        }
                        releaseSinnerButton.setEnabled(false);

                        long sinnerId = (long) sinnerCauldronTable.getValueAt(selectedRow, 0);
                        releaseSinnerWorker = new ReleaseSinnerWorker(sinnerId);
                        releaseSinnerWorker.execute();

                    } catch (ServiceFailureException ex) {
                        correctionLabelCauldron.setText("Cannot delete sinner");
                        correctionLabelCauldron.setForeground(Color.RED);
                        log.error(ex.getMessage());
                    } catch (IllegalArgumentException ex) {
                        correctionLabelCauldron.setText(ex.getMessage());
                        correctionLabelCauldron.setForeground(Color.RED);
                        log.error(ex.getMessage());
                    }
                }
            }
        });
        deleteSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = sinnersTable.getSelectedRow();
                if(selectedRow != -1) {
                    try {
                        if (deleteSinnerWorker != null) {
                            log.error("IllegalStateException: Operation 'Delete sinner' is in progress");
                            throw new IllegalStateException("Operation is in progress");
                        }
                        deleteSinnerButton.setEnabled(false);

                        long id = (long) sinnersTable.getValueAt(selectedRow, 0);
                        deleteSinnerWorker = new DeleteSinnerWorker(id);
                        deleteSinnerWorker.execute();

                    } catch (ServiceFailureException ex) {
                        correctionLabelCauldron.setText("Cannot delete sinner");
                        correctionLabelCauldron.setForeground(Color.RED);
                        log.error(ex.getMessage());
                    }
                }
            }
        });

        boilSinnerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedCauldronRow = cauldronsTable.getSelectedRow();
                if (selectedCauldronRow == -1) {
                    correctionLabelCauldron.setText("Please, select cauldron.");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }

                int selectedSinnerRow = sinnerCauldronTable.getSelectedRow();
                if (selectedSinnerRow == -1) {
                    correctionLabelCauldron.setText("Please, select sinner");
                    correctionLabelCauldron.setForeground(Color.RED);
                    return;
                }

                try {
                    long sinnerId = (long) sinnerCauldronTable.getValueAt(selectedSinnerRow, 0);
                    long cauldronId = (long) cauldronsTable.getValueAt(selectedCauldronRow, 0);

                    if (boilSinnerWorker != null) {
                        log.error("IllegalStateException: Operation 'Boil sinner in cauldron' is in progress");
                        throw new IllegalStateException("Operation is in progress");
                    }
                    boilSinnerButton.setEnabled(false);

                    boilSinnerWorker = new BoilSinnerWorker(sinnerId, cauldronId);
                    boilSinnerWorker.execute();

                } catch (ServiceFailureException | IllegalEntityException ex ) {
                    correctionLabelCauldron.setText("Cannot boil sinner in cauldron: " + ex.getMessage());
                    correctionLabelCauldron.setForeground(Color.RED);
                    log.error(ex.getMessage());
                }
            }
        });

        sinnersTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SinnerTableModel model = (SinnerTableModel) sinnersTable.getModel();
                try {
                    if (updateAllSinnersWorker != null) {
                        return;
                    }
                    updateAllSinnersWorker = new UpdateAllSinnersWorker(model.getAllSinners());
                    updateAllSinnersWorker.execute();

                } catch (IllegalArgumentException ex) {
                    correctionLabelSinner.setText(ex.getMessage());
                    correctionLabelSinner.setForeground(Color.RED);
                    log.error(ex.getMessage());
                }
                refreshSinnerCauldronTable();
            }
        });

        cauldronsTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                CauldronTableModel model = (CauldronTableModel) cauldronsTable.getModel();
                try {
                    if (updateAllCauldronsWorker != null) {
                        return;
                    }
                    updateAllCauldronsWorker = new UpdateAllCauldronsWorker(model.getAllCauldrons());
                    updateAllCauldronsWorker.execute();

                } catch (IllegalArgumentException ex) {
                    correctionLabelCauldron.setText(ex.getMessage());
                    correctionLabelSinner.setForeground(Color.RED);
                    log.error(ex.getMessage());
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
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        cauldronManager.findAllCauldrons().forEach(cauldronTableModel::addCauldron);
        cauldronsTable = new JTable(cauldronTableModel);

        SinnerTableModel sinnerTableModel = new SinnerTableModel();
        sinnerManager.findAllSinners().forEach(sinnerTableModel::addSinner);
        sinnersTable = new JTable(sinnerTableModel);
        sinnersTable.setDefaultRenderer(LocalDate.class, FormatRenderer.getDateTimeRenderer());
        sinnersTable.getColumnModel().getColumn(4).setCellEditor(new DatePickerCellEditor());

        SinnerCauldronTableModel sinCaulTableModel = new SinnerCauldronTableModel();
        refreshSinnerCauldronTable();
        sinnerCauldronTable = new JXTable(sinCaulTableModel);
    }

    private void refreshSinnerCauldronTable() {
        if (relationTableWorker != null) {
            return;
        }
        relationTableWorker = new RefreshRelationTableWorker();
        relationTableWorker.execute();
    }
}
