package stuff;

import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Board extends JComponent implements MouseMotionListener, MouseListener{

	final int SIZE = 4;
	final boolean easyMode = false;
	final int WIDTH = 300, HEIGHT = 600;
	final int leftMargin = 42, rightMargin = 42, topMargin = 259, botMargin = 125;
	final int boxWidth = (WIDTH - leftMargin - rightMargin)/SIZE;
	final int boxHeight = (HEIGHT - topMargin - botMargin)/SIZE;

	BufferedImage image;

	private char[][] letters = new char[SIZE][SIZE];
	public HashSet<String> dict = new HashSet<String>(); 
	public HashSet<String> solutions;;
	private String selString;
	private ArrayList<Integer> used;
	private ArrayList<String> usedWords;
	private int points;
	private double timer;

	private int FPS = 60;
	private long targetTime = 1000 / FPS;

	private final int[] pKey = {100,400,800,1400,1800,2200,2600,3000,3400,3800}; 
	private final char[] alph = {'E','A','R','I','O','T','N','S','L','C','U','D','P','M','H','G','B','F','Y','W','K','V','X','Q','J','Z'};
	private final double[] weights = {56.88,43.31,38.64,38.45,36.51,35.43,33.92,29.23,27.98,23.13,18.51,17.25,16.14,15.36,15.31,12.59,10.56,9.24,9.06,6.57,5.61,5.13,1.48,1.39,1,1};

	public Board() {
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		addMouseListener(this);
		addMouseMotionListener(this);

		selString = "";
		points = 0; 
		timer = 60;
		used = new ArrayList<Integer>();
		usedWords = new ArrayList<String>();
		getDictionary();

		try {
			image = ImageIO.read(new File("back.jpg"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++ ) {
				letters[i][j] = chooseLetter();//"stngeiaedrlssepo".charAt(i*4+j);//
			}
		}

		solutions = Boggle.solve(letters);
		List<String> list = new ArrayList<String>(solutions);
		Collections.sort(list, Comparator.comparing(String::length));
		for(int i = list.size() - 1; i > list.size() - 10 && i >= 0; i--) {
			System.out.println(list.get(i));
		}
	}

	public void paintComponent(Graphics g) {
		long start, elapsed, wait;
		start = System.nanoTime();

		Graphics2D g2 = (Graphics2D) g;

		g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);

		g.setFont(new Font("Sempione Grotesk Bold",Font.BOLD,23));
		g2.drawString("" + String.format("%04d", points), 190, 92);
		g.setFont(new Font("Sempione Grotesk Bold",Font.BOLD,12));
		g2.drawString("" + usedWords.size(), 152, 72);
		g.setFont(new Font("Sempione Grotesk Bold",Font.BOLD,10));
		g2.setColor(Color.WHITE);
		g2.drawString("" + Math.floor(timer * 100) / 100, 225, 126);
		g2.drawString("Words Left: " + solutions.size(), 80, 505);
		RoundRectangle2D b = new RoundRectangle2D.Double((int) (150-selString.length()*4.5), 225,selString.length()*9+4,20,10,10);
		g2.fill(b);
		g2.setColor(Color.BLACK);
		g2.drawString(selString, (int) (150-selString.length()*4.5), 240);

		g.setFont(new Font("Courier",Font.BOLD, boxHeight));
		g2.setColor(Color.BLACK);
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++ ) {
				if(used.contains(i+j*SIZE)) {
					if(dict.contains(selString.toLowerCase()) && selString.length() >= 3) {
						if(usedWords.contains(selString)) {
							g2.setColor(new Color(221, 224, 126));
							if(easyMode && oneHas(solutions, selString)){
								g2.setColor(Color.CYAN);
							}
						}
						else {
							g2.setColor(new Color(105, 204, 94));
						}
					}
					else if(easyMode && oneHas(solutions, selString)){
						g2.setColor(Color.CYAN);
					}
					else {
						g2.setColor(new Color(242, 225, 194));
					}
				}
				else {g2.setColor(new Color(219,177,59));}
				//g2.setColor(new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
				RoundRectangle2D a = new RoundRectangle2D.Double(leftMargin + i*boxWidth, topMargin + j*boxHeight, boxWidth, boxHeight, 10, 10);
				g2.fill(a);

				g2.setColor(Color.BLACK);
				g2.draw(a);
				g2.drawString(Character.toString(letters[i][j]), leftMargin + i*boxWidth+boxWidth/5, topMargin + (j+1)*boxHeight-boxHeight/5);
			}
		}

		for(int c = 1; c < used.size(); c ++) {
			g2.setStroke(new BasicStroke(boxHeight/5));
			g2.setColor(new Color(255,0,0,150));
			int x = leftMargin + (used.get(c) % SIZE)*boxWidth + boxWidth/2;
			int y = topMargin + (used.get(c) / SIZE)*boxHeight + boxHeight/2;
			int px = leftMargin + (used.get(c - 1) % SIZE)*boxWidth + boxWidth/2;
			int py = topMargin + (used.get(c - 1) / SIZE)*boxHeight + boxHeight/2;
			g2.drawLine(x, y, px, py);
		}

		elapsed = System.nanoTime() - start;

		wait = targetTime - elapsed/1000000;
		if(wait<0) {
			wait = 0;
		}
		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		timer -= 1.0/60;
		repaint();
	}

	public ArrayList<String> solveBoard(String board) {
		return null;
	}

	public void getDictionary() {
		try {
			File file = new File("enable1.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line;
			while ((line = br.readLine()) != null) {
				dict.add(line);
				for(int i = 1; i < line.length(); i++) {
					String temp = line.substring(0,i);
				}
			}
		} catch(Exception e) {System.out.println(e);}

	}

	public char chooseLetter() {
		double sum = 0;
		for(double x:weights) {
			sum += x;
		}
		double rand = Math.random() * sum;
		double temp = 0;
		for(int i = 0; i < 26; i++) {
			temp += weights[i];
			if(rand < temp) return alph[i];
		}
		return ' ';
	}

	public boolean oneHas(HashSet<String> a, String b) {
		for(String c: a) {
			if(c.indexOf(b.toLowerCase()) == 0) {
				//System.out.println(c + "!!!");
				return true;
			}
		}
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(dict.contains(selString.toLowerCase()) && selString.length() >= 3 && !usedWords.contains(selString)) {
			points += pKey[selString.length()-3];
			usedWords.add(selString);
			solutions.remove(selString.toLowerCase());
		}
		selString = "";
		used.clear();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int X = e.getX();
		int Y = e.getY();
		int row = (Y - topMargin)/boxHeight;
		int col = (X - leftMargin)/boxWidth;

		int buffer = (int) (boxHeight/10);
		int posX = X-col*boxWidth-leftMargin;
		int posY = Y-row*boxHeight-topMargin;

		if(!(row < SIZE && col < SIZE && row >=0 && col >=0)) return;
		if(!(posX < (boxWidth-buffer) && posX > buffer && posY < (boxHeight-buffer) && posY > buffer)) return;

		if(used.size() > 0) {
			int pr = used.get(used.size()-1)/SIZE, pc = used.get(used.size()-1)%SIZE;
			if(!(Math.abs(row-pr) <=1 && Math.abs(col - pc) <= 1)) return;
		}

		if(!used.contains(col+row*SIZE)) {
			selString += letters[col][row];
			used.add(col+row*SIZE);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
