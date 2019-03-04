package jadx.gui.taintdoc;

import jadx.gui.ui.codearea.MarkedLocation;

import java.util.Set;

public class TaintAnalysisFinding {
    private MarkedLocation source;
    private MarkedLocation sink;
    private Set<MarkedLocation> intermediateFlows;

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
    public void removeIntermediate(MarkedLocation intermediate){
        for(MarkedLocation l: intermediateFlows){
            if(l.equals(intermediate)) {
                intermediateFlows.remove(l);
                break;
            }
        }
    }

    public boolean containsIntermediate(MarkedLocation intermediate){
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
        if(intermediateFlows.contains(intermediate)){
            intermediate.removeHighlight();
            intermediateFlows.remove(intermediate);
        }
    }
}
