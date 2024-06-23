from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel
# from PySide6.QtGui import QItemSelectionModel
import requests
import json
from functools import partial



class SuppliersPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables

        self._setup_ui()
        layout = QVBoxLayout()

        action_widget = QWidget()
        action_layout = QGridLayout()
        action_widget.setLayout(action_layout)
        action_widget.setMinimumHeight(200)
        action_widget.setMaximumHeight(300)
        action_layout.addWidget(self.search_widget, 0, 0)
        action_layout.addWidget(self.add_widget, 0, 1)
        action_layout.setColumnStretch(0, 1)
        action_layout.setColumnStretch(1, 1)


        layout.addWidget(action_widget)
        layout.addWidget(self.suppliers_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)

        self._init_signals()
        self.load_suppliers()

    def _setup_ui(self):

        self._init_add_box()
        self._init_console()

        self.search_widget = QGroupBox("Search supplier")
        self.serach_layout = QGridLayout(self.search_widget)
        self.serach_layout.setAlignment(Qt.AlignTop)


        self.search_bar = QLineEdit(self)
        self.search_bar.setPlaceholderText("Search by supplier name")
        self.search_bar.textChanged.connect(lambda text: self.filter_table_by_name(text))

        self.serach_layout.addWidget(self.search_bar, 1,0)

        # self.serach_layout.setColumnStretch(0, 1)
        # self.serach_layout.setColumnStretch(1, 1)

        self.suppliers_widget = QGroupBox("Suppliers")
        self.suppliers_widget.setMinimumHeight(210)
        self.suppliers_widget.setMaximumHeight(350)
        self.suppliers_layout = QGridLayout(self.suppliers_widget)

        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()

        self._init_table()
        self.suppliers_layout.addWidget(self.table_scrollArea)

    def _init_signals(self):
        self.globalVariables.signals.suppliers_view_clicked.connect(lambda: self.load_suppliers())

    def _init_console(self):
        self.console_box = QGroupBox("Last operation status:")
        self.console_layout = QVBoxLayout()
        self.console_box.setLayout(self.console_layout)

        self.console = QTextEdit()
        self.console.setReadOnly(True)
        self.console_layout.addWidget(self.console)
        self.console_box.setMinimumHeight(80)
        self.console_box.setMaximumHeight(90)

    def _init_add_box(self):
        form = QWidget()

        # Create the form layout
        form_layout = QFormLayout()

        # Create the input fields
        self.supplier_name = QLineEdit()
        self.country = QLineEdit()
        self.city = QLineEdit()
        self.street = QLineEdit()
        self.house_number = QLineEdit()
        self.postal_code = QLineEdit()

        # Add the input fields to the form layout
        form_layout.addRow(QLabel('Supplier Name:'), self.supplier_name)
        form_layout.addRow(QLabel('Country:'), self.country)
        form_layout.addRow(QLabel('City:'), self.city)
        form_layout.addRow(QLabel('Street:'), self.street)
        form_layout.addRow(QLabel('House number:'), self.house_number)
        form_layout.addRow(QLabel('Postal code:'), self.postal_code)


        # Set the layout for the QGroupBox
        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        # Create the submit button
        submit_button = QPushButton('Add')
        submit_button.clicked.connect(self.add_supplier)

        # Create the main layout
        add_layout = QGridLayout()
        add_layout.addWidget(form, 0,0)
        add_layout.addWidget(submit_button, 1, 0)
        self.add_widget = QGroupBox('Add supplier')
        self.add_widget.setLayout(add_layout)
        # self.add_widget.setMinimumHeight(100)
        # self.add_widget.setMaximumHeight(200)
        add_layout.setAlignment(Qt.AlignTop)

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
        self.table.setHorizontalHeaderLabels(['ID', 'Name', 'Country', 'City', 'Street', 'House Number', 'Postal Code', 'Edit', 'Delete'])
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

    def filter_table_by_name(self, name):
        print(name)
        if name:
            response = requests.get(f'http://localhost:8080/api/suppliers/supplierName/{name}')
            print(response.status_code)
            if response.status_code == 200:
                suppliers = response.json()
                self.populate_table(suppliers)
                self.writeToConsole(f"Suppliers filtered by \'{name}\'")
            else:
                if response.status_code == 404:
                    self.writeToConsole(f'Error: No suppliers found under \'{name}\'')
                    self.table.clearContents()
                    self.table.setRowCount(0)
                else:
                    body = json.loads(response.text)
                    mess = body.get('message')
                    self.writeToConsole(f'Error: {mess}')
        else:
            self.load_suppliers()

    def populate_table(self, suppliers):
        self.table.clearContents()
        self.table.setRowCount(0)
        for supplier in suppliers:
            address = supplier['address']

            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item = QTableWidgetItem(str(supplier['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)

            item = QTableWidgetItem(supplier['name'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)


            item = QTableWidgetItem(str(address['country']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)


            item = QTableWidgetItem(str(address['city']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)


            item = QTableWidgetItem(str(address['street']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)


            item = QTableWidgetItem(str(address['houseNumber']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 5, item)


            item = QTableWidgetItem(str(address['postalCode']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 6, item)

            edit_button = QPushButton('Edit')
            edit_button.clicked.connect(partial(self.edit_supplier, row_position))
            self.table.setCellWidget(row_position, 7, edit_button)

            delete_button = QPushButton('Delete')
            # delete_button.clicked.connect(lambda: self.delete_supplier(row_position))
            delete_button.clicked.connect(partial(self.delete_supplier, row_position))
            self.table.setCellWidget(row_position, 8, delete_button)

    def load_suppliers(self):
        response = requests.get('http://localhost:8080/api/suppliers')

        if response.status_code == 200:
            self.writeToConsole("Suppliers loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            suppliers = response.json()

            self.populate_table(suppliers)
        else:
            self.writeToConsole('Failed to load suppliers')

    def delete_supplier(self, row):
        selected_row = row
        print(selected_row)
        if selected_row == -1:
            self.writeToConsole('Error: No supplier selected')
            return

        supplier_id = self.table.item(selected_row, 0).text()
        print(f"supplier_id: {supplier_id}")
        response = requests.delete(f'http://localhost:8080/api/suppliers/{supplier_id}')

        if response.status_code == 200:
            self.load_suppliers()
            self.writeToConsole('Success: Supplier deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def edit_supplier(self, row_position):
        print(row_position)
        self.select_row(row_position)

        item = self.table.item(row_position, 1)
        item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)
        item = self.table.item(row_position, 2)
        item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)
        item = self.table.item(row_position, 3)
        item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)
        item = self.table.item(row_position, 4)
        item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)
        item = self.table.item(row_position, 5)
        item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)
        item = self.table.item(row_position, 6)
        item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)

        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)

        update_button = QPushButton('Update')
        update_button.clicked.connect(partial(self.update_supplier, row_position))

        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))

        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(update_button, 0 ,1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 7, edit_widget)

    def select_row(self, row):
        self.table.setSelectionMode(QTableWidget.SingleSelection)
        self.table.selectRow(row)

        index = self.table.model().index(row, 7)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 8)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)


        self.table.setSelectionMode(QTableWidget.NoSelection)

    def revert_edit(self, row_position):
        self.reset_table(row_position)
        self.load_suppliers()
        self.writeToConsole("Reverted edit")


    def update_supplier(self, row_position):

        selected_row = row_position
        if selected_row == -1:
            self.writeToConsole(f'Error: No supplier selected')
            return

        supplier_id = self.table.item(row_position, 0).text()
        name = self.table.item(row_position, 1).text()
        country = self.table.item(row_position, 2).text()
        city = self.table.item(row_position, 3).text()
        street = self.table.item(row_position, 4).text()
        house_number = self.table.item(row_position, 5).text()
        postal_code = self.table.item(row_position, 6).text()


        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': supplier_id, 'name': name, 'address': {'city': city, 'street': street, 'postalCode': postal_code, 'houseNumber': house_number, 'country': country}})
        response = requests.put(f'http://localhost:8080/api/suppliers', headers=headers, data=data)

        if response.status_code == 200:
            self.reset_table(row_position)
            self.load_suppliers()
            self.writeToConsole(f'Success: Supplier updated successfully')
        else:
            self.reset_table(row_position)
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def reset_table(self, row_position):
        for col in range(2):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
        self.table.clearSelection()
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def add_supplier(self):

        supplier_name = self.supplier_name.text()
        country = self.country.text()
        city = self.city.text()
        street = self.street.text()
        house = self.house_number.text()
        postal = self.postal_code.text()

        headers = {'Content-Type': 'application/json'}

        data = json.dumps({'name': supplier_name, 'address': {'city': city, 'street': street, 'postalCode': postal, 'houseNumber': house, 'country': country}})
        response = requests.post('http://localhost:8080/api/suppliers', headers=headers, data=data)

        if response.status_code == 201:
            self.load_suppliers()
            self.writeToConsole(f'Supplier {supplier_name} added sucessfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
            return
