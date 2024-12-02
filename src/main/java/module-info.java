module com.sokoban {
    // JavaFX模块依赖
    requires javafx.controls;    // JavaFX基础控件
    requires javafx.fxml;       // FXML支持
    requires javafx.graphics;   // JavaFX图形界面

    // 允许javafx.fxml访问我们的控制器类
    opens com.sokoban.controllers to javafx.fxml;
    exports com.sokoban.controllers to javafx.graphics;

    // 导出包，允许其他模块访问
    exports com.sokoban;
}