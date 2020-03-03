//
// Created by Busiu on 07.03.2019.
//

#ifndef DISTRIBUTED_SYSTEMS_APPLICATION_HPP
#define DISTRIBUTED_SYSTEMS_APPLICATION_HPP

#include <winsock2.h>

#include <iostream>
#include <thread>
#include <vector>

#include "Client.hpp"
#include "Message.hpp"


class Application
{
private:
    std::vector<Client*> clients;
    std::vector<Message> messagesToGrant;

public:
    Application();
    ~Application();

    void run();

private:
    void createMessages();
    void createAdamAndEwaAndApple();
    void configureFirstClient(int index);
    void configureOtherClient(int index);
    void giveMessageToSend(int index, Message& message);
    void runClient(int index);
};


#endif //DISTRIBUTED_SYSTEMS_APPLICATION_HPP
