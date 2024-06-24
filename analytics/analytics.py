from typing import List, Tuple
import requests
from matplotlib import pyplot as plt
import numpy as np
from datetime import datetime as dt
from calendar import monthrange


def _get_orders_data(per:str, endpoint_url:str, worker_id:str, date_min:str=None, date_max:str=None) -> dict:
    response = None
    worker_id = str(worker_id)
    if per == 'day' and date_min and date_max:
        date_min = dt.strptime(date_min,  '%Y-%m-%d').isoformat(timespec='microseconds')
        date_max = dt.strptime(date_max,  '%Y-%m-%d').replace(hour=23, minute=59, second=59).isoformat(timespec='microseconds')
        response = requests.get(endpoint_url + worker_id + '/' + date_min +'+00:00' + '/' + date_max + '+00:00')

    elif per == 'month' and date_min and date_max:
        date_min = dt.strptime(date_min,  '%Y-%m').isoformat(timespec='microseconds')
        date_max:dt = dt.strptime(date_max,  '%Y-%m')
        num_days = monthrange(date_max.year, date_max.month)[1]
        date_max = date_max.replace(day=num_days, hour=23, minute=59, second=59).isoformat(timespec='microseconds')
        response = requests.get(endpoint_url + worker_id + '/' + date_min +'+00:00' + '/' + date_max + '+00:00')
    
    elif per == 'month' or per == 'day':
        response = response = requests.get(endpoint_url+worker_id)
    
    else:
        raise(Exception('Incorrect arguments'))


    if response.ok:
        return response.json()

    raise(Exception(response.text))

def _get_order_num_by_day(endpoint_url:str, worker_id:str, date_min:str=None, date_max:str=None, show_empty:bool=False) -> dict:
        orders = _get_orders_data('day', endpoint_url, worker_id, date_min, date_max)
        order_num_by_day = {}
        for order in orders:
            order_date = np.datetime64(order['dateProcessed'][:10])
            order_num_by_day[order_date] = order_num_by_day.get(order_date, 0) + 1
        
        if show_empty:
            _add_empty_dates(order_num_by_day, 'D')

        return order_num_by_day

def _get_avg_order_processing_time_by_day(endpoint_url:str, worker_id:str, date_min:str=None, date_max:str=None, show_empty:bool=False):
    orders = _get_orders_data('day', endpoint_url, worker_id, date_min, date_max)
    avg_processing_time_by_day = {}
    for order in orders:
        processed_datetime = np.datetime64(order['dateProcessed'])
        processed_date = np.datetime64(order['dateProcessed'][:10])
        received_datetime = np.datetime64(order['dateReceived'])
        if processed_date not in avg_processing_time_by_day.keys():
            avg_processing_time_by_day[processed_date] = [processed_datetime - received_datetime]
        else:
            avg_processing_time_by_day[processed_date].append(processed_datetime - received_datetime)
    
    one_hour = np.timedelta64(1, 'h')
    for date in avg_processing_time_by_day.keys():
        avg_processing_time_by_day[date] = np.mean(avg_processing_time_by_day[date])/one_hour
    if show_empty:
        _add_empty_dates(avg_processing_time_by_day, 'D')
    
    return avg_processing_time_by_day

def _get_order_num_by_month(endpoint_url:str, worker_id:str, date_min:str=None, date_max:str=None, show_empty:bool=False) -> dict:
    orders = _get_orders_data('month', endpoint_url, worker_id, date_min, date_max)
    order_num_by_month = {}
    for order in orders:
        order_month = np.datetime64(order['dateProcessed'][:7])
        order_num_by_month[order_month] = order_num_by_month.get(order_month, 0) + 1
    
    if show_empty:
        _add_empty_dates(order_num_by_month, 'M')
    
    return order_num_by_month

def _get_avg_order_processing_time_by_month(endpoint_url:str, worker_id:str, date_min:str=None, date_max:str=None, show_empty:bool=False):
    orders = _get_orders_data('month', endpoint_url, worker_id, date_min, date_max)
    avg_processing_time_by_month = {}
    for order in orders:
        processed_datetime = np.datetime64(order['dateProcessed'])
        processed_month = np.datetime64(order['dateProcessed'][:7])
        received_datetime = np.datetime64(order['dateReceived'])

        if processed_datetime not in avg_processing_time_by_month.keys():
            avg_processing_time_by_month[processed_month] = [processed_datetime - received_datetime]
        else:
            avg_processing_time_by_month[processed_month].append(processed_datetime - received_datetime)
    
    one_hour = np.timedelta64(1, 'h')
    for date in avg_processing_time_by_month.keys():
        avg_processing_time_by_month[date] = np.mean(avg_processing_time_by_month[date])/one_hour

    if show_empty:
        _add_empty_dates(avg_processing_time_by_month)
    
    return avg_processing_time_by_month

