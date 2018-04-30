package game2048;
import javax.swing.JFrame;
import javax.swing.SwingConstants;


public class Run2048{
	public static void main(String [] args){
		JFrame frame = new JFrame("2048");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new GUI2048());
        frame.setSize(2000,1800);
		frame.setVisible(true);
	}
}




