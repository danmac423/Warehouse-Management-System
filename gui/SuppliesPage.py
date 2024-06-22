from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit, QDateEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel , QDate
# from PySide6.QtGui import QItemSelectionModel 
import requests
import json
from functools import partial


class SuppliesPage(QWidget):
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
        action_layout.addWidget(self.filter_widget, 0, 1)
        action_layout.setColumnStretch(0, 1)
        action_layout.setColumnStretch(1, 1)
        
        layout.addWidget(action_widget)
        layout.addWidget(self.supplies_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)
        
        self._init_signals()
        self.load_supplies()

   
    def _setup_ui(self):
        
        self._init_filter_box()
        self._init_search_box()
        self._init_console() 
        
        self.supplies_widget = QGroupBox("Supplies")
        self.supplies_widget.setMinimumHeight(210)
        self.supplies_widget.setMaximumHeight(350)
        self.supplies_layout = QGridLayout(self.supplies_widget)
        
        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()
        
        self._init_table()
        self.supplies_layout.addWidget(self.table_scrollArea)
        
    def _init_search_box(self):
        self.search_widget = QGroupBox("Search supplies")
        serach_layout = QGridLayout(self.search_widget)
        serach_layout.setAlignment(Qt.AlignTop)
        
        form_layout = QFormLayout()
        self.search_supplier_name = QLineEdit()
        self.search_worker_username = QLineEdit()
        
        form_layout.addRow(QLabel('Supplier name:'), self.search_supplier_name)
        form_layout.addRow(QLabel('Worker username:'), self.search_worker_username)
        
        form = QWidget()
        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)
        
        search_button = QPushButton('Search supplies')
        search_button.clicked.connect(self.filter_supply)
    
        serach_layout.addWidget(form, 0,0)
        serach_layout.addWidget(search_button, 1, 0)
        self.search_widget.setLayout(serach_layout)
        serach_layout.setAlignment(Qt.AlignTop)
             
    
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
    
    def _init_filter_box(self):

        self.filter_status = QComboBox()
        self.filter_status.addItems(["All", "arrived","underway"])
        
        # Create the main layout
        filter_layout = QGridLayout()
        filter_layout.addWidget(self.filter_status, 0,0)
        self.filter_widget = QGroupBox('Filter by status')
        self.filter_widget.setLayout(filter_layout)
        filter_layout.setAlignment(Qt.AlignTop)
        
        
    def _init_table(self):
        self.table_scrollArea.setWidget(self.table)
        self.table_scrollArea.setWidgetResizable(True)
        self.table_scrollArea.setMinimumHeight(120)
        self.table_scrollArea.setMaximumHeight(300)
        
        self.table.setColumnCount(12)
        self.table.setHorizontalHeaderLabels(['ID', 'Supplier name', 'Worker', 'Status', 'Expected', 'Arrival', 'Processed', 'Product ID', 'Amount', 'Confirm', 'Assign', 'Delete'])
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
        self.table.horizontalHeader().setSectionResizeMode(11, QHeaderView.Stretch)
        self.table.setSelectionBehavior(QTableWidget.SelectRows)
        self.table.setSelectionMode(QTableWidget.NoSelection)

        
     
    
    def filter_table_by_category(self, cat_id):
        # print(f"--------supply_page_filter_cat: {cat_id}")
        if cat_id == "ALL":
            self.load_supplies()
        else:
            response = requests.get(f'http://localhost:8080/api/supplies/category/{cat_id}')
            if response.status_code == 200:
                supplies = response.json(supplies)
                self.populate_table(supplies)
                self.writeToConsole("supplies filtered sucessfully")
            else:
                body = json.loads(response.text)
                mess = body.get('message')
                self.writeToConsole(f'Error: {mess}')
    
    
    def filter_table_by_name(self, name):
        # print(f"--------supply_page_filter_name: {name}")
        response = requests.get(f'http://localhost:8080/api/supplies/prefixSuffix/{name}')
        if response.status_code == 200:
            supplies = response.json(supplies)
            self.populate_table(supplies)
            self.writeToConsole("supplies filtered sucessfully")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
    
            
    # def add_supply(self):
        
    #     customer_id = self.customer_id.text()
    #     worker_id = self.worker_id.text()
    #     date_received = self.date_received_input.date().toString('yyyy-MM-dd')

    #     headers = {'Content-Type': 'application/json'}
    #     data = json.dumps({'customerId': customer_id, 'workerId': worker_id, 'dateReceived': date_received})
        
    #     response = requests.post('http://localhost:8080/api/supplies', headers=headers, data=data)
    #     print(response.status_code)
    #     if response.status_code == 201: #Change later to 201
    #         self.load_supplies()
    #         self.writeToConsole('supply added successfully')
    #     else:
    #         body = json.loads(response.text)
    #         mess = body.get('message')
    #         self.writeToConsole(f'Error: {mess}')

    def filter_supply(self):
        supplierSubstring = self.search_supplier_name.text()
        usernameSubstring = self.search_worker_username.text()
        response = requests.get(f'http://localhost:8080/api/supplies/customerEmail/{supplierSubstring}/WorkerUsername/{usernameSubstring}')
        if response.status_code == 200:
            supplies = response.json(supplies)
            self.populate_table(supplies)
            self.writeToConsole("supplies filtered sucessfully")
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
            
            item =   QTableWidgetItem(str(supply['supplierId']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)
            
            item =  QTableWidgetItem(str(supply['workerId']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)
            
            item =  QTableWidgetItem(str(supply['status']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)
            
            item =  QTableWidgetItem(str(supply['arrivalDate']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)
            
            item =  QTableWidgetItem(str(supply['processedDate']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 5, item)
            
            item =  QTableWidgetItem(str(supply['expectedDate']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 6, item)
            
            edit_button = QPushButton('Edit')
            edit_button.clicked.connect(partial(self.edit_supply, row_position))
            self.table.setCellWidget(row_position, 7, edit_button)
            
            # delete_button = QPushButton('Delete')
            # # delete_button.clicked.connect(lambda: self.delete_supply(row_position))
            # delete_button.clicked.connect(partial(self.delete_supply, row_position))
            # self.table.setCellWidget(row_position, 7, delete_button)
        

    def load_supplies(self):
        response = requests.get('http://localhost:8080/api/supplies')

        if response.status_code == 200:
            # self.update_categories()
            self.writeToConsole("Supplies loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            supplies = response.json()
            print(supplies)
            
            self.populate_table(supplies)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
                    
    def edit_supply(self, row_position):
        print(row_position)
        self.select_row(row_position)
        for col in range(7):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)
            
            
        item = self.table.item(row_position, 4)
        edit_status = QComboBox()
        edit_status.addItems(["processed","received"])
        edit_status.setCurrentText(item.text())
        self.table.setCellWidget(row_position, 4, edit_status)
        
        item = self.table.item(row_position, 5)
        edit_date_received = QDateEdit()
        edit_date_received.setCalendarPopup(True)
        if item.text():
            edit_date_received.setDate(QDate.fromString(item.text(), Qt.ISODate))
        self.table.setCellWidget(row_position, 5, edit_date_received)
        
        
        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)
        
        update_button = QPushButton('Update')
        update_button.clicked.connect(partial(self.update_supply, row_position))
        
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
        
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def revert_edit(self, row_position):
        self.reset_table(row_position)
        self.load_supplies()
        self.writeToConsole("Reverted edit")
    
    # def update_supply(self, row_position):
        
    #     selected_row = row_position
    #     if selected_row == -1:
    #         self.writeToConsole(f'Error: No supply selected')
    #         return

    #     supply_id = self.table.item(selected_row, 0).text()
    #     customer_id = self.table.item(selected_row, 1).text()
    #     worker_id = self.table.item(selected_row, 3).text()
    #     status = self.table.cellWidget(selected_row, 4).currentText()
    #     date_received = self.table.cellWidget(selected_row, 5).date().toString('yyyy-MM-dd')
        
    #     headers = {'Content-Type': 'application/json'}
    #     data = json.dumps({'id': supply_id, 'customerId': customer_id, 'workerId': worker_id, 'status': status, 'dateReceived': date_received})
    #     print(data)
    #     response = requests.put(f'http://localhost:8080/api/supplies', headers=headers, data=data)
    #     print(response.status_code)
    #     if response.status_code == 200:
    #         self.reset_table(row_position)
    #         self.load_supplies()
    #         self.writeToConsole(f'Success: supply updated successfully')
    #     else:
    #         self.reset_table(row_position)
    #         body = json.loads(response.text)
    #         mess = body.get('message')
    #         self.writeToConsole(f'Error: {mess}')
        
    # def reset_table(self, row_position):
    #     for col in range(7):
    #         item = self.table.item(row_position, col)
    #         item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
    #     self.table.clearSelection()
    #     self.table.setSelectionMode(QTableWidget.NoSelection)