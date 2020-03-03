import pika
import time
import random

from Utils import Message
from threading import Thread


def cure(ch, method, properties, body):
    message = Message.decode(body)
    print("Received from: %s. Curing patient %s his ill %s" % (message.sender,
                                                               message.patientSurname,
                                                               message.patientMalfunction))
    time.sleep(random.randint(3, 5))

    routing = message.sender
    message.sender = technicianName
    msg = str(message)
    channel.basic_publish(exchange='exchangeInfo',
                          routing_key=routing,
                          body=msg)
    channel.basic_publish(exchange='exchangeInfo',
                          routing_key='log',
                          body=msg)
    ch.basic_ack(delivery_tag=method.delivery_tag)

def printInfo(ch, method, properties, body):
    print(body.decode('UTF-8'))
    ch.basic_ack(delivery_tag=method.delivery_tag)

if __name__ == '__main__':
    credentials = pika.PlainCredentials('admin', 'admin2017')
    parameters = pika.ConnectionParameters('192.168.99.100',
                                           5672,
                                           '/',
                                           credentials)

    malfunctions = ('hip',
                    'knee',
                    'elbow')

    technicianID = input("Please, enter technician ID:")
    technicianName = "Technician%s" % (technicianID)

    specialization1 = input("Please, enter his first specialization:")
    specialization2 = input("Please, enter his second specialization:")
    if (not specialization1.startswith(malfunctions) or not specialization2.startswith(malfunctions)):
        print("Why not so serious?")
        exit()

    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()

    channel.exchange_declare(exchange='exchangeInfo', exchange_type='direct')
    channel.exchange_declare(exchange='exchangeAdmin', exchange_type='fanout')
    adminQueue = "%s admin" % (technicianName)
    channel.queue_declare(queue=adminQueue)
    channel.queue_bind(exchange='exchangeAdmin', queue=adminQueue, routing_key='')
    channel.queue_bind(exchange='exchangeInfo', queue=specialization1, routing_key=specialization1)
    channel.queue_bind(exchange='exchangeInfo', queue=specialization2, routing_key=specialization2)

    channel.basic_consume(queue=specialization1,
                          on_message_callback=cure,
                          auto_ack=False)
    channel.basic_consume(queue=specialization2,
                          on_message_callback=cure,
                          auto_ack=False)
    channel.basic_consume(queue=adminQueue,
                          on_message_callback=printInfo,
                          auto_ack=False)
    channel.start_consuming()
