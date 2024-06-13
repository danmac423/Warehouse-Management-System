import sys
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit, QPushButton, QLabel, QTableWidget, QTableWidgetItem, QMessageBox, QAbstractItemView
from PyQt5.QtWidgets import QDateEdit
from PyQt5.QtCore import QDate
import requests
import json


class OrderManager(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        self.customer_id_label = QLabel('Customer ID')
        layout.addWidget(self.customer_id_label)
        self.customer_id_input = QLineEdit()
        layout.addWidget(self.customer_id_input)

        self.worker_id_label = QLabel('Worker ID')
        layout.addWidget(self.worker_id_label)
        self.worker_id_input = QLineEdit()
        layout.addWidget(self.worker_id_input)

        self.status_label = QLabel('Status')
        layout.addWidget(self.status_label)
        self.status_input = QLineEdit()
        layout.addWidget(self.status_input)

        self.date_received_label = QLabel('Date Received')
        layout.addWidget(self.date_received_label)
        self.date_received_input = QDateEdit()
        self.date_received_input.setCalendarPopup(True)
        self.date_received_input.setDate(QDate.currentDate())
        layout.addWidget(self.date_received_input)

        self.add_button = QPushButton('Add Order')
        self.add_button.clicked.connect(self.add_order)
        layout.addWidget(self.add_button)

        self.update_button = QPushButton('Update Order')
        self.update_button.clicked.connect(self.update_order)
        layout.addWidget(self.update_button)

        self.assign_worker_button = QPushButton('Assign Worker')
        self.assign_worker_button.clicked.connect(self.assign_worker)
        layout.addWidget(self.assign_worker_button)

        self.pack_button = QPushButton('Pack Order')
        self.pack_button.clicked.connect(self.pack_order)
        layout.addWidget(self.pack_button)

        self.orders_table = QTableWidget()
        self.orders_table.setColumnCount(7)
        self.orders_table.setHorizontalHeaderLabels(['ID', 'Customer ID', 'Date Processed', 'Worker ID', 'Status', 'Date Received', 'Total Price'])
        self.orders_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.orders_table)

        self.load_orders_button = QPushButton('Load Orders')
        self.load_orders_button.clicked.connect(self.load_orders)
        self.load_orders()
        layout.addWidget(self.load_orders_button)

        self.setLayout(layout)
        self.setWindowTitle('Orders Manager')
        self.show()

    def add_order(self):
        customer_id = self.customer_id_input.text()
        worker_id = self.worker_id_input.text()
        date_received = self.date_received_input.date().toString('yyyy-MM-dd')
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'customerId': customer_id, 'workerId': worker_id, 'dateReceived': date_received})
        response = requests.post('http://localhost:8080/api/orders', headers=headers, data=data)

        if response.status_code == 201:
            QMessageBox.information(self, 'Success', 'Order added successfully')
            self.load_orders()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def update_order(self):
        selected_row = self.orders_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No order selected')
            return

        order_id = self.orders_table.item(selected_row, 0).text()
        customer_id = self.customer_id_input.text()
        worker_id = self.worker_id_input.text()
        status = self.orders_table.item(selected_row, 4).text()
        date_received = self.date_received_input.date().toString('yyyy-MM-dd')
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': order_id, 'customerId': customer_id, 'workerId': worker_id, 'status': status, 'dateReceived': date_received})
        response = requests.put('http://localhost:8080/api/orders', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Order updated successfully')
            self.load_orders()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def assign_worker(self):
        selected_row = self.orders_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No order selected')
            return

        # DOCELOWO WYSWIETLAMY TYLKO TE KTORE SA RECEIVED
        status = self.orders_table.item(selected_row, 4).text()
        if status != 'received':
            QMessageBox.warning(self, 'Error', 'Order must be received')
            return

        order_id = self.orders_table.item(selected_row, 0).text()
        worker_id = self.worker_id_input.text()

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': order_id,
                            'workerId': worker_id})
        response = requests.put(f'http://localhost:8080/api/orders/assignWorker', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Worker assigned successfully')
            self.load_orders()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def pack_order(self):
        selected_row = self.orders_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No order selected')
            return

        # DOCELOWO WYSWIETLAMY TYLKO TE KTORE SA RECEIVED (czyli chyba wszystkie?)
        status = self.orders_table.item(selected_row, 4).text()
        if status != 'received':
            QMessageBox.warning(self, 'Error', 'Order must be received')
            return

        order_id = self.orders_table.item(selected_row, 0).text()
        worker_id = self.orders_table.item(selected_row, 3).text()
        status = self.orders_table.item(selected_row, 4).text()

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': order_id,
                           'workerId': worker_id,
                            'status': status})
        response = requests.put(f'http://localhost:8080/api/orders/pack', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'Order packed successfully')
            self.load_orders()
        else:
            body = json.loads(response.text)
            QMessageBox.warning(self, 'Error', body.get('message'))

    def load_orders(self):
        try:
            response = requests.get('http://localhost:8080/api/orders')

            if response.status_code == 200:
                self.orders_table.setRowCount(0)
                orders = response.json()
                for order in orders:
                    row_position = self.orders_table.rowCount()
                    self.orders_table.insertRow(row_position)
                    self.orders_table.setItem(row_position, 0, QTableWidgetItem(str(order['id'])))
                    self.orders_table.setItem(row_position, 1, QTableWidgetItem(str(order['customerId'])))
                    self.orders_table.setItem(row_position, 2, QTableWidgetItem(str(order['dateProcessed'])))
                    self.orders_table.setItem(row_position, 3, QTableWidgetItem(str(order['workerId'])))
                    self.orders_table.setItem(row_position, 4, QTableWidgetItem(str(order['status'])))
                    self.orders_table.setItem(row_position, 5, QTableWidgetItem(str(order['dateReceived'])))
                    self.orders_table.setItem(row_position, 6, QTableWidgetItem(str(order['totalPrice'])))
            else:
                QMessageBox.warning(self, 'Error', 'Failed to load orders')
        except Exception as e:
            QMessageBox.warning(self, 'Error', str(e))


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = OrderManager()
    sys.exit(app.exec_())
