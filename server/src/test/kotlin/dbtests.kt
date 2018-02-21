import java.sql.*

open class Database(jdbcString: String) {
    protected val connect = DriverManager.getConnection(jdbcString)

    fun disconnectFromDB() = connect.close()

    fun deleteAllFromTable(qualifiedTableName: String) {
        val statement = connect.prepareStatement("DELETE from $qualifiedTableName")
        statement.use {
            statement.executeUpdate()
        }
    }
}

class TestDatabase(jdbcString: String): Database(jdbcString) {
    fun insertIntoTestTable(stringToInsert: String) {
        val statement1 = connect.prepareStatement("INSERT into test.test values (?)")
        statement1.use {
            statement1.setString(1, stringToInsert)
            statement1.executeUpdate()
        }
    }

    fun queryTestTable(): List<String> {
        val statement2 = connect.prepareStatement("SELECT text from test.test")
        statement2.use {
            val resultSet = statement2.executeQuery()
            resultSet.use {
                return extractTextFromResultSet(resultSet)
            }
        }
    }

    private fun extractTextFromResultSet(resultSet: ResultSet): List<String> {
        val texts = mutableListOf<String>()
        while (resultSet.next()) {
            texts.add(resultSet.getString("text"))
        }
        return texts
    }
}

fun main(args: Array<String>) {
    val db = TestDatabase("jdbc:mysql://localhost/test?user=admin")
    db.deleteAllFromTable("test.test")
    listOf("joel", "lewis", "dudley").forEach { string -> db.insertIntoTestTable(string) }
    db.queryTestTable().forEach { string -> println(string) }
    db.disconnectFromDB()
}