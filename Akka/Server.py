import pykka

from DatabaseScanner import DatabaseScanner


class Server(pykka.ThreadingActor):
    def __init__(self):
        super().__init__()
        self.scanner1 = DatabaseScanner.start("databases/database1").proxy()
        self.scanner2 = DatabaseScanner.start("databases/database2").proxy()

    def greet(self):
        return "Hello"

    def book_searching(self, title):
        result1 = self.scanner1.scan(title).get()
        result2 = self.scanner2.scan(title).get()
        if result2 is not None:
            result1 = result2
        return result1

    def book_order(self, title):
        searching_result = self.book_searching(title)
        if searching_result is not None:
            with open("order.txt", 'a+') as order_file:
                order_file.write(searching_result)

    def on_receive(self, message):
        if message == "Hello":
            return "NO SIEMA"
        else:
            return "I like umbrellas"