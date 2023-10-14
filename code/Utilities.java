import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

/**
 * This class acts as an auxiliary class for the rest. Its purpose is to split
 * the letters.txt file into two separate files the training.txt file & the
 * testing.txt file.
 * 
 * @author Elia Nicolaou 1012334
 * @version 1.0
 *
 */
public class Utilities {

	static Double[][] info_data; // all letters

	static String letters = "Letters.txt";
	private static Scanner input;
	static char[] info_letters;
	static int cnt_c = 16;
	static int cnt_l;

	// File writers for training & testing
	private static FileWriter train_f;
	private static FileWriter test_f;

	// final training & testing values
	static double[][] training_new;
	static double[][] testing_new;

	// test & train starting letters
	static char[] test;
	static char[] train;

	// counters
	static int train_c;
	static int test_c;

	// the latest letters
	static char[] train_l;
	static char[] test_l;

	/**
	 * This function is used for checking if a file exists, and if exists will open
	 * it
	 * 
	 * @param filename - the name of the file that we want to open.
	 */
	private static void Check(String filename) {
		File file_name = new File(filename);
		try {

			input = new Scanner(file_name);

		} catch (FileNotFoundException ex) {

			System.out.println(" EROOR: File not Found! . \n");
			System.exit(0);
		}
	}

	/**
	 * This function is being used for normalizing all data between numbers (0 - 1)
	 * . We normalize by column.
	 * 
	 * @param training_data = the data that we want to normalize
	 * @param cnt_l         = the number of the lines of data we have
	 * @param cnt_c         = the number of columns of data we have
	 * 
	 * 
	 */
	public static void normalizeData(Double[][] training_data, int cnt_l, int cnt_c) {

		double max = 0;
		double min = 100000000;

		for (int j = 0; j < cnt_c; j++) {
			max = 0;
			min = 100000000;

			for (int i = 0; i < cnt_l; i++) {
				if (info_data[i][j] < min)
					min = info_data[i][j];
				if (info_data[i][j] > max) {
					max = info_data[i][j];
				}
			}

			for (int n = 0; n < cnt_l; n++) {

				double temp = info_data[n][j];
				info_data[n][j] = (temp - min) / (max - min);
			}
		}

	}

