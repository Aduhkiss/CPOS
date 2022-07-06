package com.thecloudyco.pos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Scanner;

import com.google.gson.Gson;
import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.override.ent.ManagerProfile;
import com.thecloudyco.pos.config.ConfigFile;
import com.thecloudyco.pos.database.AtticusDB;
import com.thecloudyco.pos.items.Item;
import com.thecloudyco.pos.module.CModule;
import com.thecloudyco.pos.module.ModuleManager;
import com.thecloudyco.pos.secure.Secure;
import com.thecloudyco.pos.user.Operator;
import com.thecloudyco.pos.util.ConsoleUtil;
import com.thecloudyco.pos.util.SoundUtils;
import com.thecloudyco.pos.util.StringUtil;
import com.thecloudyco.pos.util.TillContentsUtil;

public class Main {
	
	private static ConfigFile theConfig;
	private static AtticusDB atticus_db;
	
	public static String CON_WELCOME_MESSAGE_ONE;
	public static String CON_WELCOME_MESSAGE_TWO;
	public static String CON_RECIPT_HEADER;
	public static String CON_STARTUP_MESSAGE;

	public static void main(String[] args) throws IOException, SQLException {
		
		ConsoleUtil.Clear();
		Secure.timer = 0;
		
		ConsoleUtil.Print("W024		CONTACTING", "CONTROLLER");
		StringBuilder con = new StringBuilder();
		try {
			File configFile = new File("pos.config");
			Scanner sc = new Scanner(configFile);
			while(sc.hasNextLine()) {
				String data = sc.nextLine();
				con.append(data);
			}
			sc.close();
		} catch(FileNotFoundException ex) {
			System.out.println("Uh oh.... It looks like we were unable to find a config file! Make sure to download a sample from our website, and customize it to you're settings!");
			System.exit(1);
		}
		
		Gson gson = new Gson();
		
		theConfig = gson.fromJson(con.toString(), ConfigFile.class);
		
		// Then also connect with the database and make an instance of AtticusDB so we can communicate with the db directly
		atticus_db = new AtticusDB();
		
		ConsoleUtil.Clear();
		
		ModuleManager.registerModules();
		ManagerAPI mAPI = new ManagerAPI();
		Register r = Register.access();
		r.setBalance(0.00);
		
		ResultSet result = atticus_db.query("SELECT * FROM `sys_config`;");
		while(result.next()) {
			CON_WELCOME_MESSAGE_ONE = result.getString("welcome_message_one");
			CON_WELCOME_MESSAGE_TWO = result.getString("welcome_message_two");
			CON_RECIPT_HEADER = result.getString("recipt_header");
			CON_STARTUP_MESSAGE = result.getString("startupMessage");

			Register.access().TaxPlanA = result.getDouble("taxPlanA");
			Register.access().TaxPlanB = result.getDouble("taxPlanB");
			Register.access().TaxPlanC = result.getDouble("taxPlanC");
			Register.access().TaxPlanD = result.getDouble("taxPlanD");
		}
		
		try {
			TillContentsUtil.getCashTillContents(theConfig.getTerminalNumber());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		
		// Security System (Atticus)
		new Thread() {
			public void run() {
				if(Register.access().isLoggedIn() && !Secure.isSecured() && Register.access().getTransaction() == null) {
					int current = Secure.timer;
					Secure.timer = current + 1;
					//System.out.println(Secure.timer);
					
					if(Secure.timer >= 60) {
						Secure.secureTerminal();
						Operator op = Register.access().getLoggedIn();
						System.out.println(CON_STARTUP_MESSAGE);

						System.out.println("");
						System.out.println("");

						System.out.println("  ***     TERMINAL SECURED      ***");
						System.out.println(op.getOperatorId());
						System.out.println("");
						System.out.println("");
						System.out.println("");
						System.out.println("ENTER PASSWORD TO UNLOCK...");
					}
				}
				try {
					Thread.sleep(1000);
					this.run();
				} catch (InterruptedException | StackOverflowError e) {
					//e.printStackTrace();
				}
				
			}
		}.start();
		
		Scanner sc = new Scanner(System.in);
		
		login(sc, mAPI);
		
		for(int i = 0; i < 200; i++) {
			// Always check to make sure you are logged in
			if(!Register.access().isLoggedIn()) {
				login(sc, mAPI);
			} else {
				
				if(Secure.isSecured()) {
					String input = sc.nextLine();
					Operator op = Register.access().getLoggedIn();
					ManagerProfile profile = null;
					profile = mAPI.getProfile(op.getOperatorId());
					
					// Check if the operator is trying to override
					if(input.equalsIgnoreCase("OVERRIDE") || input.equalsIgnoreCase("SIGNOUT")) {
						
						SoundUtils.beep();
						ConsoleUtil.Clear();
						ConsoleUtil.Print("MO", "REQUIRES MGR OVERRIDE. PLEASE SCAN OVERRIDE CARD");
						System.out.println("\n");
						
						boolean flag = false;
						try {
							flag = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, sc.nextLine());
						} catch (Exception e) {}
						
						if(!flag) {
							ConsoleUtil.Print("ERR", "Not Authorized");
						} else {
							ConsoleUtil.Clear();
							
							try {
								getDB().update("DELETE FROM `active_sessions` WHERE `operator_number` = '" + Register.access().getLoggedIn().getOperatorId() + "' AND `terminal_number` = '" + Main.getConfig().getTerminalNumber() + "';");
							} catch (SQLException e) {
							}
							
							Register.access().setBalance(0.00);
							Register.access().loggedInOperator = null;
						}
						
					} else {
						if(mAPI.authenticate(op, input)) {
							ConsoleUtil.Clear();
							Secure.unlockTerminal();
						} else {
							ConsoleUtil.Print("ERR", "Check Password");
							System.out.println("\n");
							Operator oop = Register.access().getLoggedIn();
							System.out.println(CON_STARTUP_MESSAGE);

							System.out.println("");
							System.out.println("");
							
							System.out.println("  ***     TERMINAL SECURED      ***");
							System.out.println(oop.getOperatorId());
							System.out.println("");
							System.out.println("");
							System.out.println("");
							System.out.println("ENTER PASSWORD TO UNLOCK...");
						}
					}
				} else {

					// Ask for input, then look for ITEMS or UPC's from the backend
					System.out.println("ENTER ITEM:   ");
					String input = sc.nextLine();
					if(Secure.isSecured()) {
						Operator op = Register.access().getLoggedIn();
						ManagerProfile profile = null;
						profile = mAPI.getProfile(op.getOperatorId());
						
						if(mAPI.authenticate(op, input)) {
							ConsoleUtil.Clear();
							Secure.unlockTerminal();
						} else {
							ConsoleUtil.Print("ERR", "Check Password");
							System.out.println("\n");
							Operator oop = Register.access().getLoggedIn();

							System.out.println(CON_STARTUP_MESSAGE);

							System.out.println("  ***     TERMINAL SECURED      ***");
							System.out.println(oop.getOperatorId());
							System.out.println("");
							System.out.println("");
							System.out.println("");
							System.out.println("ENTER PASSWORD TO UNLOCK...");
//							System.out.println("   " + oop.getOperatorId() + "   (" + oop.getFirstName().toUpperCase() + ")");
						}
					} else {
						// Reset the security timer
						Secure.timer = 0;
						
						String[] origin = StringUtil.toArray(input);
						String[] arg = Arrays.copyOfRange(origin, 1, origin.length);
						
						CModule m = ModuleManager.getModule(origin[0].toUpperCase());
						
						if(m == null) {
							boolean found = false;
							ResultSet item_look = atticus_db.query("SELECT * FROM `items` WHERE `upc` = '" + StringUtil.removeLeadingZeroes(origin[0]) + "';");
							while(item_look.next()) {
								found = true;

								double till = TillContentsUtil.getCashTillContents(theConfig.getTerminalNumber());
								if(till >= 1500.00) {
									
									ConsoleUtil.Print("CASH PICKUP", "NEEDED NOW.");
									
								} else {
									Item item = new Item(item_look.getString("upc"), item_look.getString("name"),
											item_look.getDouble("price"), item_look.getInt("custom_price"), item_look.getInt("weight")
											, item_look.getInt("quantity"), item_look.getDouble("price_per_pound"),
											item_look.getInt("transaction_limit"), item_look.getInt("restricted"), item_look.getInt("minimumLevelNeeded"), item_look.getString("taxPlan"), item_look.getString("taxStatus"));
									
									boolean allowed = false;
									boolean overrided = false;
									// If you don't meet the required level in the controller, you will require a manager to override it on
									
//									if(item.isRestricted() != 0) {
//										int age = item.isRestricted();
//
//										System.out.println("PLEASE SCAN CUSTOMER ID / ENTER CUSTOMER DOB [MM/DD/YYYY] / ENTER BYPASS TO CLEAR  ");
//										allowed = false;
//									}
									
									if((item.getMinimumLevelNeeded() > Register.access().getLoggedIn().getOverrideType().getPower())) {
										SoundUtils.beep();
										ConsoleUtil.Print("MO", "RESTRICTED PLU/UPC LIMIT CHECK");
										System.out.println("\n");
										String override = sc.nextLine();
										
										// Then check the override that was scanned
										boolean flag = false;
										try {
											flag = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, override);
										} catch (Exception e) {}
										
										if(!flag) {
											ConsoleUtil.Print("ERROR", "Not Authorized");
										} else {
											allowed = true;
											overrided = true;
											ConsoleUtil.Clear();
										}
									} else {
										allowed = true;
									}
									
									if(allowed) {
										double cPrice = item.getPrice();
										int cQTY = 1;
										double cWeight = 0.00;
										
										if(item.isCustom_price() == 1) {
											ConsoleUtil.Print("ENTER ITEM PRICE", "");
											cPrice = sc.nextDouble();
											
											System.out.print("[" + item.getUpc() + "] " + item.getName() + " | $" + StringUtil.realBalance((cPrice * 1)));
											System.out.println("\n");
											Register.access().addBalance(StringUtil.realBalance((cPrice * 1)));
											item.setMyPrice(StringUtil.realBalance((cPrice * 1)));
											if(Register.access().getTransaction() == null) {
												Register.access().createTransaction();
											}
											Register.access().getTransaction().addItem(item);
										} else if(item.isCustom_price() == 2) {
											ConsoleUtil.Print("ENTER ITEM PRICE", "");
											cPrice = sc.nextDouble();
											cPrice = (cPrice * -1);
											
											System.out.print("[" + item.getUpc() + "] " + item.getName() + " | $" + StringUtil.realBalance((cPrice * 1)));
											System.out.println("\n");
											Register.access().addBalance(StringUtil.realBalance((cPrice * 1)));
											item.setMyPrice(StringUtil.realBalance((cPrice * 1)));
											if(Register.access().getTransaction() == null) {
												Register.access().createTransaction();
											}
											Register.access().getTransaction().addItem(item);
										}
										else if(item.isQuantity() == 1) {
											ConsoleUtil.Print("ENTER ITEM QUANTITY", "");
											
											try {
												cQTY = Integer.valueOf(sc.nextLine());
											} catch(IllegalArgumentException ex) {
												// Lets test this? When the user simply hits enter, or enters an invalid "number" the system will autocorrect to simply 1 of whatever it is
												cQTY = 1;
											}
											
											System.out.print("(" + cQTY + " @ " + cPrice + ") " + "[" + item.getUpc() + "] " + item.getName() + " | $" + StringUtil.realBalance((cPrice * cQTY)));
											System.out.println("\n");
											Register.access().addBalance(StringUtil.realBalance((cPrice * cQTY)));
											item.setMyPrice(StringUtil.realBalance((cPrice * cQTY)));
											if(Register.access().getTransaction() == null) {
												Register.access().createTransaction();
											}
											item.setQuantityBuying(cQTY, cPrice);
											Register.access().getTransaction().addItem(item);
										}
										else if(item.isWeight() == 1) {
											ConsoleUtil.Print("ENTER ITEM WEIGHT", "");
											cWeight = sc.nextDouble();
											
											System.out.print("(" + cWeight + ") @ $" + item.getPrice_per_pound() + "/lb | " + "[" + item.getUpc() + "] " + item.getName() + " | $" + StringUtil.realBalance((item.getPrice_per_pound() * cWeight)));
											System.out.println("\n");
											Register.access().addBalance(StringUtil.realBalance((item.getPrice_per_pound() * cWeight)));
											item.setMyPrice(StringUtil.realBalance((item.getPrice_per_pound() * cWeight)));
											if(Register.access().getTransaction() == null) {
												Register.access().createTransaction();
											}
											item.setWeightBuying(cWeight, item.getPrice_per_pound());
											Register.access().getTransaction().addItem(item);
										}
										
										else {								
											System.out.print("[" + item.getUpc() + "] " + item.getName() + " | $" + StringUtil.realBalance((cPrice * 1)));
											System.out.println("\n");
											Register.access().addBalance(StringUtil.realBalance((cPrice * 1)));
											item.setMyPrice(StringUtil.realBalance((cPrice * 1)));
											if(Register.access().getTransaction() == null) {
												Register.access().createTransaction();
											}
											Register.access().getTransaction().addItem(item);
										}
									}
								}
							}
							
							if(!found) {
								ConsoleUtil.Print("ITEM NOT FOUND", origin[0].toUpperCase());
							}
							
							
						} else {
							if(m.requiresMgrOverride()) {
								SoundUtils.beep();
								ConsoleUtil.Clear();
								ConsoleUtil.Print("MO", "REQUIRES MGR OVERRIDE. PLEASE SCAN OVERRIDE CARD");
								System.out.println("\n");
								String override = sc.nextLine();
								
								// Then check the override that was scanned
								boolean flag = false;
								try {
									flag = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, override);
								} catch (Exception e) {}
								
								if(!flag) {
									ConsoleUtil.Print("ERR", "Not Authorized");
								} else {
									m.execute(arg, sc);
									ConsoleUtil.Clear();
								}
							} else {
								m.execute(arg, sc);
							}
						}
						
						// To reset the variable so the for statement literally never ends LOL
						i = 0;
					}
					
				}
			}
		}
	}
	
	public static ConfigFile getConfig() {
		return theConfig;
	}
	
	public static AtticusDB getDB() {
		return atticus_db;
	}
	
	public static void login(Scanner sc, ManagerAPI mAPI) throws IOException {
		for(int a = 0; a < 3; a++) {
			// Check if the current register is logged in
			if(!Register.access().isLoggedIn()) {

				System.out.println(CON_STARTUP_MESSAGE);

				System.out.println("");
				System.out.println("");
				System.out.println("Welcome! Please Login below...");
				System.out.println("");
				System.out.println("CASHIER NUMBER:");
				System.out.println("");
				
				String open_id = sc.nextLine();
				
				boolean flag = false;
				try {
					flag = mAPI.isLoginAuthorized(OverrideType.CASHIER, open_id);
				} catch (Exception e) {}
				
				if(!flag) {
					ConsoleUtil.Print("ERR", "Operator ID Not Recognized");
					System.out.println("\n");
				} else {
					//ConsoleUtil.Clear();
					System.out.println("** ENTER PASSWORD **");
					String password = sc.nextLine();
					ManagerProfile profile = null;
					
					try {
						profile = mAPI.getProfile(open_id);
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					try {
						if(mAPI.authenticate(open_id, password)) {
							
							
							// Check if the cashier is logged into another register on the same controller, if they are, we want to stop them from logging in here
							// tell them which one they're on, but also give them a chance to override it, if they can
							String session = "NONE";
							try {
								ResultSet check_result = getDB().query("SELECT * FROM `active_sessions` WHERE `operator_number` = '" + open_id + "';");
								while(check_result.next()) {
									session = check_result.getString("terminal_number");
								}
							} catch (SQLException e1) {
							}
							
							if(session.equals("NONE")) {
								Register.access().setLoggedIn(new Operator(profile.getFirstName(), profile.getLastName(), open_id));
								try {
									getDB().update("INSERT INTO `active_sessions` (`terminal_number`, `operator_number`) VALUES ('" + theConfig.getTerminalNumber() + "', '" + open_id + "');");
								} catch (SQLException e1) {
								}
								
								ConsoleUtil.Print(CON_WELCOME_MESSAGE_ONE, CON_WELCOME_MESSAGE_TWO);
								
								System.out.println("\n** WELCOME " + profile.getFirstName().toUpperCase() + " **");
								Secure.unlockTerminal();
							} else {
								// If the session that they are still logged into, also happens to be this terminal
								// Just let them login
								if(session.equalsIgnoreCase(theConfig.getTerminalNumber())) {
									Register.access().setLoggedIn(new Operator(profile.getFirstName(), profile.getLastName(), open_id));
									try {
										getDB().update("INSERT INTO `active_sessions` (`terminal_number`, `operator_number`) VALUES ('" + theConfig.getTerminalNumber() + "', '" + open_id + "');");
									} catch (SQLException e1) {
									}

									ConsoleUtil.Print(CON_WELCOME_MESSAGE_ONE, CON_WELCOME_MESSAGE_TWO);

									System.out.println("\n** WELCOME " + profile.getFirstName().toUpperCase() + " **");
									Secure.unlockTerminal();
								} else {
									ConsoleUtil.Print("OPERATOR STILL ACTIVE ON " + session, "");
									System.out.println("\n");
									boolean da = false;
									try {
										da = mAPI.isAuthorized(OverrideType.CASH_OFFICE_FES, sc.nextLine());
									} catch (Exception e) {}

									if(!da) {
										ConsoleUtil.Print("ERR", "Not Authorized");
										System.out.println("\n");
									} else {

										Register.access().setLoggedIn(new Operator(profile.getFirstName(), profile.getLastName(), open_id));
										try {
											getDB().update("INSERT INTO `active_sessions` (`terminal_number`, `operator_number`) VALUES ('" + theConfig.getTerminalNumber() + "', '" + open_id + "');");
										} catch (SQLException e1) {
										}

										ConsoleUtil.Print(CON_WELCOME_MESSAGE_ONE, CON_WELCOME_MESSAGE_TWO);
										System.out.println("\n** WELCOME " + profile.getFirstName().toUpperCase() + " **");
										Secure.unlockTerminal();
									}
								}
							}
						} else {
							ConsoleUtil.Print("ERR", "Check Password");
							System.out.println("\n");
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}
