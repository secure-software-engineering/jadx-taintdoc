package jadx.gui.taintdoc;

import jadx.gui.ui.codearea.MarkedLocation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;


public class TaintAnalysisFinding {
    private MarkedLocation source;
    private MarkedLocation sink;
    private ArrayList<MarkedLocation> intermediateFlows;
    private Map<String, Boolean> attributes;
    private final String creationDate;

    public TaintAnalysisFinding(){
        intermediateFlows = new ArrayList<MarkedLocation>();
        attributes = new TreeMap<>();
        creationDate = new SimpleDateFormat(" (HH:mm:ss)").format(new Date());
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

    public final ArrayList<MarkedLocation> getIntermediateFlows() { return intermediateFlows; }

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
        ArrayList<MarkedLocation> toRemove = new ArrayList<>();
        for(MarkedLocation l: intermediateFlows){
            if(l.equals(intermediate)){
                l.removeHighlight();
                toRemove.add(l);
            }
        }
        intermediateFlows.removeAll(toRemove);
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

    @Override
    public String toString(){
        if(source == null)
            return "<no source>" + creationDate;
        return source.toString() + creationDate;
    }

    public void setAttribute(String key, boolean value){
        attributes.put(key, value);
    }

    public Map<String, Boolean> getAttributes(){
        return attributes;
    }
}
