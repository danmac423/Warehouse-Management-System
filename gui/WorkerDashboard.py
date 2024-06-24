from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox, QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit, QDateEdit, QListWidget
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel, QDate
import requests
import json
from functools import partial

class WorkerDashboard(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables

        self._setup_ui()
        layout = QVBoxLayout()

        layout.addWidget(self.more_widget)
        layout.addWidget(self.orders_widget)
        layout.addWidget(self.supplies_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)

        self.load_supplies()
        self.load_orders()

    def set_worker_id(self, worker_id):
        self.worker_id = worker_id

    def _setup_ui(self):
        self._init_console()
        self._init_more_box()

        ###
        self.orders_widget = QGroupBox("Orders")
        self.orders_widget.setMinimumHeight(210)
        self.orders_widget.setMaximumHeight(350)
        self.orders_layout = QGridLayout(self.orders_widget)

        self.table_order = QTableWidget()
        self.table_order_scrollArea = QScrollArea()

        self._init_table_order()
        self.orders_layout.addWidget(self.table_order_scrollArea)
        ###

        ###
        self.supplies_widget = QGroupBox("Supplies")
        self.supplies_widget.setMinimumHeight(210)
        self.supplies_widget.setMaximumHeight(350)
        self.supplies_layout = QGridLayout(self.supplies_widget)

        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()

        self._init_table()
        self.supplies_layout.addWidget(self.table_scrollArea)
        ###

    def _init_console(self):
        self.console_box = QGroupBox("Last operation status:")
        self.console_layout = QVBoxLayout()
        self.console_box.setLayout(self.console_layout)

        self.console = QTextEdit()
        self.console.setReadOnly(True)
        self.console_layout.addWidget(self.console)
        self.console_box.setMinimumHeight(80)
        self.console_box.setMaximumHeight(90)

    def writeToConsole(self, message):
        current_time = QTime.currentTime()
        formatted_time = current_time.toString("hh:mm:ss")
        self.console.clear()
        self.console.append(formatted_time + " >>   " + message)

    def _init_table(self):
        self.table_scrollArea.setWidget(self.table)
        self.table_scrollArea.setWidgetResizable(True)
        self.table_scrollArea.setMinimumHeight(120)
        self.table_scrollArea.setMaximumHeight(300)

        self.table.setColumnCount(9)
        self.table.setHorizontalHeaderLabels(['ID', 'Supplier', 'Worker', 'Status', 'Expected Date', 'Arrival Date', 'Product', 'Amount', 'Unpack'])
        self.table.horizontalHeader().setSectionResizeMode(0, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(3, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(4, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(5, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(6, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(7, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(8, QHeaderView.Stretch)
        self.table.setSelectionBehavior(QTableWidget.SelectRows)
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def populate_table(self, supplies):
        self.table.clearContents()
        self.table.setRowCount(0)
        for supply in supplies:
            supplier = supply['supplier']
            worker = supply['worker']
            product = supply['product']

            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item = QTableWidgetItem(str(supply['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)

            item = QTableWidgetItem(supplier["name"])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)

            item = QTableWidgetItem(worker['username'] if worker else 'None')
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)

            item = QTableWidgetItem(supply['status'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)

            item = QTableWidgetItem(supply['expectedDate'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)

            item = QTableWidgetItem(supply['arrivalDate'] if supply['arrivalDate'] else 'None')
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 5, item)

            item = QTableWidgetItem(product['name'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 6, item)

            item = QTableWidgetItem(str(supply['amount']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 7, item)

            unpack_button = QPushButton('Unpack')
            unpack_button.clicked.connect(partial(self.unpack_supply, row_position))
            self.table.setCellWidget(row_position, 8, unpack_button)

    def load_supplies(self):
        params = {}
        params['workerId'] = self.globalVariables.loged_workerID
        response = requests.get('http://localhost:8080/api/supplies', params=params, headers=self.globalVariables.http_headers)

        if response.status_code == 200:
            self.writeToConsole("Supplies loaded successfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            supplies = response.json()
            self.populate_table(supplies)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def select_row(self, row):
        self.table.setSelectionMode(QTableWidget.SingleSelection)
        self.table.selectRow(row)
        index = self.table.model().index(row, 8)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 9)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 10)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)

        self.table.setSelectionMode(QTableWidget.NoSelection)

    def unpack_supply(self, row_position):
        supply_id = self.table.item(row_position, 0).text()
        worker_id = self.globalVariables.loged_workerID


        data = json.dumps({'id': supply_id, 'worker': {'id': worker_id}} )
        response = requests.put('http://localhost:8080/api/supplies/unpack', data=data, headers=self.globalVariables.http_headers)

        if response.status_code == 200:
            self.load_supplies()
            self.writeToConsole(f'Unpacking of supply id: {supply_id} successfully done')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def _init_more_box(self):

        self.products = QListWidget()

        self.customer_name_last_name = QLineEdit(readOnly = True)
        self.customer_email = QLineEdit(readOnly = True)
        self.customer_address = QLineEdit(readOnly = True)

        products_layout = QVBoxLayout()
        products_layout.setContentsMargins(0, 0, 0, 0)
        products_layout.addWidget(self.products)
        self.more_list = [

            self.customer_name_last_name,
            self.customer_email,
            self.customer_address
        ]


        more_layout = QGridLayout()
        more_layout.addWidget(self.products, 0, 0, 1, 2)
        more_layout.addWidget(self.customer_name_last_name, 1, 0)
        more_layout.addWidget(self.customer_email, 1, 1)
        more_layout.addWidget(self.customer_address, 2, 0, 1, 2)


        self.more_widget = QGroupBox('More info')
        self.more_widget.setLayout(more_layout)
        more_layout.setAlignment(Qt.AlignTop)
        self.more_widget.setMinimumHeight(150)
        self.more_widget.setMaximumHeight(250)

    def _init_table_order(self):
        self.table_order_scrollArea.setWidget(self.table_order)
        self.table_order_scrollArea.setWidgetResizable(True)
        self.table_order_scrollArea.setMinimumHeight(120)
        self.table_order_scrollArea.setMaximumHeight(300)

        self.table_order.setColumnCount(8)
        self.table_order.setHorizontalHeaderLabels(['ID', 'Customer email', 'Worker', 'Data Received', 'Status', 'Total Price', 'Pack', 'More'])
        self.table_order.horizontalHeader().setSectionResizeMode(0, QHeaderView.Stretch)
        self.table_order.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch)
        self.table_order.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)
        self.table_order.horizontalHeader().setSectionResizeMode(3, QHeaderView.Stretch)
        self.table_order.horizontalHeader().setSectionResizeMode(4, QHeaderView.Stretch)
        self.table_order.horizontalHeader().setSectionResizeMode(5, QHeaderView.Stretch)
        self.table_order.horizontalHeader().setSectionResizeMode(6, QHeaderView.Stretch)
        self.table_order.horizontalHeader().setSectionResizeMode(7, QHeaderView.Stretch)
        self.table_order.setSelectionBehavior(QTableWidget.SelectRows)
        self.table_order.setSelectionMode(QTableWidget.NoSelection)


    def populate_table_order(self, orders):
        self.table_order.clearContents()
        self.table_order.setRowCount(0)
        for order in orders:
            customer = order['customer']
            worker = order['worker']

            row_position = self.table_order.rowCount()
            self.table_order.insertRow(row_position)

            item = QTableWidgetItem(str(order['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table_order.setItem(row_position, 0, item)

            item =   QTableWidgetItem(str(customer['email']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table_order.setItem(row_position, 1, item)

            item =  QTableWidgetItem(str(worker['username']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table_order.setItem(row_position, 2, item)

            item =  QTableWidgetItem(str(order['dateReceived']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table_order.setItem(row_position, 3, item)

            item =  QTableWidgetItem(str(order['status']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table_order.setItem(row_position, 4, item)

            item =  QTableWidgetItem(str(order['totalPrice']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table_order.setItem(row_position, 5, item)

            more_button = QPushButton('Pack')
            more_button.clicked.connect(partial(self.pack_order, row_position))
            self.table_order.setCellWidget(row_position, 6, more_button)

            more_button = QPushButton('More')
            more_button.clicked.connect(partial(self.show_more, row_position))
            self.table_order.setCellWidget(row_position, 7, more_button)

    def pack_order(self, row_position):
        order_id = self.table.item(row_position, 0).text()
        worker_id = self.globalVariables.loged_workerID

        data = json.dumps({'id': order_id, 'worker': {'id': worker_id}} )
        response = requests.put('http://localhost:8080/api/supplies/unpack', data=data, headers=self.globalVariables.http_headers)

        if response.status_code == 200:
            self.load_supplies()
            self.writeToConsole(f'Packing of order id: {order_id} successfully done')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')


    def load_orders(self):
        param = {}
        param['workerId'] = self.globalVariables.loged_workerID

        response = requests.get('http://localhost:8080/api/orders', headers=self.globalVariables.http_headers, params=param)

        if response.status_code == 200:
            self.writeToConsole("Orders loaded sucessfully")
            self.table_order.clearContents()
            self.table_order.setRowCount(0)
            orders = response.json()

            self.populate_table_order(orders)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def clear_more_list(self):
        self.more_widget.setTitle("More info")
        self.products.clear()
        for item in self.more_list:
                item.clear()

    def show_more(self, rowposition):
        order_id = self.table.item(rowposition, 0).text()
        response = requests.get(f'http://localhost:8080/api/orders/{order_id}', headers=self.globalVariables.http_headers)

        if response.status_code == 200:
            order = response.json()

            self.populate_more_info(order)
            self.writeToConsole(f"More info on order {order_id} loaded successfully")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def populate_more_info(self, order):
        self.clear_more_list()
        self.more_widget.setTitle(f"More info on Order ID: {order['id']}")

        basket_products = order['products']
        customer = order['customer']


        for item in basket_products:
            self.products.addItem(format_item(item))

        self.customer_name_last_name.setText(f"{customer['name']} {customer['lastName']}")
        self.customer_email.setText(f"{customer['email']}")
        self.customer_address.setText(f"{customer['address']['country']}, {customer['address']['city']}, {customer['address']['street']}, {customer['address']['houseNumber']}, {customer['address']['postalCode']}")

def format_item(item):

    category = item['category']

    return f"Product: {item['name']}, Category: {category['name']}, Price: {item['price']}, Amount: {item['amount']}, Total Price: {item['totalPrice']}"