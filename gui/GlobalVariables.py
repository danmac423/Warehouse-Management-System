from PySide6.QtCore import  Signal, QObject
from PySide6.QtWidgets import QMessageBox
import copy
import json

class Signals(QObject):
    session_status = Signal(tuple)
    
    ### Views visibility signals ###
    active_view_changed = Signal(int)
    
    dashboard_view_clicked = Signal()
    workers_view_clicked = Signal()
    orders_view_clicked = Signal()
    suppliers_view_clicked = Signal()
    
    window_size_changed = Signal(tuple)
    
    menu_toggled = Signal()
    
    # sessi
    
    


class GlobalVariables(QObject):
    def __init__(self):
        super().__init__()
        self._window_size = (1280,680)
        self.session_token = ""
        self.signals = Signals()
        
    @property
    def window_size(self):
        return self._window_size
    
    @window_size.setter
    def window_size(self, new_window_size):
        self._window_size = new_window_size
        self.signals.window_size_changed.emit(new_window_size)