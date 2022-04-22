package com.lwy.demo.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Document(indexName = "mysql-study-system-school",shards = 3,replicas = 1)   //es索引名字 分片3 副本1
public class School implements Serializable {

    @Id //主键
    @Field(type = FieldType.Text)   //分词
    Integer id;

    @Field(type = FieldType.Text , analyzer = "ik_max_word")   //分词
    String name;

    @Field(type = FieldType.Keyword)  //关键字 不分词
    String createTime;
    @Field(type = FieldType.Keyword)  //关键字 不分词
    String deadline;
    @Field(type = FieldType.Keyword)  //关键字 不分词
    Integer studentNum;
}
