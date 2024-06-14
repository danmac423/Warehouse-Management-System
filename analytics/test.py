from matplotlib import pyplot as plt
import analytics
import numpy as np
import datetime as dt

if __name__ == "__main__":
    fig, ax = plt.subplots(1,1)
    

    order_num_max, date_min, date_max = analytics.graph_worker_unloaded_orders_per_day('http://localhost:8080/api/ordersHistory/worker/', 
                                                                   'http://localhost:8080/api/workers/', 
                                                                   '1', 
                                                                   ax, 
                                                                   date_min='13/06/2024 00:00:00', 
                                                                   date_max='13/06/2024 23:59:59',
                                                                graph_type='line'
                                                                   )
    ax.set_yticks(np.arange(0, order_num_max+1, 1))
    one_day = np.timedelta64(1, 'D')
    if date_min == date_max:
        ax.set_xticks([date_min])
    else:
        ax.set_xticks(np.arange(date_min, date_max+one_day, one_day))
    ax.xaxis_date()
    ax.grid(True)
    ax.legend()

    fig, ax = plt.subplots(1,1)
    analytics.graph_orders_by_category('http://localhost:8080/api/ordersHistory', 'http://localhost:8080/api/products/orderHistory/', ax)

    plt.show()

