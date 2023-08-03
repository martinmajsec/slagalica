package slagalica;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;



public class MyFileVisitor extends SimpleFileVisitor<Path> {

	/**
	 * stores file names to be fetched from main
	 */
	public Set<String> fileNames = new HashSet<>();
	/**
	 * contains entries (file name, image). The images can be displayed in the puzzle.
	 */
	private Map<String, ImageIcon> images = new HashMap<>();
//	private ImageIcon myImg;
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//		System.out.println("getting " + file.toString());
		String filename = file.toString();
		if (filename.endsWith(".png")) {
			fileNames.add(filename);
			try {
//				System.out.println("trying to read |" + filename + "|");
				BufferedImage myPicture = ImageIO.read(new File(filename));
				ImageIcon ii = new ImageIcon(myPicture);
//				myImg = ii;
				images.put(filename, ii);
			}
			catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	public Set<String> getfilenames() {
		return fileNames;
	}

	public Map<String, ImageIcon> getImages() {
		return images;
	}


	
	
	
}
