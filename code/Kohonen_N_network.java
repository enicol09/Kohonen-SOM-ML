import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 * This class is the most important class in our program. It is essentially a
 * model of a neural network of Kohonen type. Contains a table of neurons (type
 * neurons) in a 3D table with the weights of each input. The class works like a
 * neural network. Initially it initializes its neurons, giving them random
 * values. It implements the training & testing as well labeling & LVQ
 * algorithm.
 * 
 * @author Elia Nicolaou 1012334 (enicol09)
 * 
 * @version 1.0
 * @see Neuron,Layer
 */
public class Kohonen_N_network {

	// parameters

	double learning_rate; // n = learning rate
	double deviation; // σ = standard deviation
	int interations;
	int board_size; // size of board
	int inputs;
	double[][][] weights; // this is the grid - the board that connects each coordinate with the input //
	// weight

	// winner coordinates
	int win_x;
	int win_y;
	// error calculation lists
	ArrayList<Double> error_training;
	ArrayList<Double> error_testing;
	ArrayList<Double> error_Lvq_before;
	ArrayList<Double> error_Lvq_after;
	// labeling
	public char labelling_before_lvq[][];
	public char labelling_after_lvq[][];

	/**
	 * @return the errors for training / for each epoch
	 */
	public ArrayList<Double> getError_training() {
		return error_training;
	}

	/**
	 * @return the errors for testing / for each epoch
	 */
	public ArrayList<Double> getError_testing() {
		return error_testing;
	}

	/**
	 * This is the constructor = it initializes all the parameters that are given,
	 * creates the error lists, creates the labels board
	 * 
	 * @param board_size    - the size of the board
	 * @param iterations    - the number of iterations/epochs
	 * @param inputs        - the number of inputs
	 * @param deviation     - the value of standard deviation
	 * @param learning_rate - the value of learning rate
	 */
	public Kohonen_N_network(int board_size, int iterations, int inputs, double deviation, double learning_rate) {

		// initialize the parameters
		this.board_size = board_size;
		this.inputs = inputs;
		this.learning_rate = learning_rate;
		this.deviation = deviation;
		this.interations = iterations;

		// create weights
		this.weights = new double[this.board_size][this.board_size][inputs];

		for (int i = 0; i < board_size; i++) {
			for (int j = 0; j < board_size; j++) {
				for (int p = 0; p < inputs; p++) {
					this.weights[i][j][p] = setRandomWeight(); // call this function to set randomly the values.
				}
			}
		}

		// create the error lists
		error_training = new ArrayList<Double>();
		error_testing = new ArrayList<Double>();
		error_Lvq_before = new ArrayList<Double>();
		error_Lvq_after = new ArrayList<Double>();

		// create the labeling tables
		labelling_before_lvq = new char[this.board_size][this.board_size];
		labelling_after_lvq = new char[this.board_size][this.board_size];
	}

	/**
	 * @return a random number between (-1,1)
	 */
	private double setRandomWeight() {
		double s = Math.random() * 1;
		if ((int) Math.random() * 2 == 1)
			s = s * (-1);
		return s;
	}

	/**
	 * This is one of the main functions of the class. Is being used for training
	 * each epoch. This function finds the winner, call the functions for updating
	 * the weights and finds the error for each epoch. (bear in mind my error
	 * calculation is not in the power of 2)
	 * 
	 * @param time - represents in which epoch we are.
	 */
	public void train_epoch(int time) {
		double error = 0;
		int x_m = 0, y_m = 0;
		double min_distance, distance;

		for (int i = 0; i < Utilities.train_c; i++) {

			min_distance = Double.MAX_VALUE;
			for (int j = 0; j < this.board_size; j++) {
				for (int n = 0; n < this.board_size; n++) {
					distance = 0;
					for (int p = 0; p < this.inputs; p++) {
						distance += Math.pow(Utilities.training_new[i][p] - weights[j][n][p], 2);
					}
					if (distance < min_distance) {
						x_m = j;
						y_m = n;
						min_distance = distance;
					}
				}
			}

			// System.out.println(min_distance);
			this.win_x = x_m;
			this.win_y = y_m;

			// changing the weights
			for (int j = 0; j < this.board_size; j++) {
				for (int n = 0; n < this.board_size; n++) {
					for (int p = 0; p < this.inputs; p++) {
						change_weight(j, n, p, Utilities.training_new[i][p], time); // updating the weight
					}
				}

			}

			error += min_distance;

		}

		double E = Math.pow(error, 2) / Utilities.train_c;
		error_training.add(E);
	}

