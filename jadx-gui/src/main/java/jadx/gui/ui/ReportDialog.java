package jadx.gui.ui;

import jadx.gui.taintdoc.TaintAnalysisFinding;
import jadx.gui.taintdoc.TaintAnalysisReport;
import jadx.gui.ui.codearea.MarkedLocation;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

public class ReportDialog extends JDialog {
    private final JComboBox<String> findingComboBox;
    private final JTextField markedSourceText;
    private final JTextField markedSourceTargetNameText;
    private final JTextField markedSourceTargetNoText;
    private final JList markedIntermediatesList;
    private final JTextField markedSinkText;
    private final JTextField markedSinkTargetNameText;
    private final JTextField markedSinkTargetNoText;
    private boolean inUpdate;
    private final DefaultListModel<String> intermediatesListModel;
    private Map<String, JCheckBox> findingAttributesCheckboxMap;
    private Map<JCheckBox, String> findingCheckboxAttributesMap;
    private final JTextArea description;
    private final JTextArea customAttributes;

    public ReportDialog(){
        findingComboBox = new JComboBox<>();
        markedSourceText = new JTextField();
        markedSourceTargetNameText = new JTextField();
        markedSourceTargetNoText = new JTextField();
        markedIntermediatesList = new JList();
        markedSinkText = new JTextField();
        markedSinkTargetNameText = new JTextField();
        markedSinkTargetNoText = new JTextField();
        intermediatesListModel = new DefaultListModel<>();
        findingAttributesCheckboxMap = new TreeMap<>();
        findingCheckboxAttributesMap = new HashMap<>();
        description = new JTextArea();
        customAttributes = new JTextArea();
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

        markedSourceTargetNameText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!markedSourceText.getText().contains(markedSourceTargetNameText.getText()))
                    markedSourceTargetNameText.setBackground(Color.yellow);
                else
                    markedSourceTargetNameText.setBackground(Color.white);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!markedSourceText.getText().contains(markedSourceTargetNameText.getText()))
                    markedSourceTargetNameText.setBackground(Color.yellow);
                else
                    markedSourceTargetNameText.setBackground(Color.white);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if(!markedSourceText.getText().contains(markedSourceTargetNameText.getText()))
                    markedSourceTargetNameText.setBackground(Color.yellow);
                else
                    markedSourceTargetNameText.setBackground(Color.white);
            }
        });

        markedSinkTargetNameText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!markedSinkText.getText().contains(markedSinkTargetNameText.getText()))
                    markedSinkTargetNameText.setBackground(Color.yellow);
                else
                    markedSinkTargetNameText.setBackground(Color.white);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!markedSinkText.getText().contains(markedSinkTargetNameText.getText()))
                    markedSinkTargetNameText.setBackground(Color.yellow);
                else
                    markedSinkTargetNameText.setBackground(Color.white);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if(!markedSinkText.getText().contains(markedSinkTargetNameText.getText()))
                    markedSinkTargetNameText.setBackground(Color.yellow);
                else
                    markedSinkTargetNameText.setBackground(Color.white);
            }
        });

        markedSourceText.setPreferredSize(new Dimension(480, 20));
        markedSourceText.setEditable(false);
        markedSourceText.setFocusable(false);
        markedSinkText.setPreferredSize(new Dimension(480, 20));
        markedSinkText.setEditable(false);
        markedSinkText.setFocusable(false);
        description.setEditable(true);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(description);
        descriptionScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        descriptionScrollPane.setPreferredSize(new Dimension(480, 80));
        customAttributes.setEditable(true);
        customAttributes.setLineWrap(false);
        JScrollPane customAttributesScrollPane = new JScrollPane(customAttributes);
        customAttributesScrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        customAttributesScrollPane.setPreferredSize(new Dimension(100, 80));

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
        centerPanel.add(new JLabel("Target Name: "), constraints);
        constraints.gridy = 3;
        centerPanel.add(new JLabel("Target No: "), constraints);
        constraints.gridy = 4;
        centerPanel.add(new JLabel("Intermediates: "), constraints);
        constraints.gridy = 5;
        centerPanel.add(new JLabel("Sink: "), constraints);
        constraints.gridy = 6;
        centerPanel.add(new JLabel("Target Name: "), constraints);
        constraints.gridy = 7;
        centerPanel.add(new JLabel("Target No: "), constraints);
        constraints.gridy = 8;
        centerPanel.add(new JLabel("Description: "), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        centerPanel.add(findingComboBox, constraints);
        constraints.gridy = 1;
        centerPanel.add(markedSourceText, constraints);
        constraints.gridy = 2;
        centerPanel.add(markedSourceTargetNameText, constraints);
        constraints.gridy = 3;
        centerPanel.add(markedSourceTargetNoText, constraints);
        constraints.gridy = 4;
        centerPanel.add(listScroller, constraints);
        constraints.gridy = 5;
        centerPanel.add(markedSinkText, constraints);
        constraints.gridy = 6;
        centerPanel.add(markedSinkTargetNameText, constraints);
        constraints.gridy = 7;
        centerPanel.add(markedSinkTargetNoText, constraints);
        constraints.gridy = 8;
        centerPanel.add(descriptionScrollPane, constraints);

        JPanel attributesPanel = new JPanel();
        attributesPanel.setLayout(new BorderLayout());
        attributesPanel.add(checkboxPane, BorderLayout.CENTER);
        attributesPanel.add(customAttributesScrollPane, BorderLayout.PAGE_END);

        constraints.gridy = 0;
        constraints.gridx = 2;
        constraints.gridheight = 6;
        centerPanel.add(attributesPanel, constraints);

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
        markedSourceTargetNameText.setText(source.getTargetName());
        markedSourceTargetNoText.setText(source.getTargetNo().toString());
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
        markedSinkTargetNameText.setText(sink.getTargetName());
        markedSinkTargetNoText.setText(sink.getTargetNo().toString());
    }

    public void updateDescription(String des)
    {
        if(des != null)
            description.setText(des);
        else
            description.setText("");
        description.setCaretPosition(0);
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

    public String getDescription()
    {
        return this.description.getText();
    }

    public String getSourceTargetName(){
        return this.markedSourceTargetNameText.getText();
    }

    public Integer getSourceTargetNo(){
        return Integer.parseInt(this.markedSourceTargetNoText.getText());
    }

    public String getSinkTargetName(){
        return this.markedSinkTargetNameText.getText();
    }

    public Integer getSinkTargetNo(){
        return Integer.parseInt(this.markedSinkTargetNoText.getText());
    }

    public void updateSourceTargetName(String name){
        this.markedSourceTargetNameText.setText(name);
    }

    public void updateSourceTargetNo(Integer no){
        this.markedSourceTargetNoText.setText(no.toString());
    }

    public void updateSinkTargetName(String name){
        this.markedSinkTargetNameText.setText(name);
    }

    public void updateSinkTargetNo(Integer no){
        this.markedSinkTargetNoText.setText(no.toString());
    }

    public ArrayList<String> getCustomAttributes(){
        ArrayList<String> result = new ArrayList<>();
        for(String attr: customAttributes.getText().split("\\n"))
            if(!attr.equals(""))
                result.add(attr);
        return result;
    }

    public void setCustomAttributes(Map<String, Boolean> attributes, Map<String, String> nonCustomAttributes){
        StringBuilder sb = new StringBuilder();
        for(String attr: attributes.keySet()) {
            if(!nonCustomAttributes.keySet().contains(attr) && attributes.get(attr)) {
                sb.append(attr);
                sb.append("\n");
            }
        }
        customAttributes.setText(sb.toString());
    }

    public void clearCustomAttributes(){
        customAttributes.setText("");
    }
}
