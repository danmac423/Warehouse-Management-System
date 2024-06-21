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

        self.name_label = QLabel('Supplier Name')
        layout.addWidget(self.name_label)
        self.name_input = QLineEdit()
        layout.addWidget(self.name_input)

        self.address_id_label = QLabel('Address ID:')
        layout.addWidget(self.address_id_label)
        self.address_id_input = QLineEdit()
        layout.addWidget(self.address_id_input)

        self.add_button = QPushButton('Add Supplier')
        self.add_button.clicked.connect(self.add_supplier)
        layout.addWidget(self.add_button)

        self.update_button = QPushButton('Update Supplier')
        self.update_button.clicked.connect(self.update_supplier)
        layout.addWidget(self.update_button)

        self.delete_button = QPushButton('Delete Supplier')
        self.delete_button.clicked.connect(self.delete_supplier)
        layout.addWidget(self.delete_button)

        self.suppliers_table = QTableWidget()
        self.suppliers_table.setColumnCount(3)
        self.suppliers_table.setHorizontalHeaderLabels(['ID', 'Name', 'Adress Id'])
        self.suppliers_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.suppliers_table)

        self.load_suppliers_button = QPushButton('Load Suppliers')
        self.load_suppliers_button.clicked.connect(self.load_suppliers)
        self.load_suppliers()
        layout.addWidget(self.load_suppliers_button)

        self.setLayout(layout)
        self.setWindowTitle('Suppliers Manager')
        self.show()

    def add_supplier(self):
        name = self.name_input.text()
        address_id = self.address_id_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name, 'addressId': address_id})
        response = requests.post('http://localhost:8080/api/suppliers', headers=headers, data=data)

        if response.status_code == 201:
            QMessageBox.information(self, 'Success', 'Supplier added successfully')
            self.load_suppliers()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def update_supplier(self):
        selected_row = self.suppliers_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No supplier selected')
            return

        supplier_id = self.suppliers_table.item(selected_row, 0).text()
        name = self.name_input.text()
        address_id = self.address_id_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': supplier_id, 'name': name, 'addressId': address_id})
        response = requests.put(f'http://localhost:8080/api/suppliers', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Supplier updated successfully')
            self.load_suppliers()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def delete_supplier(self):
        selected_row = self.suppliers_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No product selected')
            return

        supplier_id = self.suppliers_table.item(selected_row, 0).text()
        response = requests.delete(f'http://localhost:8080/api/suppliers/{supplier_id}')

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Supplier deleted successfully')
            self.load_suppliers()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def load_suppliers(self):
        response = requests.get('http://localhost:8080/api/suppliers')

        if response.status_code == 200:
            self.suppliers_table.setRowCount(0)
            suppliers = response.json()
            for supplier in suppliers:
                row_position = self.suppliers_table.rowCount()
                self.suppliers_table.insertRow(row_position)
                self.suppliers_table.setItem(row_position, 0, QTableWidgetItem(str(supplier['id'])))
                self.suppliers_table.setItem(row_position, 1, QTableWidgetItem(supplier['name']))
                self.suppliers_table.setItem(row_position, 2, QTableWidgetItem(str(supplier['addressId'])))
        else:
            QMessageBox.warning(self, 'Error', 'Failed to load products')

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = ProductManager()
    sys.exit(app.exec_())
