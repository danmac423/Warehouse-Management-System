from typing import List, Tuple
import requests
from matplotlib import pyplot as plt
import numpy as np
from datetime import datetime as dt



# TODO Saving historical data
# def generate_worker_list(workers_url):
#     response = requests.get(workers_url)
#     if response.ok():
#         workers = response.json()
#         new_worker_lists = {}
#         for worker in workers:
#             new_worker_lists[worker['id']] = {
#                 'name' : worker['name'],
#                 'orders': [],
#                 'supplies': []   
#             }
    
#     return new_worker_lists

# def generate_order_list(order_url):
#     response = requests.get(order_url)
#     if response.ok:
#         orders = response.json()
#         new_order_list = {}
#         for order in orders:
#             new_order_list

# def generate_product_list(product_url):
#     pass

# def generate_category_list(category_url):
#     pass

# def generate_supplier_list(supplier_url):
#     pass   


def _get_order_num_by_day(endpoint_url:str, worker_id:str, date_min:str=None, date_max:str=None) -> dict:
    response = None
    if date_min and date_max:
        response = requests.get(endpoint_url+worker_id+'/'+date_min+'/'+date_max)
    else:
        response = requests.get(endpoint_url+worker_id)

    if response.ok:
        orders = response.json()
        order_num_by_day = {}
        for order in orders:
            order_date = np.datetime64(order['dateProcessed'])
            order_num_by_day[order_date] = order_num_by_day.get(order_date, 0) + 1

        return order_num_by_day
    
    raise(Exception(response.text))

def _get_worker_name(endpoint_url:str, worker_id:str) -> Tuple[str, str]:
    response = requests.get(endpoint_url+worker_id)
    if response.ok:
        worker = response.json()
        name = worker['name']
        last_name = worker['lastName']
        
        return name, last_name 
    
    raise(Exception(response.text))

def _get_order_product_list(endpoint_url:str, order_id:str):
    response = requests.get(endpoint_url+order_id)
    if response.ok:
        product_list = response.json()
        return product_list
    
    raise(Exception(response.text))

def _get_categories_stats(orders_endpoint_url:str, product_list_url:str) -> dict:
    response = requests.get(orders_endpoint_url)
    if response.ok:
        categories_stats = {}
        orders = response.json()
        for order in orders:
            order_id = order['id']
            product_list = _get_order_product_list(product_list_url, order_id)
            for product in product_list:
                category = product['categoryId']
                categories_stats[category] = categories_stats.get(category, 0) + 1

        return categories_stats
    
    raise(Exception(response.text))

# TODO rozwiązać anomalię z 13.06.2024

def graph_worker_unloaded_orders_per_day(endpoint_url_orders:str, endpoint_url_workers:str, worker_id:str, ax:plt.Axes, date_min:str=None, date_max:str=None, graph_type:str='line') -> int :
    order_num_by_day = _get_order_num_by_day(endpoint_url_orders, worker_id, date_min=date_min, date_max=date_max)
    worker_name, worker_surname = _get_worker_name(endpoint_url_workers, worker_id)
    plot_func = None
    if graph_type == 'line':
        ax.plot(order_num_by_day.keys(), list(order_num_by_day.values()), '-o', label=worker_name + ' ' + worker_surname)
    else:
        ax.bar(order_num_by_day.keys(), list(order_num_by_day.values()), label=worker_name + ' ' + worker_surname)

    max_val = 0
    min_date = None
    max_date = None
    try:
        max_val = max(list(order_num_by_day.values()))
        min_date = min(list(order_num_by_day.keys()))
        max_date = max(list(order_num_by_day.keys()))
    except:
        if len(order_num_by_day.keys()) == 1:
            min_date = order_num_by_day.keys[0]
            max_date = order_num_by_day.keys[0]
        else:
            min_date = np.datetime64(dt.today().isoformat())
            max_date = np.datetime64(dt.today().isoformat())

    return max_val, min_date, max_date   
    

def graph_worker_unloaded_orders(endpoint_url:str, worker_id_list:List[str], ax:plt.Axes, graph_type:str=None):
    pass



def graph_orders_by_category(orders_endpoint_url:str, product_list_url:str) -> bool:
    categories_stats = _get_categories_stats(orders_endpoint_url, product_list_url)


    

    