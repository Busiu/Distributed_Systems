//
// Created by Busiu on 10.03.2019.
//

#ifndef DISTRIBUTED_SYSTEMS_MESSAGE_HPP
#define DISTRIBUTED_SYSTEMS_MESSAGE_HPP


struct Message
{
    char content[1024];
    char portTo[6];
    char ipTo[16];
    char portFrom[6];
    char ipFrom[16];
    bool token;
};


#endif //DISTRIBUTED_SYSTEMS_MESSAGE_HPP
