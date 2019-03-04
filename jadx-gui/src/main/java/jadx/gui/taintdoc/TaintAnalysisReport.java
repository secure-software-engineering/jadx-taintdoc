package jadx.gui.taintdoc;

import jadx.gui.ui.codearea.CodeArea;
import jadx.gui.ui.codearea.MarkedLocation;

import java.util.List;

public class TaintAnalysisReport {
    private static List<TaintAnalysisFinding> findings;
    private static TaintAnalysisFinding currentFinding;

    public static void markSource(CodeArea codeArea){
        MarkedLocation markedLocation = new MarkedLocation(codeArea);
        if(currentFinding.getSource().equals(markedLocation)) {
            currentFinding.getSource().removeHighlight();
            currentFinding.removeSource();
        } else {
            currentFinding.removeSource();
            currentFinding.setSource(markedLocation);
            markedLocation.showHighlight();
        }
    }

    public static void markSink(CodeArea codeArea){

    }

    public static void markIntermediate(CodeArea codeArea){

    }
}
