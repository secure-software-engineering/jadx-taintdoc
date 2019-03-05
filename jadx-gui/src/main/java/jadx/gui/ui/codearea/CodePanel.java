package jadx.gui.ui.codearea;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import jadx.gui.taintdoc.TaintAnalysisReport;
import jadx.gui.treemodel.JClass;
import jadx.gui.treemodel.JNode;
import jadx.gui.treemodel.JResource;
import jadx.gui.ui.ContentPanel;
import jadx.gui.ui.TabbedPane;
import jadx.gui.utils.Utils;

public final class CodePanel extends ContentPanel {
	private static final long serialVersionUID = 5310536092010045565L;

	private final SearchBar searchBar;
	private final CodeArea codeArea;
	private final JScrollPane scrollPane;

	public CodePanel(TabbedPane panel, JNode jnode) {
		super(panel, jnode);

		codeArea = new CodeArea(this);
		searchBar = new SearchBar(codeArea);
		scrollPane = new JScrollPane(codeArea);
		initLineNumbers();

		setLayout(new BorderLayout());
		add(searchBar, BorderLayout.NORTH);
		add(scrollPane);

		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK);
		Utils.addKeyBinding(codeArea, key, "SearchAction", new SearchAction());

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0);
		Utils.addKeyBinding(codeArea, key, "MarkSourceAction", new MarkSourceAction());

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0);
		Utils.addKeyBinding(codeArea, key, "MarkIntermediateAction", new MarkIntermediateAction());

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0);
		Utils.addKeyBinding(codeArea, key, "MarkSinkAction", new MarkSinkAction());

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0);
		Utils.addKeyBinding(codeArea, key, "NewFindingAction", new NewFindingAction());

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0);
		Utils.addKeyBinding(codeArea, key, "PreviousFindingAction", new PreviousFindingAction());

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0);
		Utils.addKeyBinding(codeArea, key, "NextFindingAction", new NextFindingAction());

		key = KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0);
		Utils.addKeyBinding(codeArea, key, "SaveAction", new SaveAction());
	}

	private void initLineNumbers() {
		// TODO: fix slow line rendering on big files
		if (codeArea.getDocument().getLength() <= 100_000) {
			LineNumbers numbers = new LineNumbers(codeArea);
			numbers.setUseSourceLines(isUseSourceLines());
			scrollPane.setRowHeaderView(numbers);
		}
	}

	private boolean isUseSourceLines() {
		if (node instanceof JClass) {
			return true;
		}
		if (node instanceof JResource) {
			JResource resNode = (JResource) node;
			return !resNode.getLineMapping().isEmpty();
		}
		return false;
	}

	private class SearchAction extends AbstractAction {
		private static final long serialVersionUID = 8650568214755387093L;

		@Override
		public void actionPerformed(ActionEvent e) {
			searchBar.toggle();
		}
	}

	private class MarkSourceAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			TaintAnalysisReport.getInstance().markSource(codeArea);
		}
	}

	private class MarkSinkAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			TaintAnalysisReport.getInstance().markSink(codeArea);
		}
	}

	private class MarkIntermediateAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			TaintAnalysisReport.getInstance().markIntermediate(codeArea);
		}
	}

	private class NewFindingAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			TaintAnalysisReport.getInstance().createAndSwitchToNewFinding();
		}
	}

	private class PreviousFindingAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			TaintAnalysisReport.getInstance().previousFinding();
		}
	}

	private class NextFindingAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			TaintAnalysisReport.getInstance().nextFinding();
		}
	}

	private class SaveAction extends AbstractAction{
		@Override
		public void actionPerformed(ActionEvent e){
			TaintAnalysisReport.getInstance().serializeToJson();
		}
	}

	@Override
	public void loadSettings() {
		codeArea.loadSettings();
		initLineNumbers();
		updateUI();
	}

	@Override
	public TabbedPane getTabbedPane() {
		return tabbedPane;
	}

	@Override
	public JNode getNode() {
		return node;
	}

	SearchBar getSearchBar() {
		return searchBar;
	}

	public CodeArea getCodeArea() {
		return codeArea;
	}

	JScrollPane getScrollPane() {
		return scrollPane;
	}
}
