import pika

from Utils import Message
from threading import Thread


def sendOrder(patientSurname, patientMalfunction):
    msg = str(Message(doctorName, patientSurname, patientMalfunction))
    channel.basic_publish(exchange='exchangeInfo',
                          routing_key=patientMalfunction,
                          body=msg)
    channel.basic_publish(exchange='exchangeInfo',
                          routing_key='log',
                          body=msg)

def receiverHandler():
    infoQueue = "%s info" % (doctorName)
    adminQueue = "%s admin" % (doctorName)
    channel.queue_declare(queue=infoQueue)
    channel.queue_bind(exchange='exchangeInfo', queue=infoQueue, routing_key=doctorName)
    channel.queue_declare(queue=adminQueue)
    channel.queue_bind(exchange='exchangeAdmin', queue=adminQueue, routing_key='')
    channel.basic_consume(queue=infoQueue,
                          on_message_callback=cureApproval)
    channel.basic_consume(queue=adminQueue,
                          on_message_callback=printInfo)
    channel.start_consuming()


def cureApproval(ch, method, properties, body):
    message = Message.decode(body)
    print("Patient %s is cured!" % (message.patientSurname))
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

    doctorID = input("Please, enter doctor's ID:")
    doctorName = 'Doctor%s' % (doctorID)

    connection = pika.BlockingConnection(parameters)
    channel = connection.channel()
    channel.exchange_declare(exchange='exchangeInfo', exchange_type='direct')
    channel.exchange_declare(exchange='exchangeAdmin', exchange_type='fanout')
    channel.queue_declare(queue=malfunctions[0])
    channel.queue_declare(queue=malfunctions[1])
    channel.queue_declare(queue=malfunctions[2])

    receiver = Thread(target=receiverHandler)
    receiver.start()

    while True:
        patientSurname = input("Please, enter patient surname:")
        patientMalfunction = input("What is wrong with him? (hip, knee, elbow):")
        if (patientMalfunction.startswith(malfunctions)):
            sendOrder(patientSurname, patientMalfunction)
        else:
            print("Please, enter valid malfunction!")
