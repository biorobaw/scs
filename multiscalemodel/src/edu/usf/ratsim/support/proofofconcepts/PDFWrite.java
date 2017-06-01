package edu.usf.ratsim.support.proofofconcepts;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.erichseifert.vectorgraphics2d.PDFGraphics2D;

public class PDFWrite {

	public static void main (String[] args) throws IOException{
		// Create a new PDF document with a width of 210 and a height of 297
        PDFGraphics2D g = new PDFGraphics2D(0.0, 0.0, 210.0, 297.0);

        // Draw a red ellipse at the position (20, 30) with a width of 100 and a height of 150
        g.setColor(Color.RED);
        g.fillOval(20, 30, 100, 150);

        // Write the PDF output to a file
        FileOutputStream file = new FileOutputStream("ellipse.pdf");
        try {
            file.write(g.getBytes());
        } finally {
            file.close();
        }
	}
}
