//
// Created by Busiu on 10.03.2019.
//

#ifndef DISTRIBUTED_SYSTEMS_MESSAGEQUEUE_HPP
#define DISTRIBUTED_SYSTEMS_MESSAGEQUEUE_HPP

#include <queue>

#include "Message.hpp"

class MessageQueue
{
private:
    std::queue<Message> messages;

public:
    void write(Message message);
    Message read();
    bool isEmpty();
    void assignToken();

};


#endif //DISTRIBUTED_SYSTEMS_MESSAGEQUEUE_HPP
