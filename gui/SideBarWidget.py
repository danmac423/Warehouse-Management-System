from PySide6.QtCore import Qt
from PySide6.QtWidgets import   QHBoxLayout, QWidget, QPushButton, QSizePolicy, QSpacerItem, QFrame, QVBoxLayout

class SideBarWidget(QFrame):
    def __init__(self, globalVariables):
        super().__init__()
        self.sidebar_visible = True
        self.globalVariables = globalVariables
        self._init_sidebar()
        self._init_signals()
    
    def _init_sidebar(self):
        self.setFrameShape(QFrame.StyledPanel)
        self.setFixedWidth(self.globalVariables.window_size[0]/8)
        self.sideBarWidget_layout = QVBoxLayout()
        self.sideBarWidget_layout.setAlignment(Qt.AlignTop)
        
        self.setStyleSheet("background-color: #232222;")
        
        self._init_buttons()
        
        self.sideBarWidget_layout.addWidget(self.switch_to_dashboard_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_products_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_categories_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_workers_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_orders_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_supplies_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_suppliers_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_orders_history_page)
        self.sideBarWidget_layout.addWidget(self.switch_to_supplies_history_page)
        
        # self.sideBarWidget_layout.setContentsMargins(0, 0, 0, 0)
        self.setLayout(self.sideBarWidget_layout)
        
    def _init_buttons(self):

        self.switch_to_dashboard_page = QPushButton('Dashboard', self)
        self.switch_to_dashboard_page.clicked.connect(lambda: self.dashboard_clicked())
        
        self.switch_to_products_page = QPushButton('Products', self)
        self.switch_to_products_page.clicked.connect(lambda: self.products_clicked())
        
        self.switch_to_categories_page = QPushButton('Categories', self)
        self.switch_to_categories_page.clicked.connect(lambda: self.categories_clicked())
        
        self.switch_to_workers_page = QPushButton('Workers', self)
        self.switch_to_workers_page.clicked.connect(lambda: self.workers_clicked())

        self.switch_to_orders_page = QPushButton('Orders', self)
        self.switch_to_orders_page.clicked.connect(lambda: self.orders_clicked()) 
        
        self.switch_to_supplies_page = QPushButton('Supplies', self)
        self.switch_to_supplies_page.clicked.connect(lambda: self.supplies_clicked())

        self.switch_to_suppliers_page = QPushButton('Suppliers', self)
        self.switch_to_suppliers_page.clicked.connect(lambda: self.suppliers_clicked())
        
        self.switch_to_orders_history_page = QPushButton('Order history', self)
        self.switch_to_orders_history_page.clicked.connect(lambda: self.orders_history_clicked()) 
        
        self.switch_to_supplies_history_page = QPushButton('Supplies history', self)
        self.switch_to_supplies_history_page.clicked.connect(lambda: self.supplies_history_clicked())
        
        
    def _init_signals(self):
        self.globalVariables.signals.menu_toggled.connect(lambda: self.toggle_sidebar())
        
    def toggle_sidebar(self):
        if self.sidebar_visible:
            self.hide()
        else:
            self.show()
        self.sidebar_visible = not self.sidebar_visible
        
    def dashboard_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(0)
        self.highlight_button(self.switch_to_dashboard_page)

    def products_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(1)
        self.highlight_button(self.switch_to_products_page)
    
    def categories_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(2)
        self.highlight_button(self.switch_to_categories_page)
        
    def workers_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(3)
        self.highlight_button(self.switch_to_workers_page)

    def orders_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(4)
        self.highlight_button(self.switch_to_orders_page)
    
    def supplies_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(6)
        self.highlight_button(self.switch_to_supplies_page)

    def suppliers_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(5)
        self.highlight_button(self.switch_to_suppliers_page)
    
    def orders_history_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(7)
        self.highlight_button(self.switch_to_orders_history_page)
        
    def supplies_history_clicked(self):
        self.globalVariables.signals.active_view_changed.emit(8)
        self.highlight_button(self.switch_to_supplies_history_page)

    def highlight_button(self, button):
        for btn in[self.switch_to_dashboard_page, self.switch_to_products_page, self.switch_to_categories_page, 
                   self.switch_to_workers_page, self.switch_to_orders_page, self.switch_to_supplies_page, self.switch_to_suppliers_page,
                    self.switch_to_orders_history_page, self.switch_to_supplies_history_page ]:
            btn.setStyleSheet('') 
        button.setStyleSheet('background-color: #1b3b5f')

   