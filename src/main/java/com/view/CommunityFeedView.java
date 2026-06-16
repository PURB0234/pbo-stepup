package com.view;

import com.api.CommunityFeedService;
import com.api.SessionManager;
import com.models.Post;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

/**
 * Community Feed View — Social media-style feed.
 * Menampilkan post card bergaya Facebook/Instagram.
 * Mendukung CRUD dengan role-based access (Admin vs User).
 */
public class CommunityFeedView {
    private VBox root;
    private VBox feedContainer;
    private ObservableList<Post> postData;
    private boolean isAdmin;
    private int currentUserId;
    private String currentUsername;

    // Image base URL untuk gambar upload di server
    private static final String IMAGE_BASE_URL = "http://localhost/webpro-stepup/uploads/";

    // Warna palette (konsisten dengan existing app)
    private static final String COLOR_PRIMARY = "#1a73e8";
    private static final String COLOR_PRIMARY_DARK = "#1557b0";
    private static final String COLOR_BG = "#f0f2f5";
    private static final String COLOR_CARD = "#ffffff";
    private static final String COLOR_TEXT = "#1c1e21";
    private static final String COLOR_TEXT_SECONDARY = "#65676b";
    private static final String COLOR_BORDER = "#e4e6eb";
    private static final String COLOR_DANGER = "#ea4335";
    private static final String COLOR_SUCCESS = "#34a853";

    // Warna avatar berdasarkan index
    private static final String[] AVATAR_COLORS = {
            "#1a73e8", "#ea4335", "#34a853", "#fbbc04",
            "#9c27b0", "#00bcd4", "#ff5722", "#607d8b",
            "#e91e63", "#3f51b5", "#009688", "#ff9800"
    };

