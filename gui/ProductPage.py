from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox, QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel
import requests
import json
from functools import partial

class ProductPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables

        self.current_category_id = "ALL"
        self.current_product_name = ""

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
        layout.addWidget(self.products_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)

        self._init_signals()
        self.load_products()

    def _setup_ui(self):

        self._init_add_box()
        self._init_console()

        self.search_widget = QGroupBox("Search Products")
        self.search_layout = QGridLayout(self.search_widget)
        self.search_layout.setAlignment(Qt.AlignTop)

        self.search_bar = QLineEdit(self)
        self.search_bar.setPlaceholderText("Search by product name")
        self.search_bar.textChanged.connect(self.apply_filters)

        self.category_dropdown = QComboBox(self)
        self.category_dropdown.currentIndexChanged.connect(self.apply_filters)

        self.reset_button = QPushButton("Reset Filters")
        self.reset_button.clicked.connect(self.reset_filters)

        self.search_layout.addWidget(QLabel("Category:"), 0, 0)
        self.search_layout.addWidget(self.category_dropdown, 0, 1)
        self.search_layout.addWidget(QLabel("Product Name:"), 1, 0)
        self.search_layout.addWidget(self.search_bar, 1, 1)
        self.search_layout.addWidget(self.reset_button, 2, 0, 1, 2)

        self.products_widget = QGroupBox("Products")
        self.products_widget.setMinimumHeight(210)
        self.products_widget.setMaximumHeight(350)
        self.products_layout = QGridLayout(self.products_widget)

        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()

        self._init_table()
        self.products_layout.addWidget(self.table_scrollArea)

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
        self.globalVariables.signals.products_view_clicked.connect(lambda: self.load_products())

    def _init_add_box(self):
        form = QWidget()

        form_layout = QFormLayout()

        self.product_name = QLineEdit()
        self.price = QLineEdit()
        self.category = QComboBox()
        self.stock = QLineEdit()

        form_layout.addRow(QLabel('Product Name:'), self.product_name)
        form_layout.addRow(QLabel('Price:'), self.price)
        form_layout.addRow(QLabel('Category:'), self.category)
        form_layout.addRow(QLabel('Stock:'), self.stock)

        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        submit_button = QPushButton('Add Product')
        submit_button.clicked.connect(self.add_product)

        add_layout = QGridLayout()
        add_layout.addWidget(form, 0, 0)
        add_layout.addWidget(submit_button, 1, 0)
        self.add_widget = QGroupBox('Add Product')
        self.add_widget.setLayout(add_layout)
        add_layout.setAlignment(Qt.AlignTop)

    def _init_table(self):
        self.table_scrollArea.setWidget(self.table)
        self.table_scrollArea.setWidgetResizable(True)
        self.table_scrollArea.setMinimumHeight(120)
        self.table_scrollArea.setMaximumHeight(300)

        self.table.setColumnCount(7)
        self.table.setHorizontalHeaderLabels(['ID', 'Product Name', 'Price', 'Category', 'Stock', 'Edit', 'Delete'])
        self.table.horizontalHeader().setSectionResizeMode(0, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(3, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(4, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(5, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(6, QHeaderView.Stretch)
        self.table.setSelectionBehavior(QTableWidget.SelectRows)
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def apply_filters(self):
        self.current_category_id = self.category_dropdown.currentData()
        self.current_product_name = self.search_bar.text()

        current_category_id = self.current_category_id
        current_product_name = self.current_product_name

        url = 'http://localhost:8080/api/products'
        params = {}

        if current_category_id != "ALL":
            params['categoryId'] = current_category_id
        if current_product_name:
            params['productName'] = current_product_name

        response = requests.get(url, params=params)

        if response.status_code == 200:
            products = response.json()
            self.populate_table(products)
            self.writeToConsole("Filters applied successfully")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def add_product(self):
        name = self.product_name.text()
        price = self.price.text()
        category_id = self.category.currentData()
        stock = self.stock.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name, 'price': price, 'category': {'id': category_id}, 'stock': stock})
        response = requests.post('http://localhost:8080/api/products', headers=headers, data=data)
        
        if response.status_code == 201:
            self.clear_form()
            self.reset_filters()
            self.load_products()
            self.writeToConsole('Product added successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def clear_form(self):
        self.product_name.clear()
        self.price.clear()
        self.category.setCurrentIndex(0)
        self.stock.clear()

    def reset_filters(self):
        self.search_bar.setText("")
        self.category_dropdown.setCurrentIndex(0)
        self.current_category_id = "ALL"
        self.current_search_text = ""
        self.load_products()

    def populate_table(self, products):
        self.table.clearContents()
        self.table.setRowCount(0)
        for product in products:
            category = product['category']
            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item = QTableWidgetItem(str(product['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)

            item = QTableWidgetItem(product['name'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)

            item = QTableWidgetItem(str(product['price']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)

            item = QTableWidgetItem(category['name'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)

            item = QTableWidgetItem(str(product['stock']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)

            edit_button = QPushButton('Edit')
            edit_button.clicked.connect(partial(self.edit_product, row_position))
            self.table.setCellWidget(row_position, 5, edit_button)

            delete_button = QPushButton('Delete')
            delete_button.clicked.connect(partial(self.delete_product, row_position))
            self.table.setCellWidget(row_position, 6, delete_button)

    def load_products(self, reset_filters=False):

        response = requests.get('http://localhost:8080/api/products')

        if response.status_code == 200:
            self.update_categories()
            self.writeToConsole("Products loaded successfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            products = response.json()
            self.populate_table(products)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def delete_product(self, row):
        selected_row = row
        if selected_row == -1:
            self.writeToConsole('Error: No product selected')
            return

        product_id = self.table.item(selected_row, 0).text()
        response = requests.delete(f'http://localhost:8080/api/products/{product_id}')

        if response.status_code == 200:
            self.apply_filters()
            self.writeToConsole('Success: Product deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def get_categories(self):
        response = requests.get('http://localhost:8080/api/categories')
        if response.status_code == 200:
            result = response.json()
            return [(cat['name'], cat['id']) for cat in result]
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
            return []

    def edit_product(self, row_position):
        self.select_row(row_position)
        for col in range(1, 5):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)

        poduct_name = QLineEdit(self.table.item(row_position, 1).text())
        price = QLineEdit(self.table.item(row_position, 2).text())

        category_combo = QComboBox()
        self.populate_category_dropdown(category_combo)

        stock = QLineEdit(self.table.item(row_position, 4).text())

        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)

        update_button = QPushButton('Update')
        update_button.clicked.connect(partial(self.update_product, row_position, poduct_name, price, category_combo, stock))

        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))

        for col in range(1, 5):
            self.table.setItem(row_position, col, QTableWidgetItem(""))

        self.table.setCellWidget(row_position, 1, poduct_name)
        self.table.setCellWidget(row_position, 2, price)
        self.table.setCellWidget(row_position, 3, category_combo)
        self.table.setCellWidget(row_position, 4, stock)

        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(update_button, 0, 1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 5, edit_widget)

    def populate_category_dropdown(self, dropdown):
        categories = self.get_categories()
        dropdown.clear()
        for category in categories:
            dropdown.addItem(category[0], category[1])

    def select_row(self, row):
        self.table.setSelectionMode(QTableWidget.SingleSelection)
        self.table.selectRow(row)
        index = self.table.model().index(row, 5)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 6)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)

        self.table.setSelectionMode(QTableWidget.NoSelection)

    def revert_edit(self, row_position):
        self.reset_table(row_position)
        self.load_products()
        self.writeToConsole("Reverted edit")

    def update_categories(self):
        response = requests.get('http://localhost:8080/api/categories')
        if response.status_code == 200:
            result = response.json()
            categories_tuples = [(cat['name'], cat['id']) for cat in result]
        
            self.category_dropdown.blockSignals(True)
            self.category.clear()
            self.category_dropdown.clear()
            self.category_dropdown.addItem("All Categories", "ALL")

            for display_text, data in categories_tuples:
                self.category.addItem(display_text, data)
                self.category_dropdown.addItem(display_text, data)
            self.category_dropdown.blockSignals(False)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def update_product(self, row_position, product_name, price, category_dropdown, stock):
        product_id = self.table.item(row_position, 0).text()
        product_name = product_name.text()
        price = price.text()
        category_id = category_dropdown.currentData()
        stock = stock.text()

        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': product_id, 'name': product_name, 'price': price, 'category': {'id': category_id}, 'stock': stock})
        response = requests.put(f'http://localhost:8080/api/products', headers=headers, data=data)

        if response.status_code == 200:
            self.reset_table(row_position)
            self.load_products()
            self.writeToConsole("Product updated successfully")
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
