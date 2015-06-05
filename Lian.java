import javax.swing.*; 
import java.awt.*; 
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

public class Lian implements ActionListener {
	String picDir = "pics/";
	int nrow = 6, ncol = 8, n = 21;
	int imageWidth = 100, imageHeight = 80;
	JFrame mainFrame; 
	Container thisContainer; 
	JPanel centerPanel,southPanel,northPanel; 
	JButton[][] diamondsButton = new JButton[nrow][ncol]; 
	JButton exitButton,resetButton,newGameButton; 
	int[][] grid = new int[nrow][ncol];
	boolean[][] notCanceled = new boolean[nrow+2][ncol+2]; 
	ArrayList<ImageIcon> iconList = new ArrayList<ImageIcon>();
	int x=-1,y; 

	public static void main (String[] args) {
		Lian llk = new Lian();
		llk.readPics();
		llk.generate();
		llk.init();
		llk.newGame();
	}

	public void readPics() {
		File[] files = new File(picDir).listFiles();
		for (File file : files) {
			if (file.isFile() && file.getName().substring(file.getName().lastIndexOf(".")).equals(".jpg")) {
				try {
					Image img = ImageIO.read(getClass().getResource(picDir+file.getName()));
    				Image resizedImg = img.getScaledInstance(imageWidth, imageHeight, java.awt.Image.SCALE_SMOOTH);
					iconList.add(new ImageIcon(resizedImg));
				} catch (IOException e) {}
			}
		}
	}

	public void init(){ 
		mainFrame=new JFrame("端午连连看"); 
		thisContainer = mainFrame.getContentPane(); 
		thisContainer.setLayout(new BorderLayout()); 
		centerPanel=new JPanel(); 
		southPanel=new JPanel(); 
		northPanel=new JPanel(); 
		thisContainer.add(centerPanel,"Center"); 
		thisContainer.add(southPanel,"South"); 
		thisContainer.add(northPanel,"North"); 
		centerPanel.setLayout(new GridLayout(nrow,ncol)); 
		for(int i=0; i<nrow; i++){ 
			for(int j=0; j<ncol; j++){
				diamondsButton[i][j]=new JButton();
				diamondsButton[i][j].addActionListener(this); 
				centerPanel.add(diamondsButton[i][j]); 
				notCanceled[i+1][j+1] = true;
			} 
		}
		exitButton=new JButton("Exit"); 
		exitButton.addActionListener(this); 
		resetButton=new JButton("Reset"); 
		resetButton.addActionListener(this); 
		newGameButton=new JButton("Try Again"); 
		newGameButton.addActionListener(this); 
		southPanel.add(exitButton); 
		southPanel.add(resetButton); 
		southPanel.add(newGameButton); 
		mainFrame.setBounds(200, 200, (int) (1.1*imageWidth*ncol), (int) (1.25*imageHeight*nrow));		 
		mainFrame.setResizable(false);
	}

	public void newGame() {
		generate();
		mainFrame.setVisible(false);
		for(int i=0; i<nrow; i++){ 
			for(int j=0; j<ncol; j++){ 
				try {	
    				diamondsButton[i][j].setIcon(iconList.get(grid[i][j]));
  				} catch (Exception e) {}
				diamondsButton[i][j].setVisible(true);
				notCanceled[i+1][j+1] = true;
			} 
		}
		mainFrame.setVisible(true);
	}

	public void generate() { 
		boolean[][] hasFilled = new boolean[nrow][ncol];
		boolean[] hasPicked = new boolean[iconList.size()];
		int i, j, r;
		int[] sampleList = new int[n];
		int t = 0;
		while (t<n) {
			int d = (int) (Math.random()*iconList.size());
			if (!hasPicked[d]) {
				sampleList[t] = d;
				t++;
				hasPicked[d] = true;
			}
		}
		for (int k=0; k<nrow*ncol/2; k++) {
			r = (int) (Math.random()*n);
			for (int m=0; m<2; m++) {
				do {
					i = (int) (Math.random()*nrow);
					j = (int) (Math.random()*ncol);
				} while (hasFilled[i][j]);
				grid[i][j] = sampleList[r];
				hasFilled[i][j] = true;
			}
		}
	}

	public void reset() {
		boolean[][] newNotCanceled = new boolean[nrow+2][ncol+2];
		int[][] newGrid = new int[nrow][ncol];
		boolean[][] hasFilled = new boolean[nrow][ncol];
		int newi, newj;
		for (int i=0; i<nrow; i++) {
			for (int j=0; j<ncol; j++) {
				if (notCanceled[i+1][j+1]) {
					do {
						newi = (int) (Math.random()*nrow);
						newj = (int) (Math.random()*ncol);
					} while (hasFilled[newi][newj]);
					newGrid[newi][newj] = grid[i][j];
					newNotCanceled[newi+1][newj+1] = true;
					diamondsButton[i][j].setVisible(false);
					hasFilled[newi][newj] = true;
				}
			}
		}
		notCanceled = newNotCanceled;
		grid = newGrid;
		for (int i=0; i<nrow; i++) {
			for (int j=0; j<ncol; j++) {
				if (notCanceled[i+1][j+1]) {
					diamondsButton[i][j].setIcon(iconList.get(grid[i][j]));
					diamondsButton[i][j].setVisible(true);
				}
			}
		}	
	}

	public void actionPerformed(ActionEvent e) { 
		if (e.getSource()==newGameButton)
			newGame(); 
		if (e.getSource()==exitButton) 
			System.exit(0); 
		if (e.getSource()==resetButton) 
			reset();
		for (int i=0; i<nrow; i++){ 
			for (int j=0; j<ncol; j++){ 
				if (e.getSource()==diamondsButton[i][j]) {
					if (x==-1) {
						x = i;
						y = j;
					} else if (!(x==i && y==j)) {
						if (grid[x][y]==grid[i][j] && connected(x+1,y+1,i+1,j+1)) {
							remove(x,y,i,j);
							x = -1;
						} else {
							x = i;
							y = j;
						}
					}
				} 
			} 
		}
	}

	public boolean connected(int i1, int j1, int i2, int j2) {
		if(staightConnected(i1,j1,i2,j2)) return true;
		for(int i=0; i<notCanceled.length; i++) {
			if((i==i1 || !notCanceled[i][j1]) && (i==i2 || !notCanceled[i][j2]) && staightConnected(i,j1,i1,j1) && staightConnected(i,j2,i2,j2) && staightConnected(i,j1,i,j2)) 
				return true;
		}
		for(int j=0; j<notCanceled[0].length; j++) {
			if((j==j1 || !notCanceled[i1][j]) && (j==j2 || !notCanceled[i2][j]) &&staightConnected(i1,j,i1,j1) && staightConnected(i2,j,i2,j2) && staightConnected(i1,j,i2,j)) 
				return true;
		}
		return false;
	}

	public boolean staightConnected(int i1, int j1, int i2, int j2) {
		if (i1!=i2 && j1!=j2)
			return false;
		if (i1==i2)
			for(int k=Math.min(j1,j2)+1; k<Math.max(j1,j2); k++)
				if(notCanceled[i1][k]) 
					return false;
		if (j1==j2)
			for(int k=Math.min(i1,i2)+1; k<Math.max(i1,i2); k++)
				if(notCanceled[k][j1]) 
					return false;
		return true;
	}

	public void remove(int x, int y, int i, int j){ 
		diamondsButton[x][y].setVisible(false);
		diamondsButton[i][j].setVisible(false);
		notCanceled[x+1][y+1] = false;
		notCanceled[i+1][j+1] = false;
	}
} 