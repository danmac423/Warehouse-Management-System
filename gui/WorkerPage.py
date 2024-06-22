from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel 
# from PySide6.QtGui import QItemSelectionModel 
import requests
import json
from functools import partial



class WorkerPage(QWidget):
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
        layout.addWidget(self.workers_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)
        
        self._init_signals()
        self.load_workers()
    
    def _setup_ui(self):
        
        self._init_add_box()
        self._init_console()
        
        self.search_widget = QGroupBox("Search workers")
        self.search_bar = QLineEdit(self)
        self.search_bar.setPlaceholderText("Search workers by username")
        self.search_bar.textChanged.connect(lambda text: self.filter_table_by_name(text))
        # self.search_widget.setMinimumHeight(50)
        # self.search_widget.setMaximumHeight(100)
        self.serach_layout = QGridLayout(self.search_widget)
        self.serach_layout.addWidget(self.search_bar)
        self.serach_layout.setAlignment(Qt.AlignTop)
    
        
        # self.serach_layout.setColumnStretch(0, 1)
        # self.serach_layout.setColumnStretch(1, 1)
        
        self.workers_widget = QGroupBox("Workers")
        self.workers_widget.setMinimumHeight(210)
        self.workers_widget.setMaximumHeight(350)
        self.workers_layout = QGridLayout(self.workers_widget)
        
        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()
        
        self._init_table()
        self.workers_layout.addWidget(self.table_scrollArea)
        
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
        self.globalVariables.signals.workers_view_clicked.connect(lambda: self.load_workers())
    
    def _init_add_box(self):
        form = QWidget()

        # Create the form layout
        form_layout = QFormLayout()

        # Create the input fields
        self.worker_name = QLineEdit()
        self.woker_lastName = QLineEdit()
        self.role = QComboBox()
        self.username = QLineEdit()
        self.password = QLineEdit()
        self.password.setEchoMode(QLineEdit.Password)

        roles = ["WORKER", "ADMIN"]
        self.role.addItems(roles)
        
        # Add the input fields to the form layout
        
        form_layout.addRow(QLabel('Name:'), self.worker_name)
        form_layout.addRow(QLabel('Last name:'), self.woker_lastName)
        form_layout.addRow(QLabel('Role:'), self.role)
        form_layout.addRow(QLabel('Username:'), self.username)
        form_layout.addRow(QLabel('Password:'), self.password)

        # Set the layout for the QGroupBox
        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)

        # Create the submit button
        submit_button = QPushButton('Add worker')
        submit_button.clicked.connect(self.add_worker)

        # Create the main layout
        add_layout = QGridLayout()
        add_layout.addWidget(form, 0,0)
        add_layout.addWidget(submit_button, 1, 0)
        self.add_widget = QGroupBox('Add worker')
        self.add_widget.setLayout(add_layout)
        # self.add_widget.setMinimumHeight(100)
        # self.add_widget.setMaximumHeight(200)
        add_layout.setAlignment(Qt.AlignTop)
        
        
    def _init_table(self):
        self.table_scrollArea.setWidget(self.table)
        self.table_scrollArea.setWidgetResizable(True)
        self.table_scrollArea.setMinimumHeight(120)
        self.table_scrollArea.setMaximumHeight(300)
        
        self.table.setColumnCount(8)
        self.table.setHorizontalHeaderLabels(['ID', 'Name', 'Last Name', 'Role', 'Username', 'Password', 'Edit', 'Delete'])
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

    
            
    def add_worker(self):
        
        name = self.worker_name.text()
        lastName = self.woker_lastName.text()
        role = self.role.currentText()
        username = self.username.text()
        password = self.password.text()
        
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'username': username, 'password': password,'name': name, 'lastName': lastName, 'role': role})
        response = requests.post('http://localhost:8080/api/workers', headers=headers, data=data)

        if response.status_code == 201: #Change later to 201
            self.load_workers()
            self.writeToConsole('Worker added successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')

    def populate_table(self, workers):
        self.table.clearContents()
        self.table.setRowCount(0)
        for worker in workers:
            
            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item =  QTableWidgetItem(str(worker['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)
            
            item =  QTableWidgetItem(worker['name'])
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)
            
            item =  QTableWidgetItem(str(worker['lastName']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)
            
            item =  QTableWidgetItem(str(worker['role']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)
            
            item =  QTableWidgetItem(str(worker['username']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)
            
            # item =  QTableWidgetItem(str(worker['password']))
            # item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            # item.setTextAlignment(Qt.AlignCenter)
            # self.table.setItem(row_position, 5, item)
            password_widget = QLineEdit(str(worker['password']))
            password_widget.setEchoMode(QLineEdit.Password)
            password_widget.setReadOnly(True)
            self.table.setCellWidget(row_position, 5, password_widget)
            
            edit_button = QPushButton('Edit')
            edit_button.clicked.connect(partial(self.edit_worker, row_position))
            self.table.setCellWidget(row_position, 6, edit_button)
            
            delete_button = QPushButton('Delete')
            delete_button.clicked.connect(partial(self.delete_worker, row_position))
            self.table.setCellWidget(row_position, 7, delete_button)
    
    def filter_table_by_name(self, name):
        if name:
            response = requests.get(f'http://localhost:8080/api/workers/username/{name}')
            if response.status_code == 200:
                workers = response.json()
                self.populate_table(workers)
                self.writeToConsole(f"Workers filtered by \'{name}\'")
            else:
                if response.status_code == 404:
                    self.writeToConsole(f'Error: No workers found under \'{name}\'')
                    self.table.clearContents()
                    self.table.setRowCount(0)
                else: 
                    body = json.loads(response.text)
                    mess = body.get('message')
                    self.writeToConsole(f'Error: {mess}')
        else:
            self.load_workers()            

    def load_workers(self):
        response = requests.get('http://localhost:8080/api/workers')

        if response.status_code == 200:
            self.writeToConsole("Workers loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            workers = response.json()
            
            self.populate_table(workers)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
            
    def delete_worker(self, row):
        selected_row = row
        print(selected_row)
        if selected_row == -1:
            self.writeToConsole('Error: No worker selected')
            return

        worker_id = self.table.item(selected_row, 0).text()
        print(f"worker_id: {worker_id}")
        response = requests.delete(f'http://localhost:8080/api/workers/{worker_id}')

        if response.status_code == 200:
            self.load_workers()
            self.writeToConsole('Success: Worker deleted successfully')
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
        
    def edit_worker(self, row_position):
        print(row_position)
        self.select_row(row_position)
        for col in range(1,5):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() | Qt.ItemFlag.ItemIsEditable)
        
        password_item = self.table.cellWidget(row_position, 5)
        password = password_item.text() 
        self.table.removeCellWidget(row_position, 5)
        item =  QTableWidgetItem(password)
        item.setFlags(item.flags() | Qt.ItemIsEditable)
        item.setTextAlignment(Qt.AlignCenter)
        self.table.setItem(row_position, 5, item)
        
        edit_widget = QWidget()
        edit_layout = QGridLayout()
        edit_widget.setLayout(edit_layout)
        
        update_button = QPushButton('Update')
        update_button.clicked.connect(partial(self.update_worker, row_position))
        
        revert_button = QPushButton('Revert')
        revert_button.clicked.connect(partial(self.revert_edit, row_position))
        
        edit_layout.addWidget(revert_button, 0, 0)
        edit_layout.addWidget(update_button, 0 ,1)
        edit_layout.setColumnStretch(0, 1)
        edit_layout.setColumnStretch(1, 1)
        edit_layout.setRowStretch(0, 1)
        edit_layout.setContentsMargins(0, 0, 0, 0)
        edit_layout.setSpacing(0)
        self.table.setCellWidget(row_position, 6, edit_widget)
        
    def select_row(self, row):
        self.table.setSelectionMode(QTableWidget.SingleSelection)
        self.table.selectRow(row)
        index = self.table.model().index(row, 6)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        index = self.table.model().index(row, 7)
        self.table.selectionModel().select(index, QItemSelectionModel.Deselect)
        
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def revert_edit(self, row_position):
        self.reset_table(row_position)
        self.load_workers()
        self.writeToConsole("Reverted edit")
    
    
    def update_worker(self, row_position):
        
        selected_row = row_position
        if selected_row == -1:
            self.writeToConsole(f'Error: No worker selected')
            return
        worker_id = self.table.item(row_position, 0).text() 
        name = self.table.item(row_position, 0).text() 
        lastName = self.table.item(row_position, 2).text() 
        role = self.table.item(row_position, 3).text() 
        username = self.table.item(row_position, 4).text() 
        password = self.table.item(row_position, 5).text() 
        
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': worker_id,'username': username, 'password': password,'name': name, 'lastName': lastName, 'role': role})
    
        response = requests.put(f'http://localhost:8080/api/workers', headers=headers, data=data)

        if response.status_code == 200:
            self.reset_table(row_position)
            self.load_workers()
            self.writeToConsole(f'Success: Worker updated successfully')
        else:
            self.reset_table(row_position)
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
        
    def reset_table(self, row_position):
        for col in range(6):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
        self.table.clearSelection()
        self.table.setSelectionMode(QTableWidget.NoSelection)
     