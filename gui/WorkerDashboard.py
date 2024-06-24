from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox, QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit, QDateEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel, QDate
import requests
import json
from functools import partial

class WorkerDashboard(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables

        self.current_supplier_name = ""
        self.current_worker_username = ""
        self.current_product_name = ""

        self.worker_id = '0'

        self._setup_ui()
        layout = QVBoxLayout()

        action_widget = QWidget()
        action_layout = QGridLayout()
        action_widget.setLayout(action_layout)
        action_widget.setMinimumHeight(150)
        action_widget.setMaximumHeight(250)

        layout.addWidget(self.supplies_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)

        self._init_signals()
        self.load_supplies()

    def set_worker_id(self, worker_id):
        self.worker_id = worker_id

    def _setup_ui(self):
        self._init_console()

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
        params['workerId'] = self.worker_id
        response = requests.get('http://localhost:8080/api/supplies', params=params)

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
        status = self.table.item(row_position, 3).text()
        worker_id = self.worker_id

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': supply_id, 'status': status, 'worker':{'id': worker_id}} )
        response = requests.put('http://localhost:8080/api/supplies/unpack', headers=headers, data=data)

        if response.status_code == 200:
            self.load_supplies()
            self.writeToConsole(f'Unpacking of supply id: {supply_id} successfully done')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def reset_table(self, row_position):
        for col in range(5):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
        self.table.clearSelection()
        self.table.setSelectionMode(QTableWidget.NoSelection)

