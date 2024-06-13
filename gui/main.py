
import sys
import os
import shutil
import atexit
from PySide6.QtWidgets import   QApplication, QMainWindow, QVBoxLayout, QHBoxLayout,\
                                QWidget, QStackedWidget, QFrame, QPushButton
from DashboardPage import DashboardPage
from ProductPage import ProductPage
from SideBarWidget import SideBarWidget
from loginGUI import LoginWindow
from ToolBarWidget import ToolBarWidget
from GlobalVariables import GlobalVariables


class MainWindow(QMainWindow):

    def __init__(self):
        super().__init__()
        self.central_widget = QWidget()
        self.setCentralWidget(self.central_widget)
        self.central_layout = QHBoxLayout()

        self.setWindowTitle("Warehouse Application")
        self.setMinimumSize(1280, 860)
        
        self.globalVariables = GlobalVariables()
        
        self.login_widget = LoginWindow(self.globalVariables)
        self.dashboard_page = DashboardPage(self.globalVariables)
        self.product_page = ProductPage(self.globalVariables)
  
        self.stacked_widget = QStackedWidget()
        self.stacked_widget_init()
        
        self.init_toolbar()
        
        self.sideBarWidget = SideBarWidget(self.globalVariables)
        
        self.init_signals()
        self.central_layout.addWidget(self.sideBarWidget)
        self.central_layout.setContentsMargins(0, 0, 0, 0)
        self.central_layout.addWidget(self.stacked_widget)
        self.central_widget.setLayout(self.central_layout)
        

    def init_toolbar(self):
        toolBarWidget = ToolBarWidget( self.globalVariables)
        self.toolbar = self.addToolBar("Tools")
        self.toolbar.setMovable(False)
        self.toolbar.addWidget(toolBarWidget)
    
    def stacked_widget_init(self):
        self.stacked_widget.addWidget(self.login_widget)
        self.stacked_widget.addWidget(self.dashboard_page)
        self.stacked_widget.addWidget(self.product_page)
        
        self.stacked_widget.setCurrentIndex(0)
    
    def init_signals(self):
        self.globalVariables.signals.active_view_changed.connect(lambda view: self.change_view(view))
    
    def change_view(self, view):
        self.stacked_widget.setCurrentIndex(view)

        if view == 0:
            self.globalVariables.signals.dashboard_view_clicked.emit()
        elif view == 1:
            self.globalVariables.signals.workers_view_clicked.emit()
        elif view == 2:
            self.globalVariables.signals.orders_view_clicked.emit()
        elif view == 3:
            self.globalVariables.signals.suppliers_view_clicked.emit()
            
    def resizeEvent(self, event):
        self.globalVariables.window_size = (self.width(), self.height())
            
if __name__ == "__main__":
    app = QApplication(sys.argv)

    window = MainWindow()
    window.show()

    sys.exit(app.exec())
