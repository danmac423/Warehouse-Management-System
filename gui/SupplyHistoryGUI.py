import sys
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit, QPushButton, QLabel, QTableWidget, QTableWidgetItem, QMessageBox, QAbstractItemView
from PyQt5.QtWidgets import QDateEdit
from PyQt5.QtCore import QDate
import requests
import json


class SupplyManager(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        # Add all inputs
        self.supplier_name_label = QLabel('Supplier Name:')
        layout.addWidget(self.supplier_name_label)
        self.supplier_name_input = QLineEdit()
        layout.addWidget(self.supplier_name_input)

        self.worker_id_label = QLabel('Worker ID:')
        layout.addWidget(self.worker_id_label)
        self.worker_id_input = QLineEdit()
        layout.addWidget(self.worker_id_input)

        self.product_name_label = QLabel('Product Name:')
        layout.addWidget(self.product_name_label)
        self.product_name_input = QLineEdit()
        layout.addWidget(self.product_name_input)

        self.worker_button = QPushButton('Search supply by worker_id')
        self.worker_button.clicked.connect(self.load_supplies_by_worker)
        layout.addWidget(self.worker_button)

        self.product_button = QPushButton('Search supply by product name')
        self.product_button.clicked.connect(self.load_supplies_by_product)
        layout.addWidget(self.product_button)

        self.supplier_button = QPushButton('Search supply by supplier name')
        self.supplier_button.clicked.connect(self.load_supplies_by_supplier)
        layout.addWidget(self.supplier_button)

        self.supplies_table = QTableWidget()
        self.supplies_table.setColumnCount(9)
        self.supplies_table.setHorizontalHeaderLabels(['ID', 'supplier ID', 'worker ID', 'Status', 'Arrival Date', 'Processed Date', 'Expected Date', "Product ID", "Amount"])
        self.supplies_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.supplies_table)

        self.load_supplies_button = QPushButton('Load supplies')
        self.load_supplies_button.clicked.connect(self.load_supplies_all)
        self.load_supplies_all()
        layout.addWidget(self.load_supplies_button)

        self.setLayout(layout)
        self.setWindowTitle('Products Manager')
        self.show()

    def load_supplies_all(self):
        response = requests.get('http://localhost:8080/api/suppliesHistory')
        self.load_supplies(response)

    def load_supplies_by_worker(self):
        worker = self.worker_id_input.text()
        response = requests.get(f'http://localhost:8080/api/suppliesHistory/worker/{worker}')
        self.load_supplies(response)

    def load_supplies_by_supplier(self):
        supplier_name = self.supplier_name_input.text()
        response = requests.get(f'http://localhost:8080/api/suppliesHistory/worker/{supplier_name}')
        self.load_supplies(response)

    def load_supplies_by_product(self):
        product_name = self.product_name_input.text()
        response = requests.get(f'http://localhost:8080/api/suppliesHistory/worker/{product_name}')
        self.load_supplies(response)


    def load_supplies(self, response):
        try:
            if response.status_code == 200:
                self.supplies_table.setRowCount(0)
                supplies = response.json()
                for supply in supplies:
                    row_position = self.supplies_table.rowCount()
                    self.supplies_table.insertRow(row_position)
                    self.supplies_table.setItem(row_position, 0, QTableWidgetItem(str(supply['id'])))
                    self.supplies_table.setItem(row_position, 1, QTableWidgetItem(str(supply['supplierId'])))
                    self.supplies_table.setItem(row_position, 2, QTableWidgetItem(str(supply['workerId'])))
                    self.supplies_table.setItem(row_position, 3, QTableWidgetItem(str(supply['status'])))
                    self.supplies_table.setItem(row_position, 4, QTableWidgetItem(str(supply['arrivalDate'])))
                    self.supplies_table.setItem(row_position, 5, QTableWidgetItem(str(supply['processedDate'])))
                    self.supplies_table.setItem(row_position, 6, QTableWidgetItem(str(supply['expectedDate'])))
                    self.supplies_table.setItem(row_position, 7, QTableWidgetItem(str(supply['productId'])))
                    self.supplies_table.setItem(row_position, 8, QTableWidgetItem(str(supply['amount'])))
            else:
                body = response.json()
                QMessageBox.warning(self, 'Error', body.get('message', 'An error occurred'))
        except Exception as e:
            QMessageBox.warning(self, 'Error', str(e))

if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = SupplyManager()
    sys.exit(app.exec_())
