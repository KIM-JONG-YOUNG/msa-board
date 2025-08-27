package com.jong.msa.board.support.domain.document;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@Mapping(mappingPath = "elasticsearch/member-mapping.json")
@Setting(settingPath = "elasticsearch/analyzer-setting.json")
@Document(indexName = "members", writeTypeHint = WriteTypeHint.FALSE, storeVersionInSource = false)
public class MemberDocument extends AbstractBaseDocument<MemberDocument> {

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
