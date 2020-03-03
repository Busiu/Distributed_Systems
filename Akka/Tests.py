with open("databases/database1", "r") as database:
    books = []
    for line in database:
        books.append(line)
for book in books:
    tmp = book.split(' : ')
    print(tmp[0])
