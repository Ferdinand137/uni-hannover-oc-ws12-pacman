package gui;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFram.java
 */


import javax.swing.JComponent;
import javax.swing.UIManager;

public class MainFrame extends javax.swing.JFrame {

	private static final long serialVersionUID = -6408345358982189571L;

	public MainFrame(final JComponent component) {
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ex) {
			/*blub*/
		}
        initComponents(component);
        setVisible(true);
    }

	private void initComponents(final JComponent component) {
        java.awt.GridBagConstraints gridBagConstraints;

        comboGhosts = new javax.swing.JComboBox();
        comboPacMan = new javax.swing.JComboBox();
        labelSchritte = new javax.swing.JLabel();
        trials = new javax.swing.JTextField();
        startButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        comboGhosts.setModel(new javax.swing.DefaultComboBoxModel(
				Configuration.getGhostName()));
        comboGhosts.setAlignmentX(1.0F);
        comboGhosts.setAlignmentY(1.0F);
        comboGhosts.setMaximumSize(new java.awt.Dimension(200, 20));
        comboGhosts.setMinimumSize(new java.awt.Dimension(200, 20));
        comboGhosts.setPreferredSize(new java.awt.Dimension(200, 20));
        comboGhosts.setSelectedIndex(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(comboGhosts, gridBagConstraints);

        comboPacMan.setModel(new javax.swing.DefaultComboBoxModel(
				Configuration.getPlayerNames()));
        comboPacMan.setMaximumSize(new java.awt.Dimension(200, 20));
        comboPacMan.setMinimumSize(new java.awt.Dimension(200, 20));
        comboPacMan.setPreferredSize(new java.awt.Dimension(200, 20));
        comboPacMan.setSelectedIndex(3);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        getContentPane().add(comboPacMan, gridBagConstraints);

        labelSchritte.setText("Anzahl der Spiele");
        labelSchritte.setAlignmentX(1.0F);
        labelSchritte.setAlignmentY(0.0F);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(labelSchritte, gridBagConstraints);

        trials.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        trials.setText("1");
        trials.setAlignmentY(0.0F);
        trials.setMaximumSize(new java.awt.Dimension(45, 20));
        trials.setMinimumSize(new java.awt.Dimension(45, 20));
        trials.setPreferredSize(new java.awt.Dimension(45, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        getContentPane().add(trials, gridBagConstraints);

        startButton.setText("Start");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 5);
        getContentPane().add(startButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(component, gridBagConstraints);
        setResizable(false);

        pack();
    }// </editor-fold>

    public javax.swing.JButton getButton() {
		return startButton;
	}

    public AbstractPlayer getSelectedPacMan() {
		return Configuration.getPlayer(comboPacMan.getSelectedIndex());
	}

	public AbstractGhost getSelectedGhost() {
		return Configuration.getGhost(comboGhosts.getSelectedIndex());
	}

	public int getTrials() {
		try {
			return Integer.parseInt(trials.getText());
		} catch (final NumberFormatException e) {
			trials.setText("1");
			return 1;
		}

	}

	public void setTrials(final String i) {
		trials.setText(i);
	}

    // Variables declaration - do not modify
    private javax.swing.JComboBox comboGhosts;
	private javax.swing.JComboBox comboPacMan;
    private javax.swing.JTextField trials;
    private javax.swing.JLabel labelSchritte;
    private javax.swing.JButton startButton;
    // End of variables declaration

}