	/**
	 * This function is being used for creating the training & testing file. It
	 * separates the data into 2 . The 70 % of each letter goes to training and the
	 * other 30% goes to testing. It also swaps the lines.
	 * 
	 */
	public static void create_Files() {

		int cnt_t = 20000;
		int cnt_te = 20000;

		double training_letters[][] = new double[cnt_t][cnt_c];
		double test_letters[][] = new double[cnt_te][cnt_c];
		train = new char[cnt_t];
		test = new char[cnt_te];

		train_c = 0;
		test_c = 0;

		for (char n = 'A'; n <= 'Z'; n++) {
			int temp = 0;

			for (int i = 0; i < cnt_l; i++) {
				if (info_letters[i] == n) {
					temp++;
				}
			}

			int t = 0;
			int k = 0;
			while (t < (int) temp * 0.7 && k < cnt_l) {
				if (info_letters[k] == n) {
					train[train_c] = n;
					t++;
					for (int j = 0; j < cnt_c; j++) {
						training_letters[train_c][j] = info_data[k][j];
					}
					train_c++;
				}
				k++;
			}

			t = 0;
			while (t < (int) temp * 0.3 && k < cnt_l) {
				if (info_letters[k] == n) {
					test[test_c] = n;
					t++;
					for (int j = 0; j < cnt_c; j++) {
						test_letters[test_c][j] = info_data[k][j];
					}
					test_c++;
				}
				k++;
			}
		}

		// System.out.print("\n TRAIN : " + train_c + " " + test_c);

		training_new = new double[train_c][cnt_c];
		testing_new = new double[test_c][cnt_c];
		train_l = new char[train_c];
		test_l = new char[test_c];

		for (int i = 0; i < train_c; i++) {
			train_l[i] = train[i];
			for (int j = 0; j < cnt_c; j++) {
				double value = training_letters[i][j];
				training_new[i][j] = value;
			}
		}
		for (int i = 0; i < test_c; i++) {
			test_l[i] = test[i];
			for (int j = 0; j < cnt_c; j++) {
				double value = test_letters[i][j];
				testing_new[i][j] = value;
			}
		}

		String Training = "training.txt";
		String Test = "test.txt";

		Random rand = new Random();

		// Set randomly = swap the values of training file.
		for (int i = 0; i < train_c; i++) {
			int randomIndexToSwap = rand.nextInt(train_c);
			// System.out.println(train[i] + " " + train[randomIndexToSwap]);
			while (train_l[i] == train_l[randomIndexToSwap]) {
				randomIndexToSwap = rand.nextInt(train_c);
			}
			char temp = train_l[i];
			train_l[i] = train_l[randomIndexToSwap];
			train_l[randomIndexToSwap] = temp;

			for (int j = 0; j < cnt_c; j++) {
				training_new[i][j] = training_letters[randomIndexToSwap][j];
				training_new[randomIndexToSwap][j] = training_letters[i][j];
			}
		}

		// Set randomly = swap the values of testing file.
		for (int i = 0; i < test_c; i++) {
			int randomIndexToSwap = rand.nextInt(test_c);
			while (test_l[i] == test_l[randomIndexToSwap]) {
				randomIndexToSwap = rand.nextInt(test_c);
			}
			char temp = test[i];
			test_l[i] = test_l[randomIndexToSwap];
			test_l[randomIndexToSwap] = temp;

			for (int j = 0; j < cnt_c; j++) {
				testing_new[i][j] = test_letters[randomIndexToSwap][j];
				testing_new[randomIndexToSwap][j] = test_letters[i][j];

			}
		}

		try {
			train_f = new FileWriter(Training);
			test_f = new FileWriter(Test);

			for (int i = 0; i < train_c; i++) {
				train_f.write(train_l[i] + " ");
				for (int j = 0; j < cnt_c; j++) {
					train_f.write(training_new[i][j] + " ");
				}
				train_f.write("\n");
			}
			for (int i = 0; i < test_c; i++) {
				test_f.write(test_l[i] + " ");
				for (int j = 0; j < cnt_c; j++) {
					test_f.write(testing_new[i][j] + " ");
				}
				test_f.write("\n");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	/**
	 * This is function is called by the Driver, and is the main function of the
	 * class Utilities. It gets all the data from Letters.txt file and then sent it
	 * to the other functions for making the class completing the task that it has.
	 */
	public static void Modify_data() {

		cnt_c = 16;
		cnt_l = 0;

		Check(letters);

		while (input.hasNextLine()) {
			cnt_l++;
			input.nextLine();
		}

		input.close();

		info_data = new Double[cnt_l][cnt_c]; // initialize the size of the training_data
		info_letters = new char[cnt_l];

		int j;
		int i = j = 0;

		Check(letters);

		while (input.hasNext()) {

			String line = input.next();
			info_letters[i] = line.charAt(0);

			j = 0;
			int k = 1;
			while (k < line.length()) {
				String temp = line;
				if (k + 1 != line.length()) {
					if (line.charAt(k) != ',') {
						if (line.charAt(k + 1) == ',') {
							info_data[i][j] = Double.parseDouble(line.substring(k, k + 1));
							j++;
							k++;
						} else {
							line = temp;
							int p;
							int c;
							String num;
							if (k + 1 != line.length()) {
								p = k + 1;
								c = 1;
								boolean go = true;
								while (go) {
									if (line.charAt(p) != ',')
										c++;
									else {
										go = false;
									}
									p++;
									if (p == line.length())
										go = false;
								}

								num = line.substring(k, k + c);
							} else {
								c = 2;
								num = line.substring(k);

							}
							int v = 1;
							double value = 0;

							for (int o = 0; o < c; o++) {
								String temp2 = num;
								value = value * v + Double.parseDouble(num.substring(o, o + 1));
								v *= 10;
								num = temp2;
							}

							info_data[i][j] = value;

							j++;
							k += c;
						}
					} else {
						k++;
					}

					line = temp;
				} else {
					if (line.charAt(k) != ',') {
						info_data[i][j] = Double.parseDouble(line.substring(k));
						k++;
					}
				}
			}

			i++;
		}
		Utilities.normalizeData(info_data, cnt_l, cnt_c);

	}

}
