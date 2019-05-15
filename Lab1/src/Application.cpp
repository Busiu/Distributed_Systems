//
// Created by Busiu on 07.03.2019.
//

#include "Application.hpp"


Application::Application()
{
    WSADATA wsaData;
    int iResult;

    //  Initialize Winsock
    iResult = WSAStartup(MAKEWORD(2, 2), &wsaData);
    if (iResult != 0)
    {
        std::cout << "WSAStartup failed: "
                     << iResult
                  << std::endl;
    }
}

Application::~Application()
{
    std::cout << "Exiting." << std::endl;

    //  Delete Clients
    for(int i = 0; i < clients.size(); i++)
    {
        delete clients[i];
    }

    //  Exit Winsock
    WSACleanup();
}

void Application::run()
{
    createMessages();
    createAdamAndEwaAndApple();

    std::vector<std::thread*> threads;
    threads.resize(clients.size());
    for(int i = 0; i < clients.size(); i++)
    {
        threads[i] = new std::thread(runClient, this, i);
    }
    for(int i = 0; i < clients.size(); i++)
    {
        threads[i]->join();
        delete threads[i];
    }
}

void Application::createMessages()
{
    Message message1 = {"SIEMA",
                        "10003",
                        "127.0.0.1",
                        "10001",
                        "127.0.0.1",
                        false};
    Message message2 = {"ELO",
                        "10005",
                        "127.0.0.1",
                        "10001",
                        "127.0.0.1",
                        false};
    Message message3 = {"Szukam cieplego i troskliwego mezczyzny",
                        "10001",
                        "127.0.0.1",
                        "10003",
                        "127.0.0.1",
                        false};
    Message message4 = {"Jestem jablkiem",
                        "10001",
                        "127.0.0.1",
                        "10005",
                        "127.0.0.1",
                        false};
    Message message5 = {"Wiadomosc do nikogo",
                        "12345",
                        "127.0.0.1",
                        "10005",
                        "127.0.0.1",
                        false};

    messagesToGrant.push_back(message1);
    messagesToGrant.push_back(message2);
    messagesToGrant.push_back(message3);
    messagesToGrant.push_back(message4);
    messagesToGrant.push_back(message5);
}

void Application::createAdamAndEwaAndApple()
{
    Client* Adam;
    Client* Ewa;
    Client* Apple;
    Adam = new Client("Adam", "127.0.0.1", "10001", "10006", "10002", "10003", true);
    Ewa = new Client("Ewa", "127.0.0.1", "10003", "10002", "10004", "10005", false);
    Apple = new Client("Apple", "127.0.0.1", "10005", "10004", "10006", "10001", false);
    clients.push_back(Adam);
    clients.push_back(Ewa);
    clients.push_back(Apple);

    std::thread AdamConfigureThread(configureFirstClient, this, 0);
    std::thread AppleConfigureThread(configureOtherClient, this, 2);
    std::thread EwaConfigureThread(configureOtherClient, this, 1);

    giveMessageToSend(0, messagesToGrant[0]);
    giveMessageToSend(0, messagesToGrant[1]);
    giveMessageToSend(1, messagesToGrant[2]);
    giveMessageToSend(2, messagesToGrant[3]);
    giveMessageToSend(2, messagesToGrant[4]);

    AdamConfigureThread.join();
    EwaConfigureThread.join();
    AppleConfigureThread.join();
}

void Application::configureFirstClient(int index)
{
    clients[index]->connectListeningPort();
    clients[index]->connectSendingPort();
}

void Application::configureOtherClient(int index)
{
    clients[index]->connectSendingPort();
    clients[index]->connectListeningPort();
}

void Application::giveMessageToSend(int index, Message& message)
{
    clients[index]->acquireMessageToSend(message);
}

void Application::runClient(int index)
{
    clients[index]->run();
}
