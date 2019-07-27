package ru.otus.springlibrary.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "author")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorMongo {

    @Id
    private ObjectId id;

    @Field(value = "first_name")
    private String firstName;

    @Field(value = "last_name")
    private String lastName;

}