def _add_empty_dates(dated_data:dict, step:str) -> None:
    if len(dated_data) == 0:
        return
    for date in np.arange(min(dated_data), max(dated_data), np.timedelta64(1, step)):
        if date not in dated_data.keys():
            dated_data[date] = 0
    
    return 

def _get_worker_name(endpoint_url:str, worker_id:str) -> Tuple[str, str]:
    response = requests.get(endpoint_url+str(worker_id))
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

def _get_categories_stats(orders_endpoint_url:str, dates_endpoint_url:str, product_list_url:str, date_min, date_max) -> dict:
    response = None
    if date_min and date_max:
        date_min = dt.strptime(date_min,  '%Y-%m-%d').isoformat(timespec='microseconds')
        date_max = dt.strptime(date_max,  '%Y-%m-%d').replace(hour=23, minute=59, second=59).isoformat(timespec='microseconds')
        response = requests.get(dates_endpoint_url + date_min +'+00:00' + '/' + date_max + '+00:00')
    else:
        response = requests.get(orders_endpoint_url)
    
    if response.ok:
        categories_stats = {}
        orders = response.json()
        for order in orders:
            order_id = order['id']
            product_list = _get_order_product_list(product_list_url, order_id)
            for product in product_list:
                category = product['category']['name']
                categories_stats[category] = categories_stats.get(category, 0) + int(product['amount'])

        return categories_stats
    
    raise(Exception(response.text))

def graph_worker_orders_data(data:str, endpoint_url_orders:str, endpoint_url_workers:str, worker_id_list:List[str], per:str,
                                         date_min:str=None, date_max:str=None, graph_type:str='line', avg:bool=False, show_empty:bool=False) -> None :
    """This function graphs chosen statistics per month or day
    data - 'processed_nums' or 'processed_times'
    per - 'month' or 'day'
    avg - show average or not
    show empty - add days within range with no data
    """
    fig, ax = plt.subplots(1,1, num='Orders statistics')


    if len(worker_id_list) == 0:
        raise(Exception('Worker id list cannot be empty'))

    get_func = None
    if data == 'processed_nums' and per == 'month':
        fig.suptitle('Number of processed orders per month')
        get_func = _get_order_num_by_month
        ax.set_xlabel('Month')
        ax.set_ylabel('No. of unpacked orders')

    elif data == 'processed_nums' and per == 'day':
        fig.suptitle('Number of processed orders per day')
        get_func = _get_order_num_by_day
        ax.set_xlabel('Date')
        ax.set_ylabel('No. of unpacked orders')

    elif data == 'processed_times' and per == 'month':
        fig.suptitle('Time of order processing per month')
        get_func = _get_avg_order_processing_time_by_month
        ax.set_xlabel('Month')
        ax.set_ylabel('Avg. processing time (H)')

    elif data == 'processed_times' and per == 'day':
        fig.suptitle('Time of order processing per day')
        get_func = _get_avg_order_processing_time_by_day
        ax.set_xlabel('Day')
        ax.set_ylabel('Avg. processing time (H)')

    else:
        raise(Exception('Incorrect arguments'))
            
    ax.grid(True)
    
    max_y = 0
    date_min_x = np.datetime64(dt.today().isoformat())
    date_max_x = np.datetime64(dt.min)

    averages = {}

    for worker_id in worker_id_list:
        worker_name, worker_surname = _get_worker_name(endpoint_url_workers, worker_id)

        order_nums = get_func(endpoint_url_orders, worker_id, date_min=date_min, date_max=date_max, show_empty=show_empty)

        for date in order_nums.keys():
            averages[date] = averages.get(date, 0) + order_nums[date]

        x = list(order_nums.keys())
        y = list(order_nums.values())

        if len(x) > 0:
            x, y = zip(*sorted(zip(x, y)))

        if graph_type == 'line':
            ax.plot(x, y, '-o', label=worker_name + ' ' + worker_surname)
        else:
            ax.bar(x, y, label=worker_name + ' ' + worker_surname)

        try:
            date_min_x = min(min(x), date_min_x)
            date_max_x = max(max(x), date_max_x)
            max_y = max(max(y), max_y)
        except:
            pass
    
    if date_max_x == np.datetime64(dt.min):
        date_max_x = np.datetime64(dt.today().isoformat())
    
    if avg:
        num_workers = len(worker_id_list)
        averages = {k:v/num_workers for k,v in averages.items()}

        x = list(averages.keys())
        y = list(averages.values())

        if len(x) > 0:
            x, y = zip(*sorted(zip(x, y)))

        if graph_type == 'line':
            ax.plot(x, y, '-o', label='Average')
        else:
            ax.bar(x, y, label='Average')


    if per == 'month' and date_min_x != date_max_x:
        one_month = np.timedelta64(1, 'M').astype('<m8[us]')
        ax.set_xticks(np.arange(date_min_x, date_max_x+one_month, one_month))
    
    elif per == 'day' and date_min_x  != date_max_x:
        one_day = np.timedelta64(1, 'D').astype('<m8[us]')
        ax.set_xticks(np.arange(date_min_x, date_max_x+one_day, one_day))

    if data == 'processed_nums':
        ax.set_yticks(np.arange(0, max_y+1, 1))
    else:
        pass

    ax.legend()
    plt.show()

    return

