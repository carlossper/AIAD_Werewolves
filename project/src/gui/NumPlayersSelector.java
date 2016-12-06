package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class NumPlayersSelector extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private JLabel numWerewolvesLabel = new JLabel("Werewolves(30%): 1");
	private JSlider numPlayersSlider = new JSlider();
	
	/**
	 * Create the dialog.
	 */
	public NumPlayersSelector() {
		//dialog settings
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setTitle("Select number of players");
		setBounds(100, 100, 300, 150);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		//label
		JLabel numPlayersLabel = new JLabel("Number of players:");
		numPlayersLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPanel.add(numPlayersLabel, BorderLayout.NORTH);
		numPlayersSlider.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
			}
		});
		
		//slider
		numPlayersSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					numWerewolvesLabel.setText("Werewolves(30%): "+(int)Math.floor(0.3*(int)source.getValue()));
				}
			}
		});
		numPlayersSlider.setValue(5);
		numPlayersSlider.setSnapToTicks(true);
		numPlayersSlider.setPaintTicks(true);
		numPlayersSlider.setPaintLabels(true);
		numPlayersSlider.setMajorTickSpacing(5);
		numPlayersSlider.setMinorTickSpacing(1);
		numPlayersSlider.setMinimum(5);
		numPlayersSlider.setMaximum(30);
		contentPanel.add(numPlayersSlider, BorderLayout.CENTER);
		
		//buttonPane
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		numWerewolvesLabel.setHorizontalAlignment(SwingConstants.LEFT);
		buttonPane.add(numWerewolvesLabel);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	}

	public int getValue() {
		return numPlayersSlider.getValue();
	}
	
}
