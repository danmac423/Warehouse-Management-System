from PySide6.QtWidgets import QApplication, QWidget, QLabel, QVBoxLayout, QGridLayout, QSizePolicy
from PySide6.QtGui import QColor, QFont
from PySide6.QtCore import Qt

class DashboardPage(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables
        self.colors = ['#FF5733', '#33FF57', '#3357FF', '#FF33A8', '#A833FF', '#33FFF5', '#FF8C33', ' #E4D00A']
        self._init_signals()
        self._init_blocks()
        self.initUI()

    def initUI(self):
        # layout = QVBoxLayout()
   
        layout = QGridLayout()

        # Define colors for the blocks
        
        
        # print(f"height: {self.height()}")
        # print(f"width: {self.width()}")
        
        for i in range (len(self.blocks_list)):
            # label = QLabel()
            # label.setStyleSheet(f"background-color: {color};")
            
            # label.setStyleSheet(f"""
            #     background-color: {color};
            #     border-radius: 2px;
            # """)
            
            size_policy = QSizePolicy(QSizePolicy.Expanding, QSizePolicy.Expanding)
            self.blocks_list[i].setSizePolicy(size_policy)  
            global_w, global_h = self.globalVariables.window_size


            size = min(global_w/4 - global_w/40, global_h/2 - 20)
            self.blocks_list[i].setMaximumSize(size, size)  

            # row and column positions
            row = i // 4
            col = i % 4
            layout.addWidget(self.blocks_list[i], row, col)  

        self.setLayout(layout)
        
    def _init_blocks(self):
        self.products_block = Block("Products", self.globalVariables, self.colors[0])
        self.categories_block = Block("Categories", self.globalVariables, self.colors[1])
        self.workers_block = Block("Workers", self.globalVariables, self.colors[2])
        self.orders_block = Block("Orders", self.globalVariables, self.colors[3])
        self.supplies_block = Block("Supplies", self.globalVariables, self.colors[4])
        self.suppliers_block = Block("Suppliers", self.globalVariables, self.colors[5])
        self.order_history_block = Block("Orders history", self.globalVariables, self.colors[6])
        self.supplies_history_block = Block("Supplies history", self.globalVariables, self.colors[7])
        
        self.blocks_list = [self.products_block, self.categories_block, self.workers_block, self.orders_block, self.supplies_block, self.suppliers_block, self.order_history_block, self.supplies_history_block]
    
    def _init_signals(self):
        self.globalVariables.signals.window_size_changed.connect(lambda new_window_size: self.update_window_size(new_window_size))
        
    def update_window_size(self, new_window_size):
        for block in self.blocks_list:
            global_w, global_h = new_window_size
            size = min(global_w/4 - global_w/40, global_h/2 - 20)
            if block:
                block.setMaximumSize(size, size)
    
class Block(QLabel):
    def __init__(self, name, globalVariables, color):
        super().__init__()
        self.globalVariables = globalVariables
        self.name = name
        self.color = color
        # self.setStyleSheet(f"""
        #     background-color: {color};
        #     border-radius: 4px;
        # """)
        darker_color = self.darker_color(color)
        self.setStyleSheet(f"""
            QLabel {{
                background-color: {self.color};
                border-radius: 8px;
                padding: 10px;
                color: white; 
            }}
            QLabel:hover {{
                background-color: {darker_color};
            }}
        """)
    
        self.setAlignment(Qt.AlignCenter)

        self.setFont(QFont("Helvetica", 18, QFont.Bold))  
        self.setText(self.name)
        
    def darker_color(self, color):
        
        q_color = QColor(color)
        darker_q_color = q_color.darker(150)  # 200 is twice as dark
        return darker_q_color.name()
            
    
        

