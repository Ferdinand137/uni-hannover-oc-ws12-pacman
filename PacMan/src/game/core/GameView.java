/*
 * Implementation of "Ms Pac-Man" for the "Ms Pac-Man versus Ghost Team Competition", brought
 * to you by Philipp Rohlfshagen, David Robles and Simon Lucas of the University of Essex.
 *
 * www.pacman-vs-ghosts.net
 *
 * Code written by Philipp Rohlfshagen, based on earlier implementations of the game by
 * Simon Lucas and David Robles.
 *
 * You may use and distribute this code freely for non-commercial purposes. This notice
 * needs to be included in all distributions. Deviations from the original should be
 * clearly documented. We welcome any comments and suggestions regarding the code.
 */
package game.core;

import gui.MainFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

@SuppressWarnings("serial")
public final class GameView extends JComponent
{
	private static String pathImages="/images/";
	private static String[] mazes={"maze-a.png","maze-b.png","maze-c.png","maze-d.png"};

	private final int MAG=2;
	private int pacManDir=G.INITIAL_PAC_DIR;
	public static boolean isVisible=false;

    private final _G_ game;
    private final BufferedImage[][] pacmanImgs=new BufferedImage[4][3];
    private final BufferedImage[][][] ghostsImgs=new BufferedImage[6][4][2];
    private final BufferedImage[] images;

    //for debugging/illustration purposes only: draw colors in the maze to check whether controller is working
    //correctly or not; can draw squares and lines (see NearestPillPacManVS for demonstration).
    public static ArrayList<DebugPointer> debugPointers=new ArrayList<DebugPointer>();
    public static ArrayList<DebugLine> debugLines=new ArrayList<DebugLine>();

    private MainFrame frame;
    private Graphics bufferGraphics;
    private Image offscreen;

    public GameView(final _G_ game)
    {
        this.game=game;
        images=loadImages();
        isVisible=true;

        pacmanImgs[G.UP][0]=getImage("mspacman-up-normal.png");
        pacmanImgs[G.UP][1]=getImage("mspacman-up-open.png");
        pacmanImgs[G.UP][2]=getImage("mspacman-up-closed.png");
        pacmanImgs[G.RIGHT][0]=getImage("mspacman-right-normal.png");
        pacmanImgs[G.RIGHT][1]=getImage("mspacman-right-open.png");
        pacmanImgs[G.RIGHT][2]=getImage("mspacman-right-closed.png");
        pacmanImgs[G.DOWN][0]=getImage("mspacman-down-normal.png");
        pacmanImgs[G.DOWN][1]=getImage("mspacman-down-open.png");
        pacmanImgs[G.DOWN][2]=getImage("mspacman-down-closed.png");
        pacmanImgs[G.LEFT][0]=getImage("mspacman-left-normal.png");
        pacmanImgs[G.LEFT][1]=getImage("mspacman-left-open.png");
        pacmanImgs[G.LEFT][2]=getImage("mspacman-left-closed.png");

        ghostsImgs[0][G.UP][0]=getImage("blinky-up-1.png");
        ghostsImgs[0][G.UP][1]=getImage("blinky-up-2.png");
        ghostsImgs[0][G.RIGHT][0]=getImage("blinky-right-1.png");
        ghostsImgs[0][G.RIGHT][1]=getImage("blinky-right-2.png");
        ghostsImgs[0][G.DOWN][0]=getImage("blinky-down-1.png");
        ghostsImgs[0][G.DOWN][1]=getImage("blinky-down-2.png");
        ghostsImgs[0][G.LEFT][0]=getImage("blinky-left-1.png");
        ghostsImgs[0][G.LEFT][1]=getImage("blinky-left-2.png");

        ghostsImgs[1][G.UP][0]=getImage("pinky-up-1.png");
        ghostsImgs[1][G.UP][1]=getImage("pinky-up-2.png");
        ghostsImgs[1][G.RIGHT][0]=getImage("pinky-right-1.png");
        ghostsImgs[1][G.RIGHT][1]=getImage("pinky-right-2.png");
        ghostsImgs[1][G.DOWN][0]=getImage("pinky-down-1.png");
        ghostsImgs[1][G.DOWN][1]=getImage("pinky-down-2.png");
        ghostsImgs[1][G.LEFT][0]=getImage("pinky-left-1.png");
        ghostsImgs[1][G.LEFT][1]=getImage("pinky-left-2.png");

        ghostsImgs[2][G.UP][0]=getImage("inky-up-1.png");
        ghostsImgs[2][G.UP][1]=getImage("inky-up-2.png");
        ghostsImgs[2][G.RIGHT][0]=getImage("inky-right-1.png");
        ghostsImgs[2][G.RIGHT][1]=getImage("inky-right-2.png");
        ghostsImgs[2][G.DOWN][0]=getImage("inky-down-1.png");
        ghostsImgs[2][G.DOWN][1]=getImage("inky-down-2.png");
        ghostsImgs[2][G.LEFT][0]=getImage("inky-left-1.png");
        ghostsImgs[2][G.LEFT][1]=getImage("inky-left-2.png");

        ghostsImgs[3][G.UP][0]=getImage("sue-up-1.png");
        ghostsImgs[3][G.UP][1]=getImage("sue-up-2.png");
        ghostsImgs[3][G.RIGHT][0]=getImage("sue-right-1.png");
        ghostsImgs[3][G.RIGHT][1]=getImage("sue-right-2.png");
        ghostsImgs[3][G.DOWN][0]=getImage("sue-down-1.png");
        ghostsImgs[3][G.DOWN][1]=getImage("sue-down-2.png");
        ghostsImgs[3][G.LEFT][0]=getImage("sue-left-1.png");
        ghostsImgs[3][G.LEFT][1]=getImage("sue-left-2.png");

        ghostsImgs[4][0][0]=getImage("edible-ghost-1.png");
        ghostsImgs[4][0][1]=getImage("edible-ghost-2.png");
        ghostsImgs[5][0][0]=getImage("edible-ghost-blink-1.png");
        ghostsImgs[5][0][1]=getImage("edible-ghost-blink-2.png");
    }

