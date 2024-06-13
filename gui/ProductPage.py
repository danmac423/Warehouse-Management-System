from PySide6.QtWidgets import QApplication, QTableWidget, QTableWidgetItem, QVBoxLayout, QPushButton, QWidget, QHBoxLayout, QGroupBox,QGridLayout, QScrollArea, QHeaderView, \
    QComboBox, QLineEdit
import sys
from PySide6.QtCore import Qt

class ProductPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables
        
        self.products = [
            [1, 'Product A', 10.99, 'Category 1', 100],
            [2, 'Product B', 15.99, 'Category 2', 150],
            [3, 'Product C', 12.99, 'Category 1', 200],
            [4, 'Product D', 20.99, 'Category 3', 250],
            [1, 'Product A', 10.99, 'Category 1', 100],
            [2, 'Product B', 15.99, 'Category 2', 150],
            [3, 'Product C', 12.99, 'Category 1', 200],
            [1, 'Product A', 10.99, 'Category 1', 100],
            [2, 'Product B', 15.99, 'Category 2', 150],
            [3, 'Product C', 12.99, 'Category 1', 200],
            [1, 'Product A', 10.99, 'Category 1', 100],
            [2, 'Product B', 15.99, 'Category 2', 150],
            [3, 'Product C', 12.99, 'Category 1', 200],
            [1, 'Product A', 10.99, 'Category 1', 100],
            [2, 'Product B', 15.99, 'Category 2', 150],
            [3, 'Product C', 12.99, 'Category 1', 200],
            [1, 'Product A', 10.99, 'Category 1', 100],
            [2, 'Product B', 15.99, 'Category 2', 150],
            [3, 'Product C', 12.99, 'Category 1', 200],
        ]

        self._setup_ui()
        layout = QVBoxLayout()
        layout.addWidget(self.search_widget)
        layout.addWidget(self.products_widget)
        layout.setAlignment(Qt.AlignTop)
        self.setLayout(layout)
    
    def _setup_ui(self):
        self.search_widget = QGroupBox("Search Products")
        self.search_widget.setMinimumHeight(50)
        self.search_widget.setMaximumHeight(100)
        self.serach_layout = QGridLayout(self.search_widget)
    
        
        self.search_bar = QLineEdit(self)
        self.search_bar.setPlaceholderText("Search by product name")
        self.search_bar.textChanged.connect(self.update_table)
        
        self.category_dropdown = QComboBox(self)
        self.category_dropdown.addItem("All Categories")
        categories = set([product[3] for product in self.products])
        self.category_dropdown.addItems(categories)
        self.category_dropdown.currentTextChanged.connect(self.update_table)
        
        self.serach_layout.addWidget(self.search_bar, 0,0)
        self.serach_layout.addWidget(self.category_dropdown, 0,1,)
        self.serach_layout.setColumnStretch(0, 1)
        self.serach_layout.setColumnStretch(1, 1)
        
        self.products_widget = QGroupBox("Products")
        self.products_widget.setMinimumHeight(210)
        self.products_widget.setMaximumHeight(350)
        self.products_layout = QGridLayout(self.products_widget)
        
        self.table = QTableWidget()
        self.table_scrollArea = QScrollArea()
        
        self._init_table()
        self.products_layout.addWidget(self.table_scrollArea)
    
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
        
        self.populate_table()
        
    def populate_table(self):
    
        # data = [
        #     [1, 'Product A', 10.99, 'Category 1', 100],
        #     [2, 'Product B', 15.99, 'Category 2', 150],
        #     [3, 'Product C', 12.99, 'Category 1', 200],
        #     [1, 'Product A', 10.99, 'Category 1', 100],
        #     [2, 'Product B', 15.99, 'Category 2', 150],
        #     [3, 'Product C', 12.99, 'Category 1', 200],
        #     [1, 'Product A', 10.99, 'Category 1', 100],
        #     [2, 'Product B', 15.99, 'Category 2', 150],
        #     [3, 'Product C', 12.99, 'Category 1', 200],
        #     [1, 'Product A', 10.99, 'Category 1', 100],
        #     [2, 'Product B', 15.99, 'Category 2', 150],
        #     [3, 'Product C', 12.99, 'Category 1', 200],
        #     [1, 'Product A', 10.99, 'Category 1', 100],
        #     [2, 'Product B', 15.99, 'Category 2', 150],
        #     [3, 'Product C', 12.99, 'Category 1', 200],
        # ]
        
        self.table.setRowCount(len(self.products))
        
        for row, (id, product_name, price, category, stock) in enumerate(self.products):
            
            item =  QTableWidgetItem(str(id))
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 0, item)
            
            item =  QTableWidgetItem(product_name)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 1, item)
            
            item =  QTableWidgetItem(f"${price:.2f}")
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 2, item)
            
            item =  QTableWidgetItem(category)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 3, item)
            
            item =  QTableWidgetItem(str(stock))
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 4, item )
            
            edit_button = QPushButton('Edit')
            self.table.setCellWidget(row, 5, edit_button)
            
            delete_button = QPushButton('Delete')
            self.table.setCellWidget(row, 6, delete_button)
        
    def update_table(self):
        search_text = self.search_bar.text().lower()
        selected_category = self.category_dropdown.currentText()
        
        filtered_products = [
            product for product in self.products
            if (search_text in product[1].lower()) and
               (selected_category == "All Categories" or product[3] == selected_category)
        ]
        
        self.table.setRowCount(len(filtered_products))
        
        for row, (id, product_name, price, category, stock) in enumerate(filtered_products):
            item = QTableWidgetItem(str(id))
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 0, item)
            
            item = QTableWidgetItem(product_name)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 1, item)
            
            item = QTableWidgetItem(f"${price:.2f}")
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 2, item)
            
            item = QTableWidgetItem(category)
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 3, item)
            
            item = QTableWidgetItem(str(stock))
            item.setTextAlignment(Qt.AlignCenter)
            self.table.setItem(row, 4, item)
            
            edit_button = QPushButton('Edit')
            self.table.setCellWidget(row, 5, edit_button)
            
            delete_button = QPushButton('Delete')
            self.table.setCellWidget(row, 6, delete_button)

