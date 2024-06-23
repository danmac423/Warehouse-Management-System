from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox, QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit, QDateEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel, QDate
import requests
import json
from functools import partial

class SuppliesPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables

        self.current_supplier_name = ""
        self.current_worker_username = ""
        self.current_product_name = ""
        self.current_status = "ALL"

        self._setup_ui()
        layout = QVBoxLayout()

        action_widget = QWidget()
        action_layout = QGridLayout()
        action_widget.setLayout(action_layout)
        action_widget.setMinimumHeight(150)
        action_widget.setMaximumHeight(250)
        action_layout.addWidget(self.search_widget, 0, 0)
        action_layout.addWidget(self.add_widget, 0, 1)

        layout.addWidget(action_widget)
        layout.addWidget(self.supplies_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)

        self._init_signals()
        self.load_supplies()

    def _setup_ui(self):
        self._init_add_box()
        self._init_console()

        self.search_widget = QGroupBox("Search Supplies")
        self.search_layout = QGridLayout(self.search_widget)
        self.search_layout.setAlignment(Qt.AlignTop)

        self.search_bar_supplier = QLineEdit(self)
        self.search_bar_supplier.setPlaceholderText("Search by supplier name")
        self.search_bar_supplier.textChanged.connect(lambda text: self.filter_table_by_supplier_name(text))

        self.search_bar_worker = QLineEdit(self)
        self.search_bar_worker.setPlaceholderText("Search by worker username")
        self.search_bar_worker.textChanged.connect(lambda text: self.filter_table_by_worker_username(text))

        self.search_bar_product = QLineEdit(self)
        self.search_bar_product.setPlaceholderText("Search by product name")
        self.search_bar_product.textChanged.connect(lambda text: self.filter_table_by_product_name(text))

        self.status_dropdown = QComboBox(self)
        self.status_dropdown.addItem("All", "ALL")
        self.status_dropdown.addItem("Arrived", "arrived")
        self.status_dropdown.addItem("Underway", "underway")
        self.status_dropdown.currentIndexChanged.connect(lambda index: self.filter_table_by_status(self.status_dropdown.itemData(index)))

        self.reset_button = QPushButton('Reset Filters')
        self.reset_button.clicked.connect(self.reset_filters)

        self.search_layout.addWidget(QLabel('Supplier Name:'), 0, 0)
        self.search_layout.addWidget(self.search_bar_supplier, 0, 1)
        self.search_layout.addWidget(QLabel('Worker Username:'), 1, 0)
        self.search_layout.addWidget(self.search_bar_worker, 1, 1)
        self.search_layout.addWidget(QLabel('Product Name:'), 2, 0)
        self.search_layout.addWidget(self.search_bar_product, 2, 1)
        self.search_layout.addWidget(QLabel('Status:'), 3, 0)
        self.search_layout.addWidget(self.status_dropdown, 3, 1)
        self.search_layout.addWidget(self.reset_button, 4, 0, 1, 2)

        self.supplies_widget = QGroupBox("Supplies")
        self.supplies_widget.setMinimumHeight(210)
        self.supplies_widget.setMaximumHeight(350)
        self.supplies_layout = QGridLayout(self.supplies_widget)

        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()

        self._init_table()
        self.supplies_layout.addWidget(self.table_scrollArea)

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

    def _init_signals(self):
        self.globalVariables.signals.supplies_view_clicked.connect(lambda: self.load_supplies())

    def _init_add_box(self):
        form = QWidget()

        form_layout = QFormLayout()

        self.supply_name = QLineEdit()
        self.price = QLineEdit()
        self.status = QComboBox()
        self.status.addItems(["arrived", "underway"])
        self.expected_date = QDateEdit()
        self.expected_date.setCalendarPopup(True)
        self.expected_date.setDate(QDate.currentDate())
        self.arrival_date = QDateEdit()
        self.arrival_date.setCalendarPopup(True)
        self.arrival_date.setDate(QDate.currentDate())

        form_layout.addRow(QLabel('Supply Name:'), self.supply_name)
        form_layout.addRow(QLabel('Price:'), self.price)
        form_layout.addRow(QLabel('Status:'), self.status)
        form_layout.addRow(QLabel('Expected Date:'), self.expected_date)
        form_layout.addRow(QLabel('Arrival Date:'), self.arrival_date)

        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        submit_button = QPushButton('Add Supply')
        submit_button.clicked.connect(self.add_supply)

        add_layout = QGridLayout()
        add_layout.addWidget(form, 0, 0)
        add_layout.addWidget(submit_button, 1, 0)
        self.add_widget = QGroupBox('Add Supply')
        self.add_widget.setLayout(add_layout)
        add_layout.setAlignment(Qt.AlignTop)

    def _init_table(self):
        self.table_scrollArea.setWidget(self.table)
        self.table_scrollArea.setWidgetResizable(True)
        self.table_scrollArea.setMinimumHeight(120)
        self.table_scrollArea.setMaximumHeight(300)

        self.table.setColumnCount(11)
        self.table.setHorizontalHeaderLabels(['ID', 'Supplier', 'Worker', 'Status', 'Expected Date', 'Arrival Date', 'Product', 'Amount', 'Confirm', 'Assign', 'Delete'])
        self.table.horizontalHeader().setSectionResizeMode(0, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(3, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(4, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(5, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(6, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(7, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(8, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(9, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(10, QHeaderView.Stretch)
        self.table.setSelectionBehavior(QTableWidget.SelectRows)
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def filter_table_by_status(self, status):
        self.current_status = status
        self.apply_filters()

    def filter_table_by_supplier_name(self, supplier_name):
        self.current_supplier_name = supplier_name
        self.apply_filters()

    def filter_table_by_worker_username(self, username):
        self.current_worker_username = username
        self.apply_filters()

    def filter_table_by_product_name(self, product_name):
        self.current_product_name = product_name
        self.apply_filters()

    def apply_filters(self):
        status = self.current_status
        worker_username = self.current_worker_username
        supplier_name = self.current_supplier_name
        product_name = self.current_product_name

        url = 'http://localhost:8080/api/supplies/formated'
        params = {}

        if status != "ALL":
            params['status'] = status
        if worker_username:
            params['workerUsername'] = worker_username
        if supplier_name:
            params['supplierName'] = supplier_name
        if product_name:
            params['productName'] = product_name

        response = requests.get(url, params=params)
        if response.status_code == 200:
            supplies = response.json()
            self.populate_table(supplies)
            self.writeToConsole("Supplies filtered successfully")
        else:
            if response.status_code == 404:
                self.writeToConsole(f'Error: No supplies found')
                self.table.clearContents()
                self.table.setRowCount(0)
            else:
                body = json.loads(response.text)
                mess = body.get('message')
                self.writeToConsole(f'Error: {mess}')

    def add_supply(self):
        name = self.supply_name.text()
        price = self.price.text()
        status = self.status.currentText()
        expected_date = self.expected_date.date().toString("yyyy-MM-dd")
        arrival_date = self.arrival_date.date().toString("yyyy-MM-dd")

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name, 'price': price, 'status': status, 'expectedDate': expected_date, 'arrivalDate': arrival_date})
        response = requests.post('http://localhost:8080/api/supplies', headers=headers, data=data)
        print(response.status_code)
        if response.status_code == 201:
            self.clear_form()
            self.reset_filters()
            self.writeToConsole('Supply added successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def clear_form(self):
        self.supply_name.clear()
        self.price.clear()
        self.status.setCurrentIndex(0)
        self.expected_date.setDate(QDate.currentDate())
        self.arrival_date.setDate(QDate.currentDate())

    def reset_filters(self):
        self.search_bar_worker.setText("")
        self.search_bar_supplier.setText("")
        self.search_bar_product.setText("")
        self.status_dropdown.setCurrentIndex(0)
        self.current_status = "ALL"
        self.current_worker_username = ""
        self.current_supplier_name = ""
        self.current_product_name = ""
        self.load_supplies()

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

            confirm_button = QPushButton('Confirm')
            confirm_button.clicked.connect(partial(self.confirm_supply, row_position))
            self.table.setCellWidget(row_position, 8, confirm_button)

            assign_button = QPushButton('Assign')
            assign_button.clicked.connect(partial(self.assign_supply, row_position))
            self.table.setCellWidget(row_position, 9, assign_button)

            delete_button = QPushButton('Delete')
            delete_button.clicked.connect(partial(self.delete_supply, row_position))
            self.table.setCellWidget(row_position, 10, delete_button)

    def load_supplies(self):
        response = requests.get('http://localhost:8080/api/supplies')

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

    def delete_supply(self, row):
        selected_row = row
        print(selected_row)
        if selected_row == -1:
            self.writeToConsole('Error: No supply selected')
            return

        supply_id = self.table.item(selected_row, 0).text()
        print(f"supply_id: {supply_id}")
        response = requests.delete(f'http://localhost:8080/api/supplies/{supply_id}')

        if response.status_code == 200:
            self.apply_filters()
            self.writeToConsole('Success: Supply deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def edit_confirm_supply(self, row_position):
        self.select_row(row_position)

        confirm_button = QPushButton('Confirm')
        confirm_button.clicked.connect(lambda: self.confirm_supply(row_position))

        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))

        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)
        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(confirm_button, 0, 1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 8, edit_widget)

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

    def revert_edit(self, row_position):
        self.reset_table(row_position)
        self.load_supplies()
        self.writeToConsole("Reverted edit")

    def confirm_supply(self, row_position):
        supply_id = self.table.item(row_position, 0).text()
        status = self.table.item(row_position, 3).text()

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': supply_id, 'status': status})
        response = requests.put('http://localhost:8080/api/supplies/acknowledge', headers=headers, data=data)

        if response.status_code == 200:
            self.load_supplies()
            self.writeToConsole(f'Confirmation of arrival supply id: {supply_id} successfully done')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def assign_supply(self, row_position):
        self.select_row(row_position)

        workers_to_assign = QComboBox()
        workers = self.worker_list()

        for worker in workers:
            workers_to_assign.addItem(worker[0], worker[1])  # get all possible workers

        current_worker_username = self.table.item(row_position, 2).text()
        if current_worker_username:
            workers_to_assign.setCurrentText(current_worker_username)
        self.table.setCellWidget(row_position, 2, workers_to_assign)

        assign_button = QPushButton('Update')
        assign_button.clicked.connect(lambda: self.assign_supply_update(row_position, workers_to_assign.currentData()))

        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))

        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)
        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(assign_button, 0, 1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 9, edit_widget)

    def assign_supply_update(self, row_position, worker_id):
        supply_id = self.table.item(row_position, 0).text()
        status = self.table.item(row_position, 3).text()
        print(f"order_id {supply_id}")
        print(f"worked_id {worker_id}")

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': supply_id, 'workerId': worker_id, 'status': status})
        response = requests.put(f'http://localhost:8080/api/supplies/updateWorker', headers=headers, data=data)
        print(response.status_code)
        if response.status_code == 200:
            self.load_supplies()
            self.writeToConsole(f'Success: Worker assigned successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def worker_list(self):
        response = requests.get('http://localhost:8080/api/workers')

        if response.status_code == 200:
            workers = response.json()
            workers_list = [(item['username'], item['id']) for item in workers]
            return workers_list
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
            return []

    def reset_table(self, row_position):
        for col in range(5):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
        self.table.clearSelection()
        self.table.setSelectionMode(QTableWidget.NoSelection)
