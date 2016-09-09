package sample;

import org.h2.engine.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by RdDvls on 9/8/16.
 */
public class ToDoDatabaseTest {
    static ToDoDatabase todoDatabase;

    public ToDoDatabaseTest() {
        super();
        System.out.println("Building a new database instance!!!!");
    }

    @Before
    public void setUp() throws Exception {
        if (todoDatabase == null) {
            todoDatabase = new ToDoDatabase();
            todoDatabase.init();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInit() throws Exception {
        // test to make sure we can access the new database
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        PreparedStatement todoQuery = conn.prepareStatement("SELECT * FROM todos");
        ResultSet results = todoQuery.executeQuery();
        assertNotNull(results);

    }

    @Test
    public void testInsertToDo() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-ToDo";

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = todoDatabase.insertUser(conn, username, fullName);

        todoDatabase.insertToDo(conn, todoText, userID);

        // make sure we can retrieve the todo we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos where text = ?");
        stmt.setString(1, todoText);
        ResultSet results = stmt.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(1, numResults);

        todoDatabase.deleteToDo(conn, todoText);
        // make sure we remove the test user we added earlier
        todoDatabase.deleteUser(conn, username);

        // make sure there are no more records for our test todo
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }


    @Test
    public void testSelectAllToDos() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";

        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn);
        int todosBefore = todos.size();

        String userName = "unittester@tiy.com";
        String name = "anything";
        int UserID = todoDatabase.insertUser(conn, userName, name);
        todoDatabase.insertToDo(conn, firstToDoText, UserID);
        todoDatabase.insertToDo(conn, secondToDoText, UserID);
        todoDatabase.insertUser(conn, userName, name);
        todos = todoDatabase.selectToDos(conn);

        System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() > 1);

        todoDatabase.deleteToDo(conn, firstToDoText);
        todoDatabase.deleteToDo(conn, secondToDoText);
    }

    @Test
    public void testInsertToDoForUser() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-ToDo";
        String todoText2 = "UnitTest-ToDo2";

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = todoDatabase.insertUser(conn, username, fullName);

        String username2 = "unitester2@tiy.com";
        String fullName2 = "Unit Tester 2";
        int userID2 = todoDatabase.insertUser(conn, username2, fullName2);

        todoDatabase.insertToDo(conn, todoText, userID);
        todoDatabase.insertToDo(conn, todoText2, userID2);

        // make sure each user only has one todo item
        ArrayList<ToDoItem> todosUser1 = todoDatabase.selectToDosForUser(conn, userID);
        ArrayList<ToDoItem> todosUser2 = todoDatabase.selectToDosForUser(conn, userID2);

        assertEquals(1, todosUser1.size());
        assertEquals(1, todosUser2.size());

        // make sure each todo item matches
        ToDoItem todoUser1 = todosUser1.get(0);
        assertEquals(todoText, todoUser1.text);
        ToDoItem todoUser2 = todosUser2.get(0);
        assertEquals(todoText2, todoUser2.text);

        todoDatabase.deleteToDo(conn, todoText);
        todoDatabase.deleteToDo(conn, todoText2);
        // make sure we remove the test user we added earlier
        todoDatabase.deleteUser(conn, username);
        todoDatabase.deleteUser(conn, username2);

    }


    @Test
    public void testInsertUser() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String name = "Name";
        String userName = "UserName";

        todoDatabase.insertUser(conn, name, userName);

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE userName = ?");
        stmt.setString(1, userName);
        ResultSet result;
//        assertNotNull(result);

        todoDatabase.deleteUser(conn, userName);

        result = stmt.executeQuery();
        int numResult = 0;
        while (result.next()) {
            numResult++;
        }
        assertEquals(0, numResult);


    }
}

//    @Test
//    public void testToggleToDos() throws Exception{
//        ToDoItem tdItem = new ToDoItem();
////        Controller testController = new Controller();
//        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
//        String todoText = "Unit-Test Toggle Me";
//
//
//
//        todoDatabase.insertToDo(conn, todoText,);
//
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos WHERE text = ? ");
//        stmt.setString(1, todoText);
//
//        ResultSet result = stmt.executeQuery();
////        assertNotNull(result);
//        if(todoText != null){
//            tdItem.isDone = !tdItem.isDone;
//
//        }
//        }
//
//
//    }
