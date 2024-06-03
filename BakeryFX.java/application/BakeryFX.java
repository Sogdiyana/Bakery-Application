


package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

//imports needed for this application
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.binding.Bindings;

public class BakeryFX extends Application {
	// declare components that require class scope
	Label lblMenu, lblyourOrder, lblTotal, LblOrder, lbltotal2;
	Text txtTotal, txttotal2;
	TextField txtfMenu, txtfyourOrder, txtfOrder;
	Button btnStart, btnClose, btnAdd, btnRemove, btnClear, btnConfirm, btnCancel, btnProceed;
	TextArea txtMenu, txtyourOrder, txtOrder;

	Image img;
	ImageView imview;
	ImageView imviewbakery;
	ProgressBar progBar;

	ListView<String> lvMenu;
	ListView<String> lvOrder;
	double currentTotal = 0.0;

	private HashMap<String, Double> menuItems = new HashMap<>();
	HashMap<String, Double> menuPrices;

	ArrayList<String> orderArr;
	// threads
	Task<Void> task;

	public BakeryFX() {

		//instantiate components using keyword 'New'
		lblMenu = new Label("Menu");
		lblyourOrder = new Label("Your Order");
		LblOrder = new Label("Your Order");
		lblTotal = new Label("Total: ");
		lbltotal2 = new Label("Total: ");
		LblOrder = new Label("Your Order");
		
		txtfMenu = new TextField();
		txtfyourOrder = new TextField();
		txtOrder = new TextArea();
		txtOrder.setDisable(true);
		
		txttotal2 = new Text();
		txtTotal = new Text();
		txtMenu = new TextArea();
		txtyourOrder = new TextArea();
		
		//set instantiate the buttons for the first page
		btnStart = new Button("Get started");
		btnClose = new Button("Close");
		
		// set buttons width and height
		btnStart.setMinWidth(240);
		btnStart.setMinHeight(40);
		btnClose.setMinWidth(240);
		btnClose.setMinHeight(40);
		
		//set instantiate the buttons for the Order Page
		btnAdd = new Button("Add to order");
		btnRemove = new Button("Remove from Order");
		btnClear = new Button("Clear Order");
		btnProceed = new Button("Proceed to payment");
		
		//instantiate the buttons for the Payment page
		btnConfirm = new Button("Confirm order");
		btnCancel = new Button("Cancel");
		
		//this array wil store string elements
		orderArr = new ArrayList<String>();

		//initialize the progress Bar for payment page
		progBar = new ProgressBar(1);
		//initialize the progress Bar to the False, so it won't work 
		//until the confirm button clicked
		progBar.setVisible(false);
		// initialized the listView with list of items in the menu
		lvMenu = new ListView<String>();
		//the LVOrder will take the items from the menu
		lvOrder = new ListView<String>();
		// initialized the way to csv file wit list of items for the menu
		readMenu("Assets/bakerymenu.csv");

		// get the image - Logo on First Page

		try {
			img = new Image("./Assets/bakery 1.png"); // the path for the image - logo of the appplication
			imview = new ImageView(img); //initialized the Image
		} catch (Exception oops) { //if there is a Error of finding the image, this message will come up
			oops.printStackTrace();
			System.err.println("Something went wrong with logo in welcome page");
		}

	}//bakeryFx
	
	//Created method readMenu, to read menu from csv list
	private void readMenu(String MenuList) {
		//read the csv file
		try {
			String line = "";
			BufferedReader buf = new BufferedReader(new FileReader(MenuList));

			while ((line = buf.readLine()) != null) {
				String[] readData = line.split(":");
				System.out.println(readData[0]);

				//add just the names to the listView (use index[0])
				String menuItem = readData[0] + " - " + readData[1];
				lvMenu.getItems().add(menuItem);
			}
			//close the file
			buf.close();
		} catch (Exception e) {
			System.err.println("Error reading the menu " + MenuList);
		}
	}//readMenu closed

