import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class CorpusToolExecutor extends Frame {
	private Label l1, l2;
	private Button convertButton;
	private TextArea t1, t2;
	private GridBagLayout gbLayout;
	private GridBagConstraints gbConstraints;
	private MenuItem openItem, exitItem, aboutItem;
	private OpenFiles o;
//	private JFileChooser chooser;
	private About a;
	
	public CorpusToolExecutor() {
		super("Corpus Tool Executor");
		l1 = new Label("File opened:");
		l2 = new Label("File saves in:");
		
		convertButton = new Button("Convert!");
		
		t1 = new TextArea();
		t1.setEditable(false);
		t1.setFocusable(false);
		
		t2 = new TextArea();
		t2.setEditable(false);
		t2.setFocusable(false);
		
		gbLayout = new GridBagLayout();
		setLayout(gbLayout);
		
		gbConstraints = new GridBagConstraints();
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		addComponent(l1, gbLayout, gbConstraints, 0, 0, 2, 1);
		
		gbConstraints.weightx = 2;
		addComponent(t1, gbLayout, gbConstraints, 1, 0, 1, 1);
		
		gbConstraints.fill = GridBagConstraints.NONE;
		gbConstraints.weightx = 1;
		addComponent(convertButton, gbLayout, gbConstraints, 1, 1, 1, 1);
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.weightx = 0;
		addComponent(l2, gbLayout, gbConstraints, 2, 0, 2, 1);
		
		addComponent(t2, gbLayout, gbConstraints, 3, 0, 2, 1);
		
		MenuBar bar = new MenuBar();
		Menu fileMenu = new Menu("File");
		Menu helpMenu = new Menu("Help");
		
		openItem = new MenuItem("Open File");
		exitItem = new MenuItem("Exit");
		aboutItem = new MenuItem("About");
		
		fileMenu.add(openItem);
		fileMenu.add(exitItem);
		helpMenu.add(aboutItem);
		
		bar.setHelpMenu(helpMenu);
		bar.add(fileMenu);
		bar.add(helpMenu);
		
		setMenuBar(bar);
		setSize(600, 450);
		setVisible(true);
	}
	
	private void addComponent(Component c, GridBagLayout g,
			GridBagConstraints gc, int row, int column, int width, int height) {
		// set gridx and gridy
		gc.gridx = column;
		gc.gridy = row;

		// set gridwidth and gridheight
		gc.gridwidth = width;
		gc.gridheight = height;

		g.setConstraints(c, gc); // set constraints
		add(c); // add component to applet
	}
	
	public boolean action(Event e, Object o) {
		if (e.target instanceof MenuItem) {
			if (e.arg.equals(openItem.getLabel())) {
				this.o = new OpenFiles(this);
//				chooser = new JFileChooser();
//				chooser.setFileFilter(new FileNameExtensionFilter("XML", "xml"));
//				chooser.showOpenDialog(this);
			} else if (e.arg.equals(aboutItem.getLabel())) {
				a = new About(this);
				setItemState(false);
			} else {
				removeFrame();
			}
		} else if (e.target == convertButton) {
			String[] paths = t1.getText().split("\n");
			for (String path : paths) {
				CorpusTool ct = new CorpusTool(path);
				try {
					ct.setTextbookBuffer(CorpusTool.readTextInBuffer(ct.getTextbookPath()));
				} catch (IOException e1) {
					System.out.println("READING ERROR");
					System.exit(0);
				}

				ct.constructMatcher();
				StringBuffer resultBuffer = ct.handleCorpus();
				
				String outPath = path.replace(".xml", "-1.xml");
				try {
					CorpusTool.writeBufferToText(resultBuffer, outPath);
				} catch (IOException e1) {
					System.out.println("WRITING ERROR");
					System.exit(0);
				}
				
				t2.append(outPath + "\n");
			}
		}
		
		return true;
	}
	
	public boolean handleEvent(Event e) {
		if (e.id == Event.WINDOW_DESTROY) {
			removeFrame();
			return true;
		}
		
		return super.handleEvent(e);
	}
	
	public void addText(String s) {
		t1.append(s + "\n");
	}
	
	// setItemState is programmer-defined
	public void setItemState(boolean state) {
		if (state == true)
			aboutItem.setEnabled(true);
		else
			aboutItem.setEnabled(false);
	}
	
	// removeFrame is programmer-defined
	public void removeFrame() {
		setVisible(false);
		dispose();
		System.exit(0);
	}
	
	public static void main(String args[]) {
		CorpusToolExecutor cte;
		cte = new CorpusToolExecutor();
	}
}

class OpenFiles extends FileDialog {
	private CorpusToolExecutor parent;
	
	public OpenFiles(Frame f) {
		super(f, "Open Dialog", FileDialog.LOAD);
		parent = (CorpusToolExecutor) f;
		
		setFile("*.xml");
		setMultipleMode(true);
		setSize(400, 250);
		setVisible(true);
		
		if (getFiles().length != 0) {
			File[] files = getFiles();
			for (File file : files) {
				parent.addText(file.getPath());
			}
		}
	}
}

class About extends Dialog {
	private Button b;
	private Label l;
	private Panel p, p2;
	private CorpusToolExecutor parent;

	public About(Frame f) {
		super(f, "About", false);
		parent = (CorpusToolExecutor) f;

		b = new Button("Ok");
		p = new Panel();
		p2 = new Panel();
		l = new Label("Corpus Tool Executor");

		p.add(l);
		p2.add(b);

		add("Center", p);
		add("South", p2);
		setSize(200, 100);
		setVisible(true);
	}
	

	public boolean handleEvent(Event e) {
		if (e.id == Event.WINDOW_DESTROY) {
			removeDialog();
			return true;
		}

		return super.handleEvent(e);
	}

	public boolean action(Event e, Object o) {
		if (e.target == b)
			removeDialog();

		return true;
	}

	// removeDialog is user-defined
	public void removeDialog() {
		setVisible(false);
		dispose();
		parent.setItemState(true);
	}
}