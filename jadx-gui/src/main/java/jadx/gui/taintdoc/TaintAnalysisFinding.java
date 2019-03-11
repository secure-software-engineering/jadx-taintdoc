package jadx.gui.taintdoc;

import jadx.gui.ui.codearea.MarkedLocation;

import java.util.ArrayList;
import java.util.List;


public class TaintAnalysisFinding {
    private MarkedLocation source;
    private MarkedLocation sink;
    private List<MarkedLocation> intermediateFlows;

    public TaintAnalysisFinding(){
        intermediateFlows = new ArrayList<MarkedLocation>();
    }

    public void removeSource(){
        if(source != null){
            source.removeHighlight();
            source = null;
        }
    }

    public void removeSink(){
        if(sink != null){
            sink.removeHighlight();
            sink = null;
        }
    }

    //(In)equality is weird. Probably I don't even have to iterate here since equals() is implemented ...?
    public boolean containsIntermediateFlow(MarkedLocation intermediate){
        for(MarkedLocation l: intermediateFlows){
            if(l.equals(intermediate))
                return true;
        }
        return false;
    }

    public final MarkedLocation getSource(){
        return source;
    }

    public final MarkedLocation getSink(){
        return sink;
    }

    public void setSource(MarkedLocation source){
        assert(this.source == null);
        this.source = source;
    }

    public void setSink(MarkedLocation sink){
        assert(this.sink == null);
        this.sink = sink;
    }

    public void addIntermediateFlow(MarkedLocation intermediate){
        assert(!intermediateFlows.contains(intermediate));
        intermediateFlows.add(intermediate);
    }

    public void removeIntermediateFlow(MarkedLocation intermediate){
        for(MarkedLocation l: intermediateFlows){
            if(l.equals(intermediate)){
                l.removeHighlight();
                intermediateFlows.remove(l);
            }
        }
    }

    public void removeAllHighlights(){
        if(source != null)
            source.removeHighlight();
        if(sink != null)
            sink.removeHighlight();
        for(MarkedLocation l: intermediateFlows)
            l.removeHighlight();
    }

    public void showAllHighlights(){
        if(source != null)
            source.showHighlight();
        if(sink != null)
            sink.showHighlight();
        for(MarkedLocation l: intermediateFlows)
            l.showHighlight();
    }
}