	@Override
	public void init() {
		// Event Handling for main UI
		
		// clicking on confirm payment button payment window opens
		btnConfirm.setOnAction(event -> startTask());
		// clicking btnClose should close the application
		btnClose.setOnAction(event -> Platform.exit());
		// clicking to btnStart should order page
		btnStart.setOnAction(event -> showOrderpage());
		// clicking in btnProceed the payment window should open
		btnProceed.setOnAction(event -> showPaymentPage());
		
		// clicking on clear button it will clear whole order page with items
		btnClear.setOnAction(event -> {
			lvOrder.getItems().clear();
			currentTotal = 0;
			// txtTotal.setText(currentTotal + "");
			System.out.println("Current Total: " + currentTotal);
			txtTotal.setText(String.format("%.2f", currentTotal));
			orderArr.clear();

		});// btnClear

		// clicking on Remove button it removes the item from order list
		btnRemove.setOnAction(event -> {
			//get the name selected
			String selectedItem = lvOrder.getSelectionModel().getSelectedItem();
			//check the selected item before attempting to process it
			if (selectedItem != null) {
				//string using the dash and stores the resulting substrings in an array
				String[] parts = selectedItem.split(" - ");
				//checks if the selected Item could be successfully split into two parts itemName and price 
				if (parts.length == 2) {
					String itemName = parts[0];
					String price = parts[1];
					String OrderItem = itemName + " " + price;
					//this initializes a loop that itirates over the indices of the arraylist
					for (int i = 0; i < orderArr.size(); i++) {
						//it chec if the element at the current index i equal to th OrderItem string
						if (orderArr.get(i) == OrderItem) {
							orderArr.remove(i);//removing the element from order list if it was removed from order list
						}
					}
					//
					double itemPrice = Double.parseDouble(parts[1].replaceAll("[^\\d.]", "").trim());
					lvOrder.getItems().remove(selectedItem);//it removes the selected item from Order List
					currentTotal -= itemPrice;//it removes the selected item from Order List
					//it will update the displayed total with the new current Total
					txtTotal.setText(String.format("%.2f", currentTotal));
					
					//debugging
					System.out.println("Item Price: " + itemPrice);
					System.out.println("Current Total: " + currentTotal);
				}
			}
		});// btnRemove

		// add order button
		btnAdd.setOnAction(event -> {

			// Get the selected item from the menu list
			String selectedItem = lvMenu.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				// Extract the item name and price from the selected menu item
				String[] parts = selectedItem.split(" - ");

				if (parts.length == 2) {
					String itemName = parts[0];
					String price = parts[1];
					//concatenates two strings, wit a space in between, and stores the result in a new string variable
					String OrderItem = itemName + " " + price;
					orderArr.add(OrderItem);
					System.out.println(orderArr);
					// Remove any currency symbol and trim any whitespace
					String itemPriceStr = parts[1].replaceAll("[^\\d.]", "").trim();
					try {
						double itemPrice = Double.parseDouble(itemPriceStr);

						// Add the selected item to the customer order list
						lvOrder.getItems().add(selectedItem);

						// Assuming 'currentTotal' is a class member variable
						currentTotal += itemPrice; // Add to the current total
						//debugging
						System.out.println("Item Price: " + itemPrice);
						System.out.println("Current Total: " + currentTotal);
						//this line updates the text displayed in the txtTotal component to show the value of currentTotal
						txtTotal.setText(String.format("%.2f", currentTotal));
						//throw an exception
					} catch (NumberFormatException e) {
						System.err.println("Invalid price format: ");
					}
				} else {
					System.err.println("Invalid menu item format: " + selectedItem);
				}
			}

		});

		// btn Cancel
		btnCancel.setOnAction(event -> cancelOrder());

