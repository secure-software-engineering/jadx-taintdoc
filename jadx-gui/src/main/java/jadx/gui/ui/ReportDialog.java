package jadx.gui.ui;

import jadx.gui.taintdoc.TaintAnalysisFinding;
import jadx.gui.taintdoc.TaintAnalysisReport;
import jadx.gui.ui.codearea.MarkedLocation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ReportDialog extends JDialog {
    private final JComboBox<String> findingComboBox;
    private final JTextField markedSourceText;
    private final JList markedIntermediatesList;
    private final JTextField markedSinkText;
    private boolean inUpdate;
    private final DefaultListModel<String> intermediatesListModel;
    private Map<String, JCheckBox> findingAttributesCheckboxMap;
    private Map<JCheckBox, String> findingCheckboxAttributesMap;

    public ReportDialog(){
        findingComboBox = new JComboBox<>();
        markedSourceText = new JTextField();
        markedIntermediatesList = new JList();
        markedSinkText = new JTextField();
        intermediatesListModel = new DefaultListModel<>();
        findingAttributesCheckboxMap = new TreeMap<>();
        findingCheckboxAttributesMap = new HashMap<>();
        initCheckboxMap();
        initUI();
    }

    public void initCheckboxMap(){
        Map<String, String> findingAttributesJsonKeyToDisplayNameMap = TaintAnalysisReport.getFindingAttributesJsonKeyToDisplayNameMap();
        for(String k: findingAttributesJsonKeyToDisplayNameMap.keySet()){
            JCheckBox cb = new JCheckBox(findingAttributesJsonKeyToDisplayNameMap.get(k));
            cb.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            cb.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    JCheckBox source = (JCheckBox)(e.getSource());
                    TaintAnalysisReport.getInstance().setAttributeOfCurrent(findingCheckboxAttributesMap.get(source), source.isSelected());
                }
            });
            findingAttributesCheckboxMap.put(k, cb);
            findingCheckboxAttributesMap.put(cb, k);
        }
    }

    public void initUI(){
        findingComboBox.setSelectedItem(0);
        findingComboBox.addItemListener(e -> {
            if(!inUpdate) {
                int i = findingComboBox.getSelectedIndex();
                TaintAnalysisReport.getInstance().selectCurrentFinding(i, false);
            }
        });

        JPanel checkboxPane = new JPanel();
        checkboxPane.setLayout(new BoxLayout(checkboxPane, BoxLayout.Y_AXIS));

        for(String k: findingAttributesCheckboxMap.keySet())
            checkboxPane.add(findingAttributesCheckboxMap.get(k));

        markedIntermediatesList.setModel(intermediatesListModel);
        markedIntermediatesList.setVisibleRowCount(-1);
        markedIntermediatesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        markedSourceText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
                    TaintAnalysisReport.getInstance().navigateToCurrentSource();
                }
            }
        });
        markedIntermediatesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
                    TaintAnalysisReport.getInstance().navigateToCurrentIntermediateByIndex(markedIntermediatesList.locationToIndex(e.getPoint()));
                }
            }
        });
        markedIntermediatesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_W)
                    TaintAnalysisReport.getInstance().moveUpIntermediateOfCurrent(markedIntermediatesList.getSelectedIndex());
                if(e.getKeyCode() == KeyEvent.VK_S)
                    TaintAnalysisReport.getInstance().moveDownIntermediateOfCurrent(markedIntermediatesList.getSelectedIndex());
            }
        });
        markedSinkText.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
                    TaintAnalysisReport.getInstance().navigateToCurrentSink();
                }
            }
        });

        markedSourceText.setPreferredSize(new Dimension(480, 20));
        markedSourceText.setEditable(false);
        markedSourceText.setFocusable(false);
        markedSinkText.setPreferredSize(new Dimension(480, 20));
        markedSinkText.setEditable(false);
        markedSinkText.setFocusable(false);

        JScrollPane listScroller = new JScrollPane(markedIntermediatesList);
        listScroller.setPreferredSize(new Dimension(480, 420));
        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5,5,5,5);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        centerPanel.add(new JLabel("Findings: "), constraints);
        constraints.gridy = 1;
        centerPanel.add(new JLabel("Source: "), constraints);
        constraints.gridy = 2;
        centerPanel.add(new JLabel("Intermediates: "), constraints);
        constraints.gridy = 3;
        centerPanel.add(new JLabel("Sink: "), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        centerPanel.add(findingComboBox, constraints);
        constraints.gridy = 1;
        centerPanel.add(markedSourceText, constraints);
        constraints.gridy = 2;
        centerPanel.add(listScroller, constraints);
        constraints.gridy = 3;
        centerPanel.add(markedSinkText, constraints);

        constraints.gridy = 0;
        constraints.gridx = 2;
        constraints.gridheight = 4;
        centerPanel.add(checkboxPane, constraints);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        setTitle("Inspection Documentation");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        pack();
        setResizable(false);
    }

    public void updateFindings(ArrayList<TaintAnalysisFinding> findings){
        inUpdate = true;
        int selectedIndex = findingComboBox.getSelectedIndex();
        findingComboBox.removeAllItems();
        for(TaintAnalysisFinding f: findings){
            findingComboBox.addItem(f.toString());
        }
        if(selectedIndex >= findingComboBox.getItemCount())
            selectedIndex = findingComboBox.getItemCount() - 1;
        findingComboBox.setSelectedIndex(selectedIndex);
        inUpdate = false;
    }

    public void updateMarkedSource(MarkedLocation source){
        if(source != null)
            markedSourceText.setText(source.toString());
        else
            markedSourceText.setText("");
        markedSourceText.setCaretPosition(0);
    }

    public void updateMarkedIntermediates(ArrayList<MarkedLocation> intermediates){
        intermediatesListModel.removeAllElements();
        if(intermediates == null)
            return;
        for(MarkedLocation l: intermediates){
            if(l != null)
                intermediatesListModel.addElement(l.toString());
        }
    }

    public void updateMarkedSink(MarkedLocation sink){
        if(sink != null)
            markedSinkText.setText(sink.toString());
        else
            markedSinkText.setText("");
        markedSinkText.setCaretPosition(0);
    }

    public void selectCurrentFinding(int index){
        findingComboBox.setSelectedIndex(index);
    }

    public void focusIntermediate(int index){
        markedIntermediatesList.setSelectedIndex(index);
    }

    public void updateAttributes(Map<String, Boolean> attributes){
        for(String key: findingAttributesCheckboxMap.keySet()){
            if(attributes != null && attributes.containsKey(key) && attributes.get(key))
                findingAttributesCheckboxMap.get(key).setSelected(true);
            else
                findingAttributesCheckboxMap.get(key).setSelected(false);
        }
    }
}
