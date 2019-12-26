package jadx.gui.taintdoc;

import com.google.gson.GsonBuilder;
import jadx.gui.treemodel.JNode;
import jadx.gui.ui.ReportDialog;
import jadx.gui.ui.codearea.CodeArea;
import jadx.gui.ui.codearea.MarkedLocation;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton for the manual analysis report. In essence this holds all attributes that are serialized to JSON
 * and furthermore provides the actions that are triggered by the key strokes.
 */
public class TaintAnalysisReport {
    private static transient TaintAnalysisReport instance;
    private String fileName;
    private ArrayList<TaintAnalysisFinding> findings;
    private transient TaintAnalysisFinding currentFinding;
    private transient int currentFindingIndex;
    private transient ReportDialog reportDialog;

    private transient final Color sourceColor;
    private transient final Color sinkColor;
    private transient final Color intermediateColor;

    private transient static final Map<String, String> findingAttributesJsonKeyToDisplayNameMap = Collections.unmodifiableMap(createFindingAttributesJsonKeyToDisplayNameMap());

    private TaintAnalysisReport(){
        findings = new ArrayList<TaintAnalysisFinding>();
        sourceColor = new Color(0x91, 0xad, 0x4c);
        sinkColor = new Color(0x1b, 0x89, 0x7f);
        intermediateColor = new Color(0xe4, 0xd9, 0x73);
        currentFindingIndex = -1;
        reportDialog = new ReportDialog();
        reportDialog.setVisible(true);
    }

    public static synchronized TaintAnalysisReport getInstance(){
        if(instance == null)
            instance = new TaintAnalysisReport();
        return instance;
    }

    // I would have loved to initialize it directly....
    private static HashMap<String, String> createFindingAttributesJsonKeyToDisplayNameMap(){
        HashMap<String, String> result = new HashMap<>();
        result.put("intraProcedural", "Intra-procedural");
        result.put("interProcedural", "Inter-procedural");
        result.put("staticField", "Static Field");
        result.put("array", "Array");
        result.put("reflection", "Reflection");
        result.put("exception", "Exception");
        result.put("threading", "Threading");
        result.put("callbacks", "Callbacks");
        result.put("interComponentCommunication", "Inter-Component Communication");
        result.put("interAppCommunication", "Inter-App Communication");
        result.put("collections", "Collections");
        result.put("partialFlow", "Partial Flow");
        return result;
    }

    public static Map<String, String> getFindingAttributesJsonKeyToDisplayNameMap(){
        return findingAttributesJsonKeyToDisplayNameMap;
    }

    /**
     * Marks a line as source, if it has not been marked as source yet. Unmarks a line that has been marked as source
     * and is toggled to be marked again. Unmarks the old source line if a different line is toggled to be marked as
     * source.
     */
    public void markSource(CodeArea codeArea){
        assert(currentFindingIndex >= 0 && currentFinding != null);
        MarkedLocation markedLocation = new MarkedLocation(codeArea, sourceColor);
        //if location is marked otherwise, remove the old mark
        if(currentFinding.containsIntermediateFlow(markedLocation))
            markIntermediate(codeArea);
        if(currentFinding.getSink() != null && currentFinding.getSink().equals(markedLocation))
            markSink(codeArea);
        if(currentFinding.getSource() != null && currentFinding.getSource().equals(markedLocation)) {
            currentFinding.getSource().removeHighlight();
            currentFinding.removeSource();
        } else {
            currentFinding.removeSource();
            currentFinding.setSource(markedLocation);
            markedLocation.showHighlight();
        }
        reportDialog.updateFindings(findings);
        reportDialog.updateMarkedSource(currentFinding.getSource());
    }

    /**
     * Marks a line as sink, if it has not been marked as sink yet. Unmarks a line that has been marked as sink
     * and is toggled to be marked again. Unmarks the old sink line if a different line is toggled to be marked as sink.
     */
    public void markSink(CodeArea codeArea){
        assert(currentFindingIndex >= 0 && currentFinding != null);
        MarkedLocation markedLocation = new MarkedLocation(codeArea, sinkColor);
        //if location is marked otherwise, remove the old mark
        if(currentFinding.containsIntermediateFlow(markedLocation))
            markIntermediate(codeArea);
        if(currentFinding.getSource() != null && currentFinding.getSource().equals(markedLocation))
            markSource(codeArea);
        if(currentFinding.getSink() != null && currentFinding.getSink().equals(markedLocation)) {
            currentFinding.getSink().removeHighlight();
            currentFinding.removeSink();
        } else {
            currentFinding.removeSink();
            currentFinding.setSink(markedLocation);
            markedLocation.showHighlight();
        }
        reportDialog.updateMarkedSink(currentFinding.getSink());
    }

    /**
     * Marks a line as intermediate, if it has not been marked as intermediate yet. Unmarks a line that has been marked
     * as intermediate and is toggled to be marked again.
     */
    public void markIntermediate(CodeArea codeArea){
        assert(currentFindingIndex >= 0 && currentFinding != null);
        MarkedLocation markedLocation = new MarkedLocation(codeArea, intermediateColor);
        //if location is marked otherwise, remove the old mark
        if(currentFinding.getSource() != null && currentFinding.getSource().equals(markedLocation))
            markSource(codeArea);
        if(currentFinding.getSink() != null && currentFinding.getSink().equals(markedLocation))
            markSink(codeArea);
        if(currentFinding.containsIntermediateFlow(markedLocation)){
            currentFinding.removeIntermediateFlow(markedLocation);
        } else{
            currentFinding.addIntermediateFlow(markedLocation);
            markedLocation.showHighlight();
        }
        reportDialog.updateMarkedIntermediates(currentFinding.getIntermediateFlows());
    }

