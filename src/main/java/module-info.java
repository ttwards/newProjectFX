module com.sokoban {
    requires javafx.controls;
    requires javafx.fxml;
	requires transitive javafx.graphics;
    requires static lombok;


	opens com.sokoban.ui to javafx.base, javafx.fxml;
    opens com.sokoban.controllers to javafx.fxml;
    exports com.sokoban.controllers to javafx.graphics;
	exports com.sokoban.ui to javafx.graphics;
    exports com.sokoban;
}