    ////////////////////////////////////////
    ////// Visual aids for debugging ///////
    ////////////////////////////////////////

    //Adds a node to be highlighted using the color specified
    //NOTE: This won't do anything in the competition but your code will still work
    public synchronized static void addPoints(final Game game,final Color color,final int... nodeIndices)
    {
    	if(isVisible) {
			for (final int nodeIndice : nodeIndices) {
				final DebugPointer dp = new DebugPointer(game.getX(nodeIndice),game.getY(nodeIndice),color);
				assert dp != null;
				debugPointers.add(dp);
			}
		}
    }

    //Adds a set of lines to be drawn using the color specified (fromNnodeIndices.length must be equals toNodeIndices.length)
    //NOTE: This won't do anything in the competition but your code will still work
    public synchronized static void addLines(final Game game,final Color color,final int[] fromNnodeIndices,final int[] toNodeIndices)
    {
    	if(isVisible) {
			for(int i=0;i<fromNnodeIndices.length;i++) {
				debugLines.add(new DebugLine(game.getX(fromNnodeIndices[i]),game.getY(fromNnodeIndices[i]),game.getX(toNodeIndices[i]),game.getY(toNodeIndices[i]),color));
			}
		}
    }

    //Adds a line to be drawn using the color specified
    //NOTE: This won't do anything in the competition but your code will still work
    public synchronized static void addLines(final Game game,final Color color,final int fromNnodeIndex,final int toNodeIndex)
    {
    	if(isVisible) {
			debugLines.add(new DebugLine(game.getX(fromNnodeIndex),game.getY(fromNnodeIndex),game.getX(toNodeIndex),game.getY(toNodeIndex),color));
		}
    }

    private void drawDebugInfo()
    {
    	assert bufferGraphics != null;
    	assert debugPointers != null;

    	for(int i=0;i<debugPointers.size();i++)
    	{
    		final DebugPointer dp=debugPointers.get(i);
    		if(dp == null) {
    			System.err.println("i: " + i);
    			System.err.println("size: " + debugPointers.size());
    			System.err.println("dp: " + dp);
    			assert dp != null;
    		}
    		else {
	    		bufferGraphics.setColor(dp.color);
	    		bufferGraphics.fillRect(dp.x*MAG+1,dp.y*MAG+5,10,10);
    		}
    	}

    	for(int i=0;i<debugLines.size();i++)
    	{
    		final DebugLine dl=debugLines.get(i);
    		bufferGraphics.setColor(dl.color);
    		bufferGraphics.drawLine(dl.x1*MAG+5,dl.y1*MAG+10,dl.x2*MAG+5,dl.y2*MAG+10);
    	}

    	debugPointers.clear();
    	debugLines.clear();
    }
    ////////////////////////////////////////
    ////// Visual aids for debugging ///////
    ////////////////////////////////////////

    @Override
	public void paintComponent(final Graphics g)
    {
    	if(offscreen==null)
    	{
    		offscreen=createImage(this.getPreferredSize().width,this.getPreferredSize().height);
    		bufferGraphics=offscreen.getGraphics();
    	}

        drawMaze();
        drawDebugInfo();	//this will be used during testing only and will be disabled in the competition itself
        drawPills();
        drawPowerPills();
        drawPacMan();
        drawGhosts();
        drawLives();
        drawGameInfo();

        if(game.gameOver()) {
			drawGameOver();
		}

        g.drawImage(offscreen,0,0,this);
    }

    private void drawMaze()
    {
    	bufferGraphics.setColor(Color.BLACK);
    	bufferGraphics.fillRect(0,0,game.getWidth()*MAG,game.getHeight()*MAG+20);

        if(images[game.getCurMaze()]!=null) {
			bufferGraphics.drawImage(images[game.getCurMaze()],2,6,null);
		}
    }

