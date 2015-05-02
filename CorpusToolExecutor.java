import java.awt.Button;
import java.awt.Component;
import java.awt.Dialog;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class CorpusToolExecutor extends Frame {
	private Label l1, l2, l3;
	private Button convertButton;
	private TextArea t1, t2;
	private GridBagLayout gbLayout;
	private GridBagConstraints gbConstraints;
	private MenuItem openItem, clearItem, exitItem, aboutItem;
	
	private OpenFiles o;
	private About a;
	private Info i;
	
	private int convertedNumber;
	private int filesNumber;
	private int errNum;
	private String errInfo;
	
	public CorpusToolExecutor() {
		super("Corpus Tool Executor");
		l1 = new Label("Files opened:");
		l2 = new Label("Files save in:");
		l3 = new Label("Executor started.");
		
		convertButton = new Button("Convert!");
		convertButton.addActionListener(listener);
		
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
		clearItem = new MenuItem("Clear");
		exitItem = new MenuItem("Exit");
		aboutItem = new MenuItem("About");
		
		openItem.addActionListener(listener);
		clearItem.addActionListener(listener);
		exitItem.addActionListener(listener);
		aboutItem.addActionListener(listener);
		
		fileMenu.add(openItem);
		fileMenu.add(clearItem);
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
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				removeFrame();
			}
		});
	}
	
	private ActionListener listener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof MenuItem) {
				String buttonName = e.getActionCommand();
				if (buttonName.equals(openItem.getLabel())) {
					o = new OpenFiles(CorpusToolExecutor.this);
				} else if (buttonName.equals(clearItem.getLabel())) {
					t1.setText(null);
				} else if (buttonName.equals(exitItem.getLabel())) {
					removeFrame();
				} else if (buttonName.equals(aboutItem.getLabel())) {
					a = new About(CorpusToolExecutor.this);
				}
			} else if (e.getSource() == convertButton) {
				t2.setText(null);
				if (t1.getText().equals("")) {
					i = new Info(CorpusToolExecutor.this, "No file opened.");
				} else {
					convert();
				}
			}
		}
	};
	
	private void addComponent(Component c, GridBagLayout g,
			GridBagConstraints gc, int row, int column, int width, int height) {
		gc.gridx = column;
		gc.gridy = row;

		gc.gridwidth = width;
		gc.gridheight = height;

		g.setConstraints(c, gc);
		add(c);
	}
	
	public void convert() {
		convertedNumber = 0;
		errNum = 0;
		errInfo = "";
		
		l3.setText("Converting...");
		String[] paths = t1.getText().split("\n");
		filesNumber = paths.length;
		for (String path : paths) {
			ConvertWork cw = new ConvertWork(this, path);
			Thread t = new Thread(cw);
			t.start();
		}
	}
	
	public synchronized void display(boolean success, String info) {
		if (success) {
			t2.append(info + "\n");
		} else {
			errNum++;
			errInfo += info;
		}
		
		convertedNumber++;
		if (convertedNumber == filesNumber) {
			if (errNum > 0) {
				t2.append("\nError information: " + errNum + "\n" + errInfo);
			}
			
			l3.setText("Task completed.");
		}
	}
	
	public void addFiles(File[] files) {
		String[] oldPaths = t1.getText().split("\n");
		
		for (File file : files) {
			String path = file.getPath();
			if (!path.endsWith("-1.xml")) {
				
				boolean found = false;
				for (String oldPath : oldPaths) {
					if (oldPath.equals(path)) {
						found = true;
						break;
					}
				}
				if (found == false) {
					t1.append(path + "\n");
				}
				
			}
		}
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
		setVisible(true);
		
		parent.addFiles(getFiles());
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
		
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				removeDialog();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				removeDialog();
			}
		});
		
		add("Center", p1);
		add("South", p2);
		setSize(300, 150);
		setResizable(false);
		setLocationRelativeTo(parent);
		setVisible(true);
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
		
		b.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Info.this.removeDialog();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				removeDialog();
			}
		});
		
		add("Center", p1);
		add("South", p2);
		setSize(200, 100);
		setResizable(false);
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	public void removeDialog() {
		setVisible(false);
		dispose();
	}
}

class ConvertWork implements Runnable {
	private String path;
	private CorpusToolExecutor parent;
	
	public ConvertWork(Frame f, String path) {
		this.path = path;
		parent = (CorpusToolExecutor) f;
	}

	@Override
	public void run() {
		CorpusFile cfIn = new CorpusFile(path);
		CorpusTool ct;
		try {
			ct = new CorpusTool(cfIn);
		} catch (IOException e) {
			parent.display(false, path + ": READING ERROR!\n");
			return;
		}
		
		try {
			ct.handleCorpus();
		} catch (IllegalStateException e) {
			parent.display(false, path + ": WRONG FORMAT!\n");
			return;
		}
		
		String outPath = path.replace(".xml", "-1.xml");
		CorpusFile cfOut = new CorpusFile(outPath);
		try {
			ct.writeResultIn(cfOut);
		} catch (IOException e) {
			parent.display(false, path + ": WRITING ERROR!\n");
			return;
		}
		
		parent.display(true, outPath);
	}
}