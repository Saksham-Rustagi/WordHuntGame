package stuff;

import javax.swing.JFrame;

public class wordHuntViewer {

	public static void main(String[] args) {
//		char[][] g = {	{'a','g','h','i'},
//						{'a','g','h','i'},
//						{'a','g','h','i'},
//						{'a','g','h','i'},
//		}; 
//		Boggle.solve(g);
		
		JFrame frame = new JFrame();

		frame.setTitle("GamePigeon");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Board component = new Board();
		frame.add(component);
		
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);		
		
	}

}
