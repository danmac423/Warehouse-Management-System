from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit, QDateEdit, QListWidget, QListWidgetItem
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel , QDate
# from PySide6.QtGui import QItemSelectionModel 
import requests
import json
from functools import partial



class OrderHistoryPage(QWidget):
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
        action_layout.addWidget(self.more_widget, 0, 1)
        action_layout.setColumnStretch(0, 35)
        action_layout.setColumnStretch(1, 70)
        
        layout.addWidget(action_widget)
        layout.addWidget(self.ordersHistory_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)
        
        self._init_signals()
        self.load_ordersHistory()
    
    def _setup_ui(self):
        
        self._init_more_box()
        self._init_search_box()
        self._init_console() 
        
        self.ordersHistory_widget = QGroupBox("Orders History")
        self.ordersHistory_widget.setMinimumHeight(210)
        self.ordersHistory_widget.setMaximumHeight(350)
        self.ordersHistory_layout = QGridLayout(self.ordersHistory_widget)
        
        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()
        
        self._init_table()
        self.ordersHistory_layout.addWidget(self.table_scrollArea)
        
    def _init_search_box(self):
        self.search_widget = QGroupBox("Search orders history")
        serach_layout = QGridLayout(self.search_widget)
        serach_layout.setAlignment(Qt.AlignTop)
        
        form_layout = QFormLayout()
        self.search_customer_email = QLineEdit()
        self.search_worker_username = QLineEdit()
        
        form_layout.addRow(QLabel('Customer email:'), self.search_customer_email)
        form_layout.addRow(QLabel('Worker username:'), self.search_worker_username)
        
        form = QWidget()
        form.setLayout(form_layout)
        form_layout.setAlignment(Qt.AlignTop)
        
        search_button = QPushButton('Search')
        search_button.clicked.connect(self.filter_order)
    
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
        self.globalVariables.signals.order_history_view_clicked.connect(lambda: self._view_clicked())
    
    def _view_clicked(self):
        self.load_ordersHistory()
        self.clear_more_list()
        
    def _init_more_box(self): 
        # products_box= QGroupBox("Products")
        products_box = QGroupBox()
        form1 = QWidget()
        form2 = QWidget()
        
        # self.order_id = QLineEdit(readOnly = True)
        # self.order_id.setText("23112")
        # self.order_id.clear()
        # self.order_id.setText("dsads")
        # self.products = QLineEdit(readOnly = True)
        self.products =  QListWidget()
         
        
        self.customer_id = QLineEdit(readOnly = True)
        self.customer_name = QLineEdit(readOnly = True)
        
        self.date_processed = QLineEdit(readOnly = True)
        self.worker_username = QLineEdit(readOnly = True)
        self.worker_id = QLineEdit(readOnly = True)
        
        self.status = QLineEdit(readOnly = True)
        self.date_received = QLineEdit(readOnly = True)
        self.total_price = QLineEdit(readOnly = True)
        
        self.customer_email = QLineEdit(readOnly = True)
        self.customer_lastname = QLineEdit(readOnly = True)
        
        products_layout = QVBoxLayout()
        products_layout.setContentsMargins(0, 0, 0, 0)
        # form_layout.addRow(QLabel("Order ID:"), self.order_id)
        products_layout.addWidget(self.products)
        
        form_layout1 = QFormLayout()
        
        form_layout1.addRow(QLabel("Customer ID:"), self.customer_id)
        form_layout1.addRow(QLabel("Customer name:"), self.customer_name)
        form_layout1.addRow(QLabel("Worker ID:"), self.worker_id)
        form_layout1.addRow(QLabel("Date Recieved:"), self.date_received)
        form_layout1.addRow(QLabel("Status:"), self.status)
        
        
        form_layout2 = QFormLayout()
        form_layout2.addRow(QLabel("Customer email:"), self.customer_email)
        form_layout2.addRow(QLabel("Customer lastname:"), self.customer_lastname)
        form_layout2.addRow(QLabel("Worker username:"), self.worker_username)
        form_layout2.addRow(QLabel("Date processed:"), self.date_processed)
        form_layout2.addRow(QLabel("Total price:"), self.total_price)
        
        self.more_list = [
            # self.order_id,
            self.products,
            self.customer_id,
            self.customer_name,
            self.worker_id,
            self.date_received,
            self.status,
            self.customer_email,
            self.customer_lastname,
            self.worker_username,
            self.date_processed,
            self.total_price
        ]
        
        products_box.setLayout(products_layout)
        form1.setLayout(form_layout1)
        form2.setLayout(form_layout2)
        

        more_layout = QGridLayout()
        more_layout.addWidget(products_box, 0, 0, 1, 2)
        more_layout.addWidget(form1, 1, 0, 1, 1)
        more_layout.addWidget(form2, 1, 1, 1, 1)
        more_layout.setColumnStretch(0, 1)
        more_layout.setColumnStretch(1, 1)

        self.more_widget = QGroupBox('More info')
        self.more_widget.setLayout(more_layout)
        more_layout.setAlignment(Qt.AlignTop)
          
    def _init_table(self):
        self.table_scrollArea.setWidget(self.table)
        self.table_scrollArea.setWidgetResizable(True)
        self.table_scrollArea.setMinimumHeight(120)
        self.table_scrollArea.setMaximumHeight(300)
        
        self.table.setColumnCount(6)
        self.table.setHorizontalHeaderLabels(['ID', 'Customer email', 'Worker', 'Recieved', 'Processed', 'More'])
        self.table.horizontalHeader().setSectionResizeMode(0, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(1, QHeaderView.Stretch) 
        self.table.horizontalHeader().setSectionResizeMode(2, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(3, QHeaderView.Stretch)
        self.table.horizontalHeader().setSectionResizeMode(4, QHeaderView.Stretch) 
        self.table.horizontalHeader().setSectionResizeMode(5, QHeaderView.Stretch)
        
        self.table.setSelectionBehavior(QTableWidget.SelectRows)
        self.table.setSelectionMode(QTableWidget.NoSelection)

    def filter_order(self):
        emailSubstring = self.search_customer_email.text()
        usernameSubstring = self.search_worker_username.text()
        
        if emailSubstring and usernameSubstring:
            response = requests.get(f'http://localhost:8080/api/ordersHistory/formated/email/{emailSubstring}/username/{usernameSubstring}')
        elif emailSubstring:
            response = requests.get(f'http://localhost:8080/api/ordersHistory/formated/email/{emailSubstring}')
        elif usernameSubstring:
            response = requests.get(f'http://localhost:8080/api/ordersHistory/formated/username/{usernameSubstring}')
        else:
            self.load_ordersHistory()
            return
        
        print(response.status_code)
        if response.status_code == 200:
            ordersHistory = response.json()
            self.populate_table(ordersHistory)
            self.writeToConsole(f"Orders history filtered by email: \'{emailSubstring}\' and worker: \'{usernameSubstring}\'")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
    
    def populate_table(self, ordersHistory):
        self.table.clearContents()
        self.table.setRowCount(0)
        for order in ordersHistory:
            row_position = self.table.rowCount()
            self.table.insertRow(row_position)

            item = QTableWidgetItem(str(order['id']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 0, item)
            
            item =   QTableWidgetItem(str(order['email']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 1, item)
            
            item =  QTableWidgetItem(str(order['username']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 2, item)
            
            item =  QTableWidgetItem(str(order['dateReceived']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 3, item)
            
            item =  QTableWidgetItem(str(order['dateProcessed']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)
            
            more_button = QPushButton('More')
            more_button.clicked.connect(partial(self.show_more, row_position))
            self.table.setCellWidget(row_position, 5, more_button)

    def load_ordersHistory(self):
        response = requests.get('http://localhost:8080/api/ordersHistory/formated')
        print(response.status_code)
        
        if response.status_code == 200:
            self.writeToConsole("ordersHistory loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            ordersHistory = response.json()
            
            self.populate_table(ordersHistory)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
                            
    def clear_more_list(self):
        self.more_widget.setTitle("More info")
        for item in self.more_list:
            item.clear()
    
    def show_more(self, rowposition):
        order_id = self.table.item(rowposition, 0).text() 
        response = requests.get(f'http://localhost:8080/api/ordersHistoryViews/orderId/{order_id}')
        response2 = requests.get(f'http://localhost:8080/api/products/order/{order_id}')

        if response.status_code == 200 and response2.status_code == 200:
            self.writeToConsole(f"More info for order {order_id}")
            ordersHistory = response.json()
            products = response2.json()
            self.populate_more_info(ordersHistory[0], products)
        else:
            
            body = json.loads(response.text)
            body2 = json.loads(response2.text)
            mess = body.get('message')
            mess2 = body2.get('message')
            self.writeToConsole(f'Error: {mess} and {mess2}')
        
    def populate_more_info(self, order, products):
        self.clear_more_list()
        self.more_widget.setTitle(f"More info on Order ID: {order['id']}")
        
        for item in products:
            list_item = QListWidgetItem(format_item(item))
            self.products.addItem(list_item)
        
        self.customer_id.setText(str(order['customerId']))
        self.customer_name.setText(str(order['name']))
        self.worker_id.setText(str(order['workerId']))
        self.date_received.setText(str(order['dateReceived']))
        self.status.setText(str(order['status']))
        self.customer_email.setText(str(order['email']))
        self.customer_lastname.setText(str(order['surname']))
        self.worker_username.setText(str(order['username']))
        self.date_processed.setText(str(order['dateProcessed']))
        self.total_price.setText(str(order['totalPrice']))
        

   
def format_item(item):
    return f"ID: {item['id']} | Name: {item['name']} | Price: ${item['price']} | Category: {item['categoryName']} | Amount: {item['amount']} | Total: ${item['totalPrice']}"


        