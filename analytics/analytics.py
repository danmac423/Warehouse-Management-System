import requests
import json



def generate_worker_list(workers_url):
    response = requests.get(workers_url)
    if response.ok():
        workers = response.json()
        new_worker_lists = {}
        for worker in workers:
            new_worker_lists[worker['id']] = {
                'name' : worker['name'],
                'orders': [],
                'supplies': []   
            }
    
    return new_worker_lists

def generate_order_list(order_url):
    response = requests.get(order_url)
    if response.ok():
        orders = response.json()
        new_order_list = {}
        for order in orders:
            new_order_list

def generate_product_list(product_url):
    pass

def generate_category_list(category_url):
    pass

def generate_supplier_list(supplier_url):
    pass
            