package com.jong.msa.board.support.domain;

import com.jong.msa.board.common.constants.DateTimeFormats;
import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Builder;
import org.junit.jupiter.api.Test;

public class DomainDataTests {

    private Random random = new Random();
    private List<MemberRecord> memberRecords;
    private List<PostRecord> postRecords;
    private List<CommentRecord> commentRecords;

    public static String uuidToHex(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        return "X'" + String.format("%016x%016x", msb, lsb) + "'";
    }

    private static void createSqlFile(String filePath, String content) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)))) {
            writer.write(content);
        }
    }

    @Test
    void createDataSql() throws Exception {

        memberRecords = IntStream.range(0, 10)
            .mapToObj(i -> MemberRecord.builder()
                .id(UUID.randomUUID())
                .username("username-" + i)
                .password("password-" + i)
                .name("name-" + i)
                .gender(i % 2 == 0 ? Gender.MALE : Gender.FEMALE)
                .email("email-" + i + "@email.com")
                .group(i % 2 == 0 ? Group.ADMIN : Group.USER)
                .createdDateTime(LocalDateTime.now())
                .updatedDateTime(LocalDateTime.now())
                .state(State.ACTIVE)
                .build())
            .toList();

        postRecords = memberRecords.stream()
            .flatMap(member -> IntStream.range(0, 10)
                .mapToObj(i -> PostRecord.builder()
                    .id(UUID.randomUUID())
                    .title(String.format("Post Title %d by %s", i, member.username()))
                    .content(String.format("Post Content %d by %s", i, member.username()))
                    .writerId(member.id())
                    .views(1)
                    .createdDateTime(LocalDateTime.now())
                    .updatedDateTime(LocalDateTime.now())
                    .state(State.ACTIVE)
                    .build()))
            .toList();

        commentRecords = new ArrayList<>();
        commentRecords.addAll(postRecords.stream()
            .flatMap(post -> IntStream.range(0, 3)
                .mapToObj(i -> {
                    MemberRecord member = memberRecords.get(random.nextInt(memberRecords.size() - 1));
                    return CommentRecord.builder()
                        .id(UUID.randomUUID())
                        .content(String.format("Comment Content %d by %s", i, member.username()))
                        .writerId(member.id())
                        .postId(post.id())
                        .parentId(null)
                        .createdDateTime(LocalDateTime.now())
                        .updatedDateTime(LocalDateTime.now())
                        .state(State.ACTIVE)
                        .build();
                }))
            .toList());

        commentRecords.addAll(commentRecords.stream()
            .map(comment -> {
                MemberRecord member = memberRecords.get(random.nextInt(memberRecords.size() - 1));
                return CommentRecord.builder()
                    .id(UUID.randomUUID())
                    .content(String.format("Child Comment Content by %s", member.username()))
                    .writerId(member.id())
                    .postId(comment.postId())
                    .parentId(comment.id())
                    .createdDateTime(LocalDateTime.now())
                    .updatedDateTime(LocalDateTime.now())
                    .state(State.ACTIVE)
                    .build();
            })
            .toList());

        commentRecords.addAll(commentRecords.stream()
            .map(comment -> {
                MemberRecord member = memberRecords.get(random.nextInt(memberRecords.size() - 1));
                return CommentRecord.builder()
                    .id(UUID.randomUUID())
                    .content(String.format("Child Comment Content by %s", member.username()))
                    .writerId(member.id())
                    .postId(comment.postId())
                    .parentId(comment.id())
                    .createdDateTime(LocalDateTime.now())
                    .updatedDateTime(LocalDateTime.now())
                    .state(State.ACTIVE)
                    .build();
            })
            .toList());

        String memberDataQueryPrefix = """
            INSERT INTO `tb_member` 
                (`id`,
                `member_username`,
                `member_password`,
                `member_name`,
                `member_gender`,
                `member_email`,
                `member_group`,
                `created_date_time`,
                `updated_date_time`,
                `state`
            ) VALUES  
            """;
        String postDataQueryPrefix = """
            INSERT INTO `tb_post` (
                `id`,
                `post_title`,
                `post_content`,
                `post_writer_id`,
                `post_views`,
                `created_date_time`,
                `updated_date_time`,
                `state`
            ) VALUES  
            """;
        String commentDataQueryPrefix = """
            INSERT INTO `tb_comment` (
                `id`,
                `comment_content`,
                `comment_writer_id`,
                `comment_post_id`,
                `comment_parent_id`,
                `created_date_time`,
                `updated_date_time`,
                `state`
            ) VALUES  
            """;

        String memberDataQuery = memberRecords.stream()
            .map(MemberRecord::toInsertQuery)
            .collect(Collectors.joining(",\n", memberDataQueryPrefix, ";"));

        String postDataQuery = postRecords.stream()
            .map(PostRecord::toInsertQuery)
            .collect(Collectors.joining(",\n", postDataQueryPrefix, ";"));

        StringBuilder commentDataQueries = new StringBuilder();
        int loopCount = (int) Math.ceil((double) commentRecords.size() / 100);
        for (int i = 0; i < loopCount; i++) {
            int startIndex = i * 100;
            int endIndex = Math.min((i + 1) * 100, commentRecords.size());
            commentDataQueries.append(commentRecords
                .subList(startIndex, endIndex).stream()
                .map(CommentRecord::toInsertQuery)
                .collect(Collectors.joining(",\n", commentDataQueryPrefix, ";")));
        }

        createSqlFile("src/main/resources/jpa/data-member.sql", memberDataQuery);
        createSqlFile("src/main/resources/jpa/data-post.sql", postDataQuery);
        createSqlFile("src/main/resources/jpa/data-comment.sql", commentDataQueries.toString());
    }

    @Builder
    public record MemberRecord(
        UUID id,
        String username,
        String password,
        String name,
        Gender gender,
        String email,
        Group group,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime,
        State state
    ) {

        public String toInsertQuery() {
            String idString = uuidToHex(id);
            String genderString = gender == Gender.MALE ? "M" : "F";
            int groupCode = group == Group.ADMIN ? 1 : 2;
            int stateCode = state == State.ACTIVE ? 1 : 0;
            String createdDateTimeString = createdDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER);
            String updatedDateTimeString = updatedDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER);
            return String.format("(%s, '%s', '%s', '%s', '%s', '%s', %d, '%s', '%s', %d)",
                idString, username, password, name, genderString, email, groupCode,
                createdDateTimeString, updatedDateTimeString, stateCode);
        }
    }

    @Builder
    public record PostRecord(
        UUID id,
        String title,
        String content,
        UUID writerId,
        int views,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime,
        State state
    ) {

        public String toInsertQuery() {
            String idString = uuidToHex(id);
            String writerIdString = uuidToHex(writerId);
            int stateCode = state == State.ACTIVE ? 1 : 0;
            String createdDateTimeString = createdDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER);
            String updatedDateTimeString = updatedDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER);
            return String.format("(%s, '%s', '%s', %s, %d, '%s', '%s', %d)",
                idString, title, content, writerIdString, views,
                createdDateTimeString, updatedDateTimeString, stateCode);
        }
    }

    @Builder
    public record CommentRecord(
        UUID id,
        String content,
        UUID writerId,
        UUID postId,
        UUID parentId,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime,
        State state
    ) {

        public String toInsertQuery() {
            String idString = uuidToHex(id);
            String writerIdString = uuidToHex(writerId);
            String postIdString = uuidToHex(postId);
            String parentIdString = parentId == null ? "NULL" : uuidToHex(parentId);
            int stateCode = state == State.ACTIVE ? 1 : 0;
            String createdDateTimeString = createdDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER);
            String updatedDateTimeString = updatedDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER);
            return String.format("(%s, '%s', %s, %s, %s, '%s', '%s', %d)",
                idString, content, writerIdString, postIdString, parentIdString,
                createdDateTimeString, updatedDateTimeString, stateCode);
        }
    }

}
