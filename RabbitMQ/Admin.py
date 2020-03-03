import pika

from threading import Thread
from Utils import Message


def loggerHandler():
    channel.exchange_declare(exchange='exchangeInfo', exchange_type='direct')
    channel.queue_declare(queue='log')
    channel.queue_bind(exchange='exchangeInfo', queue='log', routing_key='log')
    channel.basic_consume(queue='log',
                          on_message_callback=supervise)
    channel.start_consuming()

def supervise(ch, method, properties, body):
    message = Message.decode(body)
    print("Received from %s: Information about patient %s and his problem with %s" % (message.sender,
                                                                                      message.patientSurname,
                                                                                      message.patientMalfunction))
    ch.basic_ack(delivery_tag=method.delivery_tag)

if __name__ == '__main__':
    credentials = pika.PlainCredentials('admin', 'admin2017')
    parameters = pika.ConnectionParameters('192.168.99.100',
                                           5672,
                                           '/',
                                           credentials)

    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.queue_declare(queue='info')

    logger = Thread(target=loggerHandler)
    logger.start()

    while True:
        message = input("Please, enter the message to all hospital workers:")
        channel.basic_publish(exchange='exchangeAdmin',
                              body=message,
                              routing_key='')