    public void selectCurrentFinding(int index, boolean updateReportDialog){
        if(index >= 0) {
            assert (index < findings.size());
            currentFinding.removeAllHighlights();
            currentFindingIndex = index;
            currentFinding = findings.get(currentFindingIndex);
            currentFinding.showAllHighlights();
            if (updateReportDialog)
                reportDialog.selectCurrentFinding(index);
            reportDialog.updateMarkedSource(currentFinding.getSource());
            reportDialog.updateMarkedIntermediates(currentFinding.getIntermediateFlows());
            reportDialog.updateMarkedSink(currentFinding.getSink());
            reportDialog.updateAttributes(currentFinding.getAttributes());
            reportDialog.updateDescription(currentFinding.getDescription());
        }
        else{
            reportDialog.updateFindings(findings);
            reportDialog.updateMarkedSource(null);
            reportDialog.updateMarkedIntermediates(null);
            reportDialog.updateMarkedSink(null);
            reportDialog.updateAttributes(null);
            reportDialog.updateDescription(null);
        }
    }

    public void nextFinding(boolean updateReportDialog){
        selectCurrentFinding((currentFindingIndex + 1) % findings.size(), updateReportDialog);
    }

    public void previousFinding(boolean updateReportDialog){
        selectCurrentFinding((currentFindingIndex - 1 + findings.size()) % findings.size(), updateReportDialog);
    }

    public void createAndSwitchToNewFinding(){
        String description= this.reportDialog.getDescription();
        if(currentFinding!=null)
                currentFinding.setDescription(description);
        TaintAnalysisFinding finding = new TaintAnalysisFinding();
        currentFindingIndex = findings.size();
        findings.add(findings.size(), finding);
        if(currentFinding != null)
            currentFinding.removeAllHighlights();
        currentFinding = finding;
        reportDialog.updateFindings(findings);
        selectCurrentFinding(currentFindingIndex, true);
    }

    public void removeCurrentFinding(){
        if(currentFinding == null)
            return;
        currentFinding.removeAllHighlights();
        findings.remove(currentFinding);
        if(currentFindingIndex == findings.size())
            currentFindingIndex -= 1;
        if(currentFindingIndex >= 0)
            currentFinding = findings.get(currentFindingIndex);
        else
            currentFinding = null;
        reportDialog.updateFindings(findings);
        selectCurrentFinding(currentFindingIndex, true);
    }

    public void setFileName(String filename){
        fileName = filename;
    }

    public void serializeToJson(){
        String description= this.reportDialog.getDescription();
        if(currentFinding!=null)
            currentFinding.setDescription(description);
        String json = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(this);
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if(f.isDirectory())
                    return true;
                return f.getName().toLowerCase().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON reports";
            }
        });
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Save Analysis Report ...");
        if(fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                PrintWriter out = new PrintWriter(fileChooser.getSelectedFile());
                out.print(json);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void navigateToCurrentSource(){
        currentFinding.getSource().navigateTo();
    }

    public void navigateToCurrentIntermediateByIndex(int index){
        if(index < currentFinding.getIntermediateFlows().size())
            currentFinding.getIntermediateFlows().get(index).navigateTo();
    }

    public void navigateToCurrentSink(){
        currentFinding.getSink().navigateTo();
    }

    public void updateCodeAreasForNode(JNode node, CodeArea codeArea){
        for(TaintAnalysisFinding f: findings){
            if(f.getSource() != null) {
                f.getSource().removeHighlight();
                f.getSource().updateCodeAreaForNode(node, codeArea);
            }
            for(MarkedLocation intermediate: f.getIntermediateFlows()){
                intermediate.removeHighlight();
                intermediate.updateCodeAreaForNode(node, codeArea);
            }
            if(f.getSink() != null) {
                f.getSink().removeHighlight();
                f.getSink().updateCodeAreaForNode(node, codeArea);
            }
        }
        if(currentFinding != null){
            if(currentFinding.getSource() != null)
                currentFinding.getSource().showHighlight();
            for(MarkedLocation intermediate: currentFinding.getIntermediateFlows())
                intermediate.showHighlight();
            if(currentFinding.getSink() != null)
                currentFinding.getSink().showHighlight();
        }
    }

    public void moveUpIntermediateOfCurrent(int index){
        if(index > 0) {
            Collections.swap(currentFinding.getIntermediateFlows(), index, index - 1);
            reportDialog.updateMarkedIntermediates(currentFinding.getIntermediateFlows());
            reportDialog.focusIntermediate(index - 1);
        }
    }

    public void moveDownIntermediateOfCurrent(int index){
        if(index < currentFinding.getIntermediateFlows().size() - 1) {
            Collections.swap(currentFinding.getIntermediateFlows(), index, index + 1);
            reportDialog.updateMarkedIntermediates(currentFinding.getIntermediateFlows());
            reportDialog.focusIntermediate(index + 1);
        }
    }

    public void setAttributeOfCurrent(String key, boolean value){
        if(currentFinding != null)
            currentFinding.setAttribute(key, value);
    }

}
