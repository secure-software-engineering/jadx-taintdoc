package jadx.gui.taintdoc;

import com.google.gson.GsonBuilder;
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

/**
 * Singleton for the manual analysis report. In essence this holds all attributes that are serialized to JSON
 * and furthermore provides the actions that are triggered by the key strokes.
 */
public class TaintAnalysisReport {
    private static transient TaintAnalysisReport instance;
    private String fileName;
    private String day;
    private ArrayList<TaintAnalysisFinding> findings;
    private transient TaintAnalysisFinding currentFinding;
    private transient int currentFindingIndex;

    private transient final Color sourceColor;
    private transient final Color sinkColor;
    private transient final Color intermediateColor;

    private TaintAnalysisReport(){
        findings = new ArrayList<TaintAnalysisFinding>();
        sourceColor = new Color(0x91, 0xad, 0x4c);
        sinkColor = new Color(0x1b, 0x89, 0x7f);
        intermediateColor = new Color(0xe4, 0xd9, 0x73);
        currentFindingIndex = -1;
        day = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static synchronized TaintAnalysisReport getInstance(){
        if(instance == null)
            instance = new TaintAnalysisReport();
        return instance;
    }

    /**
     * Marks a line as source, if it has not been marked as source yet. Unmarks a line that has been marked as source
     * and is toggled to be marked again. Unmarks the old source line if a different line is toggled to be marked as
     * source.
     */
    public void markSource(CodeArea codeArea){
        assert(currentFindingIndex >= 0 && currentFinding != null);
        MarkedLocation markedLocation = new MarkedLocation(codeArea, sourceColor);
        if(currentFinding.getSource() != null && currentFinding.getSource().equals(markedLocation)) {
            currentFinding.getSource().removeHighlight();
            currentFinding.removeSource();
        } else {
            currentFinding.removeSource();
            currentFinding.setSource(markedLocation);
            markedLocation.showHighlight();
        }
    }

    /**
     * Marks a line as sink, if it has not been marked as sink yet. Unmarks a line that has been marked as sink
     * and is toggled to be marked again. Unmarks the old sink line if a different line is toggled to be marked as sink.
     */
    public void markSink(CodeArea codeArea){
        assert(currentFindingIndex >= 0 && currentFinding != null);
        MarkedLocation markedLocation = new MarkedLocation(codeArea, sinkColor);
        if(currentFinding.getSink() != null && currentFinding.getSink().equals(markedLocation)) {
            currentFinding.getSink().removeHighlight();
            currentFinding.removeSink();
        } else {
            currentFinding.removeSink();
            currentFinding.setSink(markedLocation);
            markedLocation.showHighlight();
        }
    }

    /**
     * Marks a line as intermediate, if it has not been marked as intermediate yet. Unmarks a line that has been marked
     * as intermediate and is toggled to be marked again.
     */
    public void markIntermediate(CodeArea codeArea){
        assert(currentFindingIndex >= 0 && currentFinding != null);
        MarkedLocation markedLocation = new MarkedLocation(codeArea, intermediateColor);
        if(currentFinding.containsIntermediateFlow(markedLocation)){
            currentFinding.removeIntermediateFlow(markedLocation);
        } else{
            currentFinding.addIntermediateFlow(markedLocation);
            markedLocation.showHighlight();
        }
    }

    public void selectCurrentFinding(int index){
        assert(index < findings.size());
        currentFinding.removeAllHighlights();
        currentFindingIndex = index;
        currentFinding = findings.get(currentFindingIndex);
        currentFinding.showAllHighlights();
    }

    public void nextFinding(){
        selectCurrentFinding((currentFindingIndex + 1) % findings.size());
    }

    public void previousFinding(){
        selectCurrentFinding((currentFindingIndex - 1 + findings.size()) % findings.size());
    }

    public void createAndSwitchToNewFinding(){
        TaintAnalysisFinding finding = new TaintAnalysisFinding();
        currentFindingIndex = findings.size();
        findings.add(findings.size(), finding);
        if(currentFinding != null)
            currentFinding.removeAllHighlights();
        currentFinding = finding;
    }

    public void setFileName(String filename){
        fileName = filename;
    }

    public void serializeToJson(){
        String json = new GsonBuilder().setPrettyPrinting().create().toJson(this);
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
}
