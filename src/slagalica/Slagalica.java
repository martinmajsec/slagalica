package slagalica;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;




public class Slagalica extends JFrame{

	/**
	 * contains all puzzle tiles
	 */
	private static JButton[] buttons = new JButton[100];
	/**
	 * contains the center of each button
	 */
	private static Point btnCenters[] = new Point[100];
	/**
	 * contains images that can be displayed in the puzzle
	 */
	private static BufferedImage[] imgs = new BufferedImage[100];
	/**
	 * collection that links the image to its index 
	 */
	private static Map <BufferedImage, Integer> imgMap = new HashMap<>();
	/**
	 * collection that links ImageIcon with its parent image
	 */
	private static Map <ImageIcon, BufferedImage> IIToBI = new HashMap<>();
	/**
	 * contains button selection
	 */
	private int selectedBtnIndex = -1;
	/**
	 * contains height after the frame is resized. It will automatically be resized to a square according to its heigth.
	 */
	private int lazyNewHeight;
	/**
	 * 
	 */

	/**
	 * 
	 * @param emptyInd index of empty tile, 1-indexed
	 * @param DIMENSION number of rows/columns
	 * @param TILE_SIZE inital frame heigth and width
	 */
	public Slagalica(int emptyInd, int DIMENSION, int TILE_SIZE) {
		super("Slagalica");
		lazyNewHeight = TILE_SIZE;
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//		me = this;
		
		setLayout(new GridLayout(DIMENSION, DIMENSION));
		
		for (int i = 1;i <= DIMENSION * DIMENSION;i++) {
			
			if (i == emptyInd) {
				JButton emptyButton = new JButton("");
				buttons[emptyInd] = emptyButton;
				
				emptyButton.addActionListener(e -> {
					setSize(lazyNewHeight, lazyNewHeight);
					final int TILE_SIZE_COPY = lazyNewHeight / DIMENSION;
					if (selectedBtnIndex == -1) {
						System.out.println("no selection yet");
						return;
					}
//					System.out.println("clicked");
//					System.out.println("selected " + selectedBtnIndex);
					if (Point.distance(btnCenters[emptyInd].getX(), btnCenters[emptyInd].getY(), btnCenters[selectedBtnIndex].getX(), btnCenters[selectedBtnIndex].getY()) - TILE_SIZE_COPY < 1) {
						
						// swap icons
						BufferedImage pom = imgs[emptyInd];
						imgs[emptyInd] = imgs[selectedBtnIndex];
						imgs[selectedBtnIndex] = pom;
//						System.out.printf("contains %b %b\n", 
//								imgMap.containsKey(imgs[emptyInd]), imgMap.containsKey(imgs[selectedBtnIndex]));

						// refresh mapping
						
						int x = imgMap.get(imgs[selectedBtnIndex]), y = imgMap.get(imgs[emptyInd]);
						imgMap.put(imgs[emptyInd], y);
						imgMap.put(imgs[selectedBtnIndex], x);
//						System.out.printf("swapping %d %d\n", y, x);
						// switchTiles(selectedBtnIndex, emptyInd);
						// selectedBtnIndex = new empty tile
						dispose();
						SwingUtilities.invokeLater(()->  new Slagalica(selectedBtnIndex, DIMENSION, TILE_SIZE_COPY).setVisible(true));
						
//						System.out.println("btn " + selectedBtnIndex + " goes to " + emptyInd);
						
					}
					else {
						// the selected tile cant be swapped with the empty tile
						// no info is displayed in UI for smoothness
						System.out.println("too far");
					}
					
				});
			}
			else {
				buttons[i] = new JButton(Integer.toString(i));
				final int iCopy = i; // final for listener
				buttons[i].addActionListener(e -> {
					setSize(lazyNewHeight, lazyNewHeight);
					selectedBtnIndex = iCopy;
					System.out.println("btn" + iCopy);
				});
				
			}
			add(buttons[i]);
			
		}
		
		setSize(TILE_SIZE*DIMENSION, TILE_SIZE*DIMENSION);
		
		// display tiles 
		
		for (int i = 1;i <= DIMENSION * DIMENSION;i++) {
			
			if (i == emptyInd) continue;
			
			Image tmp = imgs[i].getScaledInstance(lazyNewHeight, lazyNewHeight, Image.SCALE_SMOOTH);
			BufferedImage dimg = new BufferedImage(lazyNewHeight, lazyNewHeight, BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2d = dimg.createGraphics();
		    g2d.drawImage(tmp, 0, 0, null);
		    ImageIcon II = new ImageIcon(dimg);
			buttons[i].setIcon(II);
			IIToBI.put(II, dimg);
		}
		
		// check if puzzle is solved
		
		int isDone = 1;
		for (int i = 1; i <= DIMENSION * DIMENSION;i++) {
//			System.out.printf("%d: %d\n", i, imgMap.get(imgs[i]));
			if (imgMap.get(imgs[i]) != i) {
				isDone = 0;
			}
		}
		if (isDone == 1) {
			// display the final tile
			Image tmp = imgs[emptyInd].getScaledInstance(lazyNewHeight, lazyNewHeight, Image.SCALE_SMOOTH);
			BufferedImage dimg = new BufferedImage(lazyNewHeight, lazyNewHeight, BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2d = dimg.createGraphics();
		    g2d.drawImage(tmp, 0, 0, null);
			buttons[emptyInd].setIcon(new ImageIcon(dimg));
			
			JOptionPane.showMessageDialog(this, "Congratulations! You have finished the puzzle.");
			
			for (int i = 1;i <= DIMENSION * DIMENSION;i++) {
				buttons[i].setEnabled(false);
			}
			
		}
		// resizing is done lazily for smoothness
		addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent componentEvent) {
		    	lazyNewHeight = (int) getSize().getHeight();
//		        System.out.println("new size is h=" + me.getSize().getHeight() + " w=" + me.getSize().getWidth());

		    }
		});
		
	}
	


