from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel
# from PySide6.QtGui import QItemSelectionModel
import requests
import json
from functools import partial



class SuppliesHistoryPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables
        self._setup_ui()
        layout = QVBoxLayout()

        action_widget = QWidget()
        action_layout = QGridLayout()
        action_widget.setLayout(action_layout)
        action_widget.setMinimumHeight(150)
        action_widget.setMaximumHeight(250)
        action_layout.addWidget(self.search_widget, 0, 0)

        # action_layout.setColumnStretch(0, 1)
        # action_layout.setColumnStretch(1, 1)

        layout.addWidget(action_widget)
        layout.addWidget(self.supplies_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)

        self._init_signals()
        self.load_supplies_history()

    def _setup_ui(self):

        self._init_search_box()
        self._init_console()

        self.supplies_widget = QGroupBox("Supplies history")
        self.supplies_widget.setMinimumHeight(210)
        self.supplies_widget.setMaximumHeight(350)
        self.supplies_layout = QGridLayout(self.supplies_widget)

        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()

        self._init_table()
        self.supplies_layout.addWidget(self.table_scrollArea)

    def _init_search_box(self):
        self.search_widget = QGroupBox("Search supplies history")
        search_layout = QGridLayout(self.search_widget)
        search_layout.setAlignment(Qt.AlignTop)

        form_layout = QFormLayout()

        self.search_supplier_name = QLineEdit(self)
        self.search_supplier_name.textChanged.connect(self.apply_filters)

        self.search_worker_username = QLineEdit(self)
        self.search_worker_username.textChanged.connect(self.apply_filters)

        self.search_product_name = QLineEdit(self)
        self.search_product_name.textChanged.connect(self.apply_filters)

        self.search_category_name = QLineEdit(self)
        self.search_category_name.textChanged.connect(self.apply_filters)

        form_layout.addRow(QLabel('Supplier name:'), self.search_supplier_name)
        form_layout.addRow(QLabel('Worker username:'), self.search_worker_username)
        form_layout.addRow(QLabel('Product name:'), self.search_product_name)
        form_layout.addRow(QLabel('Category:'), self.search_category_name)

        form = QWidget()
        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        reset_button = QPushButton('Reset Filters')
        reset_button.clicked.connect(self.reset_filters)

        search_layout.addWidget(form, 0, 0)
        search_layout.addWidget(reset_button, 1, 0)
        self.search_widget.setLayout(search_layout)
        search_layout.setAlignment(Qt.AlignTop)

    def apply_filters(self):
        worker_username = self.search_worker_username.text()
        supplier_name = self.search_supplier_name.text()
        product_name = self.search_product_name.text()
        category_name = self.search_category_name.text()

        url = 'http://localhost:8080/api/supplies-history'
        params = {}

        if worker_username:
            params['workerUsername'] = worker_username
        if supplier_name:
            params['supplierName'] = supplier_name
        if product_name:
            params['productName'] = product_name
        if category_name:
            params['categoryName'] = category_name

        response = requests.get(url, params=params, headers=self.globalVariables.http_headers)

        if response.status_code == 200:
            supplies = response.json()
            self.populate_table(supplies)
            self.writeToConsole("Supplies history filtered")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def reset_filters(self):
        self.search_worker_username.clear()
        self.search_supplier_name.clear()
        self.search_product_name.clear()
        self.search_category_name.clear()
        self.load_supplies_history()



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
        self.globalVariables.signals.supplies_history_view_clicked.connect(lambda: self.load_supplies_history())

    def _init_table(self):
            self.table_scrollArea.setWidget(self.table)
            self.table_scrollArea.setWidgetResizable(True)
            self.table_scrollArea.setMinimumHeight(120)
            self.table_scrollArea.setMaximumHeight(300)

            self.table.setColumnCount(8)
            self.table.setHorizontalHeaderLabels(['ID', 'Supplier', 'Worker', 'Expected', 'Arrival', 'Processed', 'Product', 'Amount'])
            self.table.horizontalHeader().setSectionResizeMode(0, QHeaderView.Stretch)
            self.table.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch)
            self.table.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)
            self.table.horizontalHeader().setSectionResizeMode(3, QHeaderView.Stretch)
            self.table.horizontalHeader().setSectionResizeMode(4, QHeaderView.Stretch)
            self.table.horizontalHeader().setSectionResizeMode(5, QHeaderView.Stretch)
            self.table.horizontalHeader().setSectionResizeMode(6, QHeaderView.Stretch)
            self.table.horizontalHeader().setSectionResizeMode(7, QHeaderView.Stretch)
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

            item =   QTableWidgetItem(str(supplier['name']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)

            item =  QTableWidgetItem(str(worker['username']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)

            item =  QTableWidgetItem(str(supply['arrivalDate']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)

            item =  QTableWidgetItem(str(supply['expectedDate']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)

            item =  QTableWidgetItem(str(supply['processedDate']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 5, item)

            item =  QTableWidgetItem(str(product['name']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 6, item)

            item =  QTableWidgetItem(str(supply['amount']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 7, item)

    def load_supplies_history(self):
        response = requests.get('http://localhost:8080/api/supplies-history', headers=self.globalVariables.http_headers)

        if response.status_code == 200:
            self.writeToConsole("Supplies history loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            supplies = response.json()

            self.populate_table(supplies)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

