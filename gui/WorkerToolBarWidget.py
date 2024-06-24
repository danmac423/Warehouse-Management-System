from PySide6.QtWidgets import   QHBoxLayout, QWidget, QPushButton, QSizePolicy, QSpacerItem, QLabel

class WorkerToolBarWidget(QWidget):
    def __init__(self, globalVariables):
        super().__init__()
        self.globalVariables = globalVariables
        self._init_toolbar()

    def _init_toolbar(self):
        
        toolbar_layout = QHBoxLayout()
        self._init_buttons()
        
        toolbar_layout.addWidget(self.loginData)

        spacer = QSpacerItem(10, 5, QSizePolicy.Expanding, QSizePolicy.Minimum)
        toolbar_layout.addItem(spacer)
        
        toolbar_layout.addWidget(self.log_out)

        self.setLayout(toolbar_layout)
    
    def _init_buttons(self):
        
        self.loginData = QLabel(self.login_info())
        
        self.log_out = QPushButton("Logout", self)
        self.log_out.clicked.connect(lambda: self.log_out_clicked())
    

    def menu_clicked(self):
        self.globalVariables.signals.menu_toggled.emit()
    
    def log_out_clicked(self):
        self.globalVariables.signals.log_out.emit()
    
    def login_info(self):
        role = self.globalVariables.role
        worker_id = self.globalVariables.loged_workerID
        
        return f"Logged ID: {worker_id}, role: {role}"
        
        
