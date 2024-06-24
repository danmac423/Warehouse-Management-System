from PySide6.QtWidgets import   QHBoxLayout, QWidget, QPushButton, QSizePolicy, QSpacerItem

class ToolBarWidget(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables
        self._init_toolbar()
        # self._init_signals()
        # self.login_clicked()

    def _init_toolbar(self):
        
        toolbar_layout = QHBoxLayout()
        self._init_buttons()
        
        toolbar_layout.addWidget(self.toggle_menu_page)

        spacer = QSpacerItem(10, 5, QSizePolicy.Expanding, QSizePolicy.Minimum)
        toolbar_layout.addItem(spacer)
        
        toolbar_layout.addWidget(self.log_out)

        self.setLayout(toolbar_layout)
    
    def _init_buttons(self):

        
        self.toggle_menu_page = QPushButton('Menu', self)
        self.toggle_menu_page.clicked.connect(lambda: self.menu_clicked())
        
        self.log_out = QPushButton("Logout", self)
        self.log_out.clicked.connect(lambda: self.log_out_clicked())
    

    def menu_clicked(self):
        self.globalVariables.signals.menu_toggled.emit()
    
    def log_out_clicked(self):
        self.globalVariables.signals.log_out.emit()
