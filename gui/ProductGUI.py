import sys
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit, QPushButton, QLabel, QTableWidget, QTableWidgetItem, QMessageBox, QAbstractItemView
import requests
import json


class ProductManager(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        self.name_label = QLabel('Product Name')
        layout.addWidget(self.name_label)
        self.name_input = QLineEdit()
        layout.addWidget(self.name_input)

        # Add all inputs
        self.price_label = QLabel('Product Price:')
        layout.addWidget(self.price_label)
        self.price_input = QLineEdit()
        layout.addWidget(self.price_input)

        self.category_id_label = QLabel('Category ID:')
        layout.addWidget(self.category_id_label)
        self.category_id_input = QLineEdit()
        layout.addWidget(self.category_id_input)

        self.stock_label = QLabel('Stock:')
        layout.addWidget(self.stock_label)
        self.stock_input = QLineEdit()
        layout.addWidget(self.stock_input)

        self.add_button = QPushButton('Add Product')
        self.add_button.clicked.connect(self.add_product)
        layout.addWidget(self.add_button)

        self.update_button = QPushButton('Update Product')
        self.update_button.clicked.connect(self.update_product)
        layout.addWidget(self.update_button)

        self.delete_button = QPushButton('Delete Product')
        self.delete_button.clicked.connect(self.delete_product)
        layout.addWidget(self.delete_button)

        self.products_table = QTableWidget()
        self.products_table.setColumnCount(5)
        self.products_table.setHorizontalHeaderLabels(['ID', 'Name', 'Price', 'Category ID', 'Stock'])
        self.products_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.products_table)

        self.load_products_button = QPushButton('Load Products')
        self.load_products_button.clicked.connect(self.load_products)
        self.load_products()
        layout.addWidget(self.load_products_button)

        self.setLayout(layout)
        self.setWindowTitle('Products Manager')
        self.show()

    def add_product(self):
        name = self.name_input.text()
        price = self.price_input.text()
        category_id = self.category_id_input.text()
        stock = self.stock_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name, 'price': price, 'categoryId': category_id, 'stock': stock})
        response = requests.post('http://localhost:8080/api/products', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Product added successfully')
            self.load_products()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def update_product(self):
        selected_row = self.products_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No product selected')
            return

        product_id = self.products_table.item(selected_row, 0).text()
        name = self.name_input.text()
        price = self.price_input.text()
        category_id = self.category_id_input.text()
        stock = self.stock_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': product_id, 'name': name, 'price': price, 'categoryId': category_id, 'stock': stock})
        response = requests.put(f'http://localhost:8080/api/products', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Product updated successfully')
            self.load_products()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def delete_product(self):
        selected_row = self.products_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No product selected')
            return

        product_id = self.products_table.item(selected_row, 0).text()
        response = requests.delete(f'http://localhost:8080/api/products/{product_id}')

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Product deleted successfully')
            self.load_products()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def load_products(self):
        response = requests.get('http://localhost:8080/api/products')

        if response.status_code == 200:
            self.products_table.setRowCount(0)
            products = response.json()
            for product in products:
                row_position = self.products_table.rowCount()
                self.products_table.insertRow(row_position)
                self.products_table.setItem(row_position, 0, QTableWidgetItem(str(product['id'])))
                self.products_table.setItem(row_position, 1, QTableWidgetItem(product['name']))
                self.products_table.setItem(row_position, 2, QTableWidgetItem(str(product['price'])))
                self.products_table.setItem(row_position, 3, QTableWidgetItem(str(product['categoryId'])))
                self.products_table.setItem(row_position, 4, QTableWidgetItem(str(product['stock'])))
        else:
            QMessageBox.warning(self, 'Error', 'Failed to load products')

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = ProductManager()
    sys.exit(app.exec_())
