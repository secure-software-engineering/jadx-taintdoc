package jadx.gui.ui.codearea;

import java.awt.*;

public class MarkedLocationWithTarget extends MarkedLocation {
    private String targetName;
    private Integer targetNo;

    public MarkedLocationWithTarget(CodeArea codeArea, Color markColor){
        super(codeArea, markColor);
        this.targetName = "";
        this.targetNo = 1;
    }

    public String getTargetName() {
        return targetName;
    }

    public Integer getTargetNo() {
        return targetNo;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setTargetNo(Integer targetNo) {
        this.targetNo = targetNo;
    }
}