    public CommunityFeedView() {
        this.isAdmin = SessionManager.getInstance().isAdmin();
        this.currentUserId = SessionManager.getInstance().getCurrentUser() != null
                ? SessionManager.getInstance().getCurrentUser().getId() : 0;
        this.currentUsername = SessionManager.getInstance().getCurrentUser() != null
                ? SessionManager.getInstance().getCurrentUser().getNama() : "Anonymous";

        root = new VBox(0);
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");

        // ========================
        // HEADER BAR
        // ========================
        VBox headerSection = createHeader();

        // ========================
        // FEED AREA
        // ========================
        feedContainer = new VBox(16);
        feedContainer.setPadding(new Insets(20, 0, 20, 0));
        feedContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(feedContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-background: " + COLOR_BG + ";" +
                "-fx-border-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(headerSection, scrollPane);

        // Load data awal
        loadFeed();
    }

    /**
     * Membuat header section dengan judul dan Create Post button.
     */
    private VBox createHeader() {
        VBox headerWrapper = new VBox(0);

        // Main header bar
        HBox header = new HBox(12);
        header.setPadding(new Insets(18, 28, 18, 28));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + COLOR_BORDER + ";" +
                "-fx-border-width: 0 0 1 0;");

        // Title with icon
        Label icon = new Label("\uD83D\uDCE2");
        icon.setFont(Font.font("System", 22));

        Label title = new Label("Community Feed");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        // Spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create Post button
        Button btnCreate = new Button("\u270F\uFE0F  Create Post");
        btnCreate.setPrefHeight(38);
        btnCreate.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;");

        btnCreate.setOnMouseEntered(e -> btnCreate.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY_DARK + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"));

        btnCreate.setOnMouseExited(e -> btnCreate.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 20;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;"));

        btnCreate.setOnAction(e -> showCreatePostDialog());

        // Refresh button
        Button btnRefresh = new Button("\uD83D\uDD04");
        btnRefresh.setPrefHeight(38);
        btnRefresh.setPrefWidth(38);
        btnRefresh.setStyle(
                "-fx-background-color: " + COLOR_BG + ";" +
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 16px;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: " + COLOR_BORDER + ";" +
                "-fx-border-radius: 8;");
        btnRefresh.setOnAction(e -> loadFeed());

        header.getChildren().addAll(icon, title, spacer, btnCreate, btnRefresh);

        // Compose prompt bar (mirip Facebook "What's on your mind?")
        HBox composeBar = new HBox(12);
        composeBar.setPadding(new Insets(14, 28, 14, 28));
        composeBar.setAlignment(Pos.CENTER_LEFT);
        composeBar.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + COLOR_BORDER + ";" +
                "-fx-border-width: 0 0 1 0;");

        // Avatar di compose bar
        StackPane composeAvatar = createAvatar(currentUsername, 36);

        // Compose button (klik untuk buka dialog)
        String firstName = currentUsername.contains(" ")
                ? currentUsername.substring(0, currentUsername.indexOf(" "))
                : currentUsername;

        Button composeBtn = new Button("What's on your mind, " + firstName + "?");
        composeBtn.setMaxWidth(Double.MAX_VALUE);
        composeBtn.setPrefHeight(40);
        composeBtn.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(composeBtn, Priority.ALWAYS);
        composeBtn.setStyle(
                "-fx-background-color: " + COLOR_BG + ";" +
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;");

        composeBtn.setOnMouseEntered(e -> composeBtn.setStyle(
                "-fx-background-color: #e4e6eb;" +
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"));

        composeBtn.setOnMouseExited(e -> composeBtn.setStyle(
                "-fx-background-color: " + COLOR_BG + ";" +
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-background-radius: 20;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;" +
                "-fx-border-color: transparent;"));

        composeBtn.setOnAction(e -> showCreatePostDialog());

        composeBar.getChildren().addAll(composeAvatar, composeBtn);

        headerWrapper.getChildren().addAll(header, composeBar);
        return headerWrapper;
    }

    /**
     * Load semua post dari API ke feed.
     */
    private void loadFeed() {
        List<Post> posts = CommunityFeedService.getAllPosts();
        postData = FXCollections.observableArrayList(posts);
        renderFeed();
    }

    /**
     * Render semua post card ke feedContainer.
     */
    private void renderFeed() {
        feedContainer.getChildren().clear();

        if (postData.isEmpty()) {
            VBox emptyState = createEmptyState();
            feedContainer.getChildren().add(emptyState);
            return;
        }

        for (Post post : postData) {
            VBox card = createPostCard(post);
            feedContainer.getChildren().add(card);
        }
    }

    /**
     * Empty state saat tidak ada post.
     */
    private VBox createEmptyState() {
        VBox emptyBox = new VBox(12);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPadding(new Insets(60, 20, 60, 20));

        Label emptyIcon = new Label("\uD83D\uDCED");
        emptyIcon.setFont(Font.font("System", 48));

        Label emptyTitle = new Label("No Posts Yet");
        emptyTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        emptyTitle.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";");

        Label emptySubtitle = new Label("Be the first to share something with the community!");
        emptySubtitle.setStyle("-fx-text-fill: #8a8d91; -fx-font-size: 14px;");

        Button btnCreateFirst = new Button("Create First Post");
        btnCreateFirst.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-padding: 10 24;" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;");
        btnCreateFirst.setOnAction(e -> showCreatePostDialog());

        emptyBox.getChildren().addAll(emptyIcon, emptyTitle, emptySubtitle, btnCreateFirst);
        return emptyBox;
    }

    /**
     * Membuat reusable Post Card component.
     */
    private VBox createPostCard(Post post) {
        VBox card = new VBox(0);
        card.setMaxWidth(600);
        card.setStyle(
                "-fx-background-color: " + COLOR_CARD + ";" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + COLOR_BORDER + ";" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1;");

        // Drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.08));
        shadow.setRadius(8);
        shadow.setOffsetX(0);
        shadow.setOffsetY(2);
        card.setEffect(shadow);

        // Hover effect
        card.setOnMouseEntered(e -> {
            DropShadow hoverShadow = new DropShadow();
            hoverShadow.setColor(Color.rgb(0, 0, 0, 0.14));
            hoverShadow.setRadius(14);
            hoverShadow.setOffsetX(0);
            hoverShadow.setOffsetY(4);
            card.setEffect(hoverShadow);
        });
        card.setOnMouseExited(e -> card.setEffect(shadow));

        // ========================
        // POST HEADER
        // ========================
        HBox postHeader = new HBox(10);
        postHeader.setPadding(new Insets(14, 16, 10, 16));
        postHeader.setAlignment(Pos.CENTER_LEFT);

        // Avatar (menggunakan foto profil jika ada, atau initial letter)
        StackPane avatar = createAvatar(post.getNamaUser(), 42);

        // User info
        VBox userInfo = new VBox(1);
        Label lblUsername = new Label(post.getNamaUser());
        lblUsername.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lblUsername.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        Label lblTimestamp = new Label(formatTimestamp(post.getCreatedAt()));
        lblTimestamp.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");

        userInfo.getChildren().addAll(lblUsername, lblTimestamp);

        // Spacer
        HBox headerSpacer = new HBox();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        postHeader.getChildren().addAll(avatar, userInfo, headerSpacer);

        // Three-dot menu (Edit/Delete) — role-based
        boolean canModify = isAdmin || (post.getUserId() == currentUserId);
        if (canModify) {
            MenuButton menuBtn = new MenuButton("\u22EF");
            menuBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                    "-fx-font-size: 18px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;" +
                    "-fx-padding: 2 8;" +
                    "-fx-border-color: transparent;");

            MenuItem editItem = new MenuItem("\u270F\uFE0F  Edit Post");
            editItem.setOnAction(e -> showEditPostDialog(post));

            MenuItem deleteItem = new MenuItem("\uD83D\uDDD1\uFE0F  Delete Post");
            deleteItem.setOnAction(e -> handleDeletePost(post));

            menuBtn.getItems().addAll(editItem, deleteItem);
            postHeader.getChildren().add(menuBtn);
        }

        // ========================
        // POST CONTENT
        // ========================
        VBox postContent = new VBox(10);
        postContent.setPadding(new Insets(0, 16, 12, 16));

        // Caption / Deskripsi
        if (post.getDeskripsi() != null && !post.getDeskripsi().isEmpty()) {
            Label lblCaption = new Label(post.getDeskripsi());
            lblCaption.setWrapText(true);
            lblCaption.setStyle(
                    "-fx-text-fill: " + COLOR_TEXT + ";" +
                    "-fx-font-size: 15px;" +
                    "-fx-line-spacing: 3;");
            lblCaption.setMaxWidth(568);
            postContent.getChildren().add(lblCaption);
        }

        // Activity stats (langkah, jarak, kalori) — tampilkan jika ada data
        boolean hasStats = hasValue(post.getLangkah()) || hasValue(post.getJarak()) || hasValue(post.getKalori());
        if (hasStats) {
            HBox statsBar = new HBox(16);
            statsBar.setPadding(new Insets(8, 12, 8, 12));
            statsBar.setAlignment(Pos.CENTER_LEFT);
            statsBar.setStyle(
                    "-fx-background-color: #f0f7ff;" +
                    "-fx-background-radius: 8;" +
                    "-fx-border-color: #d0e3ff;" +
                    "-fx-border-radius: 8;");

            if (hasValue(post.getLangkah())) {
                Label lblSteps = new Label("\uD83D\uDEB6 " + post.getLangkah() + " steps");
                lblSteps.setStyle("-fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-size: 13px; -fx-font-weight: bold;");
                statsBar.getChildren().add(lblSteps);
            }

            if (hasValue(post.getJarak())) {
                Label lblDistance = new Label("\uD83D\uDCCF " + post.getJarak() + " km");
                lblDistance.setStyle("-fx-text-fill: " + COLOR_SUCCESS + "; -fx-font-size: 13px; -fx-font-weight: bold;");
                statsBar.getChildren().add(lblDistance);
            }

            if (hasValue(post.getKalori())) {
                Label lblCalories = new Label("\uD83D\uDD25 " + post.getKalori() + " cal");
                lblCalories.setStyle("-fx-text-fill: #ff6b35; -fx-font-size: 13px; -fx-font-weight: bold;");
                statsBar.getChildren().add(lblCalories);
            }

            postContent.getChildren().add(statsBar);
        }

        // Image (jika ada gambar)
        if (post.getGambar() != null && !post.getGambar().isEmpty()) {
            try {
                String imageUrl = IMAGE_BASE_URL + post.getGambar();
                Image img = new Image(imageUrl, 568, 0, true, true, true);
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(568);
                imageView.setPreserveRatio(true);

                // Rounded clip
                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(568, 300);
                clip.setArcWidth(12);
                clip.setArcHeight(12);
                imageView.setClip(clip);

                VBox imageWrapper = new VBox(imageView);
                imageWrapper.setAlignment(Pos.CENTER);
                imageWrapper.setPadding(new Insets(4, 0, 0, 0));
                postContent.getChildren().add(imageWrapper);
            } catch (Exception ex) {
                // Ignore invalid image URLs silently
            }
        }

        // ========================
        // POST FOOTER
        // ========================
        HBox postFooter = new HBox(16);
        postFooter.setPadding(new Insets(8, 16, 12, 16));
        postFooter.setAlignment(Pos.CENTER_LEFT);
        postFooter.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-width: 1 0 0 0;");

        Label lblLike = new Label("\uD83D\uDC4D Like");
        lblLike.setStyle(
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 12;" +
                "-fx-background-radius: 6;");
        lblLike.setOnMouseEntered(e -> lblLike.setStyle(
                "-fx-text-fill: " + COLOR_PRIMARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 12;" +
                "-fx-background-color: #e7f3ff;" +
                "-fx-background-radius: 6;"));
        lblLike.setOnMouseExited(e -> lblLike.setStyle(
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 12;" +
                "-fx-background-radius: 6;"));

        Label lblComment = new Label("\uD83D\uDCAC Comment");
        lblComment.setStyle(
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 12;" +
                "-fx-background-radius: 6;");
        lblComment.setOnMouseEntered(e -> lblComment.setStyle(
                "-fx-text-fill: " + COLOR_PRIMARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 12;" +
                "-fx-background-color: #e7f3ff;" +
                "-fx-background-radius: 6;"));
        lblComment.setOnMouseExited(e -> lblComment.setStyle(
                "-fx-text-fill: " + COLOR_TEXT_SECONDARY + ";" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 6 12;" +
                "-fx-background-radius: 6;"));

        postFooter.getChildren().addAll(lblLike, lblComment);

        // Owner badge
        if (post.getUserId() == currentUserId) {
            Label lblOwner = new Label("\u2726 Your Post");
            lblOwner.setStyle(
                    "-fx-text-fill: " + COLOR_PRIMARY + ";" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-background-color: #e7f3ff;" +
                    "-fx-padding: 4 10;" +
                    "-fx-background-radius: 12;");
            HBox footerSpacer = new HBox();
            HBox.setHgrow(footerSpacer, Priority.ALWAYS);
            postFooter.getChildren().addAll(footerSpacer, lblOwner);
        }

        card.getChildren().addAll(postHeader, postContent, postFooter);
        return card;
    }

    /**
     * Membuat circle avatar berdasarkan username.
     */
    private StackPane createAvatar(String username, double size) {
        StackPane avatarPane = new StackPane();
        avatarPane.setPrefSize(size, size);
        avatarPane.setMinSize(size, size);
        avatarPane.setMaxSize(size, size);

        Circle circle = new Circle(size / 2);
        int colorIndex = Math.abs(username.hashCode()) % AVATAR_COLORS.length;
        circle.setFill(Color.web(AVATAR_COLORS[colorIndex]));

        String initial = username.substring(0, 1).toUpperCase();
        Text text = new Text(initial);
        text.setFont(Font.font("Arial", FontWeight.BOLD, size * 0.42));
        text.setFill(Color.WHITE);

        avatarPane.getChildren().addAll(circle, text);
        return avatarPane;
    }

    /**
     * Format timestamp relatif (e.g., "2 hours ago", "Just now").
     */
    private String formatTimestamp(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            return "Unknown";
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime postTime = LocalDateTime.parse(timestamp, formatter);
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(postTime, now);
            long seconds = duration.getSeconds();

            if (seconds < 0) {
                return "Just now";
            } else if (seconds < 60) {
                return "Just now";
            } else if (seconds < 3600) {
                long minutes = seconds / 60;
                return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
            } else if (seconds < 86400) {
                long hours = seconds / 3600;
                return hours + (hours == 1 ? " hour ago" : " hours ago");
            } else if (seconds < 604800) {
                long days = seconds / 86400;
                return days + (days == 1 ? " day ago" : " days ago");
            } else {
                DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm");
                return postTime.format(displayFormatter);
            }
        } catch (DateTimeParseException e) {
            return timestamp;
        }
    }

    // ========================================
    // CRUD DIALOGS
    // ========================================

    /**
     * Dialog untuk membuat post baru.
     */
    private void showCreatePostDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create New Post");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(500);
        dialogPane.setStyle("-fx-background-color: white;");

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));

        // Dialog header
        HBox dialogHeader = new HBox(10);
        dialogHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarDialog = createAvatar(currentUsername, 42);

        VBox userInfoDialog = new VBox(2);
        Label lblName = new Label(currentUsername);
        lblName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        lblName.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        Label lblPublic = new Label("\uD83C\uDF0D Public Post");
        lblPublic.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");

        userInfoDialog.getChildren().addAll(lblName, lblPublic);
        dialogHeader.getChildren().addAll(avatarDialog, userInfoDialog);

        // Deskripsi area
        Label lblDesc = new Label("Description");
        lblDesc.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        TextArea txtDeskripsi = new TextArea();
        txtDeskripsi.setPromptText("Share your activity or thoughts...");
        txtDeskripsi.setPrefHeight(100);
        txtDeskripsi.setWrapText(true);
        txtDeskripsi.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-border-color: " + COLOR_BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;");

        // Activity stats fields (langkah, jarak, kalori)
        Label lblStats = new Label("\uD83C\uDFCB\uFE0F Activity Stats (optional)");
        lblStats.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        HBox statsFields = new HBox(10);
        statsFields.setAlignment(Pos.CENTER_LEFT);

        VBox langkahBox = new VBox(4);
        Label lblLangkah = new Label("\uD83D\uDEB6 Steps");
        lblLangkah.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");
        TextField txtLangkah = new TextField();
        txtLangkah.setPromptText("0");
        txtLangkah.setPrefWidth(120);
        txtLangkah.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        langkahBox.getChildren().addAll(lblLangkah, txtLangkah);

        VBox jarakBox = new VBox(4);
        Label lblJarak = new Label("\uD83D\uDCCF Distance (km)");
        lblJarak.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");
        TextField txtJarak = new TextField();
        txtJarak.setPromptText("0");
        txtJarak.setPrefWidth(120);
        txtJarak.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        jarakBox.getChildren().addAll(lblJarak, txtJarak);

        VBox kaloriBox = new VBox(4);
        Label lblKalori = new Label("\uD83D\uDD25 Calories");
        lblKalori.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");
        TextField txtKalori = new TextField();
        txtKalori.setPromptText("0");
        txtKalori.setPrefWidth(120);
        txtKalori.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        kaloriBox.getChildren().addAll(lblKalori, txtKalori);

        statsFields.getChildren().addAll(langkahBox, jarakBox, kaloriBox);

        content.getChildren().addAll(dialogHeader, lblDesc, txtDeskripsi, lblStats, statsFields);

        dialogPane.setContent(content);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Style the Post button
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Post");
        okButton.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 24;" +
                "-fx-background-radius: 6;");

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String deskripsi = txtDeskripsi.getText().trim();

            if (deskripsi.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Description cannot be empty!");
                return;
            }

            String langkah = txtLangkah.getText().trim();
            String jarak = txtJarak.getText().trim();
            String kalori = txtKalori.getText().trim();

            boolean success = CommunityFeedService.createPost(
                    currentUserId, deskripsi, langkah, jarak, kalori);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Post created successfully!");
                loadFeed();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to create post. Check server connection.");
            }
        }
    }

    /**
     * Dialog untuk mengedit post.
     */
    private void showEditPostDialog(Post post) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText(null);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(500);
        dialogPane.setStyle("-fx-background-color: white;");

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));

        // Dialog header
        HBox dialogHeader = new HBox(10);
        dialogHeader.setAlignment(Pos.CENTER_LEFT);

        StackPane avatarDialog = createAvatar(post.getNamaUser(), 42);

        VBox userInfoDialog = new VBox(2);
        Label lblName = new Label(post.getNamaUser());
        lblName.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        lblName.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        Label lblEditing = new Label("\u270F\uFE0F Editing Post #" + post.getId());
        lblEditing.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");

        userInfoDialog.getChildren().addAll(lblName, lblEditing);
        dialogHeader.getChildren().addAll(avatarDialog, userInfoDialog);

        // Deskripsi area (pre-filled)
        Label lblDesc = new Label("Description");
        lblDesc.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        TextArea txtDeskripsi = new TextArea(post.getDeskripsi());
        txtDeskripsi.setPrefHeight(100);
        txtDeskripsi.setWrapText(true);
        txtDeskripsi.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-border-color: " + COLOR_BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 10;");

        // Activity stats (pre-filled)
        Label lblStats = new Label("\uD83C\uDFCB\uFE0F Activity Stats (optional)");
        lblStats.setStyle("-fx-text-fill: " + COLOR_TEXT + "; -fx-font-weight: bold; -fx-font-size: 13px;");

        HBox statsFields = new HBox(10);
        statsFields.setAlignment(Pos.CENTER_LEFT);

        VBox langkahBox = new VBox(4);
        Label lblLangkah = new Label("\uD83D\uDEB6 Steps");
        lblLangkah.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");
        TextField txtLangkah = new TextField(post.getLangkah());
        txtLangkah.setPrefWidth(120);
        txtLangkah.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        langkahBox.getChildren().addAll(lblLangkah, txtLangkah);

        VBox jarakBox = new VBox(4);
        Label lblJarak = new Label("\uD83D\uDCCF Distance (km)");
        lblJarak.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");
        TextField txtJarak = new TextField(post.getJarak());
        txtJarak.setPrefWidth(120);
        txtJarak.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        jarakBox.getChildren().addAll(lblJarak, txtJarak);

        VBox kaloriBox = new VBox(4);
        Label lblKalori = new Label("\uD83D\uDD25 Calories");
        lblKalori.setStyle("-fx-text-fill: " + COLOR_TEXT_SECONDARY + "; -fx-font-size: 12px;");
        TextField txtKalori = new TextField(post.getKalori());
        txtKalori.setPrefWidth(120);
        txtKalori.setStyle("-fx-border-color: " + COLOR_BORDER + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        kaloriBox.getChildren().addAll(lblKalori, txtKalori);

        statsFields.getChildren().addAll(langkahBox, jarakBox, kaloriBox);

        content.getChildren().addAll(dialogHeader, lblDesc, txtDeskripsi, lblStats, statsFields);

        dialogPane.setContent(content);
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Save Changes");
        okButton.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8 24;" +
                "-fx-background-radius: 6;");

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String deskripsi = txtDeskripsi.getText().trim();

            if (deskripsi.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Description cannot be empty!");
                return;
            }

            String langkah = txtLangkah.getText().trim();
            String jarak = txtJarak.getText().trim();
            String kalori = txtKalori.getText().trim();

            boolean success = CommunityFeedService.updatePost(
                    post.getId(), currentUserId, isAdmin,
                    deskripsi, langkah, jarak, kalori);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Post updated successfully!");
                loadFeed();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update post.");
            }
        }
    }

    /**
     * Konfirmasi dan hapus post.
     */
    private void handleDeletePost(Post post) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Post");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete this post?\n\n\""
                + truncate(post.getDeskripsi(), 80) + "\"");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = CommunityFeedService.deletePost(
                    post.getId(), currentUserId, isAdmin);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Post deleted successfully!");
                loadFeed();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to delete post.");
            }
        }
    }

    // ========================================
    // HELPERS
    // ========================================

    private boolean hasValue(String value) {
        return value != null && !value.isEmpty() && !value.equals("0") && !value.equals("0.0");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Parent getView() {
        return root;
    }
}
