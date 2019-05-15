class Message:
    def __init__(self, sender, patientSurname, patientMalfunction):
        self.sender = sender
        self.patientSurname = patientSurname
        self.patientMalfunction = patientMalfunction

    def __str__(self):
        return '%s %s %s' % (self.sender,
                             self.patientSurname,
                             self.patientMalfunction)

    @staticmethod
    def decode(message):
        message = str(message)
        tmp = message.split("'")[1].split(' ')
        return Message(tmp[0], tmp[1], tmp[2])
