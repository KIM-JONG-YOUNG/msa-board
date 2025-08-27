package com.jong.msa.board.support.domain.document;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Getter
@SuperBuilder
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Mapping(mappingPath = "elasticsearch/post-mapping.json")
@Setting(settingPath = "elasticsearch/analyzer-setting.json")
@Document(indexName = "posts", writeTypeHint = WriteTypeHint.FALSE)
public class PostDocument extends AbstractBaseDocument<PostDocument> {

    @MultiField(
        mainField = @Field(name = "title", type = FieldType.Text),
        otherFields = {
            @InnerField(suffix = "bigram", type = FieldType.Text),
            @InnerField(suffix = "morph", type = FieldType.Text)
        })
    private String title;

    @MultiField(
        mainField = @Field(name = "content", type = FieldType.Text),
        otherFields = {
            @InnerField(suffix = "bigram", type = FieldType.Text),
            @InnerField(suffix = "morph", type = FieldType.Text)
        })
    private String content;

    @Field(name = "views", type = FieldType.Integer)
    private int views;

    @With
    @Field(name = "writer", type = FieldType.Nested)
    private Writer writer;

    @Getter
    @SuperBuilder
    @Accessors(chain = true)
    @ToString(callSuper = true)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Writer extends AbstractBaseDocument<Writer> {

        @MultiField(
            mainField = @Field(name = "username", type = FieldType.Keyword),
            otherFields = {
                @InnerField(suffix = "bigram", type = FieldType.Text),
                @InnerField(suffix = "morph", type = FieldType.Text)
            })
        private String username;

        @MultiField(
            mainField = @Field(name = "name", type = FieldType.Keyword),
            otherFields = {
                @InnerField(suffix = "bigram", type = FieldType.Text),
                @InnerField(suffix = "morph", type = FieldType.Text)
            })
        private String name;

        @Field(name = "gender", type = FieldType.Keyword)
        private Gender gender;

        @MultiField(
            mainField = @Field(name = "email", type = FieldType.Keyword),
            otherFields = {
                @InnerField(suffix = "bigram", type = FieldType.Text),
                @InnerField(suffix = "morph", type = FieldType.Text)
            })
        private String email;

        @Field(name = "group", type = FieldType.Keyword)
        private Group group;

    }

}
