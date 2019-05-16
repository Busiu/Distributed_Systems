import pykka


class DatabaseScanner(pykka.ThreadingActor):
    def __init__(self, database):
        super().__init__()
        self.database = database

    def scan(self, title):
        with open(self.database, "r") as database:
            books = []
            for line in database:
                books.append(line)
        for book in books:
            tmp = book.split(' : ')
            if tmp[0] == title:
                return book
        return None
