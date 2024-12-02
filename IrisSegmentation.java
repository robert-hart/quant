import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import org.opencv.core.Core;
import javax.swing.SwingConstants;

public class IrisSegmentation extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField ks1;
	private JTextField light1;
	private JLabel lblStd;
	private JTextField std1;
	private JLabel lblCannyTr;
	private JTextField canny1;
	private JLabel lblThr;
	private JTextField accu1;
	private JPanel grey1;
	private JPanel edge1;
	private JLabel lblRadiusR;
	private JTextField r11;
	private JLabel lblR;
	private JTextField r12;
	private JPanel col1;
	private JLabel lblR_1;
	private JTextField r1;
	private JLabel lblX;
	private JTextField x1;
	private JLabel lblY;
	private JTextField y1;
	private JLabel label;
	private JTextField light2;
	private JLabel label_1;
	private JTextField ks2;
	private JLabel label_2;
	private JTextField std2;
	private JPanel grey2;
	private JLabel lblCannyThr;
	private JTextField canny2;
	private JLabel lblAccu;
	private JTextField accu2;
	private JPanel edge2;
	private JLabel lblR_2;
	private JTextField r21;
	private JTextField r22;
	private JLabel lblOuterCircleR;
	private JTextField r2;
	private JLabel label_7;
	private JTextField x2;
	private JLabel label_8;
	private JTextField y2;
	private JLabel lblR_3;
	private JLabel lblDp;
	private JTextField dp1;
	private JLabel lblMind;
	private JTextField minD1;
	private JLabel label_5;
	private JTextField dp2;
	private JLabel label_9;
	private JTextField minD2;
	private JButton rerun;
	private JSpinner spinner;
	private JCheckBox chckbxHeterochromia;
	private JCheckBox chckbxFreckels;
	private JCheckBox chckbxFibrills;

	/* own variables */
	private String fname;
	private Iris iris;
	private ImagePanel g1;
	private ImagePanel g2;
	private ImagePanel e1;
	private ImagePanel e2;
	private ImagePanel c1;
	private String[] filelist;
	private String inputDir;
	private String outputDir;
	private int imageIndex;
	private JButton btnUpd;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.out.println("IrisSegmentation");
		System.out.println("Segment iris from eye images.");
		System.out.println("(c) andreas.wollstein@gmail.com 2015");
		System.out.println("Usage: java -jar IrisSegmentation.jar <inputdir with png images> <outputdir>");
		
		if (args.length < 2) return;
		
		System.out.println("inputdir : " + args[0]);
		System.out.println("outputdir: " + args[1]);
			
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		IrisSegmentation frame = new IrisSegmentation();
		frame.setVisible(true);
		frame.setDirs(args[0], args[1]);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		/*EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					IrisSegmentation frame = new IrisSegmentation();
					frame.setVisible(true);
					frame.setDirs(sin, sout);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
		});*/
	}

	/**
	 * Create the frame.
	 */
	public IrisSegmentation() {
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 598, 844);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel("Gauss KS:");
		lblNewLabel.setBounds(105, 12, 61, 16);
		contentPane.add(lblNewLabel);

		ks1 = new JTextField();
		ks1.setText("9");
		ks1.setBounds(165, 6, 45, 28);
		contentPane.add(ks1);
		ks1.setColumns(10);

		JLabel lblLight = new JLabel("Light:");
		lblLight.setBounds(21, 12, 45, 16);
		contentPane.add(lblLight);

		light1 = new JTextField();
		light1.setText("70");
		light1.setColumns(10);
		light1.setBounds(59, 6, 45, 28);
		contentPane.add(light1);

		lblStd = new JLabel("STD:");
		lblStd.setBounds(216, 12, 29, 16);
		contentPane.add(lblStd);

		std1 = new JTextField();
		std1.setText("10");
		std1.setColumns(10);
		std1.setBounds(246, 6, 45, 28);
		contentPane.add(std1);

		grey1 = new JPanel();
		grey1.setBackground(Color.WHITE);
		grey1.setBounds(21, 40, 270, 160);
		contentPane.add(grey1);
		grey1.setLayout(new BorderLayout(0, 0));

		lblCannyTr = new JLabel("Canny Thr:");
		lblCannyTr.setBounds(21, 213, 83, 16);
		contentPane.add(lblCannyTr);

		canny1 = new JTextField();
		canny1.setText("50");
		canny1.setBounds(97, 207, 45, 28);
		contentPane.add(canny1);
		canny1.setColumns(10);

		lblThr = new JLabel("Accu:");
		lblThr.setBounds(171, 213, 39, 16);
		contentPane.add(lblThr);

		accu1 = new JTextField();
		accu1.setText("30");
		accu1.setBounds(204, 207, 45, 28);
		contentPane.add(accu1);
		accu1.setColumns(10);

		edge1 = new JPanel();
		edge1.setBackground(Color.WHITE);
		edge1.setBounds(21, 241, 270, 160);
		contentPane.add(edge1);
		edge1.setLayout(new BorderLayout(0, 0));

		lblRadiusR = new JLabel("r1:");
		lblRadiusR.setBounds(21, 419, 20, 16);
		contentPane.add(lblRadiusR);

		r11 = new JTextField();
		r11.setText("5");
		r11.setBounds(36, 413, 45, 28);
		contentPane.add(r11);
		r11.setColumns(10);

		lblR = new JLabel("r2:");
		lblR.setBounds(95, 419, 29, 16);
		contentPane.add(lblR);

		r12 = new JTextField();
		r12.setText("40");
		r12.setBounds(111, 413, 45, 28);
		contentPane.add(r12);
		r12.setColumns(10);

		col1 = new JPanel();
		col1.setBackground(Color.WHITE);
		col1.setForeground(Color.BLACK);
		col1.setBounds(22, 540, 554, 236);
		contentPane.add(col1);
		col1.setLayout(new BorderLayout(0, 0));

		lblR_1 = new JLabel("Inner circle r:");
		lblR_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblR_1.setBounds(21, 473, 96, 16);
		contentPane.add(lblR_1);

		r1 = new JTextField();
		r1.setBounds(121, 467, 45, 28);
		contentPane.add(r1);
		r1.setColumns(10);

		lblX = new JLabel("x:");
		lblX.setHorizontalAlignment(SwingConstants.RIGHT);
		lblX.setBounds(171, 473, 20, 16);
		contentPane.add(lblX);

		x1 = new JTextField();
		x1.setBounds(200, 467, 45, 28);
		contentPane.add(x1);
		x1.setColumns(10);

		lblY = new JLabel("y:");
		lblY.setHorizontalAlignment(SwingConstants.RIGHT);
		lblY.setBounds(257, 473, 20, 16);
		contentPane.add(lblY);

		y1 = new JTextField();
		y1.setBounds(281, 467, 45, 28);
		contentPane.add(y1);
		y1.setColumns(10);

		label = new JLabel("Light:");
		label.setBounds(306, 12, 45, 16);
		contentPane.add(label);

		light2 = new JTextField();
		light2.setText("20");
		light2.setColumns(10);
		light2.setBounds(344, 6, 45, 28);
		contentPane.add(light2);

		label_1 = new JLabel("Gauss KS:");
		label_1.setBounds(390, 12, 61, 16);
		contentPane.add(label_1);

		ks2 = new JTextField();
		ks2.setText("9");
		ks2.setColumns(10);
		ks2.setBounds(450, 6, 45, 28);
		contentPane.add(ks2);

		label_2 = new JLabel("STD:");
		label_2.setBounds(501, 12, 29, 16);
		contentPane.add(label_2);

		std2 = new JTextField();
		std2.setText("4");
		std2.setColumns(10);
		std2.setBounds(531, 6, 45, 28);
		contentPane.add(std2);

		grey2 = new JPanel();
		grey2.setBackground(Color.WHITE);
		grey2.setBounds(306, 40, 270, 160);
		contentPane.add(grey2);
		grey2.setLayout(new BorderLayout(0, 0));

		lblCannyThr = new JLabel("Canny Thr:");
		lblCannyThr.setBounds(306, 213, 83, 16);
		contentPane.add(lblCannyThr);

		canny2 = new JTextField();
		canny2.setText("50");
		canny2.setColumns(10);
		canny2.setBounds(382, 207, 45, 28);
		contentPane.add(canny2);

		lblAccu = new JLabel("Accu:");
		lblAccu.setBounds(456, 213, 39, 16);
		contentPane.add(lblAccu);

		accu2 = new JTextField();
		accu2.setText("40");
		accu2.setColumns(10);
		accu2.setBounds(489, 207, 45, 28);
		contentPane.add(accu2);

		edge2 = new JPanel();
		edge2.setBackground(Color.WHITE);
		edge2.setBounds(306, 241, 270, 160);
		contentPane.add(edge2);
		edge2.setLayout(new BorderLayout(0, 0));

		lblR_2 = new JLabel("r1:");
		lblR_2.setBounds(306, 419, 20, 16);
		contentPane.add(lblR_2);

		r21 = new JTextField();
		r21.setText("15");
		r21.setColumns(10);
		r21.setBounds(331, 413, 45, 28);
		contentPane.add(r21);

		r22 = new JTextField();
		r22.setText("200");
		r22.setColumns(10);
		r22.setBounds(406, 413, 45, 28);
		contentPane.add(r22);

		lblOuterCircleR = new JLabel("Outer circle r:");
		lblOuterCircleR.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOuterCircleR.setBounds(21, 507, 96, 16);
		contentPane.add(lblOuterCircleR);

		r2 = new JTextField();
		r2.setColumns(10);
		r2.setBounds(121, 500, 45, 28);
		contentPane.add(r2);

		label_7 = new JLabel("x:");
		label_7.setBounds(181, 507, 20, 16);
		contentPane.add(label_7);

		x2 = new JTextField();
		x2.setColumns(10);
		x2.setBounds(200, 501, 45, 28);
		contentPane.add(x2);

		label_8 = new JLabel("y:");
		label_8.setBounds(267, 506, 20, 16);
		contentPane.add(label_8);

		y2 = new JTextField();
		y2.setColumns(10);
		y2.setBounds(281, 500, 45, 28);
		contentPane.add(y2);

		rerun = new JButton("Estimate");
		rerun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run_estimate();
			}
		});
		rerun.setBounds(464, 441, 111, 29);
		contentPane.add(rerun);

		lblR_3 = new JLabel("r2:");
		lblR_3.setBounds(387, 419, 20, 16);
		contentPane.add(lblR_3);

		lblDp = new JLabel("dp:");
		lblDp.setBounds(164, 419, 29, 16);
		contentPane.add(lblDp);

		dp1 = new JTextField();
		dp1.setText("1");
		dp1.setBounds(183, 413, 30, 28);
		contentPane.add(dp1);
		dp1.setColumns(10);

		lblMind = new JLabel("minD:");
		lblMind.setBounds(216, 419, 45, 16);
		contentPane.add(lblMind);

		minD1 = new JTextField();
		minD1.setText("5");
		minD1.setBounds(255, 413, 30, 28);
		contentPane.add(minD1);
		minD1.setColumns(10);

		label_5 = new JLabel("dp:");
		label_5.setBounds(455, 419, 29, 16);
		contentPane.add(label_5);

		dp2 = new JTextField();
		dp2.setText("1");
		dp2.setColumns(10);
		dp2.setBounds(474, 413, 30, 28);
		contentPane.add(dp2);

		label_9 = new JLabel("minD:");
		label_9.setBounds(507, 419, 45, 16);
		contentPane.add(label_9);

		minD2 = new JTextField();
		minD2.setText("5");
		minD2.setColumns(10);
		minD2.setBounds(546, 413, 30, 28);
		contentPane.add(minD2);
		
		g1 = new ImagePanel();
		grey1.add(g1, BorderLayout.CENTER);
		g1.setBounds(0,0,grey1.getWidth(),grey1.getHeight());
		g2 = new ImagePanel();
		grey2.add(g2, BorderLayout.CENTER);
		g2.setBounds(0,0,grey2.getWidth(),grey2.getHeight());

		e1 = new ImagePanel();
		edge1.add(e1, BorderLayout.CENTER);
		e1.setBounds(0,0,edge1.getWidth(),edge1.getHeight());
		e2 = new ImagePanel();
		edge2.add(e2, BorderLayout.CENTER);
		e2.setBounds(0,0,edge2.getWidth(),edge2.getHeight());

		c1 = new ImagePanel();
		col1.add(c1, BorderLayout.CENTER);
		c1.setBounds(0,0,col1.getWidth(),col1.getHeight());

		chckbxHeterochromia = new JCheckBox("Heterochromia");
		chckbxHeterochromia.setBounds(21, 788, 128, 23);
		contentPane.add(chckbxHeterochromia);

		spinner = new JSpinner();
		spinner.setBounds(368, 786, 83, 28);
		contentPane.add(spinner);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save_files();
			}
		});
		btnSave.setBounds(464, 788, 117, 29);
		contentPane.add(btnSave);

		btnUpd = new JButton("Upd");
		btnUpd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				update_radius();
			}
		});
		btnUpd.setBounds(350, 499, 75, 29);
		contentPane.add(btnUpd);
		
		chckbxFreckels = new JCheckBox("Freckels");
		chckbxFreckels.setBounds(148, 788, 88, 23);
		contentPane.add(chckbxFreckels);
		
		chckbxFibrills = new JCheckBox("Fibrills");
		chckbxFibrills.setBounds(231, 788, 96, 23);
		contentPane.add(chckbxFibrills);

		spinner.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent evt) {
				spinnerChanged();
			}	
		});
		
		iris = new Iris();
	}
	public void setDirs(String indir, String outdir) {
		fname = "";
		System.out.println("set io " + indir + " " + outdir);
		setDefaultValues();
		setInputDir(indir);
		setOutputDir(outdir);
	}

	private void update_radius() {
		iris.getInner().setRadius(Integer.parseInt(x1.getText()), Integer.parseInt(y1.getText()), Integer.parseInt(r1.getText()));
		iris.getOuter().setRadius(Integer.parseInt(x2.getText()), Integer.parseInt(y2.getText()), Integer.parseInt(r2.getText()));
		double sc = (double) c1.getHeight() / (double) iris.getVisuimg().height();
		c1.setImage(iris.getVisuimgCloneWithCircles(sc),1);

		repaintall();
	}

	private void spinnerChanged() {
		Integer ival  = (Integer) spinner.getValue();
		if (ival >= 0 && ival < filelist.length) {
			imageIndex = ival.intValue();
			setImage(filelist[imageIndex]);
		}

		if(ival < 0) spinner.setValue(new Integer(0));
		if(ival >= filelist.length) spinner.setValue(new Integer(filelist.length));
	}		

	public void setDefaultValues() {
		iris.getInner().setParams(10, 9, 3, 20, 30, 1, 5, 5, 40);
		iris.getOuter().setParams(20, 9, 2, 50, 40, 1, 5, 15, 200);

		light1.setText("10");
		ks1.setText("9");
		std1.setText("3");
		canny1.setText("20");
		accu1.setText("30");
		dp1.setText("1");
		minD1.setText("5");
		r11.setText("5");
		r12.setText("40");

		light2.setText("20");
		ks2.setText("9");
		std2.setText("2");
		canny2.setText("50");
		accu2.setText("40");
		dp2.setText("1");
		minD2.setText("5");
		r21.setText("15");
		r22.setText("200");

		chckbxFibrills.setSelected(false);
		chckbxFreckels.setSelected(false);
		chckbxHeterochromia.setSelected(false);
	}

	/* method called when spinner changed */
	public void setImage(String fname) {

		this.fname = fname;
		iris.setImage(this.inputDir+fname);
		boolean bl1 = false, bl2 = false;
		File f = new File(inputDir+fname+".iprm");
		if (f.exists()) {
			iris.getInner().loadParamfile(inputDir+fname+".iprm");
			light1.setText( (int) iris.getInner().getLight() + "");
			ks1.setText((int) iris.getInner().getKs() + "");
			std1.setText((int) iris.getInner().getStd() + "");
			canny1.setText((int) iris.getInner().getCanny() + "");
			accu1.setText((int) iris.getInner().getAccum() + "");
			dp1.setText((int) iris.getInner().getDp() + "");
			minD1.setText((int) iris.getInner().getMinD() + "");
			r11.setText((int) iris.getInner().getR1() + "");
			r12.setText((int) iris.getInner().getR2() + "");
			r1.setText((int) iris.getInner().getR() + "");
			x1.setText((int) iris.getInner().getX() + "");
			y1.setText((int) iris.getInner().getY() + "");
			bl1 = true;
			
		} else {
			setDefaultValues();
		}

		f = new File(inputDir+fname+".oprm");
		if (f.exists()) { 
			iris.getOuter().loadParamfile(inputDir+fname+".oprm");
			light2.setText((int) iris.getOuter().getLight() + "");
			ks2.setText((int) iris.getOuter().getKs() + "");
			std2.setText((int) iris.getOuter().getStd() + "");
			canny2.setText((int) iris.getOuter().getCanny() + "");
			accu2.setText((int) iris.getOuter().getAccum() + "");
			dp2.setText((int) iris.getOuter().getDp() + "");
			minD2.setText((int) iris.getOuter().getMinD() + "");
			r21.setText((int) iris.getOuter().getR1() + "");
			r22.setText((int) iris.getOuter().getR2() + "");
			r2.setText((int) iris.getOuter().getR() + "");
			x2.setText((int) iris.getOuter().getX() + "");
			y2.setText((int) iris.getOuter().getY() + "");	
			bl2 = true;
			
		} else {
			setDefaultValues();
		}
		
		f = new File(outputDir+fname+".prm");
		if (f.exists()) {
			iris.loadParamFile(outputDir+fname+".prm");
			chckbxHeterochromia.setSelected(iris.isHeterochromia());
			chckbxFreckels.setSelected(iris.isFreckles());
			chckbxFibrills.setSelected(iris.isFibrills());
		}

		if (bl1 && bl2) {
			// TODO Auto-generated method stub
			rerun.setEnabled(false);
			// inner.setImage("/Users/andreas/bigdata/au_eye_skin_color/eyes/8102002cl_eye1.png");
			// outer.setImage("/Users/andreas/bigdata/au_eye_skin_color/eyes/8102002cl_eye1.png");

			iris.setImage(inputDir+fname);

			iris.getInner().exract_imgset();
			iris.getOuter().exract_imgset();

			double sc = (double) grey1.getHeight() / (double) iris.getInner().getGrey().height();

			g1.setImage(iris.getInner().getGrey(),sc);
			g2.setImage(iris.getOuter().getGrey(),sc);

			e1.setImage(iris.getInner().getEdges(),sc);
			e2.setImage(iris.getOuter().getEdges(),sc);

			sc = (double) c1.getHeight() / (double) iris.getVisuimg().height();
			c1.setImage(iris.getVisuimgCloneWithCircles(sc),1);
			
			r1.setText( "" + iris.getInner().getR());
			x1.setText( "" + iris.getInner().getX());
			y1.setText( "" + iris.getInner().getY());

			r2.setText( "" + iris.getOuter().getR());
			x2.setText( "" + iris.getOuter().getX());
			y2.setText( "" + iris.getOuter().getY());

			repaintall();

			rerun.setEnabled(true);
		} else{
			run_estimate();
		}
	}

	public void repaintall() {
		this.repaint();
		g1.repaint();
		g2.repaint();
		e1.repaint();
		e2.repaint();
		c1.repaint();
	}

	public void setInputDir(String fname) {
		inputDir = fname;
		File dir = new File(fname);
		filelist = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith("png");
			}});		
		spinner.setModel(new SpinnerNumberModel(0,0,filelist.length,1));
		imageIndex = 0;
		setImage(filelist[imageIndex]);
	}

	public void setOutputDir(String fname) {
		outputDir = fname;
	}

	/* save current file */
	public void save_files() {
		if (fname != "") {
			iris.setFibrills(chckbxFibrills.isSelected());
			iris.setFreckles(chckbxFreckels.isSelected());
			iris.setHeterochromia(chckbxHeterochromia.isSelected());

			iris.export_iris(outputDir + fname);
			iris.writeParamFile(outputDir+fname+".prm");
			iris.getInner().writeParamfile(inputDir+ fname + ".iprm");
			iris.getOuter().writeParamfile(inputDir+ fname + ".oprm");
		}
	}

	protected void run_estimate() {
		// TODO Auto-generated method stub
		rerun.setEnabled(false);
		// inner.setImage("/Users/andreas/bigdata/au_eye_skin_color/eyes/8102002cl_eye1.png");
		// outer.setImage("/Users/andreas/bigdata/au_eye_skin_color/eyes/8102002cl_eye1.png");

		iris.setImage(inputDir+fname);
		
		iris.getInner().setParams(Double.parseDouble(light1.getText()), Double.parseDouble(ks1.getText()), Double.parseDouble(std1.getText()), Double.parseDouble(canny1.getText()), Double.parseDouble(this.accu1.getText()), Double.parseDouble(this.dp1.getText()), Double.parseDouble(this.minD1.getText()), Integer.parseInt(r11.getText()), Integer.parseInt(r12.getText()));
		iris.getOuter().setParams(Double.parseDouble(light2.getText()), Double.parseDouble(ks2.getText()), Double.parseDouble(std2.getText()), Double.parseDouble(canny2.getText()), Double.parseDouble(accu2.getText()), Double.parseDouble(dp2.getText()), Double.parseDouble(minD2.getText()), Integer.parseInt(r21.getText()), Integer.parseInt(r22.getText()));	

		iris.getInner().exract_radius();
		iris.getOuter().exract_radius();

		double sc = (double) grey1.getHeight() / (double) iris.getInner().getGrey().height();

		g1.setImage(iris.getInner().getGrey(),sc);
		g2.setImage(iris.getOuter().getGrey(),sc);

		e1.setImage(iris.getInner().getEdges(),sc);
		e2.setImage(iris.getOuter().getEdges(),sc);
		
		sc = (double) c1.getHeight() / (double) iris.getVisuimg().height();
		c1.setImage(iris.getVisuimgCloneWithCircles(sc),1);

		r1.setText( "" + iris.getInner().getR());
		x1.setText( "" + iris.getInner().getX());
		y1.setText( "" + iris.getInner().getY());

		r2.setText( "" + iris.getOuter().getR());
		x2.setText( "" + iris.getOuter().getX());
		y2.setText( "" + iris.getOuter().getY());

		repaintall();

		rerun.setEnabled(true);
	}
}
