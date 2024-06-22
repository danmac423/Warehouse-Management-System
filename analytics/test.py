from matplotlib import pyplot as plt
import analytics
import numpy as np
import datetime as dt

if __name__ == "__main__":
    

   fig, ax = plt.subplots(1,1)

   analytics.graph_worker_orders_data('processed_times', 'http://localhost:8080/api/ordersHistory/worker/', 
                                                                   'http://localhost:8080/api/workers/', 
                                                                   ['1', '2'], 
                                                                   'day',
                                                                   ax, 
                                                                   date_min='2024-06-01', 
                                                                   date_max='2024-06-30',
                                                                graph_type='line',
                                                                show_empty=False,
                                                                avg=True
                                                                   )


   plt.show()

   #  analytics.graph_orders_by_category('http://localhost:8080/api/ordersHistory', 'http://localhost:8080/api/products/orderHistory/', ax)
