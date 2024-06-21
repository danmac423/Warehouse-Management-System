from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel 
# from PySide6.QtGui import QItemSelectionModel 
import requests
import json
from functools import partial



class CategoriesPage(QWidget):
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
        # self.search_widget.setMinimumHeight(50)
        # self.search_widget.setMaximumHeight(100)
        self.serach_layout = QGridLayout(self.search_widget)
        self.serach_layout.setAlignment(Qt.AlignTop)
    
        
        self.search_bar = QLineEdit(self)
        self.search_bar.setPlaceholderText("Search by category name")
        self.search_bar.textChanged.connect(lambda text: self.filter_table_by_name(text))
        
        # self.category_dropdown = QComboBox(self)
        
        # self.category_dropdown.addItem("All Categories")
        # categories = set([category[3] for category in self.categories])

        # self.category_dropdown.addItems(categories)
        # self.category_dropdown.currentTextChanged.connect(lambda: self.filter_table_by_category())
        # self.category_dropdown.currentIndexChanged.connect(lambda index: self.filter_table_by_category(self.category_dropdown.itemData(index)))
        
        # self.serach_layout.addWidget(self.category_dropdown,0,0)
        self.serach_layout.addWidget(self.search_bar, 1,0)
        
        # self.serach_layout.setColumnStretch(0, 1)
        # self.serach_layout.setColumnStretch(1, 1)
        
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

        # Create the form layout
        form_layout = QFormLayout()

        # Create the input fields
        self.category_name = QLineEdit()
        self.price = QLineEdit()
        self.category = QComboBox()
        self.stock = QLineEdit()

        # Add the input fields to the form layout
        form_layout.addRow(QLabel('Category Name:'), self.category_name)
        # form_layout.addRow(QLabel('Price:'), self.price)
        # form_layout.addRow(QLabel('Category:'), self.category)
        # form_layout.addRow(QLabel('Stock:'), self.stock)

        # Set the layout for the QGroupBox
        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        # Create the submit button
        submit_button = QPushButton('Add category')
        submit_button.clicked.connect(self.add_category)

        # Create the main layout
        add_layout = QGridLayout()
        add_layout.addWidget(form, 0,0)
        add_layout.addWidget(submit_button, 1, 0)
        self.add_widget = QGroupBox('Add category')
        self.add_widget.setLayout(add_layout)
        # self.add_widget.setMinimumHeight(100)
        # self.add_widget.setMaximumHeight(200)
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
        # self.table.horizontalHeader().setSectionResizeMode(5, QHeaderView.Stretch)
        # self.table.horizontalHeader().setSectionResizeMode(6, QHeaderView.Stretch)
        self.table.setSelectionBehavior(QTableWidget.SelectRows)
        self.table.setSelectionMode(QTableWidget.NoSelection)

        
     
        
    # def filter_table_by_category(self, cat_id):
    #     print("update table")
    #     print(f"{cat_id}")
    #     if cat_id == "ALL":
    #         self.load_categories()
    #     else:
    #         response = requests.get(f'http://localhost:8080/api/categories/category/{cat_id}')
    #         if response.status_code == 200:
    #             categories = response.json(categories)
    #             self.populate_table(categories)
    #             self.writeToConsole("Categories filtered sucessfully")
    #         else:
    #             body = json.loads(response.text)
    #             mess = body.get('message')
    #             self.writeToConsole(f'Error: {mess}')
            
            
        
    #     search_text = self.search_bar.text().lower()
    #     selected_category = self.category_dropdown.currentText()
        
    #     filtered_categories = [
    #         category for category in self.categories
    #         if (search_text in category[1].lower()) and
    #            (selected_category == "All Categories" or category[3] == selected_category)
    #     ]
    
    def filter_table_by_name(self, name):
        print(name)
        response = requests.get(f'http://localhost:8080/api/categories/prefixSuffix/{name}')
        if response.status_code == 200:
            categories = response.json(categories)
            self.populate_table(categories)
            self.writeToConsole("Categories filtered sucessfully")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
    
            
    def add_category(self):

        name = self.category_name.text()
        price = self.price.text()
        category_id = self.category.currentData()
        stock = self.stock.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'name': name, 'price': price, 'categoryId': category_id, 'stock': stock})
        response = requests.post('http://localhost:8080/api/categories', headers=headers, data=data)
        print(response.status_code)
        if response.status_code == 201: #Change later to 201
            self.load_categories()
            self.writeToConsole('Category added successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def populate_table(self, categories):
        self.table.clearContents()
        self.table.setRowCount(0)
        for category in categories:
            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item =  QTableWidgetItem(str(category['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)
            
            item =  QTableWidgetItem(category['name'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)
            
            item =  QTableWidgetItem(str(category['productCount']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)
            
            # item =  QTableWidgetItem(str(category['categoryId']))
            # item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            # item.setTextAlignment(Qt.AlignCenter)
            # self.table.setItem(row_position, 3, item)
            
            # item =  QTableWidgetItem(str(category['stock']))
            # item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            # item.setTextAlignment(Qt.AlignCenter)
            # self.table.setItem(row_position, 4, item)
            
            edit_button = QPushButton('Edit')
            edit_button.clicked.connect(partial(self.edit_category, row_position))
            self.table.setCellWidget(row_position, 3, edit_button)
            
            delete_button = QPushButton('Delete')
            # delete_button.clicked.connect(lambda: self.delete_category(row_position))
            delete_button.clicked.connect(partial(self.delete_category, row_position))
            self.table.setCellWidget(row_position, 4, delete_button)
        

    def load_categories(self):
        response = requests.get('http://localhost:8080/api/categories')

        if response.status_code == 200:
            # self.update_categories()
            self.writeToConsole("Categories loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            categories = response.json()
            
            self.populate_table(categories)
        else:
            self.writeToConsole('Failed to load categories')
            
    def delete_category(self, row):
        selected_row = row
        print(selected_row)
        if selected_row == -1:
            self.writeToConsole('Error: No category selected')
            return

        category_id = self.table.item(selected_row, 0).text()
        print(f"category_id: {category_id}")
        response = requests.delete(f'http://localhost:8080/api/categories/{category_id}')

        if response.status_code == 200:
            self.load_categories()
            self.writeToConsole('Success: Category deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
        
    def edit_category(self, row_position):
        print(row_position)
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
        edit_layout.addWidget(update_button, 0 ,1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 3, edit_widget)
        
    def select_row(self, row):
        self.table.setSelectionMode(QTableWidget.SingleSelection)
        self.table.selectRow(row)
        
        # index = self.table.model().index(row, 0)
        # self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        # index = self.table.model().index(row, 2)
        # self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 3)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 4)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)

        
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def revert_edit(self, row_position):
        self.reset_table(row_position)
        self.load_categories()
        self.writeToConsole("Reverted edit")
    
    
    # def update_categories(self):
    #     response = requests.get('http://localhost:8080/api/categories')
    #     if response.status_code == 200:
    #         result = response.json()
    #         # categories = [cat['name'] for cat in result]
    #         categories_tuples = [(cat['name'], cat['id']) for cat in result]
    #         print(categories_tuples)
    #         self.category_dropdown.blockSignals(True)
    #         self.category.clear()
    #         self.category_dropdown.clear()
    #         self.category_dropdown.addItem("All Categories", "ALL")

    #         for display_text, data in categories_tuples:
    #             self.category.addItem(display_text, data)
    #             self.category_dropdown.addItem(display_text, data)
    #         self.category_dropdown.blockSignals(False)
    #     else:
    #         body = json.loads(response.text)
    #         mess = body.get('message')
    #         self.writeToConsole(f'Error: {mess}')
    
    def update_category(self, row_position):
        
        selected_row = row_position
        if selected_row == -1:
            self.writeToConsole(f'Error: No category selected')
            return

        category_id = self.table.item(row_position, 0).text() 
        name = self.table.item(row_position, 1).text() 
        # price = self.table.item(row_position, 2).text() 
        # category_id = self.table.item(row_position, 3).text() 
        # stock = self.table.item(row_position, 4).text() 
        headers = {'Content-Type': 'application/json'}
        # data = json.dumps({'id': category_id, 'name': name, 'price': price, 'categoryId': category_id, 'stock': stock})
        data = json.dumps({'name': name, 'id': category_id})
        response = requests.put(f'http://localhost:8080/api/categories', headers=headers, data=data)

        if response.status_code == 200:
            self.reset_table(row_position)
            self.load_categories()
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
     