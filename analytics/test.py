from matplotlib import pyplot as plt
import analytics
import numpy as np
import datetime as dt

if __name__ == "__main__":
    fig, ax = plt.subplots(1,1)
    

    order_num_max = analytics.graph_worker_unloaded_orders_per_day('http://localhost:8080/api/ordersHistory/worker/', 
                                                                   'http://localhost:8080/api/workers/', 
                                                                   '1', 
                                                                   ax, 
                                                                #    date_min='10.06.2024', 
                                                                #    date_max='14.06.2024'
                                                                   )
    ax.set_yticks(np.arange(0, order_num_max+1, 1))
    ax.xaxis_date()
    ax.grid(True)
    ax.legend()
    plt.show()

