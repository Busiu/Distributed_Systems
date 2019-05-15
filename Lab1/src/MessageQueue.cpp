//
// Created by Busiu on 10.03.2019.
//

#include "MessageQueue.hpp"


void MessageQueue::write(Message message)
{
    messages.push(message);
}

Message MessageQueue::read()
{
    Message message = messages.front();
    messages.pop();

    return message;
}

bool MessageQueue::isEmpty()
{
    return messages.empty();
}

void MessageQueue::assignToken()
{
    Message token = {"0",
                     "0",
                     "0",
                     "0",
                     "0",
                     true};
    messages.push(token);
}