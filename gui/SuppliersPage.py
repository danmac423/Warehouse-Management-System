from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox, QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel
import requests
import json
from functools import partial

class SuppliersPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables

        self.current_search_text = ""

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
        self.search_layout = QGridLayout(self.search_widget)
        self.search_layout.setAlignment(Qt.AlignTop)

        self.search_bar_supplier_name = QLineEdit(self)
        self.search_bar_supplier_name.setPlaceholderText("Search by supplier name")
        self.search_bar_supplier_name.textChanged.connect(self.apply_filters)

        self.search_bar_country = QLineEdit(self)
        self.search_bar_country.setPlaceholderText("Search by country")
        self.search_bar_country.textChanged.connect(self.apply_filters)

        self.search_bar_city = QLineEdit(self)
        self.search_bar_city.setPlaceholderText("Search by city")
        self.search_bar_city.textChanged.connect(self.apply_filters)

        self.reset_filters_button = QPushButton("Reset Filters")
        self.reset_filters_button.clicked.connect(self.reset_filters)

        self.search_layout.addWidget(QLabel("Supplier name:"), 0, 0)
        self.search_layout.addWidget(self.search_bar_supplier_name, 0, 1)
        self.search_layout.addWidget(QLabel("Country:"), 1, 0)
        self.search_layout.addWidget(self.search_bar_country, 1, 1)
        self.search_layout.addWidget(QLabel("City:"), 2, 0)
        self.search_layout.addWidget(self.search_bar_city, 2, 1)
        self.search_layout.addWidget(self.reset_filters_button, 3, 0, 1, 2)

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

        form_layout = QFormLayout()

        self.supplier_name = QLineEdit()
        self.country = QLineEdit()
        self.city = QLineEdit()
        self.street = QLineEdit()
        self.house_number = QLineEdit()
        self.postal_code = QLineEdit()

        form_layout.addRow(QLabel('Supplier Name:'), self.supplier_name)
        form_layout.addRow(QLabel('Country:'), self.country)
        form_layout.addRow(QLabel('City:'), self.city)
        form_layout.addRow(QLabel('Street:'), self.street)
        form_layout.addRow(QLabel('House number:'), self.house_number)
        form_layout.addRow(QLabel('Postal code:'), self.postal_code)

        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        submit_button = QPushButton('Add')
        submit_button.clicked.connect(self.add_supplier)

        add_layout = QGridLayout()
        add_layout.addWidget(form, 0, 0)
        add_layout.addWidget(submit_button, 1, 0)
        self.add_widget = QGroupBox('Add supplier')
        self.add_widget.setLayout(add_layout)
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

    def apply_filters(self):
        supplier_name = self.search_bar_supplier_name.text()
        country = self.search_bar_country.text()
        city = self.search_bar_city.text()

        url = 'http://localhost:8080/api/suppliers'
        params = {}

        if supplier_name:
            params['supplierName'] = supplier_name
        if country:
            params['country'] = country
        if city:
            params['city'] = city

        response = requests.get(url, params=params)

        if response.status_code == 200:
            suppliers = response.json()
            self.populate_table(suppliers)
            self.writeToConsole("Filters applied successfully")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')


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

            item = QTableWidgetItem(address['country'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)

            item = QTableWidgetItem(address['city'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)

            item = QTableWidgetItem(address['street'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)

            item = QTableWidgetItem(address['houseNumber'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 5, item)

            item = QTableWidgetItem(address['postalCode'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 6, item)

            edit_button = QPushButton('Edit')
            edit_button.clicked.connect(partial(self.edit_supplier, row_position))
            self.table.setCellWidget(row_position, 7, edit_button)

            delete_button = QPushButton('Delete')
            delete_button.clicked.connect(partial(self.delete_supplier, row_position))
            self.table.setCellWidget(row_position, 8, delete_button)

    def load_suppliers(self):
        response = requests.get('http://localhost:8080/api/suppliers')

        if response.status_code == 200:
            self.writeToConsole("Suppliers loaded successfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            suppliers = response.json()
            self.populate_table(suppliers)
        else:
            self.writeToConsole('Failed to load suppliers')

    def delete_supplier(self, row):
        selected_row = row
        if selected_row == -1:
            self.writeToConsole('Error: No supplier selected')
            return

        supplier_id = self.table.item(selected_row, 0).text()
        response = requests.delete(f'http://localhost:8080/api/suppliers/{supplier_id}')

        if response.status_code == 200:
            self.apply_filters()
            self.writeToConsole('Success: Supplier deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def edit_supplier(self, row_position):
        self.select_row(row_position)
        for col in range(1, 7):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)

        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)

        update_button = QPushButton('Update')
        update_button.clicked.connect(partial(self.update_supplier, row_position))

        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))

        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(update_button, 0, 1)
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
            self.apply_filters()
            self.writeToConsole(f'Success: Supplier updated successfully')
        else:
            self.reset_table(row_position)
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def reset_table(self, row_position):
        for col in range(1, 7):
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
            self.clear_form()
            self.reset_filters()
            self.load_suppliers()
            self.writeToConsole(f'Supplier {supplier_name} added successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def clear_form(self):
        self.supplier_name.clear()
        self.country.clear()
        self.city.clear()
        self.street.clear()
        self.house_number.clear()
        self.postal_code.clear()

    def reset_filters(self):
        self.search_bar_supplier_name.clear()
        self.search_bar_country.clear()
        self.search_bar_city.clear()
        self.apply_filters()
        

