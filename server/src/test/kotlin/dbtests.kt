import java.sql.*

class MySQLAccess {
    private var connect: Connection? = null
    private var statement: Statement? = null
    private var preparedStatement: PreparedStatement? = null
    private var resultSet: ResultSet? = null

    @Throws(Exception::class)
    fun readDataBase() {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.jdbc.Driver")
            // Setup the connection with the DB
            connect = DriverManager.getConnection("jdbc:mysql://localhost/feedback?" + "user=sqluser&password=sqluserpw")

            // Statements allow to issue SQL queries to the database
            statement = connect!!.createStatement()
            // Result set get the result of the SQL query
            resultSet = statement!!
                    .executeQuery("select * from feedback.comments")
            writeResultSet(resultSet)

            // PreparedStatements can use variables and are more efficient
            preparedStatement = connect!!
                    .prepareStatement("insert into  feedback.comments values (default, ?, ?, ?, ? , ?, ?)")
            // "myuser, webpage, datum, summary, COMMENTS from feedback.comments");
            // Parameters start with 1
            preparedStatement!!.setString(1, "Test")
            preparedStatement!!.setString(2, "TestEmail")
            preparedStatement!!.setString(3, "TestWebpage")
            preparedStatement!!.setDate(4, java.sql.Date(2009, 12, 11))
            preparedStatement!!.setString(5, "TestSummary")
            preparedStatement!!.setString(6, "TestComment")
            preparedStatement!!.executeUpdate()

            preparedStatement = connect!!
                    .prepareStatement("SELECT myuser, webpage, datum, summary, COMMENTS from feedback.comments")
            resultSet = preparedStatement!!.executeQuery()
            writeResultSet(resultSet)

            // Remove again the insert comment
            preparedStatement = connect!!
                    .prepareStatement("delete from feedback.comments where myuser= ? ; ")
            preparedStatement!!.setString(1, "Test")
            preparedStatement!!.executeUpdate()

            resultSet = statement!!
                    .executeQuery("select * from feedback.comments")
            writeMetaData(resultSet)

        } catch (e: Exception) {
            throw e
        } finally {
            close()
        }

    }

    @Throws(SQLException::class)
    private fun writeMetaData(resultSet: ResultSet?) {
        //  Now get some metadata from the database
        // Result set get the result of the SQL query

        println("The columns in the table are: ")

        println("Table: " + resultSet!!.metaData.getTableName(1))
        for (i in 1..resultSet.metaData.columnCount) {
            println("Column " + i + " " + resultSet.metaData.getColumnName(i))
        }
    }

    @Throws(SQLException::class)
    private fun writeResultSet(resultSet: ResultSet?) {
        // ResultSet is initially before the first data set
        while (resultSet!!.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            val user = resultSet.getString("myuser")
            val website = resultSet.getString("webpage")
            val summary = resultSet.getString("summary")
            val date = resultSet.getDate("datum")
            val comment = resultSet.getString("comments")
            println("User: " + user)
            println("Website: " + website)
            println("summary: " + summary)
            println("Date: " + date)
            println("Comment: " + comment)
        }
    }

    // You need to close the resultSet
    private fun close() {
        try {
            if (resultSet != null) {
                resultSet!!.close()
            }

            if (statement != null) {
                statement!!.close()
            }

            if (connect != null) {
                connect!!.close()
            }
        } catch (e: Exception) {

        }
    }
}

fun main(args: Array<String>) {
    val dao = MySQLAccess()
    dao.readDataBase()
}