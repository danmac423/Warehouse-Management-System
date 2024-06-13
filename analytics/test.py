from matplotlib import pyplot as plt
import analytics
import numpy as np

if __name__ == "__main__":
    fig, ax = plt.subplots(1,1)
    

    order_num_max = analytics.graph_worker_unloaded_orders_per_day('http://localhost:8080/api/ordersHistory/worker/', 'http://localhost:8080/api/workers/', '1', ax)
    ax.set_yticks(np.arange(0, order_num_max+1, 1))
    ax.xaxis_date()
    ax.legend()
    plt.show()

