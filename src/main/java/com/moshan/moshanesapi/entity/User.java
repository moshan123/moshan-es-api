package com.moshan.moshanesapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @ClassName: User
 * @Package: com.moshan.moshanesapi.entity
 * @Description:
 * @Datetime: 2021/4/11 10:47
 * @Author: zyc
 * @Version: 1.0
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    String name;

    int age;


}