def _compile_orders_data(data:str, endpoint_url_orders:str, worker_id_list:List[str], per:str,
                                         date_min:str=None, date_max:str=None, graph_type:str='line', avg:bool=False) -> None:
    
    get_func = None
    step = None
    if data == 'processed_nums' and per == 'month':
        get_func = _get_order_num_by_month
        step = np.timedelta64(1, 'M')

    elif data == 'processed_nums' and per == 'day':
        get_func = _get_order_num_by_day
        step = np.timedelta64(1, 'D')

    elif data == 'processed_times' and per == 'month':
        get_func = _get_avg_order_processing_time_by_month
        step = np.timedelta64(1, 'M')


    elif data == 'processed_times' and per == 'day':
        get_func = _get_avg_order_processing_time_by_day
        step = np.timedelta64(1, 'D')

    else:
        raise(Exception('Incorrect arguments'))
    

    min_date = np.datetime64(dt.today().isoformat())
    max_date = np.datetime64(dt.min)
    orders_dicts = []
    for worker_id in worker_id_list:
        order_nums:dict = get_func(endpoint_url_orders, worker_id, date_min=date_min, date_max=date_max, show_empty=True)
        orders_dicts.append(order_nums)
        keys = list(order_nums.keys())
        if len(keys) > 0:
            min_date = min(min(keys), min_date)
            max_date = max(max(keys), max_date)

    if max_date == np.datetime64(dt.min):
        max_date = np.datetime64(dt.today().isoformat())
    
    rows = [['Dates'] + worker_id_list]

    dates = None
    if min_date == max_date:
        dates = [min_date]
    else:
        dates = np.arange(min_date, max_date, step)
   
    for date in dates:
        row = [date]
        for orders_dict in orders_dicts:
            row.append(orders_dict.get(date, 0))
        rows.append(row)
    
    return rows

def dump_orders_data_to_csv(filepath:str, data:str, endpoint_url_orders:str, worker_id_list:List[str], per:str,
                                         date_min:str=None, date_max:str=None):
    rows = _compile_orders_data(data, endpoint_url_orders, worker_id_list, per, date_min, date_max)
    with open(filepath, 'w') as file:
        np.savetxt(
            file,
            rows,
            delimiter=',',
            fmt="%s"
        )
    
def graph_orders_by_category(orders_endpoint_url:str, dates_endpoint_url:str, product_list_url:str, date_min:str=None, date_max:str=None) -> bool:


    categories_stats = _get_categories_stats(orders_endpoint_url, dates_endpoint_url, product_list_url, date_min, date_max)
    label_arr = []
    sum_nums = sum(categories_stats.values())
    for category, num in categories_stats.items():
        label_arr.append(str(category)+'\n'+str(round(num/sum_nums*100, 2))+'%')
    
    fig, ax = plt.subplots(1,1, num='Orders statistics')
    ax.pie(list(categories_stats.values()), labels=label_arr, normalize=True)
    plt.show()
    