/*	public static void main(String[] args) {
		
		
		final int DEFAULT_DIMENSION = 2; // ako je paran, uvijek ce bit rijesivo https://en.wikipedia.org/wiki/15_puzzle
		final int DEFAULT_TILE_SIZE = 1000/DEFAULT_DIMENSION;
		final int DEFAULT_EMPTY = 4;
//		setup(DEFAULT_DIMENSION, DEFAULT_TILE_SIZE);
		SwingUtilities.invokeLater(()->  new Slagalica(DEFAULT_EMPTY,DEFAULT_DIMENSION, DEFAULT_TILE_SIZE).setVisible(true));
		
		
		
	}*/

	/**
	 * 
	 * @param parent reference to main frame
	 * @param firstEmpty index of initial empty tile, 1-indexed
	 * @param DIMENSION number of rows/columns
	 * @param TILE_SIZE inital frame heigth and width
	 * @param IMAGE_PATH path to image to be displayed in the puzzle
	 */
	public static void setup(SetupFrame parent, int firstEmpty, int DIMENSION, int TILE_SIZE, String IMAGE_PATH) {

//		myImg = parent.getMyImg();
		
		// btnCenters 
		
		for (int i = 1;i <= DIMENSION * DIMENSION;i++) {
			int row = (i - 1) / DIMENSION;
			int col = (i - 1) % DIMENSION;
			btnCenters[i] = new Point((int)((col + 0.5) * TILE_SIZE),(int)((row + 0.5) * TILE_SIZE)); // gridLayout ih stavlja kao j,i
//					System.out.println("center of btn" + i + " is " + btnCenters[i]);
		}
		
		// split image
		
		for (int i = 1;i <= DIMENSION * DIMENSION;i++) {
			try {
				String path = IMAGE_PATH;
				BufferedImage myPicture = ImageIO.read(new File(path));
				int myH = myPicture.getHeight() / DIMENSION;
				// TODO treba razdijeliti zastavu turske, a ne frame
	//			System.out.println("picture height is " + myH);
				int row = (i - 1) / DIMENSION;
				int col = (i - 1) % DIMENSION;
//				System.out.printf("button%d %d %d\n", i, col, row); // j,i
//				System.out.printf("button%d %d %d %d %d\n", i, myH * col, myH * row, myH, myH);
				BufferedImage splitImg = myPicture.getSubimage(myH * col, myH * row, myH, myH);
				imgs[i] = splitImg;	
				imgMap.put(splitImg, i);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// shuffle = single swap
		
		for (int i = 0;;i++) {
			Random r = new Random();
			int x = r.nextInt(DIMENSION * DIMENSION) + 1;
			int y = r.nextInt(DIMENSION * DIMENSION) + 1;
			if (x != y) {
				BufferedImage pomx = imgs[x], pomy = imgs[y];
//				System.out.println("initial swap " + x + " " + y);
				// swap
				BufferedImage pom = imgs[x];
				imgs[x] = imgs[y];
				imgs[y] = pom;
				if (isSolveable(firstEmpty, DIMENSION)) {
					imgMap.put(imgs[x], y);
					imgMap.put(imgs[y], x);
					break;
				}
				// revert
				imgs[x] = pomx;
				imgs[y] = pomy;
				if (i == 100) {
					i = 0;
					firstEmpty += DIMENSION;
					firstEmpty = ((firstEmpty + DIMENSION + 1) % (DIMENSION * DIMENSION)) + 1;
					parent.setEmpty(firstEmpty);
					
					
				}
			}
		}
		for (int i = 1;i <= DIMENSION * DIMENSION;i++) {
//			System.out.printf("%d: %d\n", i, imgMap.get(imgs[i]));
		}
//		System.out.printf("solveable? %b\n", isSolveable(firstEmpty, DIMENSION));
	}

	/**
	 * 
	 * @param firstEmpty inital frame heigth and width
	 * @param DIMENSION number of rows/columns
	 * @return {@code true} if puzzle is solveable, {@code false} otherwise
	 * @see <a href="https://www.cs.princeton.edu/courses/archive/spring20/cos226/assignments/8puzzle/specification.php">
	 * https://www.cs.princeton.edu/courses/archive/spring20/cos226/assignments/8puzzle/specification.php</a>
	 * for solvability conditions
	 */
	static boolean isSolveable(int firstEmpty, int DIMENSION) {
//		System.out.printf("entered isSolveable with %d %d\n", firstEmpty, DIMENSION);
		int inversionCnt = 0;
		// pretending DIM*DIM is blank
		int dimdimRow = ((imgMap.get(imgs[DIMENSION*DIMENSION]) - 1) / DIMENSION) % 2;
		for (int i = 1; i <= DIMENSION * DIMENSION;i++) 
		{
			int cx = imgMap.get(imgs[i]);
			
			for (int j = i + 1;j <= DIMENSION * DIMENSION;j++) 
			{
				int cy = imgMap.get(imgs[j]);
				if (cx == DIMENSION * DIMENSION || cy == DIMENSION * DIMENSION) continue;
				
//				System.out.printf("comparing %d %d\n", cx, cy);
				if (cy < cx) inversionCnt++;
			}
		}
		
		if (DIMENSION % 2 == 0) {
			if ((inversionCnt + dimdimRow) % 2 == 1) {
				System.out.println("inversionCnt is " + inversionCnt + "+" + dimdimRow);
			}
			return (inversionCnt + dimdimRow) % 2 == 1;
		}
		else {
			
			if (inversionCnt % 2 == 0) {
				System.out.println("inversionCnt is " + inversionCnt);
			}
			return inversionCnt % 2 == 0;
		}
	}
}
