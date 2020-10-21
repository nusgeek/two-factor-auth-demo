package ch.rasc.twofa.db;

import java.rmi.StubNotFoundException;
import java.util.*;
import java.util.Map.Entry;

import ch.rasc.twofa.dao.UserRepository;
import ch.rasc.twofa.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/jte")
public class JdbcTemplateController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;
    private static int cnt = 0;

    @RequestMapping("/getUsers")
    public List<Map<String, Object>> getDbType(){
        String sql = "select * from user";
        String queryUserExisted = "SELECT COUNT(username) FROM user WHERE username= user1;";
        List<Map<String, Object>> list =  jdbcTemplate.queryForList(sql);

        for (Map<String, Object> map : list) {
            Set<Entry<String, Object>> entries = map.entrySet( );
            if(entries != null) {
                Iterator<Entry<String, Object>> iterator = entries.iterator( );
                while(iterator.hasNext( )) {
                    Entry<String, Object> entry =(Entry<String, Object>) iterator.next( );
                    Object key = entry.getKey( );
                    Object value = entry.getValue();
                    System.out.println(key+":"+value);
                }
            }
        }
        return list;
    }

    @RequestMapping("/queryAll")
    @ResponseBody
    public List<User> queryAll() {
        String name = "user2";
        List<User> list = new ArrayList<User>();
        list = userRepository.findAll();
        User givenNameList = userRepository.findByUsername(name);
        int num = userRepository.countAllByUsername(name);
        System.out.println("findByUsername " + givenNameList.getPassword());
        System.out.println(num);

        return list;
    }
}
