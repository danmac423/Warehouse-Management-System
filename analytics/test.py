from matplotlib import pyplot as plt
import analytics
import numpy as np
import datetime as dt

if __name__ == "__main__":
    fig, ax = plt.subplots(1,1)
    

    analytics.graph_worker_unloaded_orders('http://localhost:8080/api/ordersHistory/worker/', 
                                                                   'http://localhost:8080/api/workers/', 
                                                                   ['1', '2'], 
                                                                   'month',
                                                                   ax, 
                                                                  #  date_min='2024-06', 
                                                                  #  date_max='2024-06',
                                                                graph_type='line'
                                                                   )


    fig, ax = plt.subplots(1,1)
    analytics.graph_orders_by_category('http://localhost:8080/api/ordersHistory', 'http://localhost:8080/api/products/orderHistory/', ax)

    plt.show()

