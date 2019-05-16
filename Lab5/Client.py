import pykka

from Server import Server

if __name__ == "__main__":
    server = Server.start()
    response = server.ask("WOW")
    print(response)
    response = server.ask("Hello")
    print(response)