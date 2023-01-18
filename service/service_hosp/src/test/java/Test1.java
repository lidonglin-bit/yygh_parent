import com.donglin.yygh.hosp.ServiceHospApplication;
import com.donglin.yygh.hosp.bean.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
@SpringBootTest(classes = ServiceHospApplication.class)
public class Test1 {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testInsert(){
        User user = new User();
        user.setAge(20);
        user.setName("test");
        user.setEmail("4932200@qq.com");
        User user1 = mongoTemplate.insert(user);
        System.out.println(user1);
    }

}
