import subprocess

from kazoo.client import KazooClient, EventType
from variables import *


def open_program(path_name):
    return subprocess.Popen(path_name)


def close_program(process):
    process.terminate()


def root_watcher(event):
    global no_children
    global program
    if event.type == EventType.CREATED:
        print(node_name + " node was created")
        program = open_program(app_name)
    if event.type == EventType.DELETED:
        print(node_name + " node was deleted")
        close_program(program)
        no_children = 0

    if kazoo.exists(node_name, watch=root_watcher):
        kazoo.get_children(node_name, watch=children_watcher)


def children_watcher(event):
    global no_children
    if event.type == EventType.CHILD and kazoo.exists(node_name):
        node_data = kazoo.get(node_name)[1]
        new_no_children = node_data.children_count
        if new_no_children > no_children:
            print("Child was added, new number: " + str(new_no_children))
        no_children = new_no_children

    if kazoo.exists(node_name, watch=root_watcher):
        kazoo.get_children(node_name, watch=children_watcher)


def run():
    while True:
        key = input("Press 'a' to print tree.")
        if key == 'a':
            print_tree(node_name_short, node_name, "")
        else:
            print("Wrong key was pressed")


def print_tree(short_name, full_name, tab):
    print(tab + short_name)
    children = kazoo.get_children(full_name)
    tab += indent
    for child_name_short in children:
        child_name = full_name + "/" + child_name_short
        print_tree(child_name_short, child_name, tab)


if __name__ == '__main__':

    kazoo = KazooClient(hosts=host)
    kazoo.start()

    if kazoo.exists(node_name, watch=root_watcher):
        node_data = kazoo.get(node_name)[1]
        no_children = node_data.children_count
        kazoo.get_children(node_name, watch=children_watcher)

    run()
