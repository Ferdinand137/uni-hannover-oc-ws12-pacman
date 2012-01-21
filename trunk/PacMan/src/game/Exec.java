package game;

import game.controllers.GhostController;
import game.controllers.Human;
import game.controllers.PacManController;
import game.core.G;
import game.core.GameView;
import game.core._G_;
import game.player.ghost.lcs.LcsGhost;
import game.player.pacman.AbstractHuman;
import game.player.pacman.LcsPacMan;
import gui.AbstractGhost;
import gui.AbstractPlayer;
import gui.Configuration;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

/*
 * This class may be used to execute the game in timed or un-timed modes, with or without
 * visuals. Competitors should implement their controllers in game.entries.ghosts and
 * game.entries.pacman respectively. The skeleton classes are already provided. The package
 * structure should not be changed (although you may create sub-packages in these packages).
 */
public class Exec {
	private GameView gv = null;
	private Thread thread = null;

	// Several options are listed - simply remove comments to use the option you
	// want
	public static void main(final String[] args) {
		final Exec exec = new Exec();

		// this can be used for numerical testing (non-visual, no delays)
		// exec.runExperiment(new RandomPacMan(),new
		// AttractRepelGhosts(true),100);

		// run game without time limits (un-comment if required)
		// exec.runGame(new RandomPacMan(),new RandomGhosts(),true,G.DELAY);

		// run game with time limits (un-comment if required)
		// exec.runGameTimed(new Human(),new AttractRepelGhosts(true),true);
		// run game with time limits. Here NearestPillPacManVS is chosen to
		// illustrate how to use graphics for debugging/information purposes
//		exec.runGameTimed(new game.player.pacman.Human(), new AttractRepelGhosts(
//				false), true);

		// this allows you to record a game and replay it later. This could be
		// very useful when
		// running many games in non-visual mode - one can then pick out those
		// that appear irregular
		// and replay them in visual mode to see what is happening.
		// exec.runGameTimedAndRecorded(new Human(),new
		// AttractRepelGhosts(false),true,"human-v-Legacy2.txt");
		// exec.replayGame("human-v-Legacy2.txt");

		// this allows to select a player from GUI, the players must be save in
		// the package game.player.player and the ghosts in the package
		// game.player.ghosts
		exec.runGameMainFrame();
	}

