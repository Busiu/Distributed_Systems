import pykka

from Server import Server

if __name__ == "__main__":
    server = Server.start().proxy()
    result = server.book_searching("Attack on Titan Vol 4").get()
    print(result)
    result = server.book_searching("Attack on Titan Vol 20").get()
    print(result)
    result = server.book_searching("Peter fights Hornet").get()
    print(result)
    result = server.book_searching("Jezioro labedzie").get()
    print(result)