	/**
	 * @param cord_x    the x coordination of the neuron
	 * @param cord_y    = the y coordination of the neuron
	 * @param cord_p    = shows in which input we are.
	 * @param input_now - represents the value of the feature value of input)
	 * @param time      - represents the epoch that we are right now
	 */
	public void change_weight(int cord_x, int cord_y, int cord_p, double input_now, int time) {

		this.weights[cord_x][cord_y][cord_p] = this.weights[cord_x][cord_y][cord_p] + learning_rate_now(time)
				* HoodFunc(cord_x, cord_y, cord_p) * (input_now - this.weights[cord_x][cord_y][cord_p]);
	}

	/**
	 * @param cord_x = the x coordination of the neuron
	 * @param cord_y = the y coordination of the neuron
	 * @param time   - represents the epoch that we are right now
	 * @return the neighborhood value
	 */
	private double HoodFunc(int cord_x, int cord_y, int time) {
		double neighbourhood = Math.exp((-1) * Distance(cord_x, cord_y) / (2 * Math.pow(deviation_now(time), 2)));
		return neighbourhood;
	}

	/**
	 * @param time - represents the epoch that we are right now
	 * @return the current standard deviation value - as the standard deviation
	 *         value is being reduced during the epochs.
	 */
	private double deviation_now(int time) {
		double deviation = this.deviation
				* Math.exp((-1) * ((double) time) / (((double) this.interations) / Math.log10(this.deviation)));
		return deviation;
	}

	/**
	 * 
	 * @param cord_x = the x coordination of the neuron
	 * @param cord_y = the y coordination of the neuron
	 * @return the eucleidian distance, between the winner and the neuron.
	 */
	private double Distance(int cord_x, int cord_y) {
		double d = Math.pow(win_x - cord_x, 2) + Math.pow(win_y - cord_y, 2);
		return d;
	}

	/**
	 * @param time - represents the epoch that we are right now
	 * @return the current learning rate - as the learning rate is being reduced
	 *         during the epochs.
	 */
	private double learning_rate_now(int time) {

		double now_rate = this.learning_rate * Math.exp((-1) * ((double) time) / this.interations);

		return now_rate;
	}

	/**
	 * This is the most important function, it is called by the driver in order to
	 * start training and testing. Makes training and testing for each epoch. Calls
	 * labeling and LVQ algorithm.
	 * 
	 * @throws IOException
	 */
	public void start() throws IOException {
		for (int time = 0; time < this.interations; time++) {
			System.out.println("EPOCH = " + time);
			train_epoch(time);
			test_epoch(time,true);
		}

		Label(true);
		LVQ_ALGORITHM();
		
		System.out.println("======LVQ EVALUATION======");

		for (int time = 0; time < this.interations; time++) {
			System.out.println("EPOCH = " + time);
			test_epoch(time, false);
		}

		Label(false);

	}

