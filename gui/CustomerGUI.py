import sys
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit, QPushButton, QLabel, QTableWidget, QTableWidgetItem, QMessageBox, QAbstractItemView
import requests
import json


class CustomerManager(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        self.name_label = QLabel('Customer Name')
        layout.addWidget(self.name_label)
        self.name_input = QLineEdit()
        layout.addWidget(self.name_input)

        self.last_name_label = QLabel('Customer Last Name')
        layout.addWidget(self.last_name_label)
        self.last_name_input = QLineEdit()
        layout.addWidget(self.last_name_input)

        self.address_id_label = QLabel('Address ID')
        layout.addWidget(self.address_id_label)
        self.address_id_input = QLineEdit()
        layout.addWidget(self.address_id_input)

        self.email_label = QLabel('Email')
        layout.addWidget(self.email_label)
        self.email_input = QLineEdit()
        layout.addWidget(self.email_input)

        self.add_button = QPushButton('Add Customer')
        self.add_button.clicked.connect(self.add_product)
        layout.addWidget(self.add_button)

        self.customers_table = QTableWidget()
        self.customers_table.setColumnCount(5)
        self.customers_table.setHorizontalHeaderLabels(['ID', 'Name', 'Last Name', 'Address ID', 'Email'])
        self.customers_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.customers_table)

        self.load_customers_button = QPushButton('Load Customers')
        self.load_customers_button.clicked.connect(self.load_customers)
        self.load_customers()
        layout.addWidget(self.load_customers_button)

        self.setLayout(layout)
        self.setWindowTitle('Costumers Manager')
        self.show()

    def add_product(self):
        name = self.name_input.text()
        last_name = self.last_name_input.text()
        address_id = self.address_id_input.text()
        email = self.email_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name, 'lastName': last_name, 'addressId': address_id, 'email': email})
        response = requests.post('http://localhost:8080/api/customers', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Customer added successfully')
            self.load_customers()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def load_customers(self):
        response = requests.get('http://localhost:8080/api/customers')

        if response.status_code == 200:
            self.customers_table.setRowCount(0)
            products = response.json()
            for product in products:
                row_position = self.customers_table.rowCount()
                self.customers_table.insertRow(row_position)
                self.customers_table.setItem(row_position, 0, QTableWidgetItem(str(product['id'])))
                self.customers_table.setItem(row_position, 1, QTableWidgetItem(product['name']))
                self.customers_table.setItem(row_position, 2, QTableWidgetItem(product['lastName']))
                self.customers_table.setItem(row_position, 3, QTableWidgetItem(str(product['addressId'])))
                self.customers_table.setItem(row_position, 4, QTableWidgetItem(product['email']))
        else:
            QMessageBox.warning(self, 'Error', 'Failed to load customers')

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = CustomerManager()
    sys.exit(app.exec_())
