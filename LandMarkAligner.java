/*
 * Copyright (c) 2012, Andreas Wollstein, andreas.wollstein@gmail.com
 * 
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractModalGraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;
import edu.uci.ics.jung.visualization.renderers.Renderer.Vertex;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;

/**
 * 
 * @author Andreas Wollstein
 * 
 */
public class LandMarkAligner extends javax.swing.JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Graph<String, Number> graph;
	private double globalScaling = 1.5;
	private String[] filelist;

	/**
	 * the visual component and renderer for the graph
	 */
	private VisualizationViewer<String, Number> vv;

	private Map<String, String[]> nodelabels = new HashMap<String, String[]>();


	private Layout<String, Number> layout;

	private String fileName;
	private String inputDir;
	private String nodeListFile;
	private ImageIcon icon;

	private int imageIndex = 0;

	
	public LandMarkAligner(String idir, String nlfile) {
		setLayout(new BorderLayout());
		this.inputDir = idir;
		this.nodeListFile = nlfile;
		
		File dir = new File(idir);
        filelist = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("jpg");
            }});
        
		
		fileName = filelist[imageIndex];
		
		/* read nodelist */
		
		int numEdges = 1;
		graph = new SparseMultigraph<String, Number>();
		/* read in network definition graph */
		try {
			BufferedReader br = new BufferedReader(new FileReader(nodeListFile));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\t", " ");
				line = line.replaceAll("  ", " ");
				String[] cols = line.split(" ");
				/* add node */
				if (cols.length >= 3) {
					nodelabels.put(cols[0], new String[] { cols[1], cols[2] });
					graph.addVertex(cols[0]);
				}
				/* add edge */
				if (cols.length == 2) {
					graph.addEdge(numEdges, cols[0], cols[1]);
					numEdges++;
				}
				System.out.println(line);
			}
			br.close();

		} catch (Exception ex) {
			nodelabels.put("alR", new String[] { "200", "100" });
			graph.addVertex("alR");
			
			nodelabels.put("alL", new String[] { "100", "100" });
			graph.addVertex("alL");
			graph.addEdge(numEdges, "alL", "alR");
			numEdges++;
		}

		
		setImage(inputDir + fileName);
		
		Dimension layoutSize = new Dimension(500, 500);

		layout = new StaticLayout<String, Number>(graph,new ChainedTransformer<String, Point2D>(new Transformer[] {
								new LabelTransformer(nodelabels), new String2PixelTransformer(layoutSize) 
								}));
		
		layout.setSize(layoutSize);

		vv = new VisualizationViewer<String, Number>(layout, new Dimension(layoutSize.width,layoutSize.height));

		if (icon != null) {
			vv.addPreRenderPaintable(new VisualizationViewer.Paintable() {
				public void paint(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;

					AffineTransform oldXform = g2d.getTransform();
					AffineTransform lat = vv.getRenderContext()
							.getMultiLayerTransformer()
							.getTransformer(Layer.LAYOUT).getTransform();
					AffineTransform vat = vv.getRenderContext()
							.getMultiLayerTransformer()
							.getTransformer(Layer.VIEW).getTransform();
					AffineTransform at = new AffineTransform();

					at.concatenate(g2d.getTransform());
					at.concatenate(vat);
					at.concatenate(lat);
					g2d.setTransform(at);
					g.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(),
							icon.getIconHeight(), vv);
					g2d.setTransform(oldXform);
				}

				public boolean useTransform() {
					return false;
				}
			});
		}

		vv.setVertexToolTipTransformer(new ToStringLabeller<String>());
		vv.setEdgeToolTipTransformer(new Transformer<Number, String>() {
			public String transform(Number edge) {
				return "E" + graph.getEndpoints(edge).toString();
			}
		});

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
		vv.getRenderer().getVertexLabelRenderer()
				.setPositioner(new InsidePositioner());
		vv.getRenderer().getVertexLabelRenderer()
				.setPosition(Renderer.VertexLabel.Position.AUTO);

		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());

		vv.getRenderer().setVertexRenderer(new MyRenderer());
		

		final PickedState<String> pickedState = vv.getPickedVertexState();

		pickedState.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				Object subject = e.getItem();
			
				if (subject instanceof String) {
					String vertex = (String) subject;
					if (pickedState.isPicked(vertex)) {
						//System.out.println("Vertex " + vertex + " is now selected");
					} else {
						//System.out.println("Vertex " + vertex + " no longer selected");
					}
				}
			}
		});

		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		
		add(panel);
		
		final AbstractModalGraphMouse graphMouse = new DefaultModalGraphMouse<Object, Object>();
		
		vv.setGraphMouse(graphMouse);
		vv.addKeyListener(graphMouse.getModeKeyListener());
		vv.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode");

		graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
		final ScalingControl scaler = new CrossoverScalingControl();
		Point2D ppp = new Point();
		ppp.setLocation(0, 0);
		scaler.scale(vv, (float) globalScaling, ppp);

		JButton save = new JButton("save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				savePos();
				
			}
		});

		final JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(0,0,filelist.length,1));
		spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                                
                Integer ival  = (Integer) spinner.getValue();
                if (ival >= 0 && ival < filelist.length) {
                	
                	imageIndex = ival;
                	setImage(inputDir + filelist[imageIndex]);
                	vv.repaint();
                }  
                
                if(ival < 0) spinner.setValue(new Integer(0));
                if(ival >= filelist.length) spinner.setValue(new Integer(filelist.length));
                
            }			
        });
		
		
		JPanel controls = new JPanel();
		controls.add(save);
		controls.add(spinner);
		
		add(controls, BorderLayout.SOUTH);		
		
		setImage(inputDir + filelist[imageIndex]);
    	vv.repaint();
  
	}
	
	public void savePos() {

		try {
			String fn = fileName.replaceAll(".jpg","");
		    fn = fn.replaceAll(".JPG","");
		    fn = fn + ".pos";

			BufferedWriter bw = new BufferedWriter(new FileWriter(fn));

			for (String node : nodelabels.keySet()) {
				Point2D p = layout.transform(node);
				System.out.println("write " + node + p.getX() + " " + p.getY());
				bw.write(node + "\t" + p.getX() + "\t" + p.getY() + "\n");
			}
			bw.close();
		} catch (Exception ex) {

		}
	}
	

	public void setImage(String fname) {
		fileName = fname;
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image pic = toolkit.getImage(fileName);
		icon = new ImageIcon(pic);
		
		try {
			String fn = fileName.replaceAll(".jpg", "");
			fn = fn.replaceAll(".JPG", "");
			fn = fn + ".pos";

			BufferedReader br = new BufferedReader(new FileReader(fn));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("\t", " ");
				line = line.replaceAll("  ", " ");
				String[] cols = line.split(" ");
				/* add node */
				if (cols.length == 3) {
					Point2D p = layout.transform(cols[0]);
					p.setLocation(Double.parseDouble(cols[1]), Double.parseDouble(cols[2]));
				}
			}
			br.close();

		} catch (Exception ex) {
			System.out.print("No position file given");
		}

		/* check if tpos file exists */
		String fn = fileName.replaceAll(".jpg", "");
		fn = fn.replaceAll(".JPG", "");
		fn = fn.replaceAll(".jpg", "");
		fn = fn + ".tpos";
		File f = new File(fn);
		

		/* check if excl file exists */
	    fn = fileName.replaceAll(".jpg", "");
		fn = fn.replaceAll(".JPG", "");
		fn = fn.replaceAll(".jpg", "");
		fn = fn + ".exclude";
		f = new File(fn);
	}

	static class MyRenderer implements Vertex<String, Number> {

		public void paintVertex(RenderContext<String, Number> rc,
				Layout<String, Number> layout, String vertex) {
			
			GraphicsDecorator graphicsContext = rc.getGraphicsContext();
			Point2D center = layout.transform(vertex);
			Shape shape = null;
			Color color = null;

			graphicsContext.setPaint(Color.BLUE);
			int x = (int) ((double) center.getX());
			int y = (int) ((double) center.getY());

		}
	}

	static class LabelTransformer implements Transformer<String, String[]> {

		Map<String, String[]> map;

		public LabelTransformer(Map<String, String[]> map) {
			this.map = map;
		}

		/**
		 * transform airport code to latlon string
		 */
		public String[] transform(String city) {
			return map.get(city);
		}
	}

	static class String2PixelTransformer implements
			Transformer<String[], Point2D> {
		Dimension d;
		int startOffset;

		public String2PixelTransformer(Dimension d) {
			this.d = d;
		}

		/**
		 * transform a x y
		 */
		public Point2D transform(String[] latlon) {
			double x = 0;
			double y = 0;
			String[] lat = latlon[0].split(" ");
			String[] lon = latlon[1].split(" ");
			x = Integer.parseInt(lat[0]);

			y = Integer.parseInt(lon[0]);

			return new Point2D.Double(x, y);
		}
	}

	/**
	 * start it up
	 */
	public static void main(String[] args) {
		// create a frame to hold the graph
		System.out.println("LandmarkAligner (c) andreas.wollstein@gmail.com 2015");
		//System.out.println("nodelist.txt is required for node definition!");
		System.out.println("Usage: java -jar LandmarkAligner.jar <dir with jpg files> <graph definition file>");

		if (args.length < 2) {
			System.out.println("Not enough arguments, exiting.");
			return;
		}

		String dirname = args[0];
		String graphname = args[1];

		final LandMarkAligner frame = new LandMarkAligner(dirname,graphname);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
