package com.duewop.remsstreetfight.core;

import com.duewop.remsstreetfight.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller extends Application {

	String strOutputDir = null;

	private Random rand = new Random();

	private MediaPlayer mp;

	private Stage primaryStage;

	private Main mainApp;

	@FXML
	private Label contestantOne;
	@FXML
	private Label contestantTwo;
	@FXML
	private ImageView img_title;
	@FXML
	private ImageView img_vs;
	@FXML
	private ImageView img_anime;
	@FXML
	private Label lbl_winner_name;
	@FXML
	private Label lbl_winner;
	@FXML
	private Button btn_close;
	@FXML
	private Button btn_min;

	/**
	 * Is called by the main application to give a reference back to itself.
	 *
	 * @param mainApp
	 */
	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	public void StartListening() throws Exception {

		Timer time = new Timer();
		time.schedule( new TimerTask() {
			public void run() {
				Platform.runLater(() -> {
					Date thisDate = new Date();

					//Set vars
					String fileName = "contestants.txt";
					List<String> list = new ArrayList<>();

					//Read the file
					try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
						list = br.lines().collect(Collectors.toList());
					} catch (IOException e) {
						e.printStackTrace();
					}

					//Not just 2 rows, something is wrong so clear it out
					if (list.size() != 1) {
						String empty = "";
						try {
							Files.write(Paths.get(fileName), empty.getBytes());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					//If it's not empty we have 2 rows so process it
					if (!list.isEmpty()) {
						list.forEach(System.out::println);
						String empty = "";
						try {
							SetupRumble(list);
							Files.write(Paths.get(fileName), empty.getBytes());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}, 0, 1000);

		btn_close.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
		btn_close.setVisible(true);
		btn_min.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
		btn_min.setVisible(true);

	}

	public void SetupRumble(List<String> listOfContestants) {

		primaryStage = mainApp.getPrimaryStage();

		//clear fields
		img_title.setVisible(true);
		img_vs.setVisible(true);
		lbl_winner_name.setText("");
		lbl_winner_name.setVisible(true);
		lbl_winner.setText("");
		lbl_winner.setVisible(true);
		contestantOne.setVisible(true);
		contestantOne.getStyleClass().clear();
		contestantOne.setStyle(null);
		contestantOne.getStyleClass().addAll("label");
		contestantTwo.setVisible(true);
		contestantTwo.getStyleClass().clear();
		contestantTwo.setStyle(null);
		contestantTwo.getStyleClass().addAll("label");
		img_anime.setImage(null);
		img_anime.setVisible(true);

		//populate with current contestants
		String contestantlist = listOfContestants.get(0).toString();
		String[] contestant = contestantlist.split("\\s+");
		contestantOne.setText(contestant[0].replaceAll("[@]", ""));
		contestantTwo.setText(contestant[1].replaceAll("[@]", ""));

		RunRumble();
	}

	private void RunRumble() {

		SetImage("/com/duewop/remsstreetfight/images/terrypunch.gif");

		int first = rand.nextInt(100);
		int second = rand.nextInt(100);

		if (first > second) {
			new Timer().schedule(new TimerTask() {
				public void run () {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							String winnerEnd = null;
							winnerEnd = winTextSet(contestantOne.getText(), first, contestantTwo.getText(), second, first - second);
							writeToFile(winnerEnd);

						}
					});
				}
			}, 2500);
		} else if (first == second) {
			RunRumble();
		} else {
			new Timer().schedule(new TimerTask() {
				public void run () {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
					String winnerEnd = null;
					winnerEnd = winTextSet(contestantTwo.getText(), second, contestantOne.getText(), first, second - first);
					writeToFile(winnerEnd);
						}
					});
				}
			}, 2500);
		}

		new Timer().schedule(new TimerTask() {
			public void run () {
				mp.stop();
				img_title.setVisible(false);
				img_vs.setVisible(false);
				lbl_winner_name.setVisible(false);
				lbl_winner.setVisible(false);
				contestantOne.setVisible(false);
				contestantTwo.setVisible(false);
				img_anime.setImage(null);
			}
		}, 15000);
	}

	private String winTextSet(String winner, int first, String loser, int second, int winValue) {
		String fight_win_text;
		String fight_win_write;
		if (winValue > 50) {
			fight_win_text = loser + " curled up in a ball and " + winner + " won easily.";
			fight_win_write = loser + "(" + second + ") curled up in a ball and " + winner + "(" + first + ") won easily.";
			PlaySound("/com/duewop/remsstreetfight/sound/bodies_short.mp3");
			img_anime.setVisible(false);
		} else if (winValue < 10) {
			fight_win_text = "Tough fight! But " + winner + " scraped out a victory.";
			fight_win_write = "Tough fight! But " + winner + "(" + first + ") scraped out a victory over " + loser + "(" + second + ").";
			PlaySound("/com/duewop/remsstreetfight/sound/barely.mp3");
			img_anime.setVisible(false);
		} else {
			fight_win_text = winner + " beat " + loser;
			fight_win_write = winner + "(" + first + ") beat " + loser + "(" + second + ")";
			PlaySound("/com/duewop/remsstreetfight/sound/yay.mp3");
			img_anime.setVisible(false);
		}

		lbl_winner_name.setText(winner + " wins!");
		lbl_winner.setText(fight_win_text);
		return fight_win_write;
	}

	public void SetImage(String imageLoc) {

		Image image = new Image(this.getClass().getResourceAsStream(imageLoc));
		img_anime.setImage(image);

		double w = 0;
		double h = 0;

		double ratioX = img_anime.getFitWidth() / image.getWidth();
		double ratioY = img_anime.getFitHeight() / image.getHeight();

		double reduceCoeffient = 0;

		if (ratioX >= ratioY) {
			reduceCoeffient = ratioY;
		} else {
			reduceCoeffient = ratioX;
		}

		w = image.getWidth() * reduceCoeffient;
		h = image.getHeight() * reduceCoeffient;

		img_anime.setX((img_anime.getFitWidth() - w) / 2);
		img_anime.setY((img_anime.getFitHeight() - h) / 2);
	}

	public void PlaySound(String soundLoc) {

		String path = Controller.class.getResource(soundLoc).toString();
		Media media = new Media(path);
		mp = new MediaPlayer(media);
		mp.setAutoPlay(true);
		mp.setVolume(.05);
		mp.play();
	}

	private void writeToFile(String total_value) {
		File logFile = new File("Rems_StreetFightLog.log");

		try {
			Writer writer = new BufferedWriter(new FileWriter(logFile, true));
			writer.append(System.getProperty("line.separator"));
			writer.append(total_value);
			writer.flush();
			writer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleMinimize(ActionEvent event) {
		Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
		stage.setIconified(true);
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleClose() {
		System.exit(0);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
	}
}
