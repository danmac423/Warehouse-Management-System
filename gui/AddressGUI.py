import sys
from PyQt5.QtWidgets import QApplication, QWidget, QVBoxLayout, QLineEdit, QPushButton, QLabel, QTableWidget, QTableWidgetItem, QMessageBox, QAbstractItemView
import requests
import json


class AddressManager(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        self.street_label = QLabel('Street')
        layout.addWidget(self.street_label)
        self.street_input = QLineEdit()
        layout.addWidget(self.street_input)

        self.house_nr_label = QLabel('House number')
        layout.addWidget(self.house_nr_label)
        self.house_nr_input = QLineEdit()
        layout.addWidget(self.house_nr_input)

        self.postal_code_label = QLabel('Postal code')
        layout.addWidget(self.postal_code_label)
        self.postal_code_input = QLineEdit()
        layout.addWidget(self.postal_code_input)

        self.city_label = QLabel('City')
        layout.addWidget(self.city_label)
        self.city_input = QLineEdit()
        layout.addWidget(self.city_input)

        self.country_label = QLabel('Country')
        layout.addWidget(self.country_label)
        self.country_input = QLineEdit()
        layout.addWidget(self.country_input)

        self.add_button = QPushButton('Add Address')
        self.add_button.clicked.connect(self.add_address)
        layout.addWidget(self.add_button)

        self.update_button = QPushButton('Update Address')
        self.update_button.clicked.connect(self.update_address)
        layout.addWidget(self.update_button)

        self.delete_button = QPushButton('Delete Address')
        self.delete_button.clicked.connect(self.delete_address)
        layout.addWidget(self.delete_button)

        self.addresses_table = QTableWidget()
        self.addresses_table.setColumnCount(5)
        self.addresses_table.setHorizontalHeaderLabels(['ID', 'Street', 'House Number', 'Postal Code', 'City', 'Country'])
        self.addresses_table.setSelectionBehavior(QAbstractItemView.SelectRows)
        layout.addWidget(self.addresses_table)

        self.load_addresses_button = QPushButton('Load addresses')
        self.load_addresses_button.clicked.connect(self.load_addresses)
        self.load_addresses()
        layout.addWidget(self.load_addresses_button)

        self.setLayout(layout)
        self.setWindowTitle('Addresses Manager')
        self.show()

    def add_address(self):
        street = self.street_input.text()
        house_nr = self.house_nr_input.text()
        postal_code = self.postal_code_input.text()
        city = self.city_input.text()
        country = self.country_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'postal_code': postal_code, 'city': city, 'street': street, 'house_nr': house_nr, 'country': country})
        response = requests.post('http://localhost:8080/api/addresses', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'address added successfully')
            self.load_addresses()
        else:
            QMessageBox.warning(self, 'Error', 'Failed to add address')

    def update_address(self):
        selected_row = self.addresses_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No address selected')
            return

        address_id = self.addresses_table.item(selected_row, 0).text()
        name = self.name_input.text()
        price = self.price_input.text()
        category_id = self.category_id_input.text()
        stock = self.stock_input.text()
        headers = {'Content-Type': 'application/json'}
        data = json.dumps({'id': address_id, 'name': name, 'price': price, 'categoryId': category_id, 'stock': stock})
        response = requests.put(f'http://localhost:8080/api/addresses', headers=headers, data=data)

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'address updated successfully')
            self.load_addresses()
        else:
            QMessageBox.warning(self, 'Error', 'Failed to update address')

    def delete_address(self):
        selected_row = self.addresses_table.currentRow()
        if selected_row == -1:
            QMessageBox.warning(self, 'Error', 'No address selected')
            return

        address_id = self.addresses_table.item(selected_row, 0).text()
        response = requests.delete(f'http://localhost:8080/api/addresses/{address_id}')

        if response.status_code == 200:
            QMessageBox.information(self, 'Success', 'address deleted successfully')
            self.load_addresses()
        else:
            QMessageBox.warning(self, 'Error', 'Failed to delete address')

    def load_addresses(self):
        response = requests.get('http://localhost:8080/api/addresses')

        if response.status_code == 200:
            self.addresses_table.setRowCount(0)
            addresses = response.json()
            for address in addresses:
                row_position = self.addresses_table.rowCount()
                self.addresses_table.insertRow(row_position)
                self.addresses_table.setItem(row_position, 0, QTableWidgetItem(str(address['id'])))
                self.addresses_table.setItem(row_position, 1, QTableWidgetItem(address['name']))
                self.addresses_table.setItem(row_position, 2, QTableWidgetItem(str(address['price'])))
                self.addresses_table.setItem(row_position, 3, QTableWidgetItem(str(address['categoryId'])))
                self.addresses_table.setItem(row_position, 4, QTableWidgetItem(str(address['stock'])))
        else:
            QMessageBox.warning(self, 'Error', 'Failed to load addresses')


if __name__ == '__main__':
    app = QApplication(sys.argv)
    ex = AddressManager()
    sys.exit(app.exec_())