    private void drawPills()
    {
        final int[] pillIndices=game.getPillIndices();

        bufferGraphics.setColor(Color.white);

        for(int i=0;i<pillIndices.length;i++)
        	if(game.checkPill(i)) {
				bufferGraphics.fillOval(game.getX(pillIndices[i])*MAG+4,game.getY(pillIndices[i])*MAG+8,3,3);
			}
    }

    private void drawPowerPills()
    {
          final int[] powerPillIndices=game.getPowerPillIndices();

          bufferGraphics.setColor(Color.white);

          for(int i=0;i<powerPillIndices.length;i++)
          	if(game.checkPowerPill(i)) {
				bufferGraphics.fillOval(game.getX(powerPillIndices[i])*MAG+1,game.getY(powerPillIndices[i])*MAG+5,8,8);
			}
    }

    private void drawPacMan()
    {
    	final int pacLoc=game.getCurPacManLoc();
    	final int pacDir=game.getCurPacManDir();

    	if(pacDir>=0 && pacDir<4) {
			pacManDir=pacDir;
		}

    	bufferGraphics.drawImage(pacmanImgs[pacManDir][game.getTotalTime()%6/2],game.getX(pacLoc)*MAG-1,game.getY(pacLoc)*MAG+3,null);
    }

    private void drawGhosts()
    {
    	for(int index=0;index<G.NUM_GHOSTS;index++)
    	{
	    	final int loc=game.getCurGhostLoc(index);
	    	final int x=game.getX(loc);
	    	final int y=game.getY(loc);

	    	if(game.getEdibleTime(index)>0)
	    	{
	    		if(game.getEdibleTime(index)<_G_.EDIBLE_ALERT && game.getTotalTime()%6/3==0) {
					bufferGraphics.drawImage(ghostsImgs[5][0][game.getTotalTime()%6/3],x*MAG-1,y*MAG+3,null);
				} else {
					bufferGraphics.drawImage(ghostsImgs[4][0][game.getTotalTime()%6/3],x*MAG-1,y*MAG+3,null);
				}
	    	}
	    	else
	    	{
	    		if(game.getLairTime(index)>0) {
					bufferGraphics.drawImage(ghostsImgs[index][G.UP][game.getTotalTime()%6/3],x*MAG-1+index*5,y*MAG+3,null);
				} else {
					bufferGraphics.drawImage(ghostsImgs[index][game.getCurGhostDir(index)][game.getTotalTime()%6/3],x*MAG-1,y*MAG+3,null);
				}
	        }
    	}
    }

    private void drawLives()
    {
    	for(int i=0;i<game.getLivesRemaining()-1;i++) {
			bufferGraphics.drawImage(pacmanImgs[G.RIGHT][0],210-30*i/2,260,null);
		}
    }

    private void drawGameInfo()
    {
    	bufferGraphics.setColor(Color.WHITE);
    	bufferGraphics.drawString("S: ",4,271);
    	bufferGraphics.drawString(""+game.getScore(),16,271);
    	bufferGraphics.drawString("L: ",78,271);
    	bufferGraphics.drawString(""+(game.getCurLevel()+1),90,271);
    	bufferGraphics.drawString("T: ",116,271);
    	bufferGraphics.drawString(""+game.getLevelTime(),129,271);
    }

    private void drawGameOver()
    {
    	bufferGraphics.setColor(Color.WHITE);
    	bufferGraphics.drawString("Game Over",80,150);
    }

    @Override
	public Dimension getPreferredSize()
    {
        return new Dimension(game.getWidth()*MAG,game.getHeight()*MAG+20);
    }

    private BufferedImage[] loadImages()
    {
        final BufferedImage[] images=new BufferedImage[4];

        for(int i=0;i<images.length;i++) {
			images[i]=getImage(mazes[i]);
		}

        return images;
    }

    private BufferedImage getImage(final String fileName)
    {
        BufferedImage image=null;

        try
        {
        	//APPLET
        	image=ImageIO.read(this.getClass().getResourceAsStream(pathImages+fileName));
        	//APPLICATION
//        	image=ImageIO.read(new File(pathImages+System.getProperty("file.separator")+fileName));
        }
        catch(final IOException e)
        {
            e.printStackTrace();
        }

        return image;
    }

    public GameView showGame()
    {
    	this.frame = new MainFrame(this);

        //just wait for a bit for player to be ready
        try{Thread.sleep(2000);}catch(final Exception e){/*blub*/}

        return this;
    }


    public MainFrame getMainFrame(){
    	return frame;
    }

    private static class DebugPointer
    {
    	public int x,y;
    	public Color color;

    	public DebugPointer(final int x,final int y,final Color color)
    	{
    		this.x=x;
    		this.y=y;
    		this.color=color;
    	}
    }

    private static class DebugLine
    {
    	public int x1,y1,x2,y2;
    	public Color color;

    	public DebugLine(final int x1,final int y1,final int x2,final int y2,final Color color)
    	{
    		this.x1=x1;
    		this.y1=y1;
    		this.x2=x2;
    		this.y2=y2;
    		this.color=color;
    	}
    }

}