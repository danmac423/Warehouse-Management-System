from PySide6.QtCore import  Signal, QObject
from PySide6.QtWidgets import QMessageBox
import copy
import json

class Signals(QObject):
    session_status = Signal(tuple)

    ### Views visibility signals ###
    active_view_changed = Signal(int)

    dashboard_view_clicked = Signal()
    products_view_clicked = Signal()
    categories_view_clicked = Signal()
    workers_view_clicked = Signal()
    orders_view_clicked = Signal()
    supplies_view_clicked = Signal()
    suppliers_view_clicked = Signal()
    order_history_view_clicked = Signal()
    supplies_history_view_clicked = Signal()

    login_successful = Signal(dict)
    log_out = Signal()


    window_size_changed = Signal(tuple)

    menu_toggled = Signal()

    # sessi




class GlobalVariables(QObject):
    def __init__(self):
        super().__init__()
        self._window_size = (1280,680)
        self.session_token = ""
        self.tokenType = ""
        self.role = ""
        self.loged_workerID = ""
        self.http_headers = None
        self.signals = Signals()
        self.signals.log_out.connect(self.clear_session_data)
        self.signals.login_successful.connect(self.set_session_data)

    @property
    def window_size(self):
        return self._window_size

    @window_size.setter
    def window_size(self, new_window_size):
        self._window_size = new_window_size
        self.signals.window_size_changed.emit(new_window_size)

    def set_session_data(self, session_data):
        self.session_token = session_data['accessToken']
        self.tokenType = session_data['tokenType']
        self.role = session_data['role']
        self.loged_workerID = session_data['workerId']
        self.http_headers = {
            'Content-Type': 'application/json',
            'Authorization': f'{self.tokenType} {self.session_token}'
        }

    def clear_session_data(self):
        self.session_token = None
        self.tokenType = None
        self.role = None
        self.loged_workerID = None
        self.http_headers = None


