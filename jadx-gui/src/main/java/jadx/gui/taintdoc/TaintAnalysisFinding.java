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
    private String description;

    private Map<String, Boolean> attributes;

    public TaintAnalysisFinding(){
        intermediateFlows = new ArrayList<MarkedLocation>();
        attributes = new TreeMap<>();
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

    public void setDescription(String description)
    {
        this.description = description;
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
            return "<no source>";
        return source.toString();
    }

    public void setAttribute(String key, boolean value){
        attributes.put(key, value);
    }

    public Map<String, Boolean> getAttributes(){
        return attributes;
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setSourceTargetName(String name){
        source.setTargetName(name);
    }

    public void setSourceTargetNo(Integer no){
        source.setTargetNo(no);
    }

    public void setSinkTargetName(String name){
        sink.setTargetName(name);
    }

    public void setSinkTargetNo(Integer no){
        sink.setTargetNo(no);
    }

    public String getSourceTargetName(){
        return source.getTargetName();
    }

    public Integer getSourceTargetNo(){
        return source.getTargetNo();
    }

    public String getSinkTargetName(){
        return sink.getTargetName();
    }

    public Integer getSinkTargetNo(){
        return sink.getTargetNo();
    }
}
