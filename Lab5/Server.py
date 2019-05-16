import pykka


class Server(pykka.ThreadingActor):
    def on_receive(self, message):
        if message == "Hello":
            return "NO SIEMA"
        else:
            return "I like umbrellas"