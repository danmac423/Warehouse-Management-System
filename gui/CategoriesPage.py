from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox, QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel
import requests
import json
from functools import partial

class CategoriesPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables
        self.current_search_text = ""

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
        layout.addWidget(self.categories_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)

        self._init_signals()
        self.load_categories()

    def _setup_ui(self):
        self._init_add_box()
        self._init_console()

        self.search_widget = QGroupBox("Search Category")
        self.serach_layout = QGridLayout(self.search_widget)
        self.serach_layout.setAlignment(Qt.AlignTop)

        self.search_bar = QLineEdit(self)
        self.search_bar.setPlaceholderText("Search by category name")
        self.search_bar.textChanged.connect(self.apply_filters)

        self.reset_button = QPushButton("Reset Filters")
        self.reset_button.clicked.connect(self.reset_filters)

        self.serach_layout.addWidget(self.search_bar, 1, 0)
        self.serach_layout.addWidget(self.reset_button, 2, 0)

        self.categories_widget = QGroupBox("Categories")
        self.categories_widget.setMinimumHeight(210)
        self.categories_widget.setMaximumHeight(350)
        self.categories_layout = QGridLayout(self.categories_widget)

        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()

        self._init_table()
        self.categories_layout.addWidget(self.table_scrollArea)

    def _init_signals(self):
        self.globalVariables.signals.categories_view_clicked.connect(lambda: self.load_categories())

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

    def _init_add_box(self):
        form = QWidget()

        form_layout = QFormLayout()

        self.category_name = QLineEdit()

        form_layout.addRow(QLabel('Category Name:'), self.category_name)

        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        submit_button = QPushButton('Add Category')
        submit_button.clicked.connect(self.add_category)

        add_layout = QGridLayout()
        add_layout.addWidget(form, 0, 0)
        add_layout.addWidget(submit_button, 1, 0)
        self.add_widget = QGroupBox('Add Category')
        self.add_widget.setLayout(add_layout)
        add_layout.setAlignment(Qt.AlignTop)

    def _init_table(self):
        self.table_scrollArea.setWidget(self.table)
        self.table_scrollArea.setWidgetResizable(True)
        self.table_scrollArea.setMinimumHeight(120)
        self.table_scrollArea.setMaximumHeight(300)

        self.table.setColumnCount(5)
        self.table.setHorizontalHeaderLabels(['ID', 'Name', 'Product Count', 'Edit', 'Delete'])
        self.table.horizontalHeader().setSectionResizeMode(0, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(3, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(4, QHeaderView.Stretch)
        self.table.setSelectionBehavior(QTableWidget.SelectRows)
        self.table.setSelectionMode(QTableWidget.NoSelection)


    def apply_filters(self):
        self.current_category_name = self.search_bar.text()

        category_name = self.current_category_name

        url = 'http://localhost:8080/api/categories'
        params = {}

        if category_name:
            params['name'] = category_name

        response = requests.get(url, params=params)
        if response.status_code == 200:
            self.writeToConsole("Categories loaded successfully")
            categories = response.json()
            self.populate_table(categories)
        else:
            self.writeToConsole('Failed to load categories')
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
            self.table.clearContents()
            self.table.setRowCount(0)


    def add_category(self):
        name = self.category_name.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name})
        response = requests.post('http://localhost:8080/api/categories', headers=headers, data=data)
        if response.status_code == 201:
            self.clear_form()
            self.reset_filters()
            self.writeToConsole('Category added successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def clear_form(self):
        self.category_name.clear()

    def reset_filters(self):
        self.search_bar.setText("")
        self.current_search_text = ""
        self.load_categories()

    def populate_table(self, categories):
        self.table.clearContents()
        self.table.setRowCount(0)
        for category in categories:
            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item = QTableWidgetItem(str(category['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)

            item = QTableWidgetItem(category['name'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)

            item = QTableWidgetItem(str(category['productCount']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)

            edit_button = QPushButton('Edit')
            edit_button.clicked.connect(partial(self.edit_category, row_position))
            self.table.setCellWidget(row_position, 3, edit_button)

            delete_button = QPushButton('Delete')
            delete_button.clicked.connect(partial(self.delete_category, row_position))
            self.table.setCellWidget(row_position, 4, delete_button)

    def load_categories(self):
        response = requests.get('http://localhost:8080/api/categories')
        if response.status_code == 200:
            self.writeToConsole("Categories loaded successfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            categories = response.json()
            self.populate_table(categories)
        else:
            self.writeToConsole('Failed to load categories')

    def delete_category(self, row):
        selected_row = row
        if selected_row == -1:
            self.writeToConsole('Error: No category selected')
            return

        category_id = self.table.item(selected_row, 0).text()
        response = requests.delete(f'http://localhost:8080/api/categories/{category_id}')
        if response.status_code == 200:
            self.apply_filters()
            self.writeToConsole('Success: Category deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def edit_category(self, row_position):
        self.select_row(row_position)

        item = self.table.item(row_position, 1)
        item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)

        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)

        update_button = QPushButton('Update')
        update_button.clicked.connect(partial(self.update_category, row_position))

        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))

        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(update_button, 0, 1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 3, edit_widget)

    def select_row(self, row):
        self.table.setSelectionMode(QTableWidget.SingleSelection)
        self.table.selectRow(row)

        index = self.table.model().index(row, 3)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 4)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)

        self.table.setSelectionMode(QTableWidget.NoSelection)

    def revert_edit(self, row_position):
        self.reset_table(row_position)
        self.apply_filters()
        self.writeToConsole("Reverted edit")

    def update_category(self, row_position):
        selected_row = row_position
        if selected_row == -1:
            self.writeToConsole(f'Error: No category selected')
            return

        category_id = self.table.item(row_position, 0).text()
        name = self.table.item(row_position, 1).text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name, 'id': category_id})
        response = requests.put(f'http://localhost:8080/api/categories', headers=headers, data=data)
        if response.status_code == 200:
            self.reset_table(row_position)
            self.apply_filters()
            self.writeToConsole(f'Success: Category updated successfully')
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
