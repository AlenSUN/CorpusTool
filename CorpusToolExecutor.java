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

public class CorpusToolExecutor extends Frame {
	private Label l1, l2, l3;
	private Button convertButton;
	private TextArea t1, t2;
	private GridBagLayout gbLayout;
	private GridBagConstraints gbConstraints;
	private MenuItem openItem, exitItem, aboutItem;
	private OpenFiles o;
	private About a;
	private Info i;
	
	public CorpusToolExecutor() {
		super("Corpus Tool Executor");
		l1 = new Label("File opened:");
		l2 = new Label("File saves in:");
		l3 = new Label("Executor started.");
		
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
		
		addComponent(l3, gbLayout, gbConstraints, 4, 0, 2, 1);
		
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
		setSize(600, 460);
		setResizable(false);
		setLocation(200, 100);
		setVisible(true);
	}
	
	private void addComponent(Component c, GridBagLayout g,
			GridBagConstraints gc, int row, int column, int width, int height) {
		gc.gridx = column;
		gc.gridy = row;

		gc.gridwidth = width;
		gc.gridheight = height;

		g.setConstraints(c, gc);
		add(c);
	}
	
	public boolean action(Event e, Object o) {
		if (e.target instanceof MenuItem) {
			if (e.arg.equals(openItem.getLabel())) {
				this.o = new OpenFiles(this);
			} else if (e.arg.equals(aboutItem.getLabel())) {
				a = new About(this);
			} else {
				removeFrame();
			}
		} else if (e.target == convertButton) {
			if (t1.getText().equals("")) {
				i = new Info(this, "No file opened.");
			} else {
				l3.setText("Converting...");
				String[] paths = t1.getText().split("\n");
				for (String path : paths) {
					CorpusTool ct = new CorpusTool(path);
					try {
						ct.setTextbookBuffer(CorpusTool.readTextInBuffer(ct.getTextbookPath()));
					} catch (IOException e1) {
						i = new Info(this, "READING ERROR");
						l3.setText("Stopped");
						break;
					}

					ct.constructMatcher();
					StringBuffer resultBuffer = ct.handleCorpus();
					
					String outPath = path.replace(".xml", "-1.xml");
					try {
						CorpusTool.writeBufferToText(resultBuffer, outPath);
					} catch (IOException e1) {
						i = new Info(this, "WRITING ERROR");
						l3.setText("Stopped");
						break;
					}
					
					t2.append(outPath + "\n");
				}
				l3.setText("Task completed.");
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
		if (s.endsWith("-1.xml")) {
			return;
		}
		
		if (!t1.getText().equals("")) {
			String[] paths = t1.getText().split("\n");
			for (String path : paths) {
				if (path.equals(s)) {
					return;
				}
			}
		}
		
		t1.append(s + "\n");
	}
	
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
	private Label l1, l2;
	private Panel p1, p2;
	private CorpusToolExecutor parent;

	public About(Frame f) {
		super(f, "About", true);
		parent = (CorpusToolExecutor) f;

		b = new Button("OK");
		p1 = new Panel();
		p2 = new Panel();
		l1 = new Label("Corpus Tool Executor");
		l2 = new Label("Copyright© 2015 Sun Ronglin.");

		p1.add(l1);
		p1.add(l2);
		p2.add(b);

		add("Center", p1);
		add("South", p2);
		setSize(300, 150);
		setResizable(false);
		setLocation(parent.getX() + (parent.getWidth() - getWidth()) / 2,
				parent.getY() + (parent.getHeight() - getHeight()) / 2);
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

	public void removeDialog() {
		setVisible(false);
		dispose();
	}
}

class Info extends Dialog {
	private Button b;
	private Label l;
	private Panel p1, p2;
	private CorpusToolExecutor parent;

	public Info(Frame f, String info) {
		super(f, "Info", true);
		parent = (CorpusToolExecutor) f;

		b = new Button("OK");
		p1 = new Panel();
		p2 = new Panel();
		l = new Label("ERROR: " + info);

		p1.add(l);
		p2.add(b);

		add("Center", p1);
		add("South", p2);
		setSize(200, 100);
		setResizable(false);
		setLocation(parent.getX() + (parent.getWidth() - getWidth()) / 2,
				parent.getY() + (parent.getHeight() - getHeight()) / 2);
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

	public void removeDialog() {
		setVisible(false);
		dispose();
	}
}