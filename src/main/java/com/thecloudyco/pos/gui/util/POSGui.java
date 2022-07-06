package com.thecloudyco.pos.gui.util;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.HttpUtil;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.override.ent.ManagerProfile;
import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.Register;
import com.thecloudyco.pos.config.ConfigFile;
import com.thecloudyco.pos.user.Operator;

public class POSGui {

	private JFrame frame;
	private static POSGui window;
	
	private JLabel lbFirstHead;
	private JLabel lbSecondHead;
	
	private int loginState = 0;
	
	private String operatorId;
	private String password;
	
	private static ConfigFile theConfig;

	/**
	 * Launch the application.
	 */
	public static void main() {
		
		theConfig = Main.getConfig();
		
		try {
			window = new POSGui();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static POSGui get() {
		return window;
	}

	/**
	 * Create the application.
	 */
	public POSGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 721, 458);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(splitPane, BorderLayout.NORTH);
		
		lbFirstHead = new JLabel("** CLOSED **");
		lbFirstHead.setFont(new Font("Consolas", Font.PLAIN, 30));
		lbFirstHead.setHorizontalAlignment(SwingConstants.CENTER);
		splitPane.setLeftComponent(lbFirstHead);
		
		lbSecondHead = new JLabel(" ");
		lbSecondHead.setFont(new Font("Consolas", Font.PLAIN, 30));
		lbSecondHead.setHorizontalAlignment(SwingConstants.CENTER);
		splitPane.setRightComponent(lbSecondHead);
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		frame.getContentPane().add(panel, BorderLayout.WEST);
		panel.setLayout(new GridLayout(0, 3, 0, 0));
		
		JButton btnKey1 = new JButton("1");
		panel.add(btnKey1);
		
		btnKey1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "1");
			}
		});
		
		JButton btnKey2 = new JButton("2");
		panel.add(btnKey2);
		
		btnKey2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "2");
			}
		});
		
		JButton btnKey3 = new JButton("3");
		panel.add(btnKey3);
		
		btnKey3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "3");
			}
		});
		
		JButton btnKey4 = new JButton("4");
		panel.add(btnKey4);
		
		btnKey4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "4");
			}
		});
		
		JButton btnKey5 = new JButton("5");
		panel.add(btnKey5);
		
		btnKey5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "5");
			}
		});
		
		JButton btnKey6 = new JButton("6");
		panel.add(btnKey6);
		
		btnKey6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "6");
			}
		});
		
		JButton btnKey7 = new JButton("7");
		panel.add(btnKey7);
		
		btnKey7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "7");
			}
		});
		
		JButton btnKey8 = new JButton("8");
		panel.add(btnKey8);
		
		btnKey8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "8");
			}
		});
		
		JButton btnKey9 = new JButton("9");
		panel.add(btnKey9);
		
		btnKey9.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "9");
			}
		});
		
		JButton btnKeyLOGIN = new JButton("LOGIN");
		panel.add(btnKeyLOGIN);
		
		btnKeyLOGIN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ManagerAPI mAPI = new ManagerAPI();
				if(loginState == 0) {
					
					operatorId = lbSecondHead.getText().replaceAll("\\s", "");
					boolean flag = false;

					try {
						flag = mAPI.isAuthorized(OverrideType.CASHIER, operatorId);
					} catch (Exception exx) {}
					
					if(!flag) {
						setHeaderOne("** LOGIN NOT AUTHORIZED **");
					} else {
						setHeaderTwo(" ");
						loginState = 1;
						setHeaderOne("** ENTER PASSWORD **");
					}
					return;
				}
				if(loginState == 1) {
					password = lbSecondHead.getText();
					
					ManagerProfile profile = null;
					try {
						profile = mAPI.getProfile(operatorId);
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					try {
						if(mAPI.authenticate(operatorId, password)) {
							Register.access().setLoggedIn(new Operator(profile.getFirstName(), profile.getLastName(), operatorId));
							setHeaderTwo(" ");
							loginState = 2;
							
							Operator op = Register.access().getLoggedIn();
							//setHeaderOne(HttpUtil.get(theConfig.getControllerAddress() + "/?action=info&data=WELCOME_MESSAGE_LINE_ONE"));
							//setHeaderTwo(HttpUtil.get(theConfig.getControllerAddress() + "/?action=info&data=WELCOME_MESSAGE_LINE_TWO"));
						} else {
							setHeaderOne("** CHECK PASSWORD **");
							setHeaderTwo("");
							
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					return;
				}
			}
		});
		
		JButton btnKey0 = new JButton("0");
		panel.add(btnKey0);
		
		btnKey0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo(lbSecondHead.getText() + "0");
			}
		});
		
		JButton btnClearButton = new JButton("CLEAR");
		panel.add(btnClearButton);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JLabel lbFooterDate = new JLabel("[DATE]");
		lbFooterDate.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel label = new JLabel("");
		
		JLabel lbFooterTime = new JLabel("[TIME]");
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_1.add(lbFooterDate);
		
		JLabel label_1 = new JLabel("");
		panel_1.add(label_1);
		
		JLabel label_2 = new JLabel("");
		panel_1.add(label_2);
		panel_1.add(label);
		panel_1.add(lbFooterTime);
		
		JLabel lbFooterOperator = new JLabel("[OPERATOR NAME]");
		panel_1.add(lbFooterOperator);
		
		JPanel panel_2 = new JPanel();
		frame.getContentPane().add(panel_2, BorderLayout.EAST);
		panel_2.setLayout(new CardLayout(0, 0));
		
		JLabel lbSideBalance = new JLabel("Balance: $0.00");
		lbSideBalance.setFont(new Font("Verdana", Font.PLAIN, 17));
		panel_2.add(lbSideBalance, "name_1345918104759700");
		
		// a separate thread so we can keep all the information in the footer, up to date at all times
		Thread footerThread = new Thread() {
			public void run() {
				for(int i = 0; i < 5; i++) {
					
					// The currently logged in operator
					if(Register.access().isLoggedIn()) {
						lbFooterOperator.setText(Register.access().getLoggedIn().getFirstName().toUpperCase());
					} else {
						lbFooterOperator.setText(" ");
					}
					
					// The current date that will be printed on the transaction
					long millis=System.currentTimeMillis();  
					java.sql.Date date=new java.sql.Date(millis);
					lbFooterDate.setText(date.toString());
					
					// And the current time that will be printed on the transaction
					SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
				    Date time = new Date();
				    lbFooterTime.setText(formatter.format(time));
				    
				    // Also keep the balance updated
				    String bal = String.valueOf(Register.access().getBalance());
				    String bal2 = bal.substring(bal.indexOf("."), bal.length());
				    if(bal2.length() == 2) {
				    	bal = bal + "0";
				    }
				    
				    lbSideBalance.setText("Balance: $" + bal);
					
					i = 0;
				}
			}
		};
		footerThread.start();
		
		btnClearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeaderTwo("");
			}
		});
	}
	
	public void setHeaderOne(String text) {
		lbFirstHead.setText(text);
	}
	
	public void setHeaderTwo(String text) {
		lbSecondHead.setText(text);
	}
	
	public static ConfigFile getConfig() {
		return theConfig;
	}

}
