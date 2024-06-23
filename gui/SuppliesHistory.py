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
        self.search_supplier_name = QLineEdit()
        self.search_worker_username = QLineEdit()

        form_layout.addRow(QLabel('Supplier name:'), self.search_supplier_name)
        form_layout.addRow(QLabel('Worker username:'), self.search_worker_username)

        form = QWidget()
        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        search_button = QPushButton('Search')
        search_button.clicked.connect(self.filter_supply)

        search_layout.addWidget(form, 0,0)
        search_layout.addWidget(search_button, 1, 0)
        self.search_widget.setLayout(search_layout)
        search_layout.setAlignment(Qt.AlignTop)


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

    def filter_supply(self):
        supplierSubstring = self.search_supplier_name.text()
        usernameSubstring = self.search_worker_username.text()

        if supplierSubstring and usernameSubstring:
            response = requests.get(f'http://localhost:8080/api/suppliesHistory/formated/supplier/{supplierSubstring}/username/{usernameSubstring}')
        elif supplierSubstring:
            response = requests.get(f'http://localhost:8080/api/suppliesHistory/formated/supplier/{supplierSubstring}')
        elif usernameSubstring:
            response = requests.get(f'http://localhost:8080/api/suppliesHistory/formated/username/{usernameSubstring}')
        else:
            self.load_supplies_history()
            return

        print(response.status_code)
        if response.status_code == 200:
            supplies = response.json()
            self.populate_table(supplies)
            self.writeToConsole(f"Supplies history filtered by supplier: \'{supplierSubstring}\' and worker: \'{usernameSubstring}\'")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def populate_table(self, supplies):
        self.table.clearContents()
        self.table.setRowCount(0)
        for supply in supplies:
            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item = QTableWidgetItem(str(supply['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)

            item =   QTableWidgetItem(str(supply['supplierName']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)

            item =  QTableWidgetItem(str(supply['username']))
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

            item =  QTableWidgetItem(str(supply['productName']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 6, item)

            item =  QTableWidgetItem(str(supply['amount']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 7, item)



    def load_supplies_history(self):
        response = requests.get('http://localhost:8080/api/suppliesHistory/formated')

        if response.status_code == 200:
            self.writeToConsole("Supplies history loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            supplies = response.json()
            print(supplies)

            self.populate_table(supplies)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

