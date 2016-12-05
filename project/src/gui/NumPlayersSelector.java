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
import java.awt.Dialog.ModalityType;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class NumPlayersSelector extends JDialog {

	private final JPanel contentPanel = new JPanel();

	private JLabel numWerewolvesLable = new JLabel("Werewolves: 1");
	
	/**
	 * Create the dialog.
	 */
	public NumPlayersSelector() {
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
		{
			JLabel numPlayersLabel = new JLabel("Number of players:");
			numPlayersLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(numPlayersLabel, BorderLayout.NORTH);
		}
		{
			JSlider numPlayersSlider = new JSlider();
			numPlayersSlider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
			        if (!source.getValueIsAdjusting()) {
			        	numWerewolvesLable.setText("Werewolves: "+(int)Math.floor(0.3*(int)source.getValue()));
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
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				numWerewolvesLable.setHorizontalAlignment(SwingConstants.LEFT);
				buttonPane.add(numWerewolvesLable);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
