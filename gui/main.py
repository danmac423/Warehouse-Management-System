
import sys
import os
import shutil
import atexit
from PySide6.QtWidgets import   QApplication, QMainWindow, QVBoxLayout, \
                                QWidget
from loginGUI import LoginWindow


class MainWindow(QMainWindow):

    def __init__(self):
        super().__init__()
        self.central_widget = QWidget()
        self.setCentralWidget(self.central_widget)
        self.central_layout = QVBoxLayout()

        self.setWindowTitle("Warehouse Application")
        self.setMinimumSize(1280, 860)
        

        self.login_widget = LoginWindow()
        self.central_layout.addWidget(self.login_widget)
        self.central_widget.setLayout(self.central_layout)

if __name__ == "__main__":
    app = QApplication(sys.argv)

    window = MainWindow()
    window.show()

    sys.exit(app.exec())