		// get the items selected
		lvMenu.setOnMousePressed(event -> {
			//get the name selected
			String selectedItem = lvMenu.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				String itemName = selectedItem.split(" - ")[0];//splitting the items

				// Assuming the image names match the item names exactly, when user clicks in the item the image show up 
				String imagePath = "/Assets/bakeryImages/" + itemName + ".png";
				try {
					Image itemImage = new Image(imagePath, true); // true to load in background
					imview.setImage(itemImage);
					//throw an exception
				} catch (IllegalArgumentException e) {
					System.out.println("Error: image file for " + itemName + " not found.");
				}
			}

		});

	}// init

	//made the StartTask method for progressBar
	private void startTask() {
		//disable the cancel button
		btnCancel.setDisable(false);
		//to show the progress Bar
		progBar.setVisible(true);
		//re-enable Cancel button
		btnConfirm.setDisable(true);
		System.out.println("Confirm button was clicked"); //debugging
		//create task object for thread
		task = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {
				//the functionality for the task
				final long max = 1000000000;//if the number is long the indicator will go

				for (long i = 1; i <= max; i++) {
					if (isCancelled()) {
						updateProgress(0, max);
						break;
					}
					updateProgress(i, max);
				}
				return null;
			}

		};
		//start the thread
		new Thread(task).start();
		//bind the progress of the bar, to the progress of the thread
		progBar.progressProperty().bind(task.progressProperty());
	}

	// method for canceling order
	private void cancelOrder() {
		task.cancel();
		System.out.println("Order cancelled"); //debugging
	}

	// created method for second page
	private void showOrderpage() {

		System.out.println("Show an order page");//debugging

		// create a new window
		Stage orderStage = new Stage();

		// set title of the page
		orderStage.setTitle("Sogdiyana Idrissova - 3121175");
		orderStage.setWidth(500);
		orderStage.setHeight(900);

		// main layout for the second page
		VBox vborderpage = new VBox();
		
		//making the VBox to put the Menu Label and Lvmenu to layout
		VBox vbmenu = new VBox();
		vbmenu.getChildren().addAll(lblMenu, lvMenu);
		vbmenu.setAlignment(Pos.CENTER);
		vbmenu.setSpacing(10);
		
		//made the HBox layout for the buttons under the menu
		HBox hbbuttons = new HBox();
		hbbuttons.getChildren().addAll(btnAdd, btnRemove, btnClear);
		hbbuttons.setAlignment(Pos.CENTER);
		hbbuttons.setSpacing(15);
		
		//Made this layout for total 
		HBox hbtotal = new HBox();
		hbtotal.getChildren().addAll(lblTotal, txtTotal);
		hbtotal.setAlignment(Pos.CENTER);
		//Put the Label Order, lvorder , hb total, and btnProceed in one layout
		VBox vborder = new VBox();
		vborder.getChildren().addAll(lblyourOrder, lvOrder, hbtotal, btnProceed);
		vborder.setAlignment(Pos.CENTER);
		vborder.setSpacing(10);
		//the layout for the second page
		VBox vbmainorder = new VBox();
		vbmainorder.getChildren().addAll(vbmenu, hbbuttons, imview, vborder);
		vbmainorder.setAlignment(Pos.CENTER);
		vbmainorder.setSpacing(20);

		// set the size for images
		imview.setFitWidth(90);
		imview.setFitHeight(90);
		//showing the layout and giving the paddings
		vborderpage.setPadding(new Insets(20));
		//showing the whole layout
		vborderpage.getChildren().addAll(vbmainorder);

		// added icon of application
		try {
			orderStage.getIcons().add(new Image("Assets/bakery 1.png"));

		} catch (Exception e) {
			System.err.println("Icon not found");//debugging

		}
		
		lblTotal.setId("total1");

		// created the scene
		Scene s = new Scene(vborderpage);

		// add the style with css
		s.getStylesheets().add("./Assets/bakery_style.css");
		//showing the scene
		orderStage.setScene(s);
		//showed the page
		orderStage.show();
	}

	// created the payment page
	public void showPaymentPage() {
		//debugging
		System.out.println("Show payment page");

		// create payment stage
		Stage paymentStage = new Stage();

		// set title of the page
		paymentStage.setTitle("Sogdiyana Idrissova - 3121175");
		paymentStage.setWidth(500);
		paymentStage.setHeight(900);

		// main layout for the payment page
		VBox vbpaymentpage = new VBox();
		HBox hbpayment = new HBox();
		hbpayment.getChildren().addAll(lbltotal2, txttotal2);
		hbpayment.setAlignment(Pos.CENTER);
		//the layout for the lblorder, txtorder, hbpayment
		VBox vbpayment = new VBox();
		vbpayment.getChildren().addAll(LblOrder, txtOrder, hbpayment);
		vbpayment.setAlignment(Pos.CENTER); //put in the center
		vbpayment.setSpacing(10); //set spacing
		//btnConfirm and btnCancel i one layout
		VBox vbbuttn = new VBox();
		vbbuttn.getChildren().addAll(btnConfirm, btnCancel);
		vbbuttn.setSpacing(10);//set spacing
		vbbuttn.setAlignment(Pos.CENTER);//put in the center

		VBox vbpaymentmain = new VBox();
		vbpaymentmain.getChildren().addAll(vbpayment, vbbuttn, progBar);
		vbpaymentmain.setAlignment(Pos.CENTER);//put in the center
		vbpaymentmain.setSpacing(30);//set spacing
		//set the padding for this layout
		vbpaymentpage.setPadding(new Insets(20));
		//showed the layout in the main layout
		vbpaymentpage.getChildren().addAll(vbpaymentmain);

		// added icon of application
		try {
			paymentStage.getIcons().add(new Image("Assets/bakery 1.png"));

		} catch (Exception e) {
			System.err.println("Icon not found");

		}
		//the total 2 will show the total price
		txttotal2.setText(txtTotal.getText());
		for (String item : orderArr) {
			txtOrder.appendText(item + "\n");
		}
		// set the id for calling them in css by id
		LblOrder.setId("lblorder");
		lbltotal2.setId("total");
		btnConfirm.setId("btnConfirm");
		btnCancel.setId("btnCancel");
		progBar.setId("prog");

		// created the scene
		Scene s = new Scene(vbpaymentpage);

		// add the style with css
		s.getStylesheets().add("./Assets/bakery_style.css");
		//set the scne
		paymentStage.setScene(s);
		//showing  the mainlayout
		paymentStage.show();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Window management and Layouts
		primaryStage.setTitle("Sogdiyana Idrissova - 3121175");

		// added icon of application
		try {
			primaryStage.getIcons().add(new Image("Assets/bakery 1.png"));

		} catch (Exception e) {
			System.err.println("Icon not found");
		}

		// set the width and height
		primaryStage.setWidth(500);
		primaryStage.setHeight(900);

		// main layout
		BorderPane bpMain = new BorderPane();

		VBox orderStage = new VBox();

		// create a layout
		VBox vbBtn = new VBox();
		vbBtn.getChildren().addAll(btnStart, btnClose);
		vbBtn.setAlignment(Pos.CENTER);
		vbBtn.setSpacing(10);

		VBox vbImg = new VBox();
		vbImg.getChildren().addAll(imview, vbBtn);
		vbImg.setSpacing(50);
		vbImg.setAlignment(Pos.CENTER);
		bpMain.setPadding(new Insets(20));

		VBox vbmenu = new VBox();
		vbmenu.getChildren().addAll(lblMenu, txtMenu);

		// add containers to main layout
		bpMain.setCenter(vbImg);

		// set the ID's for calling them in css file
		bpMain.setId("bpMain");
		orderStage.setId("orderStage");
		vbmenu.setId("vbmenu");
		btnAdd.setId("btnAdd");
		btnRemove.setId("btnRemove");
		btnClear.setId("btnClear");
		lblMenu.setId("menu");
		lblyourOrder.setId("yourOrder");
		lblTotal.setId("Total");
		btnProceed.setId("btnProceed");
		lvMenu.setId("lvmenu");

		// reading menu from csv list
		readMenu("Assets/bakerymenu.csv");

		// create the scene
		Scene s = new Scene(bpMain);

		// set the scene
		primaryStage.setScene(s);

		// add the style with css
		s.getStylesheets().add("./Assets/bakery_style.css");

		// show the scene
		primaryStage.show();
	}

	public static void main(String[] args) {
		// launch the application
		launch(args);
	}
}