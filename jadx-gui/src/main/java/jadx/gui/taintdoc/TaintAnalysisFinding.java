package jadx.gui.taintdoc;

import com.sun.javafx.webkit.KeyCodeMap;
import com.sun.org.apache.xpath.internal.operations.Bool;
import jadx.gui.ui.codearea.MarkedLocation;
import jadx.gui.ui.codearea.MarkedLocationWithTarget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Date;
import java.util.stream.Collectors;


public class TaintAnalysisFinding {
    private MarkedLocationWithTarget source;
    private MarkedLocationWithTarget sink;
    private ArrayList<MarkedLocation> intermediateFlows;
    private String description;
    private boolean isNegative;

    private Map<String, Boolean> attributes;

    public TaintAnalysisFinding(){
        intermediateFlows = new ArrayList<MarkedLocation>();
        attributes = new TreeMap<>();
        isNegative = false;
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

    public final MarkedLocationWithTarget getSource(){
        return source;
    }

    public final MarkedLocationWithTarget getSink(){
        return sink;
    }

    public final ArrayList<MarkedLocation> getIntermediateFlows() { return intermediateFlows; }

    public void setSource(MarkedLocationWithTarget source){
        assert(this.source == null);
        this.source = source;
    }

    public void setSink(MarkedLocationWithTarget sink){
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
        if(source != null)
            source.setTargetName(name);
    }

    public void setSourceTargetNo(Integer no){
        if(source != null)
            source.setTargetNo(no);
    }

    public void setSinkTargetName(String name){
        if(sink != null)
            sink.setTargetName(name);
    }

    public void setSinkTargetNo(Integer no){
        if(sink != null)
            sink.setTargetNo(no);
    }

    public String getSourceTargetName(){
        if(source != null)
            return source.getTargetName();
        return "";
    }

    public Integer getSourceTargetNo(){
        if(source != null)
            return source.getTargetNo();
        return -1;
    }

    public String getSinkTargetName(){
        if(sink != null)
            return sink.getTargetName();
        return "";
    }

    public Integer getSinkTargetNo(){
        if(sink != null)
            return sink.getTargetNo();
        return -1;
    }

    public void setCustomAttributes(ArrayList<String> attributes, Map<String, String> findingAttributesJsonKeyToDisplayNameMap){
        for(String currentAttribute: this.attributes.keySet())
            if(!findingAttributesJsonKeyToDisplayNameMap.keySet().contains(currentAttribute) && !attributes.contains(currentAttribute))
                this.setAttribute(currentAttribute, false);
        for(String attr: attributes)
            this.setAttribute(attr, true);
    }

    public boolean isNegativeFlow() {
        return this.isNegative;
    }

    public void setIsNegative(boolean logNegativeFlow) {
        this.isNegative=logNegativeFlow;
    }

    public void clearUnrelatedElements() {
        if(isNegative) {
            this.attributes.clear();
            this.intermediateFlows.clear();
        }
       this.attributes=this.attributes.entrySet().stream().
               filter(x->x.getValue()).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));
    }
}
