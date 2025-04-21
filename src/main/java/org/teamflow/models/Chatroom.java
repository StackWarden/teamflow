package org.teamflow.models;

import org.teamflow.database.DatabaseConnection;
import org.teamflow.enums.ChatroomLinkType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Chatroom {
    private int id;
    private String name;

    private ChatroomLinkType linkType = ChatroomLinkType.NONE;
    private int linkedEntityId = -1;

    public Chatroom() {}

    public Chatroom(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Chatroom(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ChatroomLinkType getLinkType() {
        return linkType;
    }

    public int getLinkedEntityId() {
        return linkedEntityId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLinkType(ChatroomLinkType linkType) {
        this.linkType = linkType;
    }

    public void setLinkedEntityId(int linkedEntityId) {
        this.linkedEntityId = linkedEntityId;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", linkType=" + linkType +
                ", linkedEntityId=" + linkedEntityId +
                '}';
    }

    public static List<Chatroom> getUnlinkedChatrooms() {
        String sql = """
        SELECT * FROM Chatroom c
        WHERE NOT EXISTS (SELECT 1 FROM Epic_Chatroom ec WHERE ec.chatroom_id = c.id)
          AND NOT EXISTS (SELECT 1 FROM Story_Chatroom sc WHERE sc.chatroom_id = c.id)
          AND NOT EXISTS (SELECT 1 FROM Task_Chatroom tc WHERE tc.chatroom_id = c.id)
          AND NOT EXISTS (SELECT 1 FROM Sprint_Chatroom spc WHERE spc.chatroom_id = c.id)
    """;

        return queryChatrooms(sql);
    }

    public static List<Chatroom> getLinkedChatrooms(ChatroomLinkType linkType, int linkedEntityId) {
        return switch (linkType) {
            case NONE -> getUnlinkedChatrooms();

            case EPIC -> queryChatrooms("""
            SELECT c.id, c.name,
                'EPIC' AS link_type
            FROM Chatroom c
            INNER JOIN Epic_Chatroom ec ON c.id = ec.chatroom_id
            WHERE ec.epic_id = ?
        """, linkedEntityId);

            case STORY -> queryChatrooms("""
            SELECT c.id, c.name,
                'STORY' AS link_type
            FROM Chatroom c
            INNER JOIN Story_Chatroom sc ON c.id = sc.chatroom_id
            WHERE sc.story_id = ?
            
        """, linkedEntityId);

            case TASK -> queryChatrooms("""
            SELECT c.id, c.name,
                'TASK' AS link_type
            FROM Chatroom c
            INNER JOIN Task_Chatroom tc ON c.id = tc.chatroom_id
            WHERE tc.task_id = ?
        """, linkedEntityId);

            case SPRINT -> queryChatrooms("""
            SELECT c.id, c.name,
                'SPRINT' AS link_type
            FROM Chatroom c
            INNER JOIN Sprint_Chatroom src ON c.id = src.chatroom_id
            WHERE src.sprint_id = ?
        """, linkedEntityId);
        };
    }

    public static List<Chatroom> getAccessibleChatroomsForUser(int userId) {
        String sql = """
           SELECT DISTINCT\s
                        c.id,\s
                        c.name,
                        CASE
                            WHEN ec.chatroom_id IS NOT NULL THEN 'EPIC'
                            WHEN sc.chatroom_id IS NOT NULL THEN 'STORY'
                            WHEN tc.chatroom_id IS NOT NULL THEN 'TASK'
                            WHEN src.chatroom_id IS NOT NULL THEN 'SPRINT'
                            ELSE 'NONE'
                        END AS link_type
                    
                    FROM Chatroom c
                    
                    -- Epic joins
                    LEFT JOIN Epic_Chatroom ec ON ec.chatroom_id = c.id
                    LEFT JOIN Epic e ON e.id = ec.epic_id
                    LEFT JOIN Project ep ON ep.id = e.project_id
                    
                    -- Story joins
                    LEFT JOIN Story_Chatroom sc ON sc.chatroom_id = c.id
                    LEFT JOIN UserStory s ON s.id = sc.story_id
                    LEFT JOIN Epic se ON se.id = s.epic_id
                    LEFT JOIN Project sp ON sp.id = se.project_id
                    
                    -- Task joins
                    LEFT JOIN Task_Chatroom tc ON tc.chatroom_id = c.id
                    LEFT JOIN Task t ON t.id = tc.task_id
                    LEFT JOIN UserStory ts ON ts.id = t.story_id
                    LEFT JOIN Epic te ON te.id = ts.epic_id
                    LEFT JOIN Project tp ON tp.id = te.project_id
                    
                    -- Sprint joins
                    LEFT JOIN Sprint_Chatroom src ON src.chatroom_id = c.id
                    LEFT JOIN Sprint sr ON sr.id = src.sprint_id
                    LEFT JOIN Project srp ON srp.id = sr.project_id
                    
                    -- Membership
                    LEFT JOIN User_Project up1 ON up1.project_id = ep.id
                    LEFT JOIN User_Project up2 ON up2.project_id = sp.id
                    LEFT JOIN User_Project up3 ON up3.project_id = tp.id
                    LEFT JOIN User_Project up4 ON up4.project_id = srp.id
                    
                    WHERE\s
                        (up1.user_id = ? OR up2.user_id = ? OR up3.user_id = ? OR up4.user_id = ?)
                        OR (
                            ec.epic_id IS NULL
                            AND sc.story_id IS NULL
                            AND tc.task_id IS NULL
                            AND src.sprint_id IS NULL
                        )
        """;

        return queryChatrooms(sql, userId, userId, userId, userId);
    }

    private static List<Chatroom> queryChatrooms(String sql, Object... params) {
        List<Chatroom> chatrooms = new ArrayList<>();

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Chatroom chatroom = new Chatroom();
                    chatroom.setId(rs.getInt("id"));
                    chatroom.setName(rs.getString("name"));

                    String linkTypeString = rs.getString("link_type");
                    if (linkTypeString != null) {
                        try {
                            chatroom.setLinkType(ChatroomLinkType.valueOf(linkTypeString));
                        } catch (IllegalArgumentException e) {
                            chatroom.setLinkType(ChatroomLinkType.NONE);
                        }
                    } else {
                        chatroom.setLinkType(ChatroomLinkType.NONE);
                    }

                    chatrooms.add(chatroom);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to query chatrooms: " + e.getMessage());
        }

        return chatrooms;
    }


    public List<Message> getMessagesForChatroom() {
        List<Message> messages = new ArrayList<>();

        String sql = """
            SELECT id, chatroom_id, user_id, content, timestamp
            FROM Message
            WHERE chatroom_id = ?
            ORDER BY timestamp ASC
        """;

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Message m = new Message();
                    m.setId(rs.getInt("id"));
                    m.setChatroomId(rs.getInt("chatroom_id"));
                    m.setUserId(rs.getInt("user_id"));
                    m.setContent(rs.getString("content"));
                    m.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    messages.add(m);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to load messages: " + e.getMessage());
        }

        return messages;
    }

    public void createChatroom(Chatroom chatroom) {
        String insertChatroomSQL = "INSERT INTO Chatroom (name) VALUES (?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertChatroomSQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, chatroom.getName());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int chatroomId = rs.getInt(1);
                chatroom.setId(chatroomId);

                int linkedId = chatroom.getLinkedEntityId();

                switch (chatroom.getLinkType()) {
                    case EPIC -> linkTo("Epic_Chatroom", "epic_id", linkedId, chatroomId);
                    case STORY -> linkTo("Story_Chatroom", "story_id", linkedId, chatroomId);
                    case TASK -> linkTo("Task_Chatroom", "task_id", linkedId, chatroomId);
                    case SPRINT -> linkTo("Sprint_Chatroom", "sprint_id", linkedId, chatroomId);
                    case NONE -> {
                        // No link table needed
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to create chatroom: " + e.getMessage());
        }
    }

    private void linkTo(String table, String column, int linkedId, int chatroomId) {
        String sql = "INSERT INTO " + table + " (" + column + ", chatroom_id) VALUES (?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setInt(1, linkedId);
            stmt.setInt(2, chatroomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to link chatroom to " + table + ": " + e.getMessage());
        }
    }

    public boolean isLinked() {
        return linkType != ChatroomLinkType.NONE && linkedEntityId != -1;
    }
}
