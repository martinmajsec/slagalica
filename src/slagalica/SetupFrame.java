package slagalica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SetupFrame extends JFrame implements ListSelectionListener {

	/**
	 * default size of puzzle tile
	 */
	private final int DEFAULT_ICON_SIZE = 200;
	/**
	 * lower bound for number of rows/columns in the puzzle
	 */
	private final int fromTiles = 2;
	/**
	 * upper bound for number of rows/columns in the puzzle
	 */
	private final int toTiles = 6;
	
//	private static ImageIcon myImg;
	/**
	 * collection that contains file names of all images that can be displayed in the puzzle
	 */
	private static Set<String> filenames;
	/**
	 * JList model
	 */
	private DefaultListModel<String> model;
	/**
	 * contains list of image names and their display
	 */
	private JSplitPane spPanel;
	/**
	 * contains names of image files
	 */
	private JList<String> jlist;
	/**
	 * contains images that can be displayed in the puzzle
	 */
	private JLabel lbIcon;
	/**
	 * collection that contains entry (file name, image)
	 */
	private static Map<String, ImageIcon> images;
	/**
	 * contains JComboBox for choosing number of rows/columns
	 */
	private JPanel southPane;
	/**
	 * in {@link #southPane}
	 */
	private JLabel tileNo = new JLabel("select number of tiles:");
	/**
	 * contains options for choosing number of rows/columns
	 */
	private Integer[] tileNumbers;
	/**
	 * JComboBox for choosing number of rows/columns
	 */
	private JComboBox<Integer> tileNoBox;
	/**
	 * confirms setup and displays the puzzle
	 */
	private JButton okBtn;
	/**
	 * unused
	 */
	private int empty;
	/**
	 * counts number of attempts to make the puzzle solveable
	 */
	private int setupCnt = 0;
	
	public SetupFrame() {
		super("Setup");
		model = new DefaultListModel<>();
		model.addAll(filenames);
    	jlist = new JList<>(model);
    	jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	jlist.addListSelectionListener(this);
    	
    	JScrollPane listScrollPanel = new JScrollPane(jlist);
    	
    	lbIcon = new JLabel();
    	lbIcon.setFont(lbIcon.getFont().deriveFont(Font.ITALIC));
    	lbIcon.setHorizontalAlignment(SwingConstants.CENTER);
    	JScrollPane pictureScrollPanel = new JScrollPane(lbIcon);
    	
    	spPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                listScrollPanel, pictureScrollPanel);
    	spPanel.setDividerLocation(150);
        spPanel.setPreferredSize(new Dimension(600, 400));
        add(spPanel, BorderLayout.CENTER);
        
        // choose no of tiles
        
        southPane  = new JPanel();
        southPane.add(tileNo);
        tileNumbers = new Integer[toTiles - fromTiles];
        for (int i = 0;i < toTiles - fromTiles;i++) {
        	tileNumbers[i] = i + fromTiles;
        }
        tileNoBox = new JComboBox<>(tileNumbers);
        southPane.add(tileNoBox);
        okBtn = new JButton("ok");
        southPane.add(okBtn);
        okBtn.addActionListener(e -> {
        	// run Slagalica
        	dispose();
        	int dimension = tileNoBox.getSelectedIndex() + fromTiles;
        	System.out.println("selected dim is " + dimension);
    		final int tile_size = 1000/dimension;
    		empty = dimension*dimension;
 //   		System.out.println("first empty is " + empty);
    		String image_path = jlist.getSelectedValue();
    		if (image_path == null) image_path = model.get(0);
    		
    		Slagalica.setup(this, dimension*dimension, dimension, tile_size, image_path);
//    		System.out.println("constructor with empty " + empty);
    		Slagalica mySlagalica = new Slagalica(empty, dimension, tile_size);
    		
    		SwingUtilities.invokeLater(()->  mySlagalica.setVisible(true));
    		
        });
        add(southPane, BorderLayout.PAGE_END);
        
        
    	setSize(500, 500);
	}
	@Override
	public void valueChanged(ListSelectionEvent e) {
		@SuppressWarnings("unchecked")
		 JList<String> list = (JList<String>)e.getSource();
		 updateLabel(list.getSelectedValue());
	}
	/**
	 * unused
	 */
	void setEmpty(int x) {
		System.out.println("set new empty");
		empty = x;
	}
	/**
	 * displays image when chosen in name list
	 * @param name relative path to chosen image
	 */
	private void updateLabel(String name) {
		
        ImageIcon icon = images.get(name);
	    lbIcon.setIcon(new ImageIcon(icon.getImage()
	    		.getScaledInstance(DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE, Image.SCALE_SMOOTH)));

//        lbIcon.setIcon(icon);
        if  (icon != null) {
        	lbIcon.setText(null);
        } 
/*        else {
        	lbIcon.setText("Image not found");
        } */
    
	}
	public static void main(String[] args) {
			
		setup();
		
		SwingUtilities.invokeLater(()->  new SetupFrame().setVisible(true));
			
			
			
	}


	/**
	 * gets image info from file visitor
	 */
	private static void setup() {
		Path myPath = Path.of("data/");
		MyFileVisitor visitor = new MyFileVisitor();
		try {
			Files.walkFileTree(myPath, visitor);
		} catch (Exception e) {e.printStackTrace();}
		filenames = visitor.getfilenames();
		images = visitor.getImages();
//		myImg = visitor.getMyImg();
//		System.out.println("myImg is " + myImg);
	}

	public int getSetupCnt() {
		return setupCnt;
	}
/*	public ImageIcon getMyImg() {
		return myImg;
	}
*/	void setSetupCnt() {
		setupCnt = 1;
	}
	
}
