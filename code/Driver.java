import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class acts as a driver-main. It is the entry point of the program. Here
 * are given from the files all the parameters necessary for the operation of
 * the program. The Drives class is responsible for creating our neural network
 * of Kohonen type (type Kohonen_N_network ), and to call the functions that
 * will run the training and testing of the neural network as well as is
 * responsible for passing the results to the files.
 * 
 * 
 * Driver produces two files
 *
 * 1. clustering.txt => represents how the letter have grouped up. 2.
 * results.txt => includes the training error at the end of each iteration and
 * the testing error at the end of each iteration.
 *
 * @author Elia Nicolaou 1012334 (enicol09)
 * @version 1.0
 * @see Neurons_Network,Layer
 *
 */
public class Driver {
	// initialize files name from the beginning
	public static String results_file = "results.txt";
	public static String clustering_file = "clustering.txt";
	public static String parameters_file = "parameters.txt";
	public static String training_list = "training";
	public static String testing_list = "testing";

	// Variables that going to be taken from the parameters file
	public static double learning_rate;
	public static int interations_numbers;
	public static double deviation;
	public static int inputs;
	public static int board_size;

	// Scanner
	private static Scanner input;

	// FileWriter for the new files
	private static FileWriter error;
	private static FileWriter cluster;

	/**
	 * This function is used for checking if a file exists, and if exists will open
	 * it
	 * 
	 * @param filename - the name of the file that we want to open.
	 * @throws IOException
	 */
	private static void Check(String filename) throws IOException {

		File file_name = new File(filename);
		try {

			input = new Scanner(file_name);

		} catch (FileNotFoundException ex) {

			System.out.println(" EROOR: File not Found! . \n");
			System.exit(0);
		}
	}

	/**
	 * The main function - implement all that the Driver basically has to do.
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// Call functions of class Utilities for modifying training & testing file -
		// normalize

		Utilities.Modify_data();
		Utilities.create_Files();


		System.out.println(" ============ Kohonen : organizing maps for Letter Recognition  ============ ");

		// ---------------------------------------------------------------------------

//		System.out.println("Give the learning rate : ");
//		learning_rate = scan.nextDouble();
//
//		System.out.println("Give the number of iterations : ");
//		interations_numbers = scan.nextInt();

		// ---------------------------------------------------------------------------

		// opening the parameters file
		Check(parameters_file);

		//reading board_size
		input.next();
		if (input.hasNext())
			board_size = input.nextInt();
		input.next();
		if (input.hasNext())
			inputs = input.nextInt();
		input.next();
		if (input.hasNext())
			deviation = input.nextDouble();
		input.next();
		if (input.hasNext())
			learning_rate = input.nextDouble();
		input.next();
		if (input.hasNext())
			interations_numbers = input.nextInt();

		System.out.println(" \n We are running Kohonen algorithm with: \n learning rate =  " + learning_rate
				+ " \n iterations number = " + interations_numbers + "\n" + " board size = " + board_size
				+ "\n Data Dimensions = " + inputs + " \n standard deviation = " + deviation);

		// crate Kohonen_network
		// give all the values from parameters file
		Kohonen_N_network kohonen = new Kohonen_N_network(board_size, interations_numbers, inputs, deviation,
				learning_rate);
		kohonen.start();

		//get the errors
		ArrayList<Double> errors = kohonen.error_training;
		ArrayList<Double> errors_t = kohonen.error_testing;

		error = new FileWriter(results_file);

		// printing headers
		error.write("\n                 ------------- ERROR FILE 1012334  ------------- \n");
		error.write("------------------------------------------------------------------------------------\n");
		error.write(" Iterations_counter  |      Training Error     |   Testing Error \n");
		error.write("-------------------------------------------------------------\n");

		for (int i = 0; i < interations_numbers; i++) {
			error.write("     " + (i + 1) + "        " + errors.get(i)  + "      " + errors_t.get(i) );
			error.write("\n");
		}
		error.close();

		
		//Writing Clustering
		cluster = new FileWriter(clustering_file);

		cluster.write("\n                 ------------- CLUSTERING FILE 1012334 ------------- \n");
		cluster.write("------------------------------------------------------------------------------------\n");

		for (int i = 0; i < board_size; i++) {
			cluster.write("\n");
			for (int j = 0; j < board_size; j++) {
				cluster.write(kohonen.labelling_before_lvq[i][j] + " ");
			}
		}

		cluster.close();
		
		
		FileWriter lvq= new FileWriter("lvq.txt");
		
		lvq.write("\n                 ------------- LVQ FILE 1012334 ------------- \n");
		lvq.write("------------------------------------------------------------------------------------\n");
		lvq.write(" Number_Of_data |     BEFORE    |   AFTER \n");
		
		for (int i = 0; i < kohonen.error_Lvq_after.size(); i++) {
			lvq.write("     " + (i + 1) + "        " + kohonen.error_Lvq_before.get(i)  + "      " + kohonen.error_Lvq_after.get(i) );
			lvq.write("\n");
		}
		
		lvq.close();
		
	}

}
