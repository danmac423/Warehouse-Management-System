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
        self.supplier_id_label = QLabel('Supplier Id:')
        layout.addWidget(self.supplier_id_label)
        self.supplier_id_input = QLineEdit()
        layout.addWidget(self.supplier_id_input)

        self.worker_id_label = QLabel('Worker ID:')
        layout.addWidget(self.worker_id_label)
        self.worker_id_input = QLineEdit()
        layout.addWidget(self.worker_id_input)

        self.expected_date_label = QLabel('Expected date:')
        layout.addWidget(self.expected_date_label)
        self.expected_date_input = QDateEdit()
        self.expected_date_input.setCalendarPopup(True)  # Enable the calendar popup
        self.expected_date_input.setDate(QDate.currentDate())
        layout.addWidget(self.expected_date_input)

        self.product_id_label = QLabel('Product ID:')
        layout.addWidget(self.product_id_label)
        self.product_id_input = QLineEdit()
        layout.addWidget(self.product_id_input)

        self.amount_label = QLabel('Amount:')
        layout.addWidget(self.amount_label)
        self.amount_input = QLineEdit()
        layout.addWidget(self.amount_input)


        self.add_button = QPushButton('Add supply')
        self.add_button.clicked.connect(self.add_supply)
        layout.addWidget(self.add_button)

        self.update_button = QPushButton('Update supply')
        self.update_button.clicked.connect(self.update_supply)
        layout.addWidget(self.update_button)

        self.acknowledge_button = QPushButton('Acknowledge supply')
        self.acknowledge_button.clicked.connect(self.acknowledge_supply)
        layout.addWidget(self.acknowledge_button)

        self.acknowledge_button = QPushButton('Update worker')
        self.acknowledge_button.clicked.connect(self.update_worker)
        layout.addWidget(self.acknowledge_button)

        self.unpack_button = QPushButton('Unpack supply')
        self.unpack_button.clicked.connect(self.unpack_supply)
        layout.addWidget(self.unpack_button)

        self.supplies_table = QTableWidget()
        self.supplies_table.setColumnCount(9)
        self.supplies_table.setHorizontalHeaderLabels(['ID', 'supplier ID', 'worker ID', 'Status', 'Arrival Date', 'Processed Date', 'Expected Date', "Product ID", "Amount"])
        self.supplies_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.supplies_table)

        self.load_supplies_button = QPushButton('Load supplies')
        self.load_supplies_button.clicked.connect(self.load_supplies)
        self.load_supplies()
        layout.addWidget(self.load_supplies_button)

        self.setLayout(layout)
        self.setWindowTitle('Products Manager')
        self.show()

    def add_supply(self):
        supplier_id = self.supplier_id_input.text()
        product_id = self.product_id_input.text()
        amount = self.amount_input.text()
        expected_date = self.expected_date_input.date().toString("yyyy-MM-dd")
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'supplierId': supplier_id, 'expectedDate': expected_date, 'productId': product_id, 'amount': amount})
        response = requests.post('http://localhost:8080/api/supplies', headers=headers, data=data)

        if response.status_code == 201:
            QMessageBox.information(self, 'Success', 'supply added successfully')
            self.load_supplies()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def update_supply(self):
        selected_row = self.supplies_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No supply selected')
            return

        supply_id = self.supplies_table.item(selected_row, 0).text()
        supplier_id = self.supplier_id_input.text()
        status = self.supplies_table.item(selected_row, 3).text()
        expected_date = self.expected_date_input.date().toString("yyyy-MM-dd")
        product_id = self.product_id_input.text()
        amount = self.amount_input.text()


        headers = {'Content-Type': 'application/json'}
        data = json.dumps({ 'id': supply_id, 'supplierId': supplier_id, 'status': status, 'expectedDate': expected_date, 'productId': product_id, 'amount': amount})
        response = requests.put('http://localhost:8080/api/supplies', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Supply updated successfully')
            self.load_supplies()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def acknowledge_supply(self):
        selected_row = self.supplies_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No supply selected')
            return

        # DOCELOWO WYSWIETLAMY TYLKO TE KTORE SA UNDERWAY
        status = self.supplies_table.item(selected_row, 3).text()
        if status != 'underway':
            QMessageBox.warning(self, 'Error', 'Not UNDERWAY')
            return

        id = self.supplies_table.item(selected_row, 0).text()
        supplier_id = self.supplies_table.item(selected_row, 1).text()
        worker_id = self.supplies_table.item(selected_row, 2).text()
        status = self.supplies_table.item(selected_row, 3).text()
        # convert to YYYY-MM-DD
        arrival_date = self.supplies_table.item(selected_row, 4).text()
        # convert to YYYY-MM-DD
        processed_date = self.supplies_table.item(selected_row, 5).text()
        # convert to YYYY-MM-DD
        expected_date = self.supplies_table.item(selected_row, 6).text()
        product_id = self.supplies_table.item(selected_row, 7).text()
        amount = self.supplies_table.item(selected_row, 8).text()


        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': id,
                            'supplierId': supplier_id,
                            'workerId': worker_id,
                            'status': status,
                            'expectedDate': expected_date,
                            'productId': product_id,
                            'amount': amount})
        response = requests.put('http://localhost:8080/api/supplies/acknowledge', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Supply acknowledged successfully')
            self.load_supplies()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))


    def update_worker(self):
        selected_row = self.supplies_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No supply selected')
            return

        # DOCELOWO WYSWIETLAMY TYLKO TE KTORE SA ARRIVED
        status = self.supplies_table.item(selected_row, 3).text()
        if status != 'arrived':
            QMessageBox.warning(self, 'Error', 'Not ARRIVED')
            return

        id = self.supplies_table.item(selected_row, 0).text()
        supplier_id = self.supplies_table.item(selected_row, 1).text()

        status = self.supplies_table.item(selected_row, 3).text()
        # convert to YYYY-MM-DD
        arrival_date = self.supplies_table.item(selected_row, 4).text()
        # convert to YYYY-MM-DD
        processed_date = self.supplies_table.item(selected_row, 5).text()
        # convert to YYYY-MM-DD
        expected_date = self.supplies_table.item(selected_row, 6).text()
        product_id = self.supplies_table.item(selected_row, 7).text()
        amount = self.supplies_table.item(selected_row, 8).text()
        worker_id = self.worker_id_input.text()

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': id,
                            'supplierId': supplier_id,
                            'workerId': worker_id,
                            'status': status,
                            'expectedDate': expected_date,
                            'productDd': product_id,
                            'amount': amount})
        response = requests.put('http://localhost:8080/api/supplies/updateWorker', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Worker added successfully')
            self.load_supplies()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))


    def unpack_supply(self):
        selected_row = self.supplies_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No supply selected')
            return

        # DOCELOWO WYSWIETLAMY TYLKO TE KTORE SA ARRIVED
        status = self.supplies_table.item(selected_row, 3).text()
        if status != 'arrived':
            QMessageBox.warning(self, 'Error', 'Not ARRIVED')
            return

        id = self.supplies_table.item(selected_row, 0).text()
        supplier_id = self.supplies_table.item(selected_row, 1).text()
        worker_id = self.supplies_table.item(selected_row, 2).text()
        status = self.supplies_table.item(selected_row, 3).text()
        # convert to YYYY-MM-DD
        arrival_date = self.supplies_table.item(selected_row, 4).text()
        # convert to YYYY-MM-DD
        processed_date = self.supplies_table.item(selected_row, 5).text()
        # convert to YYYY-MM-DD
        expected_date = self.supplies_table.item(selected_row, 6).text()
        product_id = self.supplies_table.item(selected_row, 7).text()
        amount = self.supplies_table.item(selected_row, 8).text()


        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': id,
                            'supplierId': supplier_id,
                            'workerId': worker_id,
                            'status': status,
                            'expectedDate': expected_date,
                            'productDd': product_id,
                            'amount': amount})
        response = requests.put('http://localhost:8080/api/supplies/unpack', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Supply unpacked successfully')
            self.load_supplies()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))


    def load_supplies(self):
        try:
            response = requests.get('http://localhost:8080/api/supplies')
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
