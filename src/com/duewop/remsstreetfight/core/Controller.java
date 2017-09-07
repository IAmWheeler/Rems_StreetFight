package com.duewop.remsstreetfight.core;

import com.duewop.remsstreetfight.Main;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Controller extends Application {

	String strOutputDir = null;

	private Random rand = new Random();

	private MediaPlayer mp;
	private MediaPlayer mpbg;

	@FXML
	private Label contestantOne;
	@FXML
	private Label contestantTwo;
	@FXML
	private ImageView img_anime;
	@FXML
	private Label lbl_winner;

	public void SetupRumble(List<String> listOfContestants) {

		//clear fields
		lbl_winner.setText("");
		contestantOne.getStyleClass().clear();
		contestantOne.setStyle(null);
		contestantOne.getStyleClass().addAll("label");
		contestantTwo.getStyleClass().clear();
		contestantTwo.setStyle(null);
		contestantTwo.getStyleClass().addAll("label");
		img_anime.setImage(null);

		//populate with current contestants
		contestantOne.setText(listOfContestants.get(0).toString());
		contestantTwo.setText(listOfContestants.get(1).toString());

		RunRumble();

		new Timer().schedule(new TimerTask() {
			public void run () { System.exit(0); }
		}, 10000);
	}

	private void RunRumble() {

		SetImage("/com/duewop/remsstreetfight/images/terrypunch.gif");

		int first = rand.nextInt(100);
		int second = rand.nextInt(100);
		String winnerEnd = null;

		if (first > second) {
			winnerEnd = winTextSet(contestantOne.getText(), first, contestantTwo.getText(), second, first - second);
			writeToFile(winnerEnd);
		} else if (first == second) {
			RunRumble();
		} else {
			winnerEnd = winTextSet(contestantTwo.getText(), second, contestantOne.getText(), first, second - first);
			writeToFile(winnerEnd);
		}
	}

	private String winTextSet(String winner, int first, String loser, int second, int winValue) {
		String fight_win_text;
		String fight_win_write;
		if (winValue > 50) {
			fight_win_text = loser + " curled up in a ball and " + winner + " won easily.";
			fight_win_write = loser + "(" + second + ") curled up in a ball and " + winner + "(" + first + ") won easily.";
				PlaySound("/com/duewop/remsstreetfight/sound/bodies_short.mp3");
		} else if (winValue < 10) {
			fight_win_text = "Tough fight! But " + winner + " scraped out a victory.";
			fight_win_write = "Tough fight! But " + winner + "(" + first + ") scraped out a victory over " + loser + "(" + second + ").";
			PlaySound("/com/duewop/remsstreetfight/sound/barely.mp3");
		} else {
			fight_win_text = winner + " beat " + loser;
			fight_win_write = winner + "(" + first + ") beat " + loser + "(" + second + ")";
			PlaySound("/com/duewop/remsstreetfight/sound/yay.mp3");
		}

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

	public void PlayBGSound(String soundLoc) {
		String path = Controller.class.getResource(soundLoc).toString();
		Media media = new Media(path);
		mpbg = new MediaPlayer(media);
		mpbg.setAutoPlay(true);
		mpbg.setVolume(.05);

		mpbg.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				mpbg.seek(Duration.ZERO);
			}
		});

		mpbg.play();
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
	private void handleClose() {
		mpbg.stop();
		mp.stop();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
	}
}
