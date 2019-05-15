module Bank
{
    enum AccountType {STANDARD, PREMIUM}

    enum Currency {PLN, USD, EUR, JPY}

    struct RegistrationInfo
    {
        string password;
        AccountType type;
    }

    struct LoanInfo
    {
        bool isGranted;
        int cost;
    }

    struct AccountInfo
    {
        AccountType type;
        int money;
    }

    exception AccountException
    {
        string message;
    }

    exception LoginException
    {
        string message;
    }

    exception CurrencyException
    {
        string message;
    }

    interface AccountManager
    {
        RegistrationInfo createAccount(string firstName, string lastName, string pin, int income) throws AccountException;
        LoanInfo getLoan(string pin, string password, int howMuch, Currency currency) throws LoginException, CurrencyException;
        AccountInfo inspectAccount(string pin, string password) throws LoginException;
    }

}