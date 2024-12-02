import java.awt.BorderLayout;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import javax.swing.JButton;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.awt.Color;
import javax.swing.border.LineBorder;

public class IrisQuantifier extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ImagePanel ipc1;
	private ImagePanel ipc2;
	private ImagePanel ipc3;
	private ImagePanel ipc4;
	private ImagePanel ipc5;
	private ImagePanel ipc6;
	private ImagePanel ipc7;
	private ImagePanel ipc8;
	private ImagePanel ipc9;
	private ImagePanel ipc10;
	private ImagePanel ips1;
	private ImagePanel ips2;
	private ImagePanel ips3;
	private HSVSeparation hsvsep;
	private String quantificationDir;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		System.out.println("IrisQuantification");
		System.out.println("(c) andreas.wollstein@gmail.com 2015");
		System.out.println("Usage: java -jar IrisQuantification.jar <dir with iris images> <[imgclass1 imgclass2 ,..., imgclassn]>");
		
		if (args.length < 3) {
			System.out.println("Not enough arguments, exiting.");
			return;
		}

		//System.out.println("inputdir : " + args[0]);
		//System.out.println("outputdir: " + args[1]);
			
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		IrisQuantifier frame = new IrisQuantifier();
		frame.setQuantificationDir(args[0]);
		for (int i=1;i<args.length;i++) {
			frame.setClassImage(args[i], i);
		}
		
		/*frame.setQuantificationDir("/Users/andreas/Box Sync/Quanification-items/autrain1/");
		frame.setClassImage("/Users/andreas/Box Sync/Quanification-items/autrain1/svm-blue.jpg", 1);
		frame.setClassImage("/Users/andreas/Box Sync/Quanification-items/autrain1/svm-yellow-orange.jpg", 2);
		frame.setClassImage("/Users/andreas/Box Sync/Quanification-items/autrain1/svm-darkbrown.jpg", 3); */
		
		frame.train();
		//frame.run_quantification();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Create the frame.
	 */
	public IrisQuantifier() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 808, 716);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(new TitledBorder(null, "Defined Classes", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		inputPanel.setBounds(6, 6, 796, 338);
		contentPane.add(inputPanel);
		inputPanel.setLayout(null);
		
		JPanel panelC1 = new JPanel();
		panelC1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Class 1", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC1.setBounds(10, 23, 150, 150);
		inputPanel.add(panelC1);
		
		JPanel panelC2 = new JPanel();
		panelC2.setBorder(new TitledBorder(null, "Class 2", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC2.setBounds(166, 23, 150, 150);
		inputPanel.add(panelC2);
		
		JPanel panelC3 = new JPanel();
		panelC3.setBorder(new TitledBorder(null, "Class 3", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC3.setBounds(322, 23, 150, 150);
		inputPanel.add(panelC3);
		
		JPanel panelC4 = new JPanel();
		panelC4.setBorder(new TitledBorder(null, "Class 4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC4.setBounds(478, 23, 150, 150);
		inputPanel.add(panelC4);
		
		JPanel panelC5 = new JPanel();
		panelC5.setBorder(new TitledBorder(null, "Class 5", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC5.setBounds(634, 23, 150, 150);
		inputPanel.add(panelC5);
		
		JPanel panelC6 = new JPanel();
		panelC6.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Class 6", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC6.setBounds(10, 179, 150, 150);
		inputPanel.add(panelC6);
		
		JPanel panelC7 = new JPanel();
		panelC7.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Class 7", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC7.setBounds(166, 179, 150, 150);
		inputPanel.add(panelC7);
		
		JPanel panelC8 = new JPanel();
		panelC8.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Class 8", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC8.setBounds(322, 179, 150, 150);
		inputPanel.add(panelC8);
		
		JPanel panelC9 = new JPanel();
		panelC9.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Class 9", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC9.setBounds(478, 179, 150, 150);
		inputPanel.add(panelC9);
		
		JPanel panelC10 = new JPanel();
		panelC10.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Class 10", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelC10.setBounds(634, 179, 150, 150);
		inputPanel.add(panelC10);
		
		JPanel separationPanel = new JPanel();
		separationPanel.setBorder(new TitledBorder(null, "Separation in HSV", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		separationPanel.setBounds(6, 356, 796, 325);
		contentPane.add(separationPanel);
		separationPanel.setLayout(null);
		
		JPanel panelS1 = new JPanel();
		panelS1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelS1.setBounds(26, 21, 240, 240);
		separationPanel.add(panelS1);
		
		JPanel panelS2 = new JPanel();
		panelS2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelS2.setBounds(288, 21, 240, 240);
		separationPanel.add(panelS2);
		
		JPanel panelS3 = new JPanel();
		panelS3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panelS3.setBounds(550, 21, 240, 240);
		separationPanel.add(panelS3);
		
		JLabel lblH = new JLabel("H");
		lblH.setBounds(134, 267, 24, 16);
		separationPanel.add(lblH);
		
		JLabel lblD = new JLabel("S");
		lblD.setBounds(401, 267, 13, 16);
		separationPanel.add(lblD);
		
		JLabel lblH1 = new JLabel("H");
		lblH1.setBounds(674, 267, 24, 16);
		separationPanel.add(lblH1);

		JLabel lblS1 = new JLabel("V");
		lblS1.setBounds(272, 135, 13, 16);
		separationPanel.add(lblS1);
		
		JLabel lbls3 = new JLabel("S");
		lbls3.setBounds(7, 135, 13, 16);
		separationPanel.add(lbls3);
		
		JLabel lblV = new JLabel("V");
		lblV.setBounds(534, 135, 13, 16);
		separationPanel.add(lblV);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run_quantification();
			}			
		});
		btnRun.setBounds(673, 290, 117, 29);
		separationPanel.add(btnRun);
		
		ipc1 = new ImagePanel();
		ipc1.setBorder(new LineBorder(Color.CYAN, 5));
		ipc1.setForeground(Color.BLACK);
		ipc2 = new ImagePanel();
		ipc2.setBorder(new LineBorder(Color.YELLOW, 5));
		ipc3 = new ImagePanel();
		ipc3.setBorder(new LineBorder(Color.RED, 5));
		ipc4 = new ImagePanel();
		ipc4.setBorder(new LineBorder(Color.MAGENTA, 5));
		ipc5 = new ImagePanel();
		ipc5.setBorder(new LineBorder(Color.GREEN, 5));
		ipc6 = new ImagePanel();
		ipc6.setBorder(new LineBorder(Color.BLUE, 5));
		ipc7 = new ImagePanel();
		ipc7.setBorder(new LineBorder(new Color(0, 128, 128), 5));
		ipc8 = new ImagePanel();
		ipc8.setBorder(new LineBorder(new Color(255, 192, 203), 5));
		ipc9 = new ImagePanel();
		ipc9.setBorder(new LineBorder(new Color(255, 140, 0), 5));
		ipc10 = new ImagePanel();
		ipc10.setBorder(new LineBorder(new Color(128, 128, 0), 5));
		ips1 = new ImagePanel();
		ips2 = new ImagePanel();
		ips3 = new ImagePanel();
		panelC1.setLayout(new BorderLayout(0, 0));
		panelC1.add(ipc1);
		panelC2.setLayout(new BorderLayout(0, 0));
		panelC2.add(ipc2);
		panelC3.setLayout(new BorderLayout(0, 0));
		panelC3.add(ipc3);
		panelC4.setLayout(new BorderLayout(0, 0));
		panelC4.add(ipc4);
		panelC5.setLayout(new BorderLayout(0, 0));
		panelC5.add(ipc5);
		panelC6.setLayout(new BorderLayout(0, 0));
		panelC6.add(ipc6);
		panelC7.setLayout(new BorderLayout(0, 0));
		panelC7.add(ipc7);
		panelC8.setLayout(new BorderLayout(0, 0));
		panelC8.add(ipc8);
		panelC9.setLayout(new BorderLayout(0, 0));
		panelC9.add(ipc9);
		panelC10.setLayout(new BorderLayout(0, 0));
		panelC10.add(ipc10);
		panelS1.setLayout(new BorderLayout(0, 0));
		panelS1.add(ips1);
		panelS2.setLayout(new BorderLayout(0, 0));
		panelS2.add(ips2);
		panelS3.setLayout(new BorderLayout(0, 0));
		panelS3.add(ips3);
		hsvsep = new HSVSeparation();
	}

	public void setClassImage(String fname, int imgnr) {
		System.out.println("Set class " + imgnr + " to "  + fname);
		Mat image = new Mat();
		try {
			image  = Imgtools.bitmaptomat(ImageIO.read(new File(fname)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double sc = 150.0/ (double) image.height();

		if (imgnr==1) ipc1.setImage(image, sc);
		if (imgnr==2) ipc2.setImage(image, sc);
		if (imgnr==3) ipc3.setImage(image, sc);
		if (imgnr==4) ipc4.setImage(image, sc);
		if (imgnr==5) ipc5.setImage(image, sc);
		if (imgnr==6) ipc6.setImage(image, sc);
		if (imgnr==7) ipc7.setImage(image, sc);
		if (imgnr==8) ipc8.setImage(image, sc);
		if (imgnr==9) ipc9.setImage(image, sc);
		if (imgnr==10) ipc10.setImage(image, sc);

		hsvsep.addClass(image, imgnr);
		ips1.setImage(hsvsep.getSep1(), 1.0);
		ips2.setImage(hsvsep.getSep2(), 1.0);
		ips3.setImage(hsvsep.getSep3(), 1.0);
	}

	public void train() {
		hsvsep.train_svm();
	}

	private void run_quantification() {
		// run on defined directory

		File dir = new File(quantificationDir);
		String[] filelist = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("png");
			}});		
		
		/* erase quantification file */
		hsvsep.initQuantificationFile(quantificationDir);
		for (int i=0;i<filelist.length;i++) {
			hsvsep.quantify_iris(quantificationDir + filelist[i]);
		}
	}

	public void setQuantificationDir(String quantificationDir) {
		System.out.println("Quantiication directory: " + quantificationDir);
		this.quantificationDir = quantificationDir;
	}
}
