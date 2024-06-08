import sys
from PyQt5.QtWidgets import (QApplication, QWidget, QVBoxLayout, QLineEdit,
                             QPushButton, QLabel, QTableWidget, QTableWidgetItem,
                             QMessageBox, QAbstractItemView)
import requests
import json

class LoginWindow(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        self.username_label = QLabel('Username')
        layout.addWidget(self.username_label)
        self.username_input = QLineEdit()
        layout.addWidget(self.username_input)

        self.password_label = QLabel('Password')
        layout.addWidget(self.password_label)
        self.password_input = QLineEdit()
        self.password_input.setEchoMode(QLineEdit.Password)
        layout.addWidget(self.password_input)

        self.login_button = QPushButton('Login')
        self.login_button.clicked.connect(self.login)
        layout.addWidget(self.login_button)

        self.setLayout(layout)
        self.setWindowTitle('Login')
        self.show()

    def login(self):
        username = self.username_input.text()
        password = self.password_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'username': username, 'password': password})

        response = requests.post('http://localhost:8080/api/auth/login', headers=headers, data=data)

        if response.status_code == 200:
            token = response.json().get('accessToken')
            self.open_category_manager(token)
        else:
            QMessageBox.warning(self, 'Error', 'Failed to login')

    def open_category_manager(self, token):
        self.category_manager = CategoryManager(token)
        self.category_manager.show()
        self.close()

class CategoryManager(QWidget):
    def __init__(self, token):
        super().__init__()
        self.token = token
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        self.name_label = QLabel('Category Name')
        layout.addWidget(self.name_label)
        self.name_input = QLineEdit()
        layout.addWidget(self.name_input)

        self.add_button = QPushButton('Add Category')
        self.add_button.clicked.connect(self.add_category)
        layout.addWidget(self.add_button)

        self.update_button = QPushButton('Update Category')
        self.update_button.clicked.connect(self.update_category)
        layout.addWidget(self.update_button)

        self.delete_button = QPushButton('Delete Category')
        self.delete_button.clicked.connect(self.delete_category)
        layout.addWidget(self.delete_button)

        self.categories_table = QTableWidget()
        self.categories_table.setColumnCount(3)  # Zmieniamy na 3 kolumny
        self.categories_table.setHorizontalHeaderLabels(['ID', 'Name', 'Product Count'])
        self.categories_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.categories_table)

        self.load_categories_button = QPushButton('Load Categories')
        self.load_categories_button.clicked.connect(self.load_categories)
        self.load_categories()
        layout.addWidget(self.load_categories_button)

        self.setLayout(layout)
        self.setWindowTitle('Category Manager')
        self.show()

    def add_category(self):
        name = self.name_input.text()
        headers = {'Content-Type': 'application/json', 'Authorization': f'Bearer {self.token}'}
        data = json.dumps({'name': name})

        response = requests.post('http://localhost:8080/api/categories', headers=headers, data=data)

        if response.status_code == 201:
            QMessageBox.information(self, 'Success', 'Category added successfully')
            self.load_categories()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def update_category(self):
        selected_row = self.categories_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No category selected')
            return

        category_id = self.categories_table.item(selected_row, 0).text()
        name = self.name_input.text()
        headers = {'Content-Type': 'application/json', 'Authorization': f'Bearer {self.token}'}
        data = json.dumps({'name': name, 'id': category_id})

        response = requests.put(f'http://localhost:8080/api/categories', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Category updated successfully')
            self.load_categories()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def delete_category(self):
        selected_row = self.categories_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No category selected')
            return

        category_id = self.categories_table.item(selected_row, 0).text()
        headers = {'Content-Type': 'application/json', 'Authorization': f'Bearer {self.token}'}
        response = requests.delete(f'http://localhost:8080/api/categories/{category_id}', headers=headers)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Category deleted successfully')
            self.load_categories()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def load_categories(self):
        headers = {'Content-Type': 'application/json', 'Authorization': f'Bearer {self.token}'}
        response = requests.get('http://localhost:8080/api/categories', headers=headers)

        if response.status_code == 200:
            self.categories_table.setRowCount(0)
            categories = response.json()
            for category in categories:
                row_position = self.categories_table.rowCount()
                self.categories_table.insertRow(row_position)
                self.categories_table.setItem(row_position, 0, QTableWidgetItem(str(category['id'])))
                self.categories_table.setItem(row_position, 1, QTableWidgetItem(category['name']))
                self.categories_table.setItem(row_position, 2, QTableWidgetItem(str(category['productCount'])))
        else:
            QMessageBox.warning(self, 'Error', 'Failed to load categories')

if __name__ == '__main__':
    app = QApplication(sys.argv)
    login = LoginWindow()
    sys.exit(app.exec_())