	/**
	 * This function implements the Lvq - algorithm -> fixing the labels.
	 */
	private void LVQ_ALGORITHM() {
		int x_m = 0, y_m = 0;
		double min_distance, distance;

		for (int i = 0; i < Utilities.train_c; i++) {
			min_distance = Double.MAX_VALUE;
			for (int j = 0; j < this.board_size; j++) {
				for (int n = 0; n < this.board_size; n++) {
					distance = 0;
					for (int p = 0; p < this.inputs; p++) {
						distance += Math.pow(Utilities.training_new[i][p] - this.weights[j][n][p], 2);
					}

					if (distance < min_distance) {
						min_distance = distance;
						x_m = j;
						y_m = n;
					}
				}
			}
			if (labelling_before_lvq[x_m][y_m] == Utilities.train_l[i]) {
				for (int p = 0; p < this.inputs; p++) {
					this.weights[x_m][y_m][p] = this.weights[x_m][y_m][p] + learning_rate_now(this.interations)
							* (Utilities.training_new[i][p] - this.weights[x_m][y_m][p]);
				}
			} else {
				for (int p = 0; p < this.inputs; p++) {
					this.weights[x_m][y_m][p] = this.weights[x_m][y_m][p] - learning_rate_now(this.interations)
							* (Utilities.training_new[i][p] - this.weights[x_m][y_m][p]);

				}
			}
		}

	}

	/**
	 * This function is being used for the labeling - that it will be used for the
	 * representation of the letters in the clustering.txt Finds the minimum
	 * distance - > put the label
	 * 
	 * @param before - shows if it is before LVQ algorithm / or after true for
	 *               before/false for not
	 */
	private void Label(boolean before) {
		double min_distance, distance;
		double error = 0;
		char label = 0;

		for (int i = 0; i < this.board_size; i++) {
			error = 0;
			for (int j = 0; j < this.board_size; j++) {
				min_distance = Double.MAX_VALUE;
				label = 0;

				for (int p = 0; p < Utilities.test_c; p++) {
					distance = 0;

					for (int o = 0; o < this.inputs; o++) {
						distance += Math.pow(Utilities.testing_new[p][o] - this.weights[i][j][o], 2);
					}

					if (distance < min_distance) {
						min_distance = distance;
						label = Utilities.test_l[p];
					}

					error += min_distance;

				}

				if (label != 0)
					labelling_before_lvq[i][j] = label;

			}
//			if (before) {
//				error_Lvq_before.add(Math.pow(error, 2) / Utilities.test_c);
//			} else {
//				error_Lvq_after.add(Math.pow(error, 2) / Utilities.test_c);
//			}
		}

	}

	/**
	 * This is one of the main functions of the class. Is being used for testing
	 * each epoch. This function finds the winner, call the functions for updating
	 * the weights and finds the error for each epoch. (bear in mind my error
	 * calculation is not in the power of 2)
	 * 
	 * @param time - represents in which epoch we are.
	 * @param b
	 */
	private void test_epoch(int time, boolean b) {
		double error = 0;
		int x_m = 0, y_m = 0;
		double min_distance, distance;
		for (int i = 0; i < Utilities.test_c; i++) {
			min_distance = Double.MAX_VALUE;
			for (int j = 0; j < this.board_size; j++) {
				for (int n = 0; n < this.board_size; n++) {
					distance = 0;
					for (int p = 0; p < this.inputs; p++) {
						distance += Math.pow(Utilities.testing_new[i][p] - weights[j][n][p], 2);

					}
					if (distance < min_distance) {
						x_m = j;
						y_m = n;
						min_distance = distance;
					}
				}
			}

			this.win_x = x_m;
			this.win_y = y_m;

			for (int j = 0; j < this.board_size; j++) {
				for (int n = 0; n < this.board_size; n++) {
					for (int p = 0; p < this.inputs; p++) {
						change_weight(j, n, p, Utilities.testing_new[i][p], time);
					}
				}

			}

			error += min_distance;

		}

		if (b) {
			double E = Math.pow(error, 2) / Utilities.test_c;
			error_testing.add(E);
			error_Lvq_before.add(E);
		} else {
			double E = Math.pow(error, 2) / Utilities.test_c;
			error_Lvq_after.add(E);
		}

	}

}
