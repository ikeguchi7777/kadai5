package GUI;
import javax.swing.JFrame;

class FrameBase extends JFrame{
	public FrameBase(String title,int x,int y,int width,int height) {
		setTitle(title);
	    setBounds(x, y, width, height);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public FrameBase(String title,int width,int height) {
		setTitle(title);
	    setSize(width, height);
	    setLocationRelativeTo(null);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}