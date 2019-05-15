from concurrent import futures
from threading import Thread
import time
import random
import Currency_pb2
import Currency_pb2_grpc

import grpc

ONE_DAY_IN_SECONDS = 60 * 60 * 24
currencyTable = []

class CurrencySubscription(Currency_pb2_grpc.CurrencySubscriptionServicer):
    def Subscribe(self, empty, context):
        while True:
            time.sleep(2)
            for curr in Currency_pb2.Currency.values():
                yield Currency_pb2.Response(currency=curr, value=currencyTable[curr])

def init():
    currencyTable.append(1)             ##PLN
    currencyTable.append(3.8)           ##USD
    currencyTable.append(4.3)           ##EUR
    currencyTable.append(0.03)          ##JPY

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    Currency_pb2_grpc.add_CurrencySubscriptionServicer_to_server(
        CurrencySubscription(), server)
    server.add_insecure_port('[::]:50051')
    server.start()
    try:
        while True:
            time.sleep(ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)

def changeValues():
    global currencyTable
    while 1:
        time.sleep(2)
        print("Work Work!")
        valueJump = (random.randint(1, 6) / 100) + 0.97

        currencyTable = [a * valueJump for a in currencyTable]


if __name__ == '__main__':
    init()
    Thread(target=serve).start()
    changeValues()