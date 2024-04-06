package race.download;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.log4j.varia.NullAppender;
import org.bson.types.ObjectId;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.MapperOptions;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import race.download.model.speedmap.SmMeeting;
import race.download.model.review.RvMeeting;
import race.download.model.track.Site;
import race.download.model.track.Track;

public class App extends Application {

	private static final String dateShowPattern = "EEE, dd MMM yyyy";
	private String trackName = "";
	private TextField codeText;
	private String code = "";
	private List<Track> tracks;
	private List<Site> sites;
	private LocalDate date;
	private DatePicker datePicker;
	private ComboBox<String> tracksCombo;
	private Boolean isReview = true;
	private Boolean isSectionals = true;
	private Boolean isWeb = true;
	final private Datastore datastore = Morphia.createDatastore(MongoClients.create(), "races", getOpts());

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		org.apache.log4j.BasicConfigurator.configure(new NullAppender());

		datastore.getMapper().mapPackage("race.download.model");
		datastore.ensureIndexes();
		tracks = getTracks();
		sites = getSites();

		VBox root = new VBox();

		Label codeLabel = new Label("ATC code:");
		codeLabel.setPrefWidth(100);
		codeLabel.setTextAlignment(TextAlignment.LEFT);
		codeLabel.setStyle("-fx-font-weight: bold");
		codeText = new TextField();
		codeText.setPrefColumnCount(4);
		codeText.setPromptText("dddd");
		HBox codeBox = new HBox();
		codeBox.setPadding(new Insets(15, 12, 15, 12));
		codeBox.getChildren().addAll(codeLabel, codeText);
		codeText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				code = newValue;
			}
		});

		Label sectLabel = new Label("Sectionals:  ");
		sectLabel.setStyle("-fx-font-weight: bold");
		HBox sectBox = new HBox();
		sectBox.setPadding(new Insets(15, 12, 0, 12));
		ToggleGroup sectGroup = new ToggleGroup();
		RadioButton webButton = new RadioButton("Web  ");
		webButton.setStyle("-fx-font-weight: bold");
		webButton.setToggleGroup(sectGroup);
		RadioButton fileButton = new RadioButton("File ");
		fileButton.setStyle("-fx-font-weight: bold");
		fileButton.setToggleGroup(sectGroup);
		RadioButton noneButton = new RadioButton("None ");
		noneButton.setStyle("-fx-font-weight: bold");
		noneButton.setToggleGroup(sectGroup);
		webButton.setSelected(true);
		sectBox.setPadding(new Insets(15, 12, 15, 12));
		sectBox.getChildren().addAll(sectLabel, webButton, fileButton, noneButton);
		webButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (newValue) {
					isSectionals = true;
					isWeb = newValue;
				}
			}
		});
		fileButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

				if (newValue) {
					isSectionals = true;
					isWeb = false;
				}

			}
		});
		noneButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue) {
					isSectionals = false;
				}
			}
		});

		ToggleGroup selectGroup = new ToggleGroup();
		RadioButton reviewButton = new RadioButton("Review    ");
		reviewButton.setStyle("-fx-font-weight: bold");
		RadioButton speedmapButton = new RadioButton("Speedmap");
		speedmapButton.setStyle("-fx-font-weight: bold");
		reviewButton.setToggleGroup(selectGroup);
		reviewButton.setSelected(true);
		speedmapButton.setToggleGroup(selectGroup);
		HBox selectBox = new HBox();
		selectBox.setPadding(new Insets(15, 12, 15, 12));
		selectBox.getChildren().addAll(reviewButton, speedmapButton);
		reviewButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				isReview = newValue;
				if (newValue) {
					codeText.setDisable(false);
				} else {
					codeText.setText("");
					codeText.setDisable(true);
					code = "";
				}
			}
		});

		Label dateLabel = new Label("Meeting date:");
		dateLabel.setPrefWidth(100);
		dateLabel.setTextAlignment(TextAlignment.LEFT);
		dateLabel.setStyle("-fx-font-weight: bold");
		date = LocalDate.now();
		datePicker = new DatePicker(date);
		datePicker.setPromptText(dateShowPattern);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateShowPattern);
		StringConverter<LocalDate> converter = new LocalDateStringConverter(formatter, null);
		datePicker.setConverter(converter);
		datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
					LocalDate newValue) {
				date = newValue;
			}
		});
		HBox dateBox = new HBox();
		dateBox.setPadding(new Insets(15, 12, 15, 12));
		dateBox.getChildren().addAll(dateLabel, datePicker);

		Label tracksLabel = new Label("Track:");
		tracksLabel.setPrefWidth(100);
		tracksLabel.setTextAlignment(TextAlignment.LEFT);
		tracksLabel.setStyle("-fx-font-weight: bold");
		ObservableList<String> trackNames = FXCollections.observableArrayList();
		tracks.forEach(t -> trackNames.add(t.getName()));
		tracksCombo = new ComboBox<>(trackNames);
		tracksCombo.setPrefWidth(175);
		tracksCombo.getSelectionModel().selectFirst();
		trackName = tracksCombo.getValue();
		tracksCombo.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				trackName = newValue;
			}
		});
		HBox tracksBox = new HBox();
		tracksBox.setPadding(new Insets(15, 12, 15, 12));
		tracksBox.getChildren().addAll(tracksLabel, tracksCombo);

		TextArea msgArea = new TextArea("Messages will appear here.");

		Button processBtn = new Button("Process");
		processBtn.setPrefWidth(90);
		processBtn.setTextAlignment(TextAlignment.CENTER);
		processBtn.setOnAction(event -> {
			Track track = tracks.stream().filter(t -> t.getName() == trackName).findFirst().get();

			HashMap<String, String> urls = getUrls(track, date, code);
			if (isReview) {
				Query<SmMeeting> query = datastore.find(SmMeeting.class);
				SmMeeting speedmap = query.filter(Filters.eq("track", trackName), Filters.eq("date", date)).first();
				ReviewProcessor review = new ReviewProcessor();
				RvMeeting rvMeeting = review.process(track, date, urls, speedmap, isSectionals, isWeb);

				if (rvMeeting != null) {
					datastore.save(rvMeeting);
					msgArea.setText("SUCCESFULL UPDATE");
				} else {
					msgArea.setText("Unable to download meeting for " + trackName + " on " + date);
				}

			} else {
				SpeedmapProcessor processor = new SpeedmapProcessor();
				SmMeeting smMeeting = processor.process(urls.get("recent"), trackName, date);
				if (smMeeting != null) {
					datastore.save(smMeeting);
					msgArea.setText("SUCCESFULL UPDATE");
				} else {
					msgArea.setText("Unable to download meeting for " + trackName + " on " + date);
				}

			}

		});
		Button quitBtn = new Button("Quit");
		quitBtn.setPrefWidth(90);
		quitBtn.setTextAlignment(TextAlignment.CENTER);
		quitBtn.setOnAction(event -> {
			System.exit(1);
		});
		HBox buttonBox = new HBox();
		buttonBox.setPadding(new Insets(15, 12, 15, 12));
		buttonBox.setSpacing(20);
		buttonBox.setStyle("-fx-background-color:#000099;");
		buttonBox.getChildren().addAll(processBtn, quitBtn);

		Label msgLabel = new Label("MESSAGES:");
		msgLabel.setStyle("-fx-font-weight: bold");
		msgLabel.setTextAlignment(TextAlignment.LEFT);

		msgArea.setEditable(false);
		msgArea.setStyle("-fx-background-color:#ffffff;");
		msgArea.setPrefSize(380, 200);
		VBox msgBox = new VBox();
		msgBox.setPadding(new Insets(15, 12, 15, 12));
		msgBox.getChildren().addAll(msgLabel, msgArea);

		root.getChildren().addAll(selectBox, codeBox, sectBox, dateBox, tracksBox, buttonBox, msgBox);
		root.setFillWidth(true);
		root.setStyle("-fx-background-color:#9999ff;");
		Scene scene = new Scene(root, 380, 380);
		primaryStage.setTitle("Speedmap Downloader");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Boolean preCheck() {
		Query<SmMeeting> query = datastore.find(SmMeeting.class);
		long speedmapCount = query.filter(Filters.eq("track", trackName), Filters.eq("date", date)).count();
		long reviewCount = 0;
		if (isReview) {
			if (reviewCount > 0) {
				Alert alert = new Alert(AlertType.ERROR, "REVIEW ALREADY EXISTS", ButtonType.OK);
				alert.showAndWait();
				return false;
			}
			if (speedmapCount == 0) {
				Alert alert = new Alert(AlertType.ERROR, "NO SPEEDMAP FOR MEETING", ButtonType.OK);
				alert.showAndWait();
				return false;
			}
			try {
				int codeNumber = Integer.parseInt(code);
				if (codeNumber < 4000 || codeNumber > 9999) {
					Alert alert = new Alert(AlertType.ERROR, "ATC CODE MUST BE A NUMBER BETWEEN 4000 AND 9999",
							ButtonType.OK);
					alert.showAndWait();
					return false;
				}
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR, "ATC CODE MUST BE A NUMBER BETWEEN 4000 AND 9999",
						ButtonType.OK);
				alert.showAndWait();
				return false;
			}
		} else {
			if (speedmapCount > 0) {
				Alert alert = new Alert(AlertType.ERROR, "SPEEDMAP ALREADY EXISTS", ButtonType.OK);
				alert.showAndWait();
				return false;
			}
		}
		return true;
	}

	private static MapperOptions getOpts() {
		MapperOptions.Builder builder = MapperOptions.builder();
		builder.mapSubPackages(true);
		return builder.build();
	}

	private List<Track> getTracks() {

		Query<Track> query = datastore.find(Track.class);
		return query.iterator().toList();
	}

	private List<Site> getSites() {

		return datastore.find(Site.class).iterator().toList();
	}

	private HashMap<String, String> getUrls(Track track, LocalDate date, String code) {
		HashMap<String, String> urls = new HashMap<>();
		String rnswName = track.getRnswName();
		String rnswDate = date.format(DateTimeFormatter.ofPattern(track.getRnswDate()));
		String raName = track.getRaName();
		String raDate = date.format(DateTimeFormatter.ofPattern(track.getRaDate()));
		String rcomName = track.getRcomName();
		String rcomDate = date.format(DateTimeFormatter.ofPattern(track.getRcomDate()));

		sites.forEach(st -> {
			String url = st.getUrlTemplate();
			url = url.replaceFirst("<RNSWNAME>", rnswName);
			url = url.replaceFirst("<RNSWDATE>", rnswDate);
			url = url.replaceFirst("<RANAME>", raName);
			url = url.replaceFirst("<RADATE>", raDate);
			url = url.replaceFirst("<RCOMNAME>", rcomName);
			url = url.replaceFirst("<RCOMDATE>", rcomDate);
			url = url.replaceFirst("<CODE>", code);
			urls.put(st.getName(), url);
		});

		return urls;
	}
}