	private void runGameMainFrame() {
		game = new _G_();
		game.newGame();

		gv = new GameView(game).showGame();
		gv.getMainFrame().getButton()
				.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(final java.awt.event.ActionEvent evt) {
						try {
							startButtonActionPerformed(evt);
						} catch (final InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (final IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
		});
	}

	private void startButtonActionPerformed(final ActionEvent evt) throws InstantiationException, IllegalAccessException {
		final AbstractPlayer pacManController = gv.getMainFrame().getSelectedPacMan();
		final AbstractGhost ghostController = gv.getMainFrame().getSelectedGhost();

		if(thread != null && thread.isAlive()){
			game.setGameOver(true);
			gv.getMainFrame().getButton().setText("Start");
			return;
		}


		final int trials = gv.getMainFrame().getTrials();
		if(trials > 1){
			runExperiment(pacManController, ghostController, trials);
		}

		pacMan = new PacMan(pacManController);
		ghosts = new Ghosts(ghostController);

		if (gv.getMainFrame().getSelectedPacMan() instanceof game.player.pacman.AbstractHuman){
			final Human con = new Human();
			pacMan = new PacMan(con);
			gv.getMainFrame().getButton().addKeyListener(con);
		}


		if(game.gameOver()){
			game.newGame();
		}

		this.thread = new Thread(){

			@Override
			public void run(){
				while (!game.gameOver()) {
					pacMan.alert();
					ghosts.alert();

					try {
						Thread.sleep(G.DELAY);
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}

					game.advanceGame(pacDir, ghostDirs);

					gv.repaint();
				}
				pacMan.kill();
				ghosts.kill();
				gv.getMainFrame().getButton().setText("Start");
			}
		};
		this.thread.start();
		gv.getMainFrame().getButton().setText("Stop");
	}

	protected int pacDir;
	protected int[] ghostDirs;
	protected _G_ game;
	protected PacMan pacMan;
	protected Ghosts ghosts;
	protected boolean pacmanPlayed, ghostsPlayed;

	/*
	 * For running multiple games without visuals. This is useful to get a good
	 * idea of how well a controller plays against a chosen opponent: the random
	 * nature of the game means that performance can vary from game to game.
	 * Running many games and looking at the average score (and standard
	 * deviation/error) helps to get a better idea of how well the controller is
	 * likely to do in the competition.
	 */
	public void runExperiment(AbstractPlayer pacManController,
			final AbstractGhost ghostController, final int trials) throws InstantiationException, IllegalAccessException {
		double avgScore = 0;

		final _G_ gameTmp = new _G_();
		// int training = 1;

		final ArrayList<AbstractGhost> allGhosts = new ArrayList<AbstractGhost>();
		for (final Class<? extends AbstractGhost> ghostClass : Configuration.getGhosts()) {
			if(ghostClass != LcsGhost.class) {
				allGhosts.add(ghostClass.newInstance());
			}
		}

		final ArrayList<AbstractPlayer> allPacmans = new ArrayList<AbstractPlayer>();
		for (final Class<? extends AbstractPlayer> ghostClass : Configuration.getPlayers()) {
			if(ghostClass != LcsPacMan.class && ghostClass != AbstractHuman.class) {
				allPacmans.add(ghostClass.newInstance());
			}
		}

		if(pacManController instanceof LcsPacMan) {
			((LcsPacMan) pacManController).trainingBegin(trials);
		}

		if(ghostController instanceof LcsGhost) {
			((LcsGhost) ghostController).trainingBegin(trials);
		}


		// bugfix by AlexL
		GameView.isVisible = false;

		for (int i = 0; i < trials; i++) {

			//ghostController = allGhosts.get(i % allGhosts.size());
			pacManController = allPacmans.get(i % allPacmans.size());
			//System.out.print("simulating round with: " + ghostController.getGhostGroupName() + ".... ");
			//System.out.print("simulating round with: " + pacManController.getGroupName() + ".... ");

			gameTmp.newGame();

			while (!gameTmp.gameOver()) {
				final long due = System.currentTimeMillis() + G.DELAY;
				gameTmp.advanceGame(pacManController.getAction(gameTmp.copy(), due),
						ghostController.getActions(gameTmp.copy(), due));
			}

			//System.out.println(" -> " + gameTmp.getScore());
			avgScore += gameTmp.getScore();
			//System.out.println("Training "+training+++" Punkte: "+gameTmp.getScore());
			System.out.print('.');

			if(pacManController instanceof LcsPacMan) {
				((LcsPacMan) pacManController).trainingRoundOver(i, trials, gameTmp);
			}
			if(ghostController instanceof LcsGhost) {
				((LcsGhost) ghostController).trainingRoundOver(i, trials, gameTmp);
			}
		}
		if(pacManController instanceof LcsPacMan) {
			((LcsPacMan) pacManController).trainingOver(trials);
		}

		if(ghostController instanceof LcsGhost) {
			((LcsGhost) ghostController).trainingOver(trials);
		}

		System.out.println("Gesamtpunkte/Versuche: "+avgScore+"/"+trials+" "+avgScore / trials);

		// bugfix by AlexL
		GameView.isVisible = true;
	}


	// sets the latest direction to take for each game step (if controller
	// replies in time)
	public void setGhostDirs(final int[] ghostDirs) {
		this.ghostDirs = ghostDirs;
		this.ghostsPlayed = true;
	}

	// sets the latest direction to take for each game step (if controller
	// replies in time)
	public void setPacDir(final int pacDir) {
		this.pacDir = pacDir;
		this.pacmanPlayed = true;
	}

	/*
	 * Wraps the controller in a thread for the timed execution. This class then
	 * updates the directions for Exec to parse to the game.
	 */
	public class PacMan extends Thread {
		private final PacManController pacMan;
		private boolean alive;

		public PacMan(final PacManController pacMan) {
			this.pacMan = pacMan;
			alive = true;
			start();
		}

		public synchronized void kill() {
			alive = false;
			notify();
		}

		public synchronized void alert() {
			notify();
		}

		@Override
		public synchronized void run() {
			while (alive) {
				try {
					synchronized (this) {
						wait();
					}

					setPacDir(pacMan.getAction(game.copy(),
							System.currentTimeMillis() + G.DELAY));
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * Wraps the controller in a thread for the timed execution. This class then
	 * updates the directions for Exec to parse to the game.
	 */
	public class Ghosts extends Thread {
		private final GhostController ghosts;
		private boolean alive;

		public Ghosts(final GhostController ghosts) {
			this.ghosts = ghosts;
			alive = true;
			start();
		}

		public synchronized void kill() {
			alive = false;
			notify();
		}

		public synchronized void alert() {
			notify();
		}

		@Override
		public synchronized void run() {
			while (alive) {
				try {
					synchronized (this) {
						wait();
					}

					setGhostDirs(ghosts.getActions(game.copy(),
							System.currentTimeMillis() + G.DELAY));
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}