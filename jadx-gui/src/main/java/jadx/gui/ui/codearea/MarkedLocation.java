package jadx.gui.ui.codearea;

import jadx.gui.treemodel.JNode;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import java.awt.*;

public class MarkedLocation {
    private transient CodeArea codeArea;
    private transient final Color markColor;
    private String statement;
    private String methodName;
    private String className;
    private Integer lineNo;
    private transient Integer creationCaretLine;
    private transient Object highlightTag;

    public MarkedLocation(CodeArea codeArea, Color markColor){
        this.codeArea = codeArea;
        this.markColor = markColor;

        int caretLine = codeArea.getCaretLineNumber() + 1;
        lineNo = codeArea.getSourceLine(caretLine);
        if(lineNo == null)
            lineNo = -1;
        creationCaretLine = codeArea.getCaretLineNumber();
        Integer methodDeclarationElementIndex = codeArea.getDeclarationLineOfMethodContainingStmtSourceLine(
                codeArea.getCaretLineNumber()) - 1;
        Element statementElement = codeArea.getDocument().getDefaultRootElement().getElement(creationCaretLine);
        Element methodDeclarationElement = codeArea.getDocument()
                                            .getDefaultRootElement()
                                            .getElement(methodDeclarationElementIndex);

        className = codeArea.getClassNameOfMethodContainingSourceLine(codeArea.getCaretLineNumber() - 1);
        try{
            methodName = codeArea.getText(
                            methodDeclarationElement.getStartOffset(),
                        methodDeclarationElement.getEndOffset() - methodDeclarationElement.getStartOffset()
                        ).trim();
        } catch (BadLocationException ex) {}

        try{
            statement = codeArea.getText(statementElement.getStartOffset(),
                                    statementElement.getEndOffset() - statementElement.getStartOffset()
                                        ).trim();
        } catch (BadLocationException ex) {}

        //cut off "throws ..." if it is there
        int i;
        if((i = methodName.indexOf(" throws ")) >= 0)
            methodName = methodName.substring(0, i);
        //cut off "{" if it is there
        if((i = methodName.indexOf(" {")) >= 0)
            methodName = methodName.substring(0, i);
    }

    /*
    Java does not have destructors and I am crying since I have to write it like this and take care of calling this
    whenever a MarkedLocation is discarded. Booh!
    */
    public void removeHighlight(){
        if(highlightTag != null) {
            codeArea.getHighlighter().removeHighlight(highlightTag);
            highlightTag = null;
        }
    }

    public void showHighlight(){
        if(highlightTag == null){
            Element statementElement = codeArea.getDocument().getDefaultRootElement().getElement(creationCaretLine);
            Highlighter highlighter = codeArea.getHighlighter();
            try {
                //highlighter.addHighlight(codeArea.getSelectionStart(),
                //                         codeArea.getSelectionEnd(),
                //                         new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN));
                highlightTag = highlighter.addHighlight(statementElement.getStartOffset(),
                                                        statementElement.getEndOffset(),
                                                        new DefaultHighlighter.DefaultHighlightPainter(markColor));
            } catch (BadLocationException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof MarkedLocation
                && this.statement.equals(((MarkedLocation) other).statement)
                && this.methodName.equals(((MarkedLocation) other).methodName)
                && this.className.equals(((MarkedLocation) other).className)
                && this.lineNo.equals(((MarkedLocation) other).lineNo);
    }

    public Integer getLineNo(){
        return lineNo;
    }

    @Override
    public String toString(){
        if(statement.length() > 80)
            return statement.substring(0, 77) + "...";
        return statement;
    }

    public void navigateTo(){
        codeArea.codeJump(creationCaretLine + 1);
    }

    public void updateCodeAreaForNode(JNode node, CodeArea codeArea){
        if(this.codeArea.hasNode(node))
            this.codeArea = codeArea;
    }

}
