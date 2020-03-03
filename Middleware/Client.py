import sys, Ice
import Bank

def stringToEnumCurrency(stringCurrency):
    if stringCurrency == "PLN":
        return Bank.Currency.PLN
    elif stringCurrency == "USD":
        return Bank.Currency.USD
    elif stringCurrency == "EUR":
        return Bank.Currency.EUR
    elif stringCurrency == "JPY":
        return Bank.Currency.JPY
    return Bank.Currency.PLN

def createAccountClient():
    print("Please, give me your first name:")
    firstName = input()
    print("Please, give me your last name:")
    lastName = input()
    print("Please, give me your pin:")
    pin = input()
    print("Please, give me your income:")
    income = int(input())

    ans = bank.createAccount(firstName, lastName, pin, income)
    print(ans)

def getLoanClient():
    print("Sir, how much money do you want:")
    howMuch = int(input())
    print("Sir, in which currency:")
    stringCurrency = input()
    currency = stringToEnumCurrency(stringCurrency)
    print("Enter your pin:")
    pin = input()
    print("Enter your password:")
    password = input()

    ans = bank.getLoan(pin, password, howMuch, currency)
    print(ans)

def inspectAccountClient():
    print("Enter your pin:")
    pin = input()
    print("Enter your password:")
    password = input()

    ans = bank.inspectAccount(pin, password)
    print(ans)


if __name__ == '__main__':
    with Ice.initialize(sys.argv) as communicator:
        base = communicator.stringToProxy("Bank:default -p 10103")
        bank = Bank.AccountManagerPrx.checkedCast(base)
        if not bank:
            raise RuntimeError("Invalid proxy")

        while True:
            print("Hello! What do you want to do?")
            print("\t1 - Create Account")
            print("\t2 - Get Loan")
            print("\t3 - Inspect Account")
            ans = input()
            print(ans)

            if ans == "1":
                createAccountClient()
            elif ans == "2":
                getLoanClient()
            elif ans == "3":
                inspectAccountClient()
            else:
                print(ans)
                print("Smieszne, doprawdy")