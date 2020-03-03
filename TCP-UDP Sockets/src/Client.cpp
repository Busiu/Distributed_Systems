//
// Created by Busiu on 07.03.2019.
//

#include "Client.hpp"


Client::Client(std::string name,
               std::string IPAddress,
               std::string ownListeningPort,
               std::string fromListeningPort,
               std::string ownSendingPort,
               std::string toSendingPort,
               bool hasToken)
{
    this->name = name;
    this->IPAddress = IPAddress;
    this->ownListentingPort = ownListeningPort;
    this->fromListeningPort = fromListeningPort;
    this->ownSendingPort = ownSendingPort;
    this->toSendingPort = toSendingPort;
    this->hasToken = hasToken;
}

Client::~Client()
{
    std::cout << "NISZCZONKO" << std::endl;
    closesocket(listenSocket);
    closesocket(sendSocket);
}

void Client::connectListeningPort()
{
    struct addrinfo* result = nullptr;
    struct addrinfo hints;

    SecureZeroMemory(&hints, sizeof(hints));
    hints.ai_family = AF_INET;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;
    hints.ai_flags = AI_PASSIVE;

    // Resolve the server address and port
    int iResult = getaddrinfo(nullptr, fromListeningPort.c_str(), &hints, &result);
    if ( iResult != 0 )
    {
        std::cout << "Getaddrinfo failed with error: " << iResult << std::endl;
        return;
    }

    // Create a SOCKET for connecting to server
    listenSocket = socket(result->ai_family, result->ai_socktype, result->ai_protocol);
    if (listenSocket == INVALID_SOCKET)
    {
        std::cout << "Socket failed with error: " << WSAGetLastError() << std::endl;
        freeaddrinfo(result);
        return;
    }

    // Setup the TCP listening socket
    iResult = bind(listenSocket, result->ai_addr, (int)result->ai_addrlen);
    if (iResult == SOCKET_ERROR)
    {
        std::cout << "Bind failed with error: " << WSAGetLastError() << std::endl;
        freeaddrinfo(result);
        closesocket(listenSocket);
        return;
    }

    freeaddrinfo(result);

    iResult = listen(listenSocket, SOMAXCONN);
    if (iResult == SOCKET_ERROR)
    {
        std::cout << "Listen failed with error: " << WSAGetLastError() << std::endl;
        closesocket(listenSocket);
        return;
    }

    // Accept a client socket
    SOCKET ClientSocket = accept(listenSocket, nullptr, nullptr);
    if (ClientSocket == INVALID_SOCKET)
    {
        std::cout << "Accept failed with error: " << WSAGetLastError() << std::endl;
        closesocket(listenSocket);
        return;
    }

    closesocket(listenSocket);
    listenSocket = ClientSocket;
}

void Client::connectSendingPort()
{
    addrinfo* result = nullptr;
    addrinfo* ptr = nullptr;
    addrinfo hints;

    ZeroMemory( &hints, sizeof(hints) );
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_protocol = IPPROTO_TCP;

    // Resolve the server address and port
    int iResult = getaddrinfo(IPAddress.c_str(), ownSendingPort.c_str(), &hints, &result);
    if ( iResult != 0 )
    {
        std::cout << "Getaddrinfo failed with error: "
                     << iResult
                  << std::endl;
        return;
    }

    // Attempt to connect to an address until one succeeds
    for(ptr=result; ptr != nullptr; ptr=ptr->ai_next)
    {
        // Create a SOCKET for connecting to server
        sendSocket = socket(ptr->ai_family, ptr->ai_socktype, ptr->ai_protocol);
        if (sendSocket == INVALID_SOCKET)
        {
            std::cout << "Socket failed with error: "
                         << WSAGetLastError()
                      << std::endl;
            return;
        }

        // Connect to server.
        iResult = connect(sendSocket, ptr->ai_addr, (int)ptr->ai_addrlen);
        if (iResult == SOCKET_ERROR)
        {
            closesocket(sendSocket);
            sendSocket = INVALID_SOCKET;
            continue;
        }
        break;
    }

    freeaddrinfo(result);

    if (sendSocket == INVALID_SOCKET)
    {
        std::cout << "Unable to connect to server!" << std::endl;
        return;
    }
}

void Client::acquireMessageToSend(Message& message)
{
    bufforQueue.write(message);
}

void Client::run()
{
    std::thread listener(listenFromPort, this);
    std::thread sender(sendToPort, this);

    listener.join();
    sender.join();
}

void Client::listenFromPort()
{
    int recvbuflen = 1069;
    char recvbuf[recvbuflen];
    Message message;

    while(1)
    {
        int iResult = recv(listenSocket, recvbuf, recvbuflen, 0);
        if ( iResult > 0 )
        {
            message = *((Message*) &recvbuf);
            if(message.ipFrom == IPAddress && message.portFrom == ownListentingPort)
            {
                std::cout << name
                          << " eradicated message: "
                             << message.content
                          << std::endl;
            }
            else if(message.token)
            {
                hasToken = true;
            }
            else if(message.ipTo == IPAddress && message.portTo == ownListentingPort)
            {
                std::cout << name
                          << " got message: '"
                             << message.content
                          << "' from IP - "
                             << message.ipFrom
                          << ", Port - "
                             << message.portFrom
                          << std::endl;
            }
            else
            {
                bufforQueue.write(message);
            }
        }
        else if ( iResult == 0 )
        {
            std::cout << "Connection closed" << std::endl;
        }
        else
        {
            std::cout << "Recv failed with error: "
                         << WSAGetLastError()
                      << std::endl;
        }
    }
}

void Client::sendToPort()
{
    while(1)
    {
        wait();
        castMessageFromBufforToSendQueue();
        sendQueue.assignToken();

        while(!sendQueue.isEmpty())
        {
            Message message = sendQueue.read();
            int iResult = send(sendSocket, (char*) &message, 1069, 0);
            if (iResult == SOCKET_ERROR)
            {
                std::cout << "Send failed with error: "
                             << WSAGetLastError()
                          << std::endl;
                return;
            }
        }
        hasToken = false;
    }
}

void Client::wait()
{
    while(!hasToken)
    {
        sleep(2);
    }
    sleep(2);

    std::cout << name << " got Token!" << std::endl;
}

void Client::castMessageFromBufforToSendQueue()
{
    for(int i = 0; !bufforQueue.isEmpty() && i < sendQueueSize; i++)
    {
        Message tmp = bufforQueue.read();
        tmp.token = false;
        sendQueue.write(tmp);
    }
}

std::string Client::getName()
{
    return name;
}