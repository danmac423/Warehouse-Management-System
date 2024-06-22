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
        self.filter_status.currentTextChanged.connect(lambda text: self.filter_table_by_status(text))
        
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
        
        self.table.setColumnCount(11)
         # ['ID', 'Supplier name', 'Worker', 'Status', 'Expected', 'Arrival', 'Product ID', 'Amount', 'Confirm', 'Assign', 'Delete']
        self.table.setHorizontalHeaderLabels(['ID', 'Supplier name', 'Worker', 'Status', 'Expected', 'Arrival', 'Product', 'Amount', 'Confirm', 'Assign', 'Delete'])
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
        if status == "All":
            self.load_supplies()
        else:
            response = requests.get(f'http://localhost:8080/api/supplies/formated/status/{status}')
            if response.status_code == 200:
                supplies = response.json()
                self.populate_table(supplies)
                self.writeToConsole("supplies filtered sucessfully")
            else:
                body = json.loads(response.text)
                mess = body.get('message')
                self.writeToConsole(f'Error: {mess}')
    
    
    # def filter_table_by_name(self, name):
    #     # print(f"--------supply_page_filter_name: {name}")
    #     response = requests.get(f'http://localhost:8080/api/supplies/prefixSuffix/{name}')
    #     if response.status_code == 200:
    #         supplies = response.json()
    #         self.populate_table(supplies)
    #         self.writeToConsole("supplies filtered sucessfully")
    #     else:
    #         body = json.loads(response.text)
    #         mess = body.get('message')
    #         self.writeToConsole(f'Error: {mess}')
    

    def filter_supply(self):
        supplierSubstring = self.search_supplier_name.text()
        usernameSubstring = self.search_worker_username.text()
        
        response = requests.get(f'http://localhost:8080/api/supplies/formated/supplier/{supplierSubstring}/username/{usernameSubstring}')
        print(response.status_code)
        if response.status_code == 200:
            supplies = response.json()
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
            
            item =   QTableWidgetItem(str(supply['supplierName']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)
            
            item =  QTableWidgetItem(str(supply['username']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)
            
            item =  QTableWidgetItem(str(supply['status']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)
            
            item =  QTableWidgetItem(str(supply['expectedDate']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)
            
            item =  QTableWidgetItem(str(supply['arrivalDate']))
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
            
            confirm_button = QPushButton('Confirm')
            confirm_button.clicked.connect(partial(self.confirm_supply, row_position))
            self.table.setCellWidget(row_position, 8, confirm_button)
            
            assign_button = QPushButton('Assign')
            assign_button.clicked.connect(partial(self.edit_assign_supply, row_position))
            self.table.setCellWidget(row_position, 9, assign_button)
            
            delete_button = QPushButton('Delete')
            # delete_button.clicked.connect(lambda: self.delete_supply(row_position))
            delete_button.clicked.connect(partial(self.delete_supply, row_position))
            self.table.setCellWidget(row_position, 10, delete_button)
            
        
    def load_supplies(self):
        response = requests.get('http://localhost:8080/api/supplies/formated')

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
                    
    def edit_assign_supply(self, row_position):
        self.select_row(row_position)
        
        item = self.table.item(row_position, 2)
        
        workers_to_assign = QComboBox()
        workers = self.worker_list()
        
        for worker in workers:
            workers_to_assign.addItem(worker[0], worker[1]) # get all possible workers
        
        if item.text():
            workers_to_assign.setCurrentText(item.text())
        self.table.setCellWidget(row_position, 2, workers_to_assign)
        
        assign_button = QPushButton('Update')
        assign_button.clicked.connect(lambda: self.assign_supply(row_position, workers_to_assign.currentData()))
        
        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))
        
        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)
        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(assign_button, 0 ,1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 9, edit_widget)
        
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
        # self.reset_table(row_position)
        self.load_supplies()
        self.writeToConsole("Reverted edit")
        
    def confirm_supply(self, row_position):
        pass
    
    # def edit_confirm_supply(self, row_position):
    #     self.select_row(row_position)
        
    #     item = self.table.item(row_position, 2)
        
    #     workers_to_assign = QComboBox()
    #     workers = self.worker_list()
        
    #     for worker in workers:
    #         workers_to_assign.addItem(worker[0], worker[1]) # get all possible workers
        
    #     if item.text():
    #         workers_to_assign.setCurrentText(item.text())
    #     self.table.setCellWidget(row_position, 2, workers_to_assign)
        
    #     assign_button = QPushButton('Update')
    #     assign_button.clicked.connect(lambda: self.assign_supply(row_position, workers_to_assign.currentData()))
        
    #     revert_button = QPushButton('Revert')
    #     revert_button.clicked.connect(partial(self.revert_edit, row_position))
        
    #     edit_widget = QWidget()
    #     edit_layout = QGridLayout()
    #     edit_widget.setLayout(edit_layout)
    #     edit_layout.addWidget(revert_button, 0, 0)
    #     edit_layout.addWidget(assign_button, 0 ,1)
    #     edit_layout.setColumnStretch(0, 1)
    #     edit_layout.setColumnStretch(1, 1)
    #     edit_layout.setRowStretch(0, 1)
    #     edit_layout.setContentsMargins(0, 0, 0, 0)
    #     edit_layout.setSpacing(0)
    #     self.table.setCellWidget(row_position, 8, edit_widget)
        
    
    def assign_supply(self, row_position, worker_id):
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
            # self.reset_table(row_position)
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
            self.load_supplies()
            self.writeToConsole('Success: Supply deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
    
    def worker_list(self):
        response = requests.get('http://localhost:8080/api/workers')

        if response.status_code == 200:
            workers = response.json()
            # workers_list = [worker['username'] for worker in workers]
            workers_list = [(item['username'], item['id']) for item in workers]
            return workers_list
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
            return None
    
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