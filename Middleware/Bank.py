import sys
import random
from threading import Thread

import Bank
import Ice

import grpc
import Currency_pb2
import Currency_pb2_grpc


class Account:
    def __init__(self, info, password, type, money):
        self.info = info
        self.password = password
        self.type = type
        self.money = money

    def getPin(self):
        return self.info['pin']

    def getPassword(self):
        return self.password

    def getType(self):
        return self.type

    def getMoney(self):
        return self.money

    def grantMoney(self, howMuch):
        self.money += howMuch


class AccountManagerI(Bank.AccountManager):
    def createAccount(self, firstName, lastName, pin, income, current=None):
        if any([pin == acc.getPin() for acc in accountTable]):
            raise Bank.AccountException('Account with Personal Identity Number like that already exists!')

        if(income >= 5000):
            newType = Bank.AccountType.PREMIUM
        else:
            newType = Bank.AccountType.STANDARD
        newPassword = "password" + str(random.randrange(1, 100))
        newAccount = Account({'firstName': firstName, 'lastName': lastName, 'pin': pin, 'income': income},
                             newPassword,
                             newType,
                             0)
        accountTable.append(newAccount)

        return Bank.RegistrationInfo(newPassword, newType)

    def getLoan(self, pin, password, howMuch, currency, current=None):
        acc = self.login(pin, password)
        if acc is None:
            raise Bank.LoginException('Wrong pin or password')
        if acc.type is not Bank.AccountType.PREMIUM:
            return Bank.LoanInfo(False, 0)

        isGranted = True
        cost = None
        for curr in currencyTable:
            if curr[0] == currency:
                cost = howMuch * curr[1]
                break
        if cost == None:
            raise Bank.CurrencyException('Wrong type of currency')
        acc.grantMoney(cost)

        return Bank.LoanInfo(isGranted, cost)

    def inspectAccount(self, pin, password, current=None):
        acc = self.login(pin, password)
        if acc is None:
            raise Bank.LoginException('Wrong pin or password')

        return Bank.AccountInfo(acc.getType(), acc.getMoney())

    def login(self, pin, password):
        for acc in accountTable:
            if acc.getPin() == pin:
                if acc.getPassword() == password:
                    return acc

        return None

def init():
    currencyTable.append([Bank.Currency.PLN, 1])
    currencyTable.append([Bank.Currency.USD, 3.8])
    currencyTable.append([Bank.Currency.EUR, 4.3])
    currencyTable.append([Bank.Currency.JPY, 0.03])

def startCurrencyClient():
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = Currency_pb2_grpc.CurrencySubscriptionStub(channel)

        recentPrices = stub.Subscribe(Currency_pb2.Empty())

        while True:
            curr = recentPrices.next()
            currValue = curr.value
            currencyTable[curr.currency][1] = currValue

def startServer():
    with Ice.initialize(sys.argv) as communicator:
        adapter = communicator.createObjectAdapterWithEndpoints("BankAdapter", "default -p 10105")
        object = AccountManagerI()
        adapter.add(object, communicator.stringToIdentity("Bank"))
        adapter.activate()
        print("Bank has just started...")
        communicator.waitForShutdown()

accountTable = []
currencyTable = []

if __name__ == '__main__':
    init()
    Thread(target=startCurrencyClient).start()
    startServer()