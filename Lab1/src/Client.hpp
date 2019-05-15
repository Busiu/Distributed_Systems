//
// Created by Busiu on 07.03.2019.
//

#ifndef DISTRIBUTED_SYSTEMS_CLIENT_HPP
#define DISTRIBUTED_SYSTEMS_CLIENT_HPP

#include <stdlib.h>
#include <stdio.h>
#include <winsock2.h>
#include <windows.h>
#include <ws2tcpip.h>
#include <zconf.h>

#include <iostream>
#include <string>
#include <thread>

#include "MessageQueue.hpp"


class Client
{
private:
    std::string name;
    std::string IPAddress;
    std::string ownListentingPort;
    std::string fromListeningPort;
    std::string ownSendingPort;
    std::string toSendingPort;
    bool hasToken;

    SOCKET listenSocket;
    SOCKET sendSocket;

    MessageQueue bufforQueue;
    MessageQueue sendQueue;
    int sendQueueSize = 50;

public:
    Client(std::string name,
           std::string IPAddress,
           std::string ownListeningPort,
           std::string fromListeningPort,
           std::string ownSendingPort,
           std::string toSendingPort,
           bool hasToken);
    ~Client();

    void connectListeningPort();
    void connectSendingPort();
    void acquireMessageToSend(Message& message);

    void run();
    void listenFromPort();
    void sendToPort();
    void wait();
    void castMessageFromBufforToSendQueue();

    std::string getName();
};


#endif //DISTRIBUTED_SYSTEMS_CLIENT_HPP
