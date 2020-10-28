import mysql.connector
from mysql.connector import errorcode


def dbAccess():
  try:
    cnx = mysql.connector.connect(user='mcontroller',
                                  password='password123',
                                  host='127.0.0.1',
                                  database='motorcontroldb')

    cursor = cnx.cursor(dictionary=True, buffered=True)

    query = ("SELECT * FROM motorcontroldb.dbactual")

    cursor.execute(query)

    for row in cursor:
      dbval = row['actualval']

    cursor.close()
  except mysql.connector.Error as err:
    if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
      print("Something is wrong with your user name or password")
    elif err.errno == errorcode.ER_BAD_DB_ERROR:
      print("Database does not exist")
    else:
      print(err)
  else:
    cnx.close()
    return dbval
