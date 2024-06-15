from typing import List, Tuple
import requests
from matplotlib import pyplot as plt
import numpy as np
from datetime import datetime as dt
from calendar import monthrange



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
        date_min = dt.strptime(date_min,  '%Y-%m-%d').isoformat(timespec='microseconds')
        date_max = dt.strptime(date_max,  '%Y-%m-%d').replace(hour=23, minute=59, second=59).isoformat(timespec='microseconds')
        response = requests.get(endpoint_url + worker_id + '/' + date_min +'+00:00' + '/' + date_max + '+00:00')
    else:
        response = requests.get(endpoint_url+worker_id)

    if response.ok:
        orders = response.json()
        order_num_by_day = {}
        for order in orders:
            order_date = np.datetime64(order['dateProcessed'][:10])
            order_num_by_day[order_date] = order_num_by_day.get(order_date, 0) + 1

        return order_num_by_day
    
    raise(Exception(response.text))

def _get_order_num_by_month(endpoint_url:str, worker_id:str, date_min:str=None, date_max:str=None) -> dict:
    response = None
    if date_min and date_max:
        date_min = dt.strptime(date_min,  '%Y-%m').isoformat(timespec='microseconds')
        date_max:dt = dt.strptime(date_max,  '%Y-%m')
        num_days = monthrange(date_max.year, date_max.month)[1]
        date_max = date_max.replace(day=num_days, hour=23, minute=59, second=59).isoformat(timespec='microseconds')
        response = requests.get(endpoint_url + worker_id + '/' + date_min +'+00:00' + '/' + date_max + '+00:00')
    else:
        response = requests.get(endpoint_url+worker_id)

    if response.ok:
        orders = response.json()
        order_num_by_month = {}
        for order in orders:
            order_month = np.datetime64(order['dateProcessed'][:7])
            order_num_by_month[order_month] = order_num_by_month.get(order_month, 0) + 1
        
        return order_num_by_month

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
    response = requests.get(endpoint_url+str(order_id))
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
                print(product)
                category = product['categoryName']
                categories_stats[category] = categories_stats.get(category, 0) + int(product['amount'])

        return categories_stats
    
    raise(Exception(response.text))

def graph_worker_unloaded_orders(endpoint_url_orders:str, endpoint_url_workers:str, worker_id_list:List[str], per:str, ax:plt.Axes, 
                                         date_min:str=None, date_max:str=None, graph_type:str='line') -> None :
    
    get_func = None
    if per == 'month':
        get_func = _get_order_num_by_month
        ax.set_xlabel('Month')
        if date_min and date_max:
            date_min_datetime = np.datetime64(date_min)
            date_max_datetime = np.datetime64(date_max)

            one_month = np.timedelta64(1, 'M')
            ax.set_xticks(np.arange(date_min_datetime-one_month, date_max_datetime+one_month, one_month))

    else:
        get_func = _get_order_num_by_day
        ax.set_xlabel('Date')
        if date_min and date_max:
            date_min_datetime = np.datetime64(date_min)
            date_max_datetime = np.datetime64(date_max)

            one_day = np.timedelta64(1, 'D')
            ax.set_xticks(np.arange(date_min_datetime-one_day, date_max_datetime+one_day, one_day))

    ax.grid(True)
    ax.set_ylabel('No. of unpacked orders')
    
    max_num_orders = 0
    for worker_id in worker_id_list:
        worker_name, worker_surname = _get_worker_name(endpoint_url_workers, worker_id)

        order_nums = get_func(endpoint_url_orders, worker_id, date_min=date_min, date_max=date_max)

        x = list(order_nums.keys())
        y = list(order_nums.values())

        if graph_type == 'line':
            ax.plot(x, y, '-o', label=worker_name + ' ' + worker_surname)
        else:
            ax.bar(x, y, label=worker_name + ' ' + worker_surname)

        try:
            max_num_orders = max(max(y), max_num_orders)
        except:
            pass
    
    ax.set_yticks(np.arange(0, max_num_orders+1, 1))
    ax.legend()

    return 


def graph_orders_by_category(orders_endpoint_url:str, product_list_url:str, ax:plt.Axes) -> bool:
    categories_stats = _get_categories_stats(orders_endpoint_url, product_list_url)
    ax.pie(list(categories_stats.values()), labels=list(categories_stats.keys()), normalize=True)
    return True

    

    