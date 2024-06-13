import sys
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit, QPushButton, QLabel, QTableWidget, QTableWidgetItem, QMessageBox, QAbstractItemView
import requests
import json

class CategoryManager(QWidget):
    def __init__(self):
        super().__init__()
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
        headers = {'Content-Type': 'application/json'}
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
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({
            'name': name,
            'id': category_id
            })
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
        response = requests.delete(f'http://localhost:8080/api/categories/{category_id}')

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Category deleted successfully')
            self.load_categories()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def load_categories(self):
        response = requests.get('http://localhost:8080/api/categories')

        if response.status_code == 200:
            self.categories_table.setRowCount(0)
            categories = response.json()
            for category in categories:
                row_position = self.categories_table.rowCount()
                self.categories_table.insertRow(row_position)
                self.categories_table.setItem(row_position, 0, QTableWidgetItem(str(category['id'])))
                self.categories_table.setItem(row_position, 1, QTableWidgetItem(category['name']))
                self.categories_table.setItem(row_position, 2, QTableWidgetItem(str(category['productCount'])))  # Dodajemy ilość produktów
        else:
            QMessageBox.warning(self, 'Error', 'Failed to load categories')

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = CategoryManager()
    sys.exit(app.exec_())
