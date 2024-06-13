from PySide6.QtCore import Qt
from PySide6.QtWidgets import (QWidget, QVBoxLayout, QLineEdit,
                             QPushButton, QLabel, QMessageBox, QSpacerItem, QSizePolicy, QHBoxLayout)
import requests
import json

class LoginWindow(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables
        self.initUI()

    def initUI(self):
        main_layout = QVBoxLayout(self)

        vertical_spacer_top = QSpacerItem(20, 40, QSizePolicy.Minimum, QSizePolicy.Expanding)
        main_layout.addItem(vertical_spacer_top)

        title_label = QLabel('Warehouse Desktop Manager')
        title_label.setAlignment(Qt.AlignCenter)
        title_label.setStyleSheet("font-size: 30px; font-weight: bold;")
        main_layout.addWidget(title_label)

        vertical_spacer_between = QSpacerItem(20, 20, QSizePolicy.Minimum, QSizePolicy.Fixed)
        main_layout.addItem(vertical_spacer_between)

        form_layout = QVBoxLayout()

        self.username_label = QLabel('Username')
        form_layout.addWidget(self.username_label)

        self.username_input = QLineEdit()
        self.username_input.setMaximumWidth(self.width() // 2)  
        form_layout.addWidget(self.username_input)

        self.password_label = QLabel('Password')
        form_layout.addWidget(self.password_label)

        self.password_input = QLineEdit()
        self.password_input.setEchoMode(QLineEdit.Password)
        self.password_input.setMaximumWidth(self.width() // 2) 
        form_layout.addWidget(self.password_input)
        
        vertical_spacer_between_form_elements = QSpacerItem(20, 20, QSizePolicy.Minimum, QSizePolicy.Fixed)
        form_layout.addItem(vertical_spacer_between_form_elements)

        self.login_button = QPushButton('Login')
        self.login_button.clicked.connect(self.login)
        self.login_button.setMaximumWidth(self.width() // 2) 
        form_layout.addWidget(self.login_button)


        form_container = QHBoxLayout()
        horizontal_spacer_left = QSpacerItem(40, 20, QSizePolicy.Expanding, QSizePolicy.Minimum)
        form_container.addItem(horizontal_spacer_left)
        form_container.addLayout(form_layout)
        horizontal_spacer_right = QSpacerItem(40, 20, QSizePolicy.Expanding, QSizePolicy.Minimum)
        form_container.addItem(horizontal_spacer_right)

        main_layout.addLayout(form_container)

        vertical_spacer_bottom = QSpacerItem(20, 20, QSizePolicy.Minimum, QSizePolicy.Expanding)
        main_layout.addItem(vertical_spacer_bottom)

        # self.setWindowTitle('Login')
        
        self.login_status = QLabel('')
        self.login_status.setAlignment(Qt.AlignCenter)
        main_layout.addWidget(self.login_status)
        
        vertical_spacer_bottom_2 = QSpacerItem(20, 20, QSizePolicy.Minimum, QSizePolicy.Expanding)
        main_layout.addItem(vertical_spacer_bottom_2)
        
        self.setLayout(main_layout)

    def resizeEvent(self, event):
        super().resizeEvent(event)
        self.username_input.setMaximumWidth(self.width() // 2)
        self.password_input.setMaximumWidth(self.width() // 2)
        self.login_button.setMaximumWidth(self.width() // 2)

    def login(self):
        username = self.username_input.text()
        password = self.password_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'username': username, 'password': password})

        response = requests.post('http://localhost:8080/api/auth/login', headers=headers, data=data)

        if response.status_code == 200:
            token = response.json().get('accessToken')
            self.open_manager(token)
            self.login_status.setText("")
        else:

            self.login_status.setStyleSheet("QLabel { color: red; }")
            self.login_status.setText("Failed to login, try again.")

    def open_manager(self, token):
        print(token)



