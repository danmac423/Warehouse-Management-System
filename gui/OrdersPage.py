from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit, QLabel, QFormLayout, QTextEdit, QDateEdit, QListWidget, QListWidgetItem
import sys
from PySide6.QtCore import Qt, QTime, QItemSelectionModel , QDate
# from PySide6.QtGui import QItemSelectionModel 
import requests
import json
from functools import partial



class OrderPage(QWidget):
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
        layout.addWidget(self.orders_widget)
        layout.addWidget(self.console_box)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)
        
        self._init_signals()
        self.load_orders()
    
    def _setup_ui(self):
        
        self._init_more_box()
        self._init_search_box()
        self._init_console() 
        
        self.orders_widget = QGroupBox("Orders")
        self.orders_widget.setMinimumHeight(210)
        self.orders_widget.setMaximumHeight(350)
        self.orders_layout = QGridLayout(self.orders_widget)
        
        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()
        
        self._init_table()
        self.orders_layout.addWidget(self.table_scrollArea)
        
    def _init_search_box(self):
        self.search_widget = QGroupBox("Search Orders")
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
        
        search_button = QPushButton('Search Orders')
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
        self.globalVariables.signals.orders_view_clicked.connect(lambda: self._view_clicked())
    
    def _view_clicked(self):
        self.load_orders()
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
        
        self.table.setColumnCount(8)
        self.table.setHorizontalHeaderLabels(['ID', 'Customer email', 'Worker', 'Data Received', 'Status', 'Total Price', 'Assign', 'More'])
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

    def filter_order(self):
        emailSubstring = self.search_customer_email.text()
        usernameSubstring = self.search_worker_username.text()
        
        print(f"customerEmail/{emailSubstring}")
        print(f"WorkerUsername/{usernameSubstring}")
        
        response = requests.get(f'http://localhost:8080/api/orders/customerEmail/{emailSubstring}/WorkerUsername/{usernameSubstring}')
        print(response.status_code)
        if response.status_code == 200:
            orders = response.json()
            self.populate_table(orders)
            self.writeToConsole("Orders filtered sucessfully")
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
    
    def populate_table(self, orders):
        self.table.clearContents()
        self.table.setRowCount(0)
        for order in orders:
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
            
            item =  QTableWidgetItem(str(order['status']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 4, item)
            
            # item = QTableWidgetItem("niema")
            # # item =  QTableWidgetItem(str(order['itemCount']))
            # item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            # item.setTextAlignment(Qt.AlignCenter)
            # self.table.setItem(row_position, 5, item)
            
            item =  QTableWidgetItem(str(order['totalPrice']))
            item.setFlags(item.flags() & ~Qt.ItemIsEditable)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row_position, 5, item)
            
            edit_button = QPushButton('Assign')
            edit_button.clicked.connect(partial(self.edit_order, row_position))
            self.table.setCellWidget(row_position, 6, edit_button)
            
            more_button = QPushButton('More')
            more_button.clicked.connect(partial(self.show_more, row_position))
            self.table.setCellWidget(row_position, 7, more_button)
            
            # delete_button = QPushButton('Delete')
            # # delete_button.clicked.connect(lambda: self.delete_order(row_position))
            # delete_button.clicked.connect(partial(self.delete_order, row_position))
            # self.table.setCellWidget(row_position, 7, delete_button)
        

    def load_orders(self):
        response = requests.get('http://localhost:8080/api/ordersViews')

        if response.status_code == 200:
            # self.update_categories()
            self.writeToConsole("Orders loaded sucessfully")
            self.table.clearContents()
            self.table.setRowCount(0)
            orders = response.json()
            
            self.populate_table(orders)
        else:
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
                    
    def edit_order(self, row_position):
        # print(row_position)
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
        assign_button.clicked.connect(lambda: self.assign_order(row_position, workers_to_assign.currentData()))
        
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
        self.table.setCellWidget(row_position, 6, edit_widget)
        
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
        self.load_orders()
        self.writeToConsole("Reverted edit")
    
        
    def reset_table(self, row_position):
        for col in range(6):
            item = self.table.item(row_position, col)
            item.setFlags(item.flags() & ~Qt.ItemFlag.ItemIsEditable)
        self.table.clearSelection()
        self.table.setSelectionMode(QTableWidget.NoSelection)
        
    def clear_more_list(self):
        self.more_widget.setTitle("More info")
        for item in self.more_list:
            item.clear()
    
    def show_more(self, rowposition):
        order_id = self.table.item(rowposition, 0).text() 
        response = requests.get(f'http://localhost:8080/api/ordersViews/orderId/{order_id}')
        response2 = requests.get(f'http://localhost:8080/api/products/order/{order_id}')

        if response.status_code == 200 and response2.status_code == 200:
            self.writeToConsole(f"More info for order {order_id}")
            orders = response.json()
            products = response2.json()
            self.populate_more_info(orders[0], products)
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
        

    def assign_order(self, row_position, worker_id):
        order_id = self.table.item(row_position, 0).text() 
        print(f"order_id {order_id}")
        print(f"worked_id {worker_id}")
        
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': order_id, 'workerId': worker_id})
        response = requests.put(f'http://localhost:8080/api/orders/assignWorker', headers=headers, data=data)
        
        if response.status_code == 200:
            self.load_orders()
            self.writeToConsole(f'Success: Worker assigned successfully')
        else:
            self.reset_table(row_position)
            body = json.loads(response.text)
            mess = body.get('message')
            self.writeToConsole(f'Error: {mess}')
   
def format_item(item):
    return f"ID: {item['id']} | Name: {item['name']} | Price: ${item['price']} | Category: {item['categoryName']} | Amount: {item['amount']} | Total: ${item['totalPrice']}"


        