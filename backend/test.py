import sqlite3

# Connect to the database
conn = sqlite3.connect('users.db')

# Fetch all users
cursor = conn.cursor()
cursor.execute("SELECT * FROM users")
users = cursor.fetchall()

# Print all user data
print(users)

conn.close